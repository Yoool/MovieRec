package com.work.movierec.pojo;

import lombok.Data;

import javax.persistence.Id;

@Data
public class MovieReco {
    @Id
    private String _id;

    private int mid;

    private double avg;

    @Override
    public String toString() {
        return "MovieReco{" +
                "_id='" + _id + '\'' +
                ", mid=" + mid +
                ", avg=" + avg +
                ", score=" + score +
                ", count=" + count +
                ", yearmonth=" + yearmonth +
                '}';
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    private double score;

    private int count;

    private int yearmonth;

    public int getYearmonth() {
        return yearmonth;
    }

    public void setYearmonth(int yearmonth) {
        this.yearmonth = yearmonth;
    }

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

    public double getAvg() {
        return avg;
    }

    public void setAvg(double avg) {
        this.avg = avg;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
