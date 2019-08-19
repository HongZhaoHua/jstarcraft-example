package com.jstarcraft.example.book.service;

import java.time.LocalDate;

import com.jstarcraft.core.common.identification.IdentityObject;
import com.jstarcraft.core.orm.lucene.annotation.LuceneIndex;
import com.jstarcraft.core.orm.lucene.annotation.LuceneSort;
import com.jstarcraft.core.orm.lucene.annotation.LuceneStore;

/**
 * 电影
 * 
 * @author Birdy
 *
 */
public class BookItem implements IdentityObject<Integer> {

    public static final String INDEX = "index";

    public static final String TITLE = "title";

    public static final String DATE = "date";

    /** 电影标识 */
    @LuceneIndex
    @LuceneSort
    @LuceneStore
    private int index;

    /** 电影标题 */
    @LuceneIndex(analyze = true)
    private String title;

    /** 电影日期 */
    @LuceneSort
    private LocalDate date;

    protected BookItem() {
    }

    public BookItem(int index, String title, LocalDate date) {
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
