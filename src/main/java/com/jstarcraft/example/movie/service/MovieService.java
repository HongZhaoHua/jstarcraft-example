package com.jstarcraft.example.movie.service;

import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.SortField.Type;
import org.apache.lucene.search.TermQuery;
import org.springframework.stereotype.Component;

import com.jstarcraft.ai.data.module.ArrayInstance;
import com.jstarcraft.rns.configure.Configuration;
import com.jstarcraft.rns.recommend.Recommender;
import com.jstarcraft.rns.recommend.benchmark.ranking.MostPopularRecommender;
import com.jstarcraft.rns.recommend.collaborative.ranking.ItemKNNRankingRecommender;
import com.jstarcraft.rns.recommend.collaborative.ranking.UserKNNRankingRecommender;
import com.jstarcraft.rns.search.Searcher;

import it.unimi.dsi.fastutil.ints.Int2FloatMap;
import it.unimi.dsi.fastutil.ints.Int2FloatOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2LongMap;
import it.unimi.dsi.fastutil.longs.Long2IntMap;

@Component
public class MovieService {

    /** 账户索引-标识 */
    private Long2IntMap accountId2Indexes;

    /** 电影索引-标识 */
    private Int2LongMap movieIndex2Ids;

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

    public List<Movie> getRecommender(String key, long accoutId) {
        Recommender recommender = recommenders.get(key);
        ArrayInstance instance = new ArrayInstance(2, 0);
        int accountIndex = accountId2Indexes.get(accoutId);
        int size = movieIndex2Ids.size();
        Int2FloatMap scores = new Int2FloatOpenHashMap();
        for (int movieIndex = 0; movieIndex < size; movieIndex++) {
            // 过滤电影
            instance.setQualityFeature(0, accountIndex);
            instance.setQualityFeature(1, movieIndex);
            recommender.predict(instance);
            scores.put(movieIndex, instance.getQuantityMark());
        }
        return null;
    }

    public List<Movie> getSearchMovies(String key) {
        Query query = new TermQuery(new Term("title", key));
        Sort sort = new Sort(new SortField("date", Type.LONG));
        List<Document> documents = searcher.retrieveDocuments(query, sort, 1000);
        return null;
    }

}
