package com.jstarcraft.example.movie.controller;

import java.time.LocalDate;

import com.jstarcraft.example.movie.service.Movie;

/**
 * 电影输出
 * 
 * @author Birdy
 *
 */
public class MovieOutput {

    /** 电影标识 */
    private long id;

    /** 电影标题 */
    private String title;

    /** 电影日期 */
    private LocalDate date;

    /** 得分 */
    private float score;

    public MovieOutput(Movie movie) {
        synchronized (movie) {
            this.id = movie.getId();
            this.title = movie.getTitle();
            this.date = movie.getDate();
        }
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public LocalDate getDate() {
        return date;
    }

    public float getScore() {
        return score;
    }

}
