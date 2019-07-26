package com.jstarcraft.example.movie.service;

import java.time.LocalDate;
import java.util.BitSet;

import javax.persistence.Id;

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
    @Id
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

    /** 电影题材 */
    private BitSet genres;

    protected Movie() {
    }

    public Movie(int index, String title, LocalDate date, BitSet genres) {
        this.index = index;
        this.title = title;
        this.date = date;
        this.genres = genres;
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

    public BitSet getGenres() {
        return genres;
    }

}
