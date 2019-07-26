package com.jstarcraft.example.movie.service;

import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.springframework.stereotype.Component;

import com.jstarcraft.ai.data.module.ArrayInstance;
import com.jstarcraft.core.utility.KeyValue;
import com.jstarcraft.example.account.service.Account;
import com.jstarcraft.rns.configure.Configuration;
import com.jstarcraft.rns.recommend.Recommender;
import com.jstarcraft.rns.recommend.benchmark.ranking.MostPopularRecommender;
import com.jstarcraft.rns.recommend.collaborative.ranking.ItemKNNRankingRecommender;
import com.jstarcraft.rns.recommend.collaborative.ranking.UserKNNRankingRecommender;
import com.jstarcraft.rns.search.Searcher;

import it.unimi.dsi.fastutil.floats.FloatList;
import it.unimi.dsi.fastutil.longs.Long2IntMap;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;

@Component
public class MovieService {

    /** 账户标识-索引 */
    private Long2IntMap accountId2Indexes;

    /** 电影索引-标识 */
    private List<Movie> movies;

    private Configuration configuration;

    /** 推荐器(定时重新构建) */
    private HashMap<String, Recommender> recommenders;

    /** 搜索器 */
    private Searcher searcher;

    @PostConstruct
    void postConstruct() {
        {
            Recommender recommender = new MostPopularRecommender();
        }
        {
            Recommender recommender = new ItemKNNRankingRecommender();
        }
        {
            Recommender recommender = new UserKNNRankingRecommender();
        }
//        recommender.prepare(configuration, model, space);
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
