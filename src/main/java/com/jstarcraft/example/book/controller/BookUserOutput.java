package com.jstarcraft.example.book.controller;

import java.util.ArrayList;
import java.util.List;

import com.jstarcraft.example.book.service.BookUser;

/**
 * 用户输出
 * 
 * @author Birdy
 *
 */
public class BookUserOutput {

    /** 用户标识 */
    private int id;

    /** 用户名称 */
    private String name;

    public BookUserOutput(BookUser user) {
        this.id = user.getIndex();
        this.name = user.getName();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public static List<BookUserOutput> instancesOf(List<BookUser> users) {
        List<BookUserOutput> instances = new ArrayList<>(users.size());
        for (BookUser user : users) {
            BookUserOutput instance = new BookUserOutput(user);
            instances.add(instance);
        }
        return instances;
    }

}
