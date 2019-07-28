package com.jstarcraft.example.movie.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.jstarcraft.example.movie.service.Item;

import it.unimi.dsi.fastutil.objects.Object2FloatMap;

/**
 * 电影输出
 * 
 * @author Birdy
 *
 */
public class ItemOutput {

    /** 电影标识 */
    private long id;

    /** 电影标题 */
    private String title;

    /** 电影日期 */
    private LocalDate date;

    /** 得分 */
    private float score;

    public ItemOutput(Item movie, float score) {
        synchronized (movie) {
            this.id = movie.getId();
            this.title = movie.getTitle();
            this.date = movie.getDate();
            this.score = score;
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

    public static List<ItemOutput> instancesOf(Object2FloatMap<Item> items) {
        List<ItemOutput> instances = new ArrayList<>(items.size());
        for (Object2FloatMap.Entry<Item> term : items.object2FloatEntrySet()) {
            ItemOutput instance = new ItemOutput(term.getKey(), term.getFloatValue());
            instances.add(instance);
        }
        return instances;
    }

}
