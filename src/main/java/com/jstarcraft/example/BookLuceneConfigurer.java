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
import com.jstarcraft.example.book.service.BookItem;

/**
 * 搜索配置器
 * 
 * @author Birdy
 *
 */
@Configuration
public class BookLuceneConfigurer {

    @Bean("bookEngine")
    LuceneEngine getBookEngine(List<BookItem> bookItems) throws Exception {
        LuceneCodec<BookItem, BookItem> codec = new LuceneCodec<>(BookItem.class, BookItem.class);

        IndexWriterConfig config = new IndexWriterConfig();
        Path path = Paths.get("./lucene/book");
        File file = path.toFile();
        FileUtils.deleteDirectory(file);
        LuceneEngine searcher = new LuceneEngine(config, path);

        for (BookItem book : bookItems) {
            Document document = codec.encode(book);
            searcher.createDocument(String.valueOf(book.getId()), document);
        }

        return searcher;
    }

}
