package com.jstarcraft.example.movie.controller;

import java.util.ArrayList;
import java.util.List;

import com.jstarcraft.example.movie.service.User;

public class UserOutput {

    private String name;

    private int value;

    public UserOutput(User user) {
        this.name = user.getName();
        this.value = user.getIndex();
    }

    public String getName() {
        return name;
    }

    public int getValue() {
        return value;
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
