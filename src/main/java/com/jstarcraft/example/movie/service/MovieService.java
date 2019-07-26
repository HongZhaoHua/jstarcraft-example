package com.jstarcraft.example.movie.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FileUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.springframework.stereotype.Component;

import com.jstarcraft.ai.data.DataModule;
import com.jstarcraft.ai.data.DataSpace;
import com.jstarcraft.ai.data.attribute.QualityAttribute;
import com.jstarcraft.ai.data.converter.CsvConverter;
import com.jstarcraft.ai.data.converter.DataConverter;
import com.jstarcraft.ai.data.module.ArrayInstance;
import com.jstarcraft.core.utility.KeyValue;
import com.jstarcraft.example.account.service.Account;
import com.jstarcraft.rns.configure.Configuration;
import com.jstarcraft.rns.recommend.Recommender;
import com.jstarcraft.rns.recommend.benchmark.ranking.MostPopularRecommender;
import com.jstarcraft.rns.recommend.collaborative.ranking.ItemKNNRankingRecommender;
import com.jstarcraft.rns.recommend.collaborative.ranking.UserKNNRankingRecommender;
import com.jstarcraft.rns.search.Searcher;
import com.jstarcraft.rns.search.converter.SearchCodec;

import it.unimi.dsi.fastutil.floats.FloatList;
import it.unimi.dsi.fastutil.ints.Int2FloatRBTreeMap;
import it.unimi.dsi.fastutil.ints.Int2FloatSortedMap;
import it.unimi.dsi.fastutil.ints.Int2IntRBTreeMap;
import it.unimi.dsi.fastutil.ints.Int2IntSortedMap;
import it.unimi.dsi.fastutil.longs.Long2IntMap;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;

@Component
public class MovieService {

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy", Locale.US);

    private DataSpace dataSpace;
    
    private DataModule dataModule;

    /** 账户标识-索引 */
    private Long2IntMap accountId2Indexes;

    /** 电影索引-标识 */
    private List<Movie> movies;

    /** 推荐器(定时重新构建) */
    private HashMap<String, Recommender> recommenders;

    /** 搜索器 */
    private Searcher searcher;

    {
        Map<String, Class<?>> qualityDifinitions = new HashMap<>();
        qualityDifinitions.put("user", int.class);
        qualityDifinitions.put("item", int.class);
        Map<String, Class<?>> quantityDifinitions = new HashMap<>();
        dataSpace = new DataSpace(qualityDifinitions, quantityDifinitions);
    }

    private void handleUsers() throws Exception {
        QualityAttribute<Integer> userAttribute = dataSpace.getQualityAttribute("user");
        File file = new File("data/movielens/ml-100k/user.txt");
        InputStream stream = new FileInputStream(file);
        try (InputStreamReader reader = new InputStreamReader(stream); BufferedReader buffer = new BufferedReader(reader)) {
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
    }

    private void handleItems() throws Exception {
        IndexWriterConfig config = new IndexWriterConfig();
        Path path = Paths.get("./lucene");
        File file = path.toFile();
        FileUtils.deleteDirectory(file);
        searcher = new Searcher(config, path);

        SearchCodec<Movie, Movie> codec = new SearchCodec<>(Movie.class, Movie.class);
        QualityAttribute<Integer> itemAttribute = dataSpace.getQualityAttribute("item");
        InputStream stream = new FileInputStream(new File("data/movielens/ml-100k/item.txt"));
        try (InputStreamReader reader = new InputStreamReader(stream); BufferedReader buffer = new BufferedReader(reader)) {
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
    }

    private void handleScores() throws Exception {
        File file = new File("data/movielens/ml-100k/score.txt");
        TreeMap<Integer, String> configuration = new TreeMap<>();
        configuration.put(1, "user");
        configuration.put(2, "item");
        dataModule = dataSpace.makeDenseModule("score", configuration, 1000000);
        DataConverter<InputStream> convertor = new CsvConverter(' ', dataSpace.getQualityAttributes(), dataSpace.getQuantityAttributes());
        try (InputStream stream = new FileInputStream(file)) {
            convertor.convert(dataModule, stream, 3, null, null);
        }
    }

    @PostConstruct
    void postConstruct() throws Exception {
        handleUsers();
        handleItems();
        handleScores();
        
        recommenders = new HashMap<>();
        Configuration configuration = Configuration.valueOf();
        {
            Recommender recommender = new MostPopularRecommender();
            recommender.prepare(configuration, dataModule, dataSpace);
            recommenders.put("MostPopular", recommender);
        }
        {
            Recommender recommender = new ItemKNNRankingRecommender();
            recommender.prepare(configuration, dataModule, dataSpace);
            recommenders.put("ItemKNN", recommender);
        }
        {
            Recommender recommender = new UserKNNRankingRecommender();
            recommender.prepare(configuration, dataModule, dataSpace);
            recommenders.put("UserKNN", recommender);
        }
//        
    }

    public void clickMovie(Account account, int movieIndex) {
        Int2IntSortedMap qualityFeatures = new Int2IntRBTreeMap();
        qualityFeatures.put(0, 0);
        qualityFeatures.put(1, movieIndex);
        Int2FloatSortedMap quantityFeatures = new Int2FloatRBTreeMap();
        DataModule module = dataSpace.getModule("score");
        module.associateInstance(qualityFeatures, quantityFeatures, 5F);
    }

    /**
     * 个性化推荐
     * 
     * @param account
     * @param key
     * @return
     */
    public Object2FloatMap<Movie> getRecommender(Account account, String key) {
        // 标识-得分映射
        Object2FloatMap<Movie> movie2ScoreMap = new Object2FloatOpenHashMap<>();

        long accountId = account.getId();
        Recommender recommender = recommenders.get(key);
        ArrayInstance instance = new ArrayInstance(2, 0);
        int accountIndex = accountId2Indexes.get(accountId);
        int movieSize = movies.size();
        for (int movieIndex = 0; movieIndex < movieSize; movieIndex++) {
            // 过滤电影
            instance.setQualityFeature(0, accountIndex);
            instance.setQualityFeature(1, movieIndex);
            recommender.predict(instance);
            Movie movie = movies.get(movieIndex);
            float score = instance.getQuantityMark();
            movie2ScoreMap.put(movie, score);
        }

        return movie2ScoreMap;
    }

    /**
     * 个性化搜索
     * 
     * @param account
     * @param key
     * @return
     */
    public Object2FloatMap<Movie> getSearchMovies(Account account, String key) {
        // 标识-得分映射
        Object2FloatMap<Movie> movie2ScoreMap = new Object2FloatOpenHashMap<>();

        Query query = new TermQuery(new Term(Movie.TITLE, key));
        KeyValue<List<Document>, FloatList> search = searcher.retrieveDocuments(query, null, 1000);
        List<Document> documents = search.getKey();
        FloatList scores = search.getValue();
        for (int index = 0, size = documents.size(); index < size; index++) {
            Document document = documents.get(index);
            Movie movie = movies.get(document.getField(Movie.INDEX).numericValue().intValue());
            float score = scores.getFloat(index);
            movie2ScoreMap.put(movie, score);
        }

        return movie2ScoreMap;
    }

}
