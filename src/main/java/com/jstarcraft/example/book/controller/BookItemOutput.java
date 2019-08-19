package com.jstarcraft.example.book.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.jstarcraft.example.book.service.BookItem;

import it.unimi.dsi.fastutil.objects.Object2FloatMap;

/**
 * 物品输出
 * 
 * @author Birdy
 *
 */
public class BookItemOutput {

    /** 物品标识 */
    private int id;

    /** 物品标题 */
    private String title;

    /** 物品日期 */
    private LocalDate date;

    /** 得分 */
    private float score;

    public BookItemOutput(BookItem item, float score) {
        this.id = item.getIndex();
        this.title = item.getTitle();
        this.date = item.getDate();
        this.score = score;
    }

    public int getId() {
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

    public static List<BookItemOutput> instancesOf(Object2FloatMap<BookItem> items) {
        List<BookItemOutput> instances = new ArrayList<>(items.size());
        for (Object2FloatMap.Entry<BookItem> term : items.object2FloatEntrySet()) {
            BookItemOutput instance = new BookItemOutput(term.getKey(), term.getFloatValue());
            instances.add(instance);
        }
        return instances;
    }

}
