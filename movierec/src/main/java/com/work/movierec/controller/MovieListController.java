package com.work.movierec.controller;


import com.work.movierec.dao.MovieDao;
import com.work.movierec.pojo.Movie;
import com.work.movierec.result.Result;
import com.work.movierec.vo.ScoreVo;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.work.movierec.domain.SeckillUser;
//import com.work.seckill.redis.RedisService;
//import com.work.seckill.service.SeckillUserService;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@RequestMapping("/movie")
public class MovieListController {

//    @Autowired
//    SeckillUserService userService;

//    @Autowired
//    RedisService redisService;
//    @Resource
//    private MongoTemplate mongoTemplate;

    private static Logger log = Logger.getLogger(MovieListController.class.getName());

    private String genres="Western";

    @Autowired
    private MovieDao movieDao;

    @Autowired
    ThymeleafViewResolver thymeleafViewResolver;

    @Autowired
    ApplicationContext applicationContext;

    @RequestMapping(value = "/list")
    // 门票列表页面
    @ResponseBody
    public String ticketList(HttpServletRequest request, HttpServletResponse response, Model model, SeckillUser user) {
        List<Movie> movieList = movieDao.findAllMovies();

        // 高分电影推荐列表
        List<Movie> averageScoreMovies = movieDao.findAverageScoreMovies();
        model.addAttribute("averageScoreMovies",averageScoreMovies);

        // 历史热门电影推荐列表
        List<Movie> hotMovies = movieDao.findHotMovies();
        model.addAttribute("hotMovies",hotMovies);

        // 近期热门电影推荐列表
        List<Movie> recentlyHotMovies = movieDao.findRecentlyHotMovies();
        model.addAttribute("recentlyHotMovies",recentlyHotMovies);

        // 按类别热门电影
        List<Movie> hotMoviesByGenres =movieDao.findRecommendMoviesByGenres(genres);
        model.addAttribute("hotMoviesByGenres",hotMoviesByGenres);

        // 用户实时推荐
        List<Movie> onlineRecommendMovies =movieDao.findOnlineRecommendMovies(123);
        model.addAttribute("onlineRecommendMovies",onlineRecommendMovies.subList(0,6));

        // 手动渲染
        IWebContext ctx = new WebContext(request,response,
                request.getServletContext(),request.getLocale(), model.asMap());
        String html = thymeleafViewResolver.getTemplateEngine().process("movie_list", ctx);
//        if(!StringUtils.isEmpty(html)) {// 保存到缓存中去
//            redisService.set(TicketKey.getTicketList, "", html);
//        }
        return html;
    }

    // 实时电影评分
    @RequestMapping("/score")
    @ResponseBody
    public Result<Boolean> score(HttpServletResponse response, ScoreVo scoreVo) {
        // 写入评分日志
        log.info("MOVIE_RATING_PREFIX" + ":" + scoreVo.getUid() +"|"+ scoreVo.getMid() +"|"+
                scoreVo.getScore() +"|"+ System.currentTimeMillis()/1000);

        return Result.success(true);
    }

    // 切换电影分类
    @RequestMapping("/genresselect")
    @ResponseBody
    public Result<Boolean> genresSelect(HttpServletResponse response, Model model, String genres) {
        // 写入切换电影分类日志
        log.info("MOVIE_GENRES_PREFIX" + ":" + genres +"|"+ System.currentTimeMillis()/1000);
        this.genres=genres;
        List<Movie> hotMoviesByGenres =movieDao.findRecommendMoviesByGenres(genres);
        model.addAttribute("hotMoviesByGenres",hotMoviesByGenres);
        return Result.success(true);
    }
}
