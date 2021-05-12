package com.work.movierec.pojo;

import lombok.Data;

import javax.persistence.Id;

@Data
public class Movie {
    @Id
    private String _id;

    private int mid;

    private int count;

    public int getCount() {
        return count;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "_id='" + _id + '\'' +
                ", mid=" + mid +
                ", count=" + count +
                ", name='" + name + '\'' +
                ", describe='" + describe + '\'' +
                ", duration='" + duration + '\'' +
                ", issueD='" + issueD + '\'' +
                ", shootY='" + shootY + '\'' +
                ", _score=" + _score +
                ", language='" + language + '\'' +
                ", genres='" + genres + '\'' +
                ", actors='" + actors + '\'' +
                ", director='" + director + '\'' +
                ", tags='" + tags + '\'' +
                '}';
    }

    public void setCount(int count) {
        this.count = count;
    }

    private String name;

    private String describe;

    private String duration;

    private String issueD;

    private String shootY;

    private Double _score;

    private String language;

    private String genres;

    private String actors;

    private String director;

    private String tags;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public int getMid() {
        return mid;
    }

    public void setMid(int mid) {
        this.mid = mid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getIssueD() {
        return issueD;
    }

    public void setIssueD(String issueD) {
        this.issueD = issueD;
    }

    public String getShootY() {
        return shootY;
    }

    public void setShootY(String shootY) {
        this.shootY = shootY;
    }

    public Double get_score() {
        return _score;
    }

    public void set_score(Double _score) {
        this._score = _score;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getGenres() {
        return genres;
    }

    public void setGenres(String genres) {
        this.genres = genres;
    }

    public String getActors() {
        return actors;
    }

    public void setActors(String actors) {
        this.actors = actors;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }
}
