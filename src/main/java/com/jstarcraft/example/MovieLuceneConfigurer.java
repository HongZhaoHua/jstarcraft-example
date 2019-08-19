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

import com.jstarcraft.core.orm.lucene.LuceneCodec;
import com.jstarcraft.core.orm.lucene.LuceneEngine;
import com.jstarcraft.example.movie.service.MovieItem;

/**
 * 搜索配置器
 * 
 * @author Birdy
 *
 */
@Configuration
public class MovieLuceneConfigurer {

    @Bean("movieEngine")
    LuceneEngine getMovieEngine(List<MovieItem> movies) throws Exception {
        LuceneCodec<MovieItem, MovieItem> codec = new LuceneCodec<>(MovieItem.class, MovieItem.class);

        IndexWriterConfig config = new IndexWriterConfig();
        Path path = Paths.get("./lucene/movie");
        File file = path.toFile();
        FileUtils.deleteDirectory(file);
        LuceneEngine searcher = new LuceneEngine(config, path);

        for (MovieItem movie : movies) {
            Document document = codec.encode(movie);
            searcher.createDocument(String.valueOf(movie.getId()), document);
        }

        return searcher;
    }

}
