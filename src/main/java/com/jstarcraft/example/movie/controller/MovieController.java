package com.jstarcraft.example.movie.controller;

import java.util.ArrayList;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jstarcraft.example.common.output.NormalOutput;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/movies")
public class MovieController {

    private final static Logger logger = LoggerFactory.getLogger(MovieController.class);

    private ArrayList<MovieOutput> popularMovies;

    @PostConstruct
    void postConstruct() {

    }

    /**
     * 更新推荐电影
     * 
     * @return
     */
    @ApiOperation(value = "更新推荐电影", notes = "更新推荐电影")
    @PostMapping("/updateRecommendMovies")
    public NormalOutput<ArrayList<MovieOutput>> updateRecommendMovies() {
        return new NormalOutput<>(popularMovies);
    }

    /**
     * 获取推荐电影
     * 
     * @return
     */
    @ApiOperation(value = "获取推荐电影", notes = "获取推荐电影")
    @GetMapping("/getRecommendMovies")
    public NormalOutput<ArrayList<MovieOutput>> getRecommendMovies(@RequestParam String key) {
        return new NormalOutput<>(popularMovies);
    }

    /**
     * 获取搜索电影
     * 
     * @return
     */
    @ApiOperation(value = "获取搜索电影", notes = "获取搜索电影")
    @GetMapping("/getSearchMovies")
    public NormalOutput<ArrayList<MovieOutput>> getSearchMovies(@RequestParam String key) {
        return new NormalOutput<>(popularMovies);
    }

    /**
     * 测试参数
     * 
     * @return
     */
    @ApiOperation(value = "测试参数", notes = "测试参数")
    @GetMapping("/test/{ids}")
    public NormalOutput<String[]> test(@PathVariable String[] ids) {
        return new NormalOutput<>(ids);
    }

}
