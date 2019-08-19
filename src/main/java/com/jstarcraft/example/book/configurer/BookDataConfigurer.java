package com.jstarcraft.example.book.configurer;

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

import com.jstarcraft.ai.data.DataInstance;
import com.jstarcraft.ai.data.DataModule;
import com.jstarcraft.ai.data.DataSpace;
import com.jstarcraft.ai.data.attribute.QualityAttribute;
import com.jstarcraft.ai.data.converter.CsvConverter;
import com.jstarcraft.ai.data.converter.DataConverter;
import com.jstarcraft.core.utility.StringUtility;
import com.jstarcraft.example.book.service.BookItem;
import com.jstarcraft.example.book.service.BookUser;

/**
 * 数据配置器
 * 
 * @author Birdy
 *
 */
@Configuration
public class BookDataConfigurer {

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d-MMM-yyyy", Locale.US);

    /**
     * 装配数据空间
     * 
     * @return
     */
    @Bean("bookDataSpace")
    DataSpace getBookDataSpace() throws Exception {
        Map<String, Class<?>> qualityDifinitions = new HashMap<>();
        qualityDifinitions.put("user", int.class);
        qualityDifinitions.put("item", int.class);
        qualityDifinitions.put("instant", int.class);
        Map<String, Class<?>> quantityDifinitions = new HashMap<>();
        quantityDifinitions.put("score", float.class);
        DataSpace dataSpace = new DataSpace(qualityDifinitions, quantityDifinitions);
        return dataSpace;
    }

    @Bean("bookUsers")
    List<BookUser> getUsers(DataSpace bookDataSpace) throws Exception {
        File bookUserFile = new File("data/ml-100k/u.user");
        List<BookUser> users = new LinkedList<>();

        QualityAttribute<Integer> userAttribute = bookDataSpace.getQualityAttribute("user");
        {
            // TODO 匿名用户设置为0
            // 用户索引
            int index = userAttribute.convertData(0);
            BookUser user = new BookUser(index, "User" + index);
            users.add(user);
        }
        try (InputStream stream = new FileInputStream(bookUserFile); InputStreamReader reader = new InputStreamReader(stream); BufferedReader buffer = new BufferedReader(reader)) {
            try (CSVParser parser = new CSVParser(buffer, CSVFormat.newFormat('|'))) {
                Iterator<CSVRecord> iterator = parser.iterator();
                while (iterator.hasNext()) {
                    CSVRecord datas = iterator.next();
                    // 用户标识
                    int id = Integer.parseInt(datas.get(0));
                    // 用户索引
                    int index = userAttribute.convertData(id);
                    BookUser user = new BookUser(index, "User" + index);
                    users.add(user);
                }
            }
        }

        users = new ArrayList<>(users);
        return users;
    }

    @Bean("bookItems")
    List<BookItem> getItems(DataSpace bookDataSpace) throws Exception {
        File bookItemFile = new File("data/ml-100k/u.item");
        List<BookItem> items = new LinkedList<>();

        QualityAttribute<Integer> itemAttribute = bookDataSpace.getQualityAttribute("item");
        try (InputStream stream = new FileInputStream(bookItemFile); InputStreamReader reader = new InputStreamReader(stream); BufferedReader buffer = new BufferedReader(reader)) {
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
                    LocalDate date = StringUtility.isEmpty(datas.get(2)) ? LocalDate.of(1970, 1, 1) : LocalDate.parse(datas.get(2), formatter);
                    BookItem item = new BookItem(index, title, date);
                    items.add(item);
                }
            }
        }

        items = new ArrayList<>(items);
        return items;
    }

    /**
     * 装配数据模型
     * 
     * @param bookDataSpace
     * @return
     */
    @Bean("bookDataModule")
    DataModule getMovieDataModule(DataSpace bookDataSpace, List<BookUser> bookUsers, List<BookItem> bookItems) throws Exception {
        TreeMap<Integer, String> configuration = new TreeMap<>();
        configuration.put(1, "user");
        configuration.put(2, "item");
        configuration.put(3, "score");
        configuration.put(4, "instant");
        DataModule dataModule = bookDataSpace.makeDenseModule("score", configuration, 1000000);

        File file = new File("data/ml-100k/u.data");
        DataConverter<InputStream> convertor = new CsvConverter('\t', bookDataSpace.getQualityAttributes(), bookDataSpace.getQuantityAttributes());
        try (InputStream stream = new FileInputStream(file)) {
            convertor.convert(dataModule, stream);
        }

        int userDimension = dataModule.getQualityInner("user");
        int itemDimension = dataModule.getQualityInner("item");
        int scoreDimension = dataModule.getQuantityInner("score");
        for (DataInstance instance : dataModule) {
            int userIndex = instance.getQualityFeature(userDimension);
            int itemIndex = instance.getQualityFeature(itemDimension);
            instance.setQuantityMark(instance.getQuantityFeature(scoreDimension));
            bookUsers.get(userIndex).click(itemIndex);
        }

        return dataModule;
    }

}
