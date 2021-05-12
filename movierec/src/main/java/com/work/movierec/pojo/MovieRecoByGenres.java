package com.work.movierec.pojo;

import lombok.Data;

import javax.persistence.Id;
import java.util.List;

@Data
public class MovieRecoByGenres {
    @Id
    private String _id;

    private int uid;

    @Override
    public String toString() {
        return "MovieRecoByGenres{" +
                "_id='" + _id + '\'' +
                ", uid=" + uid +
                ", genres='" + genres + '\'' +
                ", recs=" + recs +
                '}';
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    private String genres;

    private List<MovieReco> recs;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getGenres() {
        return genres;
    }

    public void setGenres(String genres) {
        this.genres = genres;
    }

    public List<MovieReco> getRecs() {
        return recs;
    }

    public void setRecs(List<MovieReco> recs) {
        this.recs = recs;
    }
}
