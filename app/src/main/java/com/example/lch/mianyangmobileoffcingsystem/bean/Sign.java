package com.example.lch.mianyangmobileoffcingsystem.bean;

/**
 * Created by lch on 2017/3/19.
 */

public class Sign {
    private int score;
    private int year;
    private int month;
    private int day;
    private String workContent;

    public Sign(int score, int year, int month, int day, String workContent) {
        this.score = score;
        this.year = year;
        this.month = month;
        this.day = day;
        this.workContent = workContent;
    }

    public Sign() {
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getWorkContent() {
        return workContent;
    }

    public void setWorkContent(String workContent) {
        this.workContent = workContent;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    @Override
    public String toString() {
        return "Sign{" +
                "score=" + score +
                ", year=" + year +
                ", month=" + month +
                ", day=" + day +
                ", workContent='" + workContent + '\'' +
                '}';
    }

}
