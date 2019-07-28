package com.jstarcraft.example;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriterConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.jstarcraft.example.movie.service.Item;
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

    @Bean
    Searcher getSearcher(List<Item> movies) throws Exception {
        SearchCodec<Item, Item> codec = new SearchCodec<>(Item.class, Item.class);

        IndexWriterConfig config = new IndexWriterConfig();
        Path path = Paths.get("./lucene");
        File file = path.toFile();
        FileUtils.deleteDirectory(file);
        Searcher searcher = new Searcher(config, path);

        for (Item movie : movies) {
            Document document = codec.encode(movie);
            searcher.createDocument(String.valueOf(movie.getId()), document);
        }

        return searcher;
    }

}
