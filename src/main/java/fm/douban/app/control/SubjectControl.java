package fm.douban.app.control;

import fm.douban.model.Singer;
import fm.douban.model.Song;
import fm.douban.model.Subject;
import fm.douban.service.SingerService;
import fm.douban.service.SongService;
import fm.douban.service.SubjectService;
import fm.douban.util.SubjectUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class SubjectControl {
    @Autowired
    private SubjectService subjectService;

    @Autowired
    private SongService songService;

    @Autowired
    private SingerService singerService;

    @GetMapping(path = "/artist")
    public String mhzDetail(Model model, @RequestParam(name="subjectId") String subjectId){
        Subject subject = subjectService.get(subjectId);
        //  传递subject
        model.addAttribute("subject",subject);
        //  传递songs
        List<String> songIds = subject.getSongIds();
        List<Song> songs = new ArrayList<>();
        for (String songId : songIds) {
            songs.add(songService.get(songId));
        }
        model.addAttribute("songs",songs);
        //  传递singer
        String singerId = subject.getMaster();
        Singer singer = singerService.get(singerId);
        model.addAttribute("singer",singer);
        //  传递simSingers
        List<String> similarSingerIds = singer.getSimilarSingerIds();
        List<Singer> simSingers = new ArrayList<>();
        if(similarSingerIds != null){
            for (String similarSingerId : similarSingerIds) {
                simSingers.add(singerService.get(similarSingerId));
            }
        }
        model.addAttribute("simSingers",simSingers);
        return "mhzdetail";
    }

    @GetMapping(path = "/collection")
    public String collection(Model model){
        List<Subject> collections = subjectService.getSubjects(SubjectUtil.TYPE_COLLECTION);
        model.addAttribute("collections",collections);
        //  将歌曲和歌手利用Map进行绑定,每一个Collection对应
        Map<Subject,List<Song>> songList = new HashMap<>();
        //  歌手对应
        Map<Song,List<Singer>> singerList = new HashMap<>();
        for (Subject collection : collections) {
            //  获得歌曲Id
            List<String> songIds = collection.getSongIds();
            List<Song> songs = new ArrayList<>();
            for (String songId : songIds) {
                //  得到对应的歌曲
                Song song = songService.get(songId);
                songs.add(song);
                //  获得歌曲的演唱歌手
                List<String> singerIds = song.getSingerIds();
                List<Singer> singers = new ArrayList<>();
                for (String singerId : singerIds) {
                    Singer singer = singerService.get(singerId);
                    singers.add(singer);
                }
                //  首先将Subject与Song对应
                Map<Subject,List<Song>> songMap = new HashMap<>();
                songList.put(collection,songs);
                //  将歌手与对应的歌曲对应
                Map<Song,List<Singer>> singerMap = new HashMap<>();
                singerList.put(song,singers);
            }
        }
        //  传递歌曲信息
        model.addAttribute("songMap",songList);
        //  传递歌手信息
        model.addAttribute("singerMap",singerList);
        return "collection";
    }

    //  歌单详情
    @GetMapping(path = "/collectiondetail")
    public String collectionDetail(Model model,@RequestParam(name="subjectId") String subjectId){
        Subject subject = new Subject();
        subject.setMaster(subjectId);
        //  传递subject
        List<Subject> subjects = subjectService.getSubjects(subject);
        if(subjects!=null){
            model.addAttribute("subject",subjects);
        }
        //  传递歌手
        Singer singer = singerService.get(subjectId);
        if(singer!=null){
            model.addAttribute("singer",singer);
        }
        //  传递歌曲
        List<Song> songs = new ArrayList<>();
        for (Subject subject1 : subjects) {
            List<String> songIds = subject1.getSongIds();
            for (String songId : songIds) {
                songs.add(songService.get(songId));
            }
        }
        if(songs!=null){
            model.addAttribute("songs",songs);
        }
        //  其他歌单
        List<String> similarSingerIds = singer.getSimilarSingerIds();
        List<Subject> collections = new ArrayList<>();
        for (String similarSingerId : similarSingerIds) {
            Subject subject2 = new Subject();
            subject2.setMaster(similarSingerId);
            List<Subject> subjectList = subjectService.getSubjects(subject2);
            for (Subject subject1 : subjectList) {
                collections.add(subject1);
            }
        }
        if(collections!=null){
            model.addAttribute("otherSubjects",collections);
        }
        return "collectiondetail";
    }
}
