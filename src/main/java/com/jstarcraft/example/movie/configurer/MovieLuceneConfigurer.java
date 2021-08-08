package com.jstarcraft.example.movie.configurer;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriterConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.jstarcraft.core.codec.specification.CodecDefinition;
import com.jstarcraft.core.storage.lucene.LuceneEngine;
import com.jstarcraft.core.storage.lucene.LuceneMetadata;
import com.jstarcraft.core.storage.lucene.converter.LuceneContext;
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
    LuceneEngine getMovieEngine(List<MovieItem> movieItems) throws Exception {
        LuceneContext context = new LuceneContext(CodecDefinition.instanceOf(MovieItem.class));
        LuceneMetadata codec = new LuceneMetadata(MovieItem.class, context);

        Path path = Paths.get("./lucene/movie");
        File file = path.toFile();
        FileUtils.deleteDirectory(file);
        LuceneEngine searcher = new LuceneEngine(() -> {
            IndexWriterConfig config = new IndexWriterConfig();
            return config;
        }, path);

        for (MovieItem movie : movieItems) {
            Document document = codec.encodeDocument(movie);
            searcher.createDocument(String.valueOf(movie.getId()), document);
        }

        return searcher;
    }

}
