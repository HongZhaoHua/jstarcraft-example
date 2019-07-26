package com.jstarcraft.example;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.jstarcraft.ai.data.DataModule;
import com.jstarcraft.ai.data.DataSpace;
import com.jstarcraft.ai.data.attribute.QualityAttribute;
import com.jstarcraft.ai.data.converter.CsvConverter;
import com.jstarcraft.ai.data.converter.DataConverter;
import com.jstarcraft.example.movie.service.Movie;

/**
 * 数据配置器
 * 
 * @author Birdy
 *
 */
@Configuration
public class DataConfigurer {

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy", Locale.US);

    @Bean("userFile")
    File getUserFile() {
        File file = new File("data/movielens/ml-100k/user.txt");
        return file;
    }

    @Bean("itemFile")
    File getItemFile() {
        File file = new File("data/movielens/ml-100k/item.txt");
        return file;
    }

    /**
     * 装配数据空间
     * 
     * @return
     */
    @Bean
    DataSpace getDataSpace(File userFile) throws Exception {
        Map<String, Class<?>> qualityDifinitions = new HashMap<>();
        qualityDifinitions.put("user", int.class);
        qualityDifinitions.put("item", int.class);
        Map<String, Class<?>> quantityDifinitions = new HashMap<>();
        DataSpace dataSpace = new DataSpace(qualityDifinitions, quantityDifinitions);

        QualityAttribute<Integer> userAttribute = dataSpace.getQualityAttribute("user");
        // TODO 匿名用户设置为0
        userAttribute.convertData(0);
        try (InputStream stream = new FileInputStream(userFile); InputStreamReader reader = new InputStreamReader(stream); BufferedReader buffer = new BufferedReader(reader)) {
            try (CSVParser parser = new CSVParser(buffer, CSVFormat.newFormat('|'))) {
                Iterator<CSVRecord> iterator = parser.iterator();
                while (iterator.hasNext()) {
                    CSVRecord datas = iterator.next();
                    // 用户标识
                    int id = Integer.parseInt(datas.get(0));
                    userAttribute.convertData(id);
                }
            }
        }

        return dataSpace;
    }

    @Bean("movies")
    List<Movie> getMovies(DataSpace dataSpace, File itemFile) throws Exception {
        List<Movie> movies = new LinkedList<>();

        QualityAttribute<Integer> itemAttribute = dataSpace.getQualityAttribute("item");
        try (InputStream stream = new FileInputStream(itemFile); InputStreamReader reader = new InputStreamReader(stream); BufferedReader buffer = new BufferedReader(reader)) {
            try (CSVParser parser = new CSVParser(buffer, CSVFormat.newFormat('|'))) {
                Iterator<CSVRecord> iterator = parser.iterator();
                while (iterator.hasNext()) {
                    CSVRecord datas = iterator.next();
                    // 物品标识
                    int id = Integer.parseInt(datas.get(0));
                    // 物品索引
                    int index = itemAttribute.convertData(id);
                    // 物品标题
                    String title = datas.get(1);
                    // 物品日期
                    LocalDate date = LocalDate.parse(datas.get(2), formatter);
                    Movie movie = new Movie(index, title, date);
                    movies.add(movie);
                }
            }
        }

        movies = new ArrayList<>(movies);
        return movies;
    }

    /**
     * 装配数据模型
     * 
     * @param dataSpace
     * @return
     */
    @Bean
    DataModule getDataModule(DataSpace dataSpace, List<Movie> movies) throws Exception {
        TreeMap<Integer, String> configuration = new TreeMap<>();
        configuration.put(1, "user");
        configuration.put(2, "item");
        DataModule dataModule = dataSpace.makeDenseModule("score", configuration, 1000000);

        File file = new File("data/movielens/ml-100k/score.txt");
        DataConverter<InputStream> convertor = new CsvConverter(' ', dataSpace.getQualityAttributes(), dataSpace.getQuantityAttributes());
        try (InputStream stream = new FileInputStream(file)) {
            convertor.convert(dataModule, stream, null, 3, null);
        }
        return dataModule;
    }

}
