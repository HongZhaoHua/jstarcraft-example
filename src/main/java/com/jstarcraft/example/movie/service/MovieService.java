package com.jstarcraft.example.movie.service;

import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.flexible.standard.StandardQueryParser;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.jstarcraft.ai.data.DataModule;
import com.jstarcraft.ai.data.DataSpace;
import com.jstarcraft.ai.data.module.ArrayInstance;
import com.jstarcraft.core.common.lockable.HashLockableStrategy;
import com.jstarcraft.core.common.lockable.LockableMethod;
import com.jstarcraft.core.common.lockable.LockableParameter;
import com.jstarcraft.core.orm.lucene.LuceneEngine;
import com.jstarcraft.core.utility.KeyValue;
import com.jstarcraft.core.utility.StringUtility;
import com.jstarcraft.example.movie.configurer.MovieModelConfigurer;
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

    private final static Logger logger = LoggerFactory.getLogger(MovieService.class);

    @Autowired
    @Qualifier("movieDataSpace")
    private DataSpace dataSpace;

    @Autowired
    @Qualifier("movieDataModule")
    private DataModule dataModule;

    @Autowired
    private MovieModelConfigurer modelConfigurer;

    /** 排序预测与评分预测模型 */
    @Autowired
    @Qualifier("movieModels")
    private ConcurrentMap<String, Model> models;

    /** Lucene引擎 */
    @Autowired
    @Qualifier("movieEngine")
    private LuceneEngine engine;

    /** 用户 */
    @Autowired
    @Qualifier("movieUsers")
    private List<MovieUser> users;

    /** 条目 */
    @Autowired
    @Qualifier("movieItems")
    private List<MovieItem> items;

    private StandardQueryParser queryParser = new StandardQueryParser();

    private int userDimension;

    private int itemDimension;

    private int scoreDimension;

    private int qualityOrder;

    private int quantityOrder;

    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    /**
     * 刷新模型
     */
    private void refreshModel() {
        try {
            modelConfigurer.refreshModels(dataSpace, dataModule);
            logger.info("刷新模型成功");
        } catch (Exception exception) {
            logger.error("刷新模型失败", exception);
        }
    }

    @PostConstruct
    void postConstruct() {
        userDimension = dataModule.getQualityInner("user");
        itemDimension = dataModule.getQualityInner("item");
        scoreDimension = dataModule.getQuantityInner("score");
        qualityOrder = dataModule.getQualityOrder();
        quantityOrder = dataModule.getQuantityOrder();

        // 启动之后每间隔5分钟执行一次
        executor.scheduleAtFixedRate(this::refreshModel, 5, 5, TimeUnit.MINUTES);
    }

    @LockableMethod(strategy = HashLockableStrategy.class)
    public void click(@LockableParameter int userIndex, int itemIndex, float score) {
        Int2IntSortedMap qualityFeatures = new Int2IntRBTreeMap();
        qualityFeatures.put(userDimension, userIndex);
        qualityFeatures.put(itemDimension, itemIndex);
        Int2FloatSortedMap quantityFeatures = new Int2FloatRBTreeMap();
        quantityFeatures.put(scoreDimension, score);
        dataModule.associateInstance(qualityFeatures, quantityFeatures, 5F);
    }

    public List<MovieUser> getUsers() {
        return users;
    }

    /**
     * 个性化推荐
     * 
     * @param userIndex
     * @param recommendKey
     * @return
     */
    @LockableMethod(strategy = HashLockableStrategy.class)
    public Object2FloatMap<MovieItem> getRecommendItems(@LockableParameter int userIndex, String recommendKey) {
        // 标识-得分映射
        Object2FloatMap<MovieItem> item2ScoreMap = new Object2FloatOpenHashMap<>();

        Model model = models.get(recommendKey);
        ArrayInstance instance = new ArrayInstance(qualityOrder, quantityOrder);
        MovieUser user = users.get(userIndex);
        int itemSize = items.size();
        for (int itemIndex = 0; itemIndex < itemSize; itemIndex++) {
            // 过滤条目
            if (user.isClicked(itemIndex)) {
                continue;
            }
            instance.setQualityFeature(userDimension, userIndex);
            instance.setQualityFeature(itemDimension, itemIndex);
            model.predict(instance);
            MovieItem item = items.get(itemIndex);
            float score = instance.getQuantityMark();
            item2ScoreMap.put(item, score);
        }

        return item2ScoreMap;
    }

    /**
     * 个性化搜索
     * 
     * @param userIndex
     * @param searchKey
     * @return
     * @throws Exception
     */
    @LockableMethod(strategy = HashLockableStrategy.class)
    public Object2FloatMap<MovieItem> getSearchItems(@LockableParameter int userIndex, String searchKey) throws Exception {
        // 标识-得分映射
        Object2FloatMap<MovieItem> item2ScoreMap = new Object2FloatOpenHashMap<>();

        Query query = queryParser.parse(searchKey, MovieItem.TITLE);
        KeyValue<List<Document>, FloatList> search = engine.retrieveDocuments(query, null, 0, 1000);
        List<Document> documents = search.getKey();
        FloatList scores = search.getValue();
        for (int index = 0, size = documents.size(); index < size; index++) {
            Document document = documents.get(index);
            MovieItem item = items.get(document.getField(MovieItem.INDEX).numericValue().intValue());
            float score = scores.getFloat(index);
            item2ScoreMap.put(item, score);
        }

        return item2ScoreMap;
    }

    /**
     * 
     * @param userIndex
     * @param modelKey
     * @param queryKey
     * @param filterClicked
     * @return
     * @throws Exception
     */
    @LockableMethod(strategy = HashLockableStrategy.class)
    public Object2FloatMap<MovieItem> getItems(@LockableParameter int userIndex, String modelKey, String queryKey, boolean filterClicked) throws Exception {
        // 标识-得分映射
        Object2FloatMap<MovieItem> item2ScoreMap = new Object2FloatOpenHashMap<>();

        Model model = models.get(modelKey);
        ArrayInstance instance = new ArrayInstance(qualityOrder, quantityOrder);
        MovieUser user = users.get(userIndex);

        Query query = StringUtility.isBlank(queryKey) ? new MatchAllDocsQuery() : queryParser.parse(queryKey, MovieItem.TITLE);
        KeyValue<List<Document>, FloatList> retrieve = engine.retrieveDocuments(query, null, 0, 1000);
        List<Document> documents = retrieve.getKey();
        for (int index = 0, size = documents.size(); index < size; index++) {
            Document document = documents.get(index);
            MovieItem item = items.get(document.getField(MovieItem.INDEX).numericValue().intValue());
            int itemIndex = item.getIndex();
            // 过滤条目
            if (filterClicked && user.isClicked(itemIndex)) {
                continue;
            }
            instance.setQualityFeature(userDimension, userIndex);
            instance.setQualityFeature(itemDimension, itemIndex);
            model.predict(instance);
            float score = instance.getQuantityMark();
            item2ScoreMap.put(item, score);
        }

        return item2ScoreMap;
    }

}
