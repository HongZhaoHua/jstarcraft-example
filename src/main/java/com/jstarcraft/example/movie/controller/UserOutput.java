package com.jstarcraft.example.movie.controller;

import java.util.ArrayList;
import java.util.List;

import com.jstarcraft.example.movie.service.User;

/**
 * 用户输出
 * 
 * @author Birdy
 *
 */
public class UserOutput {

    /** 用户标识 */
    private int id;

    /** 用户名称 */
    private String name;

    public UserOutput(User user) {
        this.id = user.getIndex();
        this.name = user.getName();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public static List<UserOutput> instancesOf(List<User> users) {
        List<UserOutput> instances = new ArrayList<>(users.size());
        for (User user : users) {
            UserOutput instance = new UserOutput(user);
            instances.add(instance);
        }
        return instances;
    }

}
