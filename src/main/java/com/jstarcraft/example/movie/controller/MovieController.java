package com.jstarcraft.example.movie.controller;

import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jstarcraft.example.common.output.NormalOutput;
import com.jstarcraft.example.movie.service.Item;
import com.jstarcraft.example.movie.service.MovieService;
import com.jstarcraft.example.movie.service.User;

import io.swagger.annotations.ApiOperation;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;

@RestController
@RequestMapping("/movies")
public class MovieController {

    private final static Logger logger = LoggerFactory.getLogger(MovieController.class);

    @Autowired
    private MovieService movieService;

    /**
     * 获取用户
     * 
     * @param userIndex
     * @param recommendKey
     * @return
     */
    @ApiOperation(value = "获取用户", notes = "获取用户")
    @GetMapping("/getUsers")
    public NormalOutput<List<UserOutput>> getUsers() {
        List<User> users = movieService.getUsers();
        List<UserOutput> instances = UserOutput.instancesOf(users);
        return new NormalOutput<>(instances);
    }

    /**
     * 获取推荐电影
     * 
     * @param userIndex
     * @param recommendKey
     * @return
     */
    @ApiOperation(value = "获取推荐电影", notes = "获取推荐电影")
    @GetMapping("/getRecommendItems")
    public NormalOutput<List<ItemOutput>> getRecommendItems(@RequestParam int userIndex, @RequestParam String recommendKey) {
        Object2FloatMap<Item> movies = movieService.getRecommendItems(userIndex, recommendKey);
        List<ItemOutput> instances = ItemOutput.instancesOf(movies);
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
    public void click(@RequestParam int userIndex, @RequestParam int itemIndex) {
        movieService.click(userIndex, itemIndex);
    }

    /**
     * 获取搜索电影
     * 
     * @param userIndex
     * @param searchKey
     * @return
     */
    @ApiOperation(value = "获取搜索电影", notes = "获取搜索电影")
    @GetMapping("/getSearchItems")
    public NormalOutput<List<ItemOutput>> getSearchItems(@RequestParam int userIndex, @RequestParam String searchKey) throws Exception {
        Object2FloatMap<Item> movies = movieService.getSearchItems(userIndex, searchKey);
        List<ItemOutput> instances = ItemOutput.instancesOf(movies);
        Collections.sort(instances, (left, right) -> {
            return Float.compare(right.getScore(), left.getScore());
        });
        return new NormalOutput<>(instances);
    }

}
