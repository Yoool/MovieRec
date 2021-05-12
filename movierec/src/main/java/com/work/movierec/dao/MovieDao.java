package com.work.movierec.dao;

//import jdk.dynalink.linker.LinkerServices;
import com.work.movierec.pojo.Movie;

import java.util.List;

public interface MovieDao {

    void save(Movie movie);

    void update(Movie movie);

    void delete(String mid);

    List<Movie> findAllMovies();

    List<Movie> findAverageScoreMovies();

    List<Movie> findGenresTopMovies();

    List<Movie> findHotMovies();

    List<Movie> findRecentlyHotMovies();

    List<Movie> findOnlineRecommendMovies(int mid);

    List<Movie> findRecommendMoviesByGenres(String genres);
}
