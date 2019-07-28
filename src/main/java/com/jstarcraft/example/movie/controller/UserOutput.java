package com.jstarcraft.example.movie.controller;

import java.util.ArrayList;
import java.util.List;

public class UserOutput {

    private String name;

    private int value;

    public UserOutput(String name, int value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public int getValue() {
        return value;
    }

    public static List<UserOutput> instanceOf(int size) {
        List<UserOutput> instances = new ArrayList<>(size);
        for (int index = 0; index < size; index++) {
            UserOutput instance = new UserOutput("User" + index, index);
            instances.add(instance);
        }
        return instances;
    }

}
