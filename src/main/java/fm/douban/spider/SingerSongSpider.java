package fm.douban.spider;


import com.alibaba.fastjson.JSON;
import fm.douban.model.Singer;
import fm.douban.model.Song;
import fm.douban.service.SingerService;
import fm.douban.service.SongService;
import fm.douban.service.SubjectService;
import fm.douban.util.HttpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class SingerSongSpider {
    @Autowired
    private HttpUtil httpUtil;

    @Autowired
    private SubjectService subjectService;

    @Autowired
    private SingerService singerService;

    @Autowired
    private SongService songService;

    @Autowired
    private SubjectSpider subjectSpider;

    private static final String SONG_URL = "https://fm.douban.com/j/v2/artist/{0}/";

    private static final String HOST = "fm.douban.com";
//    @PostConstruct
    public void init(){
        doExcute();
    }

    public void doExcute(){
        getSongDataBySingers();
    }


    //  执行爬取歌曲数据
    public void getSongDataBySingers(){
        List<Singer> singers = singerService.getAll();
        for (Singer singer : singers) {
            String  url = MessageFormat.format(SONG_URL,singer.getId());
            Map<String, String> header = httpUtil.buildHeader(null, HOST, null);
            String result = httpUtil.getContent(url, header);
            //  转为Map格式
            Map map = JSON.parseObject(result, Map.class);
            //  获取songlist的内容
            Map songlist = (Map) map.get("songlist");
            //  获取songs
            List<Map> songs = (List<Map>) songlist.get("songs");
            //  开始给song赋值
            for (Map song : songs) {
                Song song1 = subjectSpider.buildSong(song);
                subjectSpider.saveSong(song1);
            }
            // 解析关联的歌手
            Map relatedChannelData = (Map)map.get("related_channel");
            // 保存关联的歌手后，收集关联歌手的 id
            List<String> similarIds = getRelatedSingers(relatedChannelData);
            // 设置给主歌曲
            singer.setSimilarSingerIds(similarIds);
        }
    }
    private List<String> getRelatedSingers(Map sourceData) {
        List<String> similarIds = new ArrayList<>();
        if (sourceData == null || sourceData.isEmpty()) {
            return similarIds;
        }

        List<Map> similarArtistsData = (List<Map>)sourceData.get("similar_artists");

        if (similarArtistsData == null || similarArtistsData.isEmpty()) {
            return similarIds;
        }

        for (Map sArtistObj : similarArtistsData) {
            Singer singer = subjectSpider.buildSinger(sArtistObj);
            subjectSpider.saveSinger(singer);
            similarIds.add(singer.getId());
        }

        return similarIds;
    }

}
