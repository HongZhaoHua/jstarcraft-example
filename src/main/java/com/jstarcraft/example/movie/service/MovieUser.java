package com.jstarcraft.example.movie.service;

import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;

/**
 * 用户
 * 
 * @author Birdy
 *
 */
public class MovieUser {

    /** 用户标识 */
    private int index;

    /** 用户名称 */
    private String name;

    /** 用户点击次数 */
    private Int2IntMap clickeds;

    public MovieUser(int index, String name) {
        this.index = index;
        this.name = name;
        this.clickeds = new Int2IntOpenHashMap();
    }

    public int getIndex() {
        return index;
    }

    public String getName() {
        return name;
    }

    public void click(int itemIndex) {
        int count = clickeds.get(itemIndex);
        clickeds.put(itemIndex, count + 1);
    }

    public boolean isClicked(int itemIndex) {
        return clickeds.containsKey(itemIndex);
    }

}
