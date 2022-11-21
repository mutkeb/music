package fm.douban.app.control;

import fm.douban.model.*;
import fm.douban.param.SongQueryParam;
import fm.douban.service.FavoriteService;
import fm.douban.service.SingerService;
import fm.douban.service.SongService;
import fm.douban.service.SubjectService;
import fm.douban.util.FavoriteUtil;
import fm.douban.util.SubjectUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
public class MainControl {
    @Autowired
    private SongService songService;

    @Autowired
    private SingerService singerService;
    
    @Autowired
    private SubjectService subjectService;

    @Autowired
    private FavoriteService favoriteService;

    @GetMapping(path= "/index")
    public String index(Model model){
        SongQueryParam songQueryParam = new SongQueryParam();
        songQueryParam.setPageSize(1);
        songQueryParam.setPageNum(1);
        Page<Song> list = songService.list(songQueryParam);
        Song song1 = new Song();
        for (Song song : list) {
            song1 = song;
        }
        List<Singer> singers = new ArrayList<>();
        for (Song song : list) {
            List<String> singerIds = song.getSingerIds();
            if(singerIds.isEmpty()){
                return null;
            }
            for (String singerId : singerIds) {
                Singer singer = singerService.get(singerId);
                if(singer != null){
                    singers.add(singer);
                }
            }
        }
        model.addAttribute("song",song1);
        model.addAttribute("singers",singers);
        //  获取兆赫面板数据
        List<Subject> subjects = subjectService.getSubjects(SubjectUtil.TYPE_MHZ);
        if(subjects == null){
            return null;
        }
        MhzViewModel age = new MhzViewModel();
        MhzViewModel mood = new MhzViewModel();
        MhzViewModel style = new MhzViewModel();
        age.setTitle("age");
        mood.setTitle("mood");
        style.setTitle("style");
        List<Subject> subjectList = new ArrayList<>();
        age.setSubjects(subjectList);
        mood.setSubjects(subjectList);
        style.setSubjects(subjectList);
        List<Subject> artist = new ArrayList<>();
        for (Subject subject : subjects) {
            //  语言/年代
            if(subject.getSubjectSubType().equals(SubjectUtil.TYPE_SUB_AGE)){
                age.getSubjects().add(subject);
            }
            if(subject.getSubjectSubType().equals(SubjectUtil.TYPE_SUB_MOOD) ){
                mood.getSubjects().add(subject);
            }
            if(subject.getSubjectSubType().equals(SubjectUtil.TYPE_SUB_STYLE) ){
                style.getSubjects().add(subject);
            }
            if(subject.getSubjectSubType().equals(SubjectUtil.TYPE_SUB_ARTIST)){
                artist.add(subject);
            }
        }
        List<MhzViewModel> mhzViewModels = new ArrayList<>();
        mhzViewModels.add(age);
        mhzViewModels.add(mood);
        mhzViewModels.add(style);
        model.addAttribute("mhzViewModels",mhzViewModels);
        model.addAttribute("artistDatas",artist);
        return "index";
    }
    @GetMapping(path = "/player")
    public String player(Model model){
        return "player";
    }
    @GetMapping(path = "/explore")
    public String explore(Model model){
        List<Subject> subjects = subjectService.getSubjects(SubjectUtil.TYPE_MHZ);
        if(subjects == null){
            return null;
        }
        MhzViewModel age = new MhzViewModel();
        MhzViewModel mood = new MhzViewModel();
        MhzViewModel style = new MhzViewModel();
        age.setTitle("age");
        mood.setTitle("mood");
        style.setTitle("style");
        List<Subject> subjectList = new ArrayList<>();
        age.setSubjects(subjectList);
        mood.setSubjects(subjectList);
        style.setSubjects(subjectList);
        List<Subject> artist = new ArrayList<>();
        for (Subject subject : subjects) {
            //  语言/年代
            if(subject.getSubjectSubType().equals(SubjectUtil.TYPE_SUB_AGE)){
                age.getSubjects().add(subject);
            }
            if(subject.getSubjectSubType().equals(SubjectUtil.TYPE_SUB_MOOD) ){
                mood.getSubjects().add(subject);
            }
            if(subject.getSubjectSubType().equals(SubjectUtil.TYPE_SUB_STYLE) ){
                style.getSubjects().add(subject);
            }
            if(subject.getSubjectSubType().equals(SubjectUtil.TYPE_SUB_ARTIST)){
                artist.add(subject);
            }
        }
        List<MhzViewModel> mhzViewModels = new ArrayList<>();
        mhzViewModels.add(age);
        mhzViewModels.add(mood);
        mhzViewModels.add(style);
        model.addAttribute("mhzViewModels",mhzViewModels);
        model.addAttribute("artistDatas",artist);
        return "explore";
    }

    //  搜索页

    @GetMapping(path = "/search")
    public String search(Model model){
        return "search";
    }

    //  搜索结果
    @GetMapping(path = "/searchContent")
    @ResponseBody
    public Map searchContent(@RequestParam(name = "keyword") String keyword){
        SongQueryParam song = new SongQueryParam();
        song.setName(keyword);
        Page<Song> songs = songService.list(song);
        List<Song> content = songs.getContent();
        Map result = new HashMap<>();
        result.put("songs",content);
        return result;
    }

    //  我的页面
    @GetMapping(path = "my")
    public String myPage(Model model, HttpServletRequest request, HttpServletResponse response){
        //  传递favorites模板
        Favorite favorite = new Favorite();
        favorite.setType(FavoriteUtil.TYPE_RED_HEART);
        List<Favorite> list = favoriteService.list(favorite);
        model.addAttribute("favorites",list);
        //  传递歌曲
        favorite.setItemType(FavoriteUtil.ITEM_TYPE_SONG);
        List<Favorite> favs = favoriteService.list(favorite);
        List<Song> songs = new ArrayList<>();
        for (Favorite fav : favs) {
            Song song = songService.get(fav.getItemId());
            songs.add(song);
        }
        model.addAttribute("songs",songs);
        return "my";
    }

    //  已经喜欢，则删除，表示执行不喜欢操作
    //  还没有喜欢记录，则新增，执行喜欢操作
    @GetMapping(path = "/fav")
    @ResponseBody
    public Map doFav(@RequestParam(name = "itemType") String itemType,@RequestParam(name = "itemId") String
                     itemId,HttpServletRequest request,HttpServletResponse response){
        Favorite fav = new Favorite();
        fav.setItemId(itemId);
        fav.setItemType(itemType);
        List<Favorite> list = favoriteService.list(fav);
        Map returnData = new HashMap();
        //  若已经喜欢
        if(list != null && !list.isEmpty()){
            favoriteService.delete(fav);
        }else{
            favoriteService.add(fav);
        }
        returnData.put("message","successful");
        return  returnData;
    }

    @GetMapping(path = "share")
    public String share(Model model){
        return "share";
    }
}
