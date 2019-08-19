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
import com.jstarcraft.example.movie.service.MovieItem;
import com.jstarcraft.example.movie.service.MovieService;
import com.jstarcraft.example.movie.service.MovieUser;

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
    public NormalOutput<List<MovieUserOutput>> getUsers() {
        List<MovieUser> users = movieService.getUsers();
        List<MovieUserOutput> instances = MovieUserOutput.instancesOf(users);
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
        movieService.click(userIndex, itemIndex, score);
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
    public NormalOutput<List<MovieItemOutput>> getRecommendItems(@RequestParam int userIndex, @RequestParam String recommendKey) {
        Object2FloatMap<MovieItem> movies = movieService.getRecommendItems(userIndex, recommendKey);
        List<MovieItemOutput> instances = MovieItemOutput.instancesOf(movies);
        Collections.sort(instances, (left, right) -> {
            return Float.compare(right.getScore(), left.getScore());
        });
        return new NormalOutput<>(instances);
    }

    /**
     * 获取搜索条目
     * 
     * @param userIndex
     * @param searchKey
     * @return
     */
    @ApiOperation(value = "获取搜索条目", notes = "获取搜索条目")
    @GetMapping("/getSearchItems")
    public NormalOutput<List<MovieItemOutput>> getSearchItems(@RequestParam int userIndex, @RequestParam String searchKey) throws Exception {
        Object2FloatMap<MovieItem> movies = movieService.getSearchItems(userIndex, searchKey);
        List<MovieItemOutput> instances = MovieItemOutput.instancesOf(movies);
        Collections.sort(instances, (left, right) -> {
            return Float.compare(right.getScore(), left.getScore());
        });
        return new NormalOutput<>(instances);
    }

    /**
     * 获取条目
     * 
     * @param userIndex
     * @param recommendKey
     * @param searchKey
     * @param filterClicked
     * @return
     * @throws Exception
     */
    @ApiOperation(value = "获取条目", notes = "获取条目")
    @GetMapping("/getItems")
    public NormalOutput<List<MovieItemOutput>> getItems(@RequestParam int userIndex, @RequestParam String modelKey, @RequestParam String queryKey, @RequestParam boolean filterClicked) throws Exception {
        Object2FloatMap<MovieItem> movies = movieService.getItems(userIndex, modelKey, queryKey, filterClicked);
        List<MovieItemOutput> instances = MovieItemOutput.instancesOf(movies);
        Collections.sort(instances, (left, right) -> {
            return Float.compare(right.getScore(), left.getScore());
        });
        return new NormalOutput<>(instances);
    }

}
