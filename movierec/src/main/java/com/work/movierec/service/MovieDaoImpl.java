package com.work.movierec.service;

import com.work.movierec.dao.MovieDao;
import com.work.movierec.pojo.MovieReco;
import com.work.movierec.pojo.MovieRecoByGenres;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import com.work.movierec.pojo.Movie;

import java.util.*;


//@Service
@Component
public class MovieDaoImpl implements MovieDao {

    private List<Movie> allMovies;
    private List<MovieReco> averageScoreMovies;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void save(Movie movie) {
        mongoTemplate.save(movie);
    }

    @Override
    public void update(Movie movie) {
        Query query=new Query(Criteria.where("_id").is(movie.get_id()));
        Update update=new Update();
        // 更新值
//        update.set("score","5");
        mongoTemplate.updateFirst(query,update,Movie.class);
    }

    @Override
    public void delete(String mid) {
        Movie movie = mongoTemplate.findById(mid, Movie.class);
        mongoTemplate.remove(movie);
    }

    @Override
    public List<Movie> findAllMovies() {
//        FindIterable<Document> movies = mongoTemplate.getCollection("Movie").find();
//        for (Document movie : movies) {
//            System.out.printf(movie.getString("name"));
//        }
        allMovies=mongoTemplate.findAll(Movie.class,"Movie");
        return allMovies;
    }

    @Override
    public List<Movie> findAverageScoreMovies() {
        averageScoreMovies = mongoTemplate.findAll(MovieReco.class, "AverageScoreMovies");
        averageScoreMovies.sort((x,y)->{
            return (int)(y.getAvg()*100-x.getAvg()*100);
        });


        return listRecoToMovie(averageScoreMovies);
    }

    @Override
    public List<Movie> findGenresTopMovies() {

        return null;
    }

    @Override
    public List<Movie> findHotMovies() {
        List<MovieReco> hotMovieRecos = mongoTemplate.findAll(MovieReco.class, "HotMovies");
        hotMovieRecos.sort((x,y)->{
            return y.getCount()-x.getCount();
        });

        return listRecoToMovie(hotMovieRecos);
    }

    @Override
    public List<Movie> findRecentlyHotMovies() {
        List<MovieReco> recentlyHotMovieRecos = mongoTemplate.findAll(MovieReco.class, "RecentlyHotMovies");

        return listRecoToMovie(recentlyHotMovieRecos);
    }

    // 根据用户id获取实时推荐列表
    @Override
    public List<Movie> findOnlineRecommendMovies(int uid) {
        List<MovieRecoByGenres> movieRecosByGenres = mongoTemplate.findAll(MovieRecoByGenres.class, "StreamRecs");
        for (MovieRecoByGenres movieRecosByGenresi : movieRecosByGenres) {
            if (movieRecosByGenresi.getUid()==uid) {
                return listRecoToMovie(movieRecosByGenresi.getRecs());
            }
        }
        return null;
    }

    // 按类别热门
    @Override
    public List<Movie> findRecommendMoviesByGenres(String genres) {
        List<MovieRecoByGenres> movieRecosByGenres = mongoTemplate.findAll(MovieRecoByGenres.class, "GenresTopMovies");
        for (MovieRecoByGenres movieRecosByGenresi : movieRecosByGenres) {
            if (movieRecosByGenresi.getGenres().equals(genres)) {
                return listRecoToMovie(movieRecosByGenresi.getRecs());
            }
        }
        return null;
    }

    // 将推荐列表转换为电影列表
    private List<Movie> listRecoToMovie(List<MovieReco> recoList) {
        List<Movie> ret=new ArrayList<>();
        for (MovieReco movieReco : recoList.subList(0, 6)) {
            for (Movie movie : allMovies) {
                if(movie.getMid()==movieReco.getMid()) {
                    movie.setCount(movieReco.getCount());
                    movie.set_score(movieReco.getAvg() > movieReco.getScore() ? movieReco.getAvg() : movieReco.getScore());
                    ret.add(movie);
                    break;
                }
            }
        }
        return ret;
    }
}
