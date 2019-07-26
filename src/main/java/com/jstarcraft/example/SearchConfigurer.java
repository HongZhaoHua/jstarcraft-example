package com.jstarcraft.example;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.Locale;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FileUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriterConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.jstarcraft.ai.data.DataSpace;
import com.jstarcraft.ai.data.attribute.QualityAttribute;
import com.jstarcraft.example.common.event.EventBus;
import com.jstarcraft.example.movie.service.Movie;
import com.jstarcraft.rns.search.Searcher;
import com.jstarcraft.rns.search.converter.SearchCodec;

/**
 * 搜索配置器
 * 
 * @author Birdy
 *
 */
@Configuration
public class SearchConfigurer {

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy", Locale.US);

    @Bean
    Searcher getSearcher(DataSpace dataSpace, File itemFile) throws Exception {
        SearchCodec<Movie, Movie> codec = new SearchCodec<>(Movie.class, Movie.class);

        IndexWriterConfig config = new IndexWriterConfig();
        Path path = Paths.get("./lucene");
        File file = path.toFile();
        FileUtils.deleteDirectory(file);
        Searcher searcher = new Searcher(config, path);

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
                    Document document = codec.encode(movie);
                    searcher.createDocument(String.valueOf(id), document);
                }
            }
        }

        return searcher;
    }

}
