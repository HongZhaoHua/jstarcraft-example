package com.jstarcraft.example.movie.service;

import java.util.HashMap;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.flexible.standard.StandardQueryParser;
import org.apache.lucene.search.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jstarcraft.ai.data.DataModule;
import com.jstarcraft.ai.data.DataSpace;
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
    private DataSpace dataSpace;

    @Autowired
    private DataModule dataModule;

    /** 推荐器 */
    @Autowired
    private HashMap<String, Recommender> recommenders;

    /** 搜索器 */
    @Autowired
    private Searcher searcher;

    /** 用户 */
    @Autowired
    private List<User> users;

    /** 电影 */
    @Autowired
    private List<Item> items;

    private StandardQueryParser queryParser = new StandardQueryParser();

    public void click(int accountIndex, int movieIndex) {
        Int2IntSortedMap qualityFeatures = new Int2IntRBTreeMap();
        qualityFeatures.put(0, accountIndex);
        qualityFeatures.put(1, movieIndex);
        Int2FloatSortedMap quantityFeatures = new Int2FloatRBTreeMap();
        dataModule.associateInstance(qualityFeatures, quantityFeatures, 5F);
    }

    public List<User> getUsers() {
        return users;
    }

    /**
     * 个性化推荐
     * 
     * @param account
     * @param key
     * @return
     */
    public Object2FloatMap<Item> getRecommendItems(int userIndex, String recommendKey) {
        // 标识-得分映射
        Object2FloatMap<Item> item2ScoreMap = new Object2FloatOpenHashMap<>();

        Recommender recommender = recommenders.get(recommendKey);
        ArrayInstance instance = new ArrayInstance(2, 0);
        int itemSize = items.size();
        for (int itemIndex = 0; itemIndex < itemSize; itemIndex++) {
            // 过滤电影
            instance.setQualityFeature(1, userIndex);
            instance.setQualityFeature(0, itemIndex);
            recommender.predict(instance);
            Item item = items.get(itemIndex);
            float score = instance.getQuantityMark();
            item2ScoreMap.put(item, score);
        }

        return item2ScoreMap;
    }

    /**
     * 个性化搜索
     * 
     * @param account
     * @param key
     * @return
     * @throws Exception
     */
    public Object2FloatMap<Item> getSearchItems(int userIndex, String searchKey) throws Exception {
        // 标识-得分映射
        Object2FloatMap<Item> item2ScoreMap = new Object2FloatOpenHashMap<>();

        Query query = queryParser.parse(searchKey, Item.TITLE);
        KeyValue<List<Document>, FloatList> search = searcher.retrieveDocuments(query, null, 1000);
        List<Document> documents = search.getKey();
        FloatList scores = search.getValue();
        for (int index = 0, size = documents.size(); index < size; index++) {
            Document document = documents.get(index);
            Item item = items.get(document.getField(Item.INDEX).numericValue().intValue());
            float score = scores.getFloat(index);
            item2ScoreMap.put(item, score);
        }

        return item2ScoreMap;
    }

}
