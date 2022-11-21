package fm.douban.spider;

import com.alibaba.fastjson.JSON;
import fm.douban.model.Singer;
import fm.douban.model.Song;
import fm.douban.model.Subject;
import fm.douban.service.SingerService;
import fm.douban.service.SongService;
import fm.douban.service.SubjectService;

import fm.douban.util.HttpUtil;
import fm.douban.util.SubjectUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import java.util.List;
import java.util.Map;

@Component
public class SubjectSpider {

    @Autowired
    private HttpUtil httpUtil;

    @Autowired
    private SubjectService subjectService;

    @Autowired
    private SingerService singerService;

    @Autowired
    private SongService songService;

    private static final String MHZ_URL = "https://fm.douban.com/j/v2/rec_channels?specific=all";
    private static final String MHZ_REFERER = "https://fm.douban.com/";
    private static final String HOST = "fm.douban.com";
    private static final String COOKIE = "bid=Ncu5kIEfqv0; _ga=GA1.2.1287594105.1658113771; _gid=GA1.2.1941403012.1658113771; _pk_id.100002.f71f=8dd5bce599c240d7.1658114100.1.1658114106.1658114100.; _pk_ref.100001.f71f=%5B%22%22%2C%22%22%2C1658120984%2C%22https%3A%2F%2Fwww.baidu.com%2Flink%3Furl%3DrPL5RdPZGiJRaA4GOMXy3_JuL_85BKjHyaBxRJ78bO3%26wd%3D%26eqid%3De42185be0048517e0000000562d4cee3%22%5D; _pk_ses.100001.f71f=*; dbcl2=\"252590153:y3gPL+FhiuY\"; ck=mE1d; _gat=1; _pk_id.100001.f71f=94073471a5a9a66b.1658113767.2.1658122229.1658114108.";
    private static final String SD_URL =
            "https://fm.douban.com/j/v2/playlist?channel={0}&kbps=128&client=s%3Amainsite%7Cy%3A3.0&app_name=radio_website&version=100&type=n";
    private static final String COL_URL ="https://fm.douban.com/j/v2/songlist/explore?type=hot&genre=0&limit=20&sample_cnt=5";
    private static final String COL_REFERER = "https://fm.douban.com/explore/songlists";

    private static final DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public void saveSubject(Subject subject){
        Subject subject1 = subjectService.get(subject.getId());
        if(subject1 == null){
            subjectService.addSubject(subject);
        }
    }

    public void saveSong(Song song){
        Song song1 = songService.get(song.getId());
        if(song1 == null){
            songService.add(song);
        }
    }

    public void saveSinger(Singer singer){
        Singer singer1 = singerService.get(singer.getId());
        if(singer1 == null){
            singerService.addSinger(singer);
        }
    }

    private Subject buildSubject(Map sourceData, String mainType, String subType) {
        Subject subject = new Subject();
        subject.setCover((String)sourceData.get("cover"));

        if (sourceData.get("title") != null) {
            subject.setName((String)sourceData.get("title"));
        } else if (sourceData.get("name") != null) {
            subject.setName((String)sourceData.get("name"));
        } else {
            subject.setName("");
        }

        subject.setDescription((String)sourceData.get("intro"));
        subject.setId(sourceData.get("id") == null ? null : sourceData.get("id").toString());

        if (sourceData.get("created_time") != null) {
            subject.setPublishDate(LocalDateTime.parse(sourceData.get("created_time").toString(), df));
        } else {
            subject.setPublishDate(LocalDateTime.now());
        }

        subject.setSubjectSubType(subType);
        subject.setSubjectType(mainType);

        List<String> ids = new ArrayList<>();
        subject.setSongIds(ids);
        if (SubjectUtil.TYPE_SUB_ARTIST.equals(subType) && sourceData.get("artist_id") != null) {
            subject.setMaster(sourceData.get("artist_id").toString());
        } else if (SubjectUtil.TYPE_COLLECTION.equals(mainType) && sourceData.get("creator") != null) {
            Map creator = (Map)sourceData.get("creator");
            if (creator != null && creator.get("id") != null) {
                subject.setMaster(creator.get("id").toString());
            }
        }

        return subject;
    }
    public Singer buildSinger(Map source) {
        Singer singer = new Singer();
        singer.setId(source.get("id") == null ? null : source.get("id").toString());
        singer.setName(source.get("name") == null ? null : source.get("name").toString());

        if (source.get("cover") != null && StringUtils.hasText(source.get("cover").toString())) {
            singer.setAvatar(source.get("cover").toString());
        } else if (source.get("picture") != null && StringUtils.hasText(source.get("picture").toString())) {
            singer.setAvatar(source.get("picture").toString());
        } else if (source.get("avatar") != null && StringUtils.hasText(source.get("avatar").toString())) {
            singer.setAvatar(source.get("avatar").toString());
        }

        if (source.get("create_time") != null && StringUtils.hasText(source.get("create_time").toString())) {
            LocalDate ld = LocalDate.parse(source.get("create_time").toString(), df);
            singer.setGmtCreated(LocalDateTime.now());
            singer.setGmtModified(singer.getGmtCreated());
        }

        if (source.get("url") != null && StringUtils.hasText(source.get("url").toString())) {
            singer.setHomepage(source.get("url").toString());
        }

        return singer;
    }
    public Song buildSong(Map source) {
        Song song = new Song();
        song.setUrl((String)source.get("url"));
        song.setCover((String)source.get("picture"));
        song.setId((String)source.get("sid"));
        song.setName((String)source.get("title"));

        List<String> singerIds = new ArrayList<>();
        List<Map> singerSources = (List<Map>)source.get("singers");

        for (Map singerObj : singerSources) {
            Singer singer = buildSinger(singerObj);
            singerIds.add(singer.getId());
            saveSinger(singer);
        }

        song.setSingerIds(singerIds);
        return song;
    }
    //  对于列表的Map增加歌手
    private void addSingers(List<Map> artists) {
        if (artists == null || artists.isEmpty()) {
            return;
        }

        for (Map artistObj : artists) {
            Singer singer = buildSinger(artistObj);
            saveSinger(singer);
        }
    }
    private void addMHZSubject(List<Map> channels, String subjectSubType) {
        if (channels == null || channels.isEmpty()) {
            return;
        }

        for (Map channelObj : channels) {
            Subject subject = buildSubject(channelObj, SubjectUtil.TYPE_MHZ, subjectSubType);
            if (SubjectUtil.TYPE_SUB_ARTIST.equals(subjectSubType)) {
                // 记录关联的歌手
                List relatedArtists = (List)channelObj.get("related_artists");
                addSingers(relatedArtists);
            }

            // 保存MHZ数据
            saveSubject(subject);

            getSubjectSongData(subject);
        }

    }
//   @PostConstruct
    //  系统开启自动执行爬取任务
    public void init(){
        doExcute();
    }

