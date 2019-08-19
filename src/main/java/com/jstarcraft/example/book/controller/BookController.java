package com.jstarcraft.example.book.controller;

import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jstarcraft.example.book.service.BookItem;
import com.jstarcraft.example.book.service.BookService;
import com.jstarcraft.example.book.service.BookUser;
import com.jstarcraft.example.common.output.NormalOutput;

import io.swagger.annotations.ApiOperation;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;

@RestController
@RequestMapping("/books")
public class BookController {

    private final static Logger logger = LoggerFactory.getLogger(BookController.class);

    @Autowired
    private BookService bookService;

    /**
     * 获取用户
     * 
     * @param userIndex
     * @param recommendKey
     * @return
     */
    @ApiOperation(value = "获取用户", notes = "获取用户")
    @GetMapping("/getUsers")
    public NormalOutput<List<BookUserOutput>> getUsers() {
        List<BookUser> users = bookService.getUsers();
        List<BookUserOutput> instances = BookUserOutput.instancesOf(users);
        return new NormalOutput<>(instances);
    }

    /**
     * 获取推荐条目
     * 
     * @param userIndex
     * @param recommendKey
     * @return
     */
    @ApiOperation(value = "获取推荐条目", notes = "获取推荐条目")
    @GetMapping("/getRecommendItems")
    public NormalOutput<List<BookItemOutput>> getRecommendItems(@RequestParam int userIndex, @RequestParam String recommendKey) {
        Object2FloatMap<BookItem> movies = bookService.getRecommendItems(userIndex, recommendKey);
        List<BookItemOutput> instances = BookItemOutput.instancesOf(movies);
        Collections.sort(instances, (left, right) -> {
            return Float.compare(right.getScore(), left.getScore());
        });
        return new NormalOutput<>(instances);
    }

    /**
     * 点击
     * 
     * @param userIndex
     * @param itemIndex
     */
    @ApiOperation(value = "点击", notes = "点击")
    @GetMapping("/click")
    public void click(@RequestParam int userIndex, @RequestParam int itemIndex, @RequestParam float score) {
        bookService.click(userIndex, itemIndex, score);
    }

    /**
     * 获取搜索电影
     * 
     * @param userIndex
     * @param searchKey
     * @return
     */
    @ApiOperation(value = "获取搜索条目", notes = "获取搜索条目")
    @GetMapping("/getSearchItems")
    public NormalOutput<List<BookItemOutput>> getSearchItems(@RequestParam int userIndex, @RequestParam String searchKey) throws Exception {
        Object2FloatMap<BookItem> movies = bookService.getSearchItems(userIndex, searchKey);
        List<BookItemOutput> instances = BookItemOutput.instancesOf(movies);
        Collections.sort(instances, (left, right) -> {
            return Float.compare(right.getScore(), left.getScore());
        });
        return new NormalOutput<>(instances);
    }

}
