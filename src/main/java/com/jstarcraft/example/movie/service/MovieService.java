package com.jstarcraft.example.movie.service;

import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.flexible.standard.StandardQueryParser;
import org.apache.lucene.search.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jstarcraft.ai.data.DataModule;
import com.jstarcraft.ai.data.module.ArrayInstance;
import com.jstarcraft.core.orm.lucene.LuceneEngine;
import com.jstarcraft.core.utility.KeyValue;
import com.jstarcraft.rns.model.Model;

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

    /** 排序预测与评分预测模型 */
    @Autowired
    private HashMap<String, Model> models;

    /** Lucene引擎 */
    @Autowired
    private LuceneEngine engine;

    /** 用户 */
    @Autowired
    private List<User> users;

    /** 电影 */
    @Autowired
    private List<Item> items;

    private StandardQueryParser queryParser = new StandardQueryParser();

    private int userDimension;

    private int itemDimension;

    private int scoreDimension;

    private int qualityOrder;

    private int quantityOrder;

    @PostConstruct
    void postConstruct() {
        userDimension = dataModule.getQualityInner("user");
        itemDimension = dataModule.getQualityInner("item");
        scoreDimension = dataModule.getQuantityInner("score");
        qualityOrder = dataModule.getQualityOrder();
        quantityOrder = dataModule.getQuantityOrder();
    }

    public void click(int userIndex, int itemIndex, float score) {
        Int2IntSortedMap qualityFeatures = new Int2IntRBTreeMap();
        qualityFeatures.put(userDimension, userIndex);
        qualityFeatures.put(itemDimension, itemIndex);
        Int2FloatSortedMap quantityFeatures = new Int2FloatRBTreeMap();
        quantityFeatures.put(scoreDimension, score);
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

        Model recommender = models.get(recommendKey);
        ArrayInstance instance = new ArrayInstance(qualityOrder, quantityOrder);
        User user = users.get(userIndex);
        int itemSize = items.size();
        for (int itemIndex = 0; itemIndex < itemSize; itemIndex++) {
            // 过滤电影
            if (user.isClicked(itemIndex)) {
                continue;
            }
            instance.setQualityFeature(userDimension, userIndex);
            instance.setQualityFeature(itemDimension, itemIndex);
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
        KeyValue<List<Document>, FloatList> search = engine.retrieveDocuments(query, null, 1000);
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