    //  爬取任务
    public void doExcute(){

        getSubjectData();

        getCollectionsData();
    }

    //  执行爬取主题数据
    public void getSubjectData(){
        //  构建表头信息
        Map<String, String> header = httpUtil.buildHeader(MHZ_REFERER,HOST,COOKIE);
        //  获取内容
        String content = httpUtil.getContent(MHZ_URL, header);
        Map returnData = JSON.parseObject(content, Map.class);
        //  获取“data”内的内容
        Map data = (Map) returnData.get("data");
        //  获得“channel”内的内容
        Map channels = (Map) data.get("channels");
        // 从艺术家出发 数据
        List artists = (List)channels.get("artist");

        // 语言/年代 数据
        List languages = (List)channels.get("language");

        // 风格/流派 数据
        List genres = (List)channels.get("genre");

        // 心情/场景 数据
        List scenarios = (List)channels.get("scenario");

        System.out.println("artists=" + artists.size());
        System.out.println("languages=" + languages.size());
        System.out.println("genres=" + genres.size());
        System.out.println("scenarios=" + scenarios.size());

        addMHZSubject(artists, SubjectUtil.TYPE_SUB_ARTIST);
        addMHZSubject(languages, SubjectUtil.TYPE_SUB_AGE);
        addMHZSubject(genres, SubjectUtil.TYPE_SUB_STYLE);
        addMHZSubject(scenarios, SubjectUtil.TYPE_SUB_MOOD);
    }
    //  爬取主题关联的歌曲
    public void getSubjectSongData(Subject subject){
        String channelNum = subject.getId();
        String url = MessageFormat.format(SD_URL,channelNum);
        Map<String, String> header = httpUtil.buildHeader(MHZ_REFERER, HOST, COOKIE);
        String content = httpUtil.getContent(url, header);
        //  转化为Map格式
        Map result = JSON.parseObject(content, Map.class);
        //  获得歌曲列表
        List<Map> songs = (List<Map>) result.get("song");
        //  获得主题的关联歌曲id表单
        List<String> songIds = subject.getSongIds();
        for (Map song : songs) {
            Song song1 = buildSong(song);
            saveSong(song1);
            //  添加歌手id
            if (!songIds.contains(song1.getId())) {
                songIds.add(song1.getId());
            }
            if(!songIds.isEmpty()){
                subject.setSongIds(songIds);
                subjectService.modify(subject);
            }
        }
    }
    //  爬取歌单数据
    public void getCollectionsData(){
        //  构建表头信息
        Map<String, String> header = httpUtil.buildHeader(COL_REFERER, HOST, COOKIE);
        //  获取信息
        String content = httpUtil.getContent(COL_URL, header);
        if(!StringUtils.hasText(content)){
            return;
        }
        //  转化格式
        List<Map> results = JSON.parseObject(content, List.class);
        //  解析数据
        for (Map result : results) {
            Subject subject = buildSubject(result,SubjectUtil.TYPE_COLLECTION,null);
            //  保存主题
            saveSubject(subject);
            //  获取创建者
            Map creator = (Map) result.get("creator");
            if (creator != null && creator.get("id") != null && StringUtils.hasText(creator.get("id").toString())) {
                Singer singer = buildSinger(creator);
                saveSinger(singer);
            }
        }
    }
}
