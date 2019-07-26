package com.jstarcraft.example.movie.service;

import java.util.HashMap;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.flexible.standard.StandardQueryParser;
import org.apache.lucene.search.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jstarcraft.ai.data.DataModule;
import com.jstarcraft.ai.data.module.ArrayInstance;
import com.jstarcraft.core.utility.KeyValue;
import com.jstarcraft.rns.recommend.Recommender;
import com.jstarcraft.rns.search.Searcher;

import it.unimi.dsi.fastutil.floats.FloatList;
import it.unimi.dsi.fastutil.ints.Int2FloatRBTreeMap;
import it.unimi.dsi.fastutil.ints.Int2FloatSortedMap;
import it.unimi.dsi.fastutil.ints.Int2IntRBTreeMap;
import it.unimi.dsi.fastutil.ints.Int2IntSortedMap;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;

@Component
public class MovieService {

    @Autowired
    private DataModule dataModule;

    /** 推荐器 */
    @Autowired
    private HashMap<String, Recommender> recommenders;

    /** 搜索器 */
    @Autowired
    private Searcher searcher;

    /** 电影 */
    @Autowired
    private List<Movie> movies;

    private StandardQueryParser queryParser = new StandardQueryParser();

    public void clickMovie(int accountIndex, int movieIndex) {
        Int2IntSortedMap qualityFeatures = new Int2IntRBTreeMap();
        qualityFeatures.put(0, accountIndex);
        qualityFeatures.put(1, movieIndex);
        Int2FloatSortedMap quantityFeatures = new Int2FloatRBTreeMap();
        dataModule.associateInstance(qualityFeatures, quantityFeatures, 5F);
    }

    /**
     * 个性化推荐
     * 
     * @param account
     * @param key
     * @return
     */
    public Object2FloatMap<Movie> getRecommender(int accountIndex, String key) {
        // 标识-得分映射
        Object2FloatMap<Movie> movie2ScoreMap = new Object2FloatOpenHashMap<>();

        Recommender recommender = recommenders.get(key);
        ArrayInstance instance = new ArrayInstance(2, 0);
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
     * @throws Exception
     */
    public Object2FloatMap<Movie> getSearchMovies(int accountIndex, String key) throws Exception {
        // 标识-得分映射
        Object2FloatMap<Movie> movie2ScoreMap = new Object2FloatOpenHashMap<>();

        Query query = queryParser.parse(key, Movie.TITLE);
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
