package com.jstarcraft.example.movie.controller;

import java.util.ArrayList;
import java.util.List;

import com.jstarcraft.example.movie.service.MovieUser;

/**
 * 用户输出
 * 
 * @author Birdy
 *
 */
public class MovieUserOutput {

    /** 用户标识 */
    private int id;

    /** 用户名称 */
    private String name;

    public MovieUserOutput(MovieUser user) {
        this.id = user.getIndex();
        this.name = user.getName();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public static List<MovieUserOutput> instancesOf(List<MovieUser> users) {
        List<MovieUserOutput> instances = new ArrayList<>(users.size());
        for (MovieUser user : users) {
            MovieUserOutput instance = new MovieUserOutput(user);
            instances.add(instance);
        }
        return instances;
    }

}
