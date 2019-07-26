package com.jstarcraft.example.movie.service;

import java.time.LocalDate;

import com.jstarcraft.core.common.identification.IdentityObject;
import com.jstarcraft.rns.search.annotation.SearchIndex;
import com.jstarcraft.rns.search.annotation.SearchSort;
import com.jstarcraft.rns.search.annotation.SearchStore;

/**
 * 电影
 * 
 * @author Birdy
 *
 */
public class Movie implements IdentityObject<Integer> {

    public static final String INDEX = "index";

    public static final String TITLE = "title";

    public static final String DATE = "date";

    /** 电影标识 */
    @SearchIndex
    @SearchSort
    @SearchStore
    private int index;

    /** 电影标题 */
    @SearchIndex(analyze = true)
    private String title;

    /** 电影日期 */
    @SearchSort
    private LocalDate date;

    protected Movie() {
    }

    public Movie(int index, String title, LocalDate date) {
        this.index = index;
        this.title = title;
        this.date = date;
    }

    @Override
    public Integer getId() {
        return index;
    }

    public int getIndex() {
        return index;
    }

    public String getTitle() {
        return title;
    }

    public LocalDate getDate() {
        return date;
    }

}
