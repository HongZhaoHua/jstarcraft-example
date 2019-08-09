package com.jstarcraft.example;

import java.util.HashMap;
import java.util.Properties;
import java.util.concurrent.Future;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.jstarcraft.ai.data.DataModule;
import com.jstarcraft.ai.data.DataSpace;
import com.jstarcraft.ai.environment.EnvironmentContext;
import com.jstarcraft.ai.environment.EnvironmentFactory;
import com.jstarcraft.core.common.reflection.ReflectionUtility;
import com.jstarcraft.core.utility.Configurator;
import com.jstarcraft.rns.recommend.Recommender;
import com.jstarcraft.rns.recommend.benchmark.RandomGuessRecommender;
import com.jstarcraft.rns.recommend.benchmark.ranking.MostPopularRecommender;
import com.jstarcraft.rns.recommend.collaborative.ranking.BPRRecommender;
import com.jstarcraft.rns.recommend.collaborative.ranking.ItemKNNRankingRecommender;
import com.jstarcraft.rns.recommend.collaborative.ranking.LDARecommender;
import com.jstarcraft.rns.recommend.collaborative.ranking.UserKNNRankingRecommender;
import com.jstarcraft.rns.recommend.collaborative.ranking.WRMFRecommender;
import com.jstarcraft.rns.recommend.extend.ranking.AssociationRuleRecommender;

/**
 * 推荐配置器
 * 
 * @author Birdy
 *
 */
@Configuration
public class RecommendConfigurer {

    private Configurator configuration;

    {
        try {
            Properties keyValues = new Properties();
            keyValues.load(this.getClass().getResourceAsStream("/data.properties"));
            configuration = new Configurator(keyValues);
        } catch (Exception exception) {
            throw new RuntimeException();
        }
    }

    private Recommender getRecommender(Class<? extends Recommender> clazz, DataSpace dataSpace, DataModule dataModule) throws Exception {
        Recommender recommender = ReflectionUtility.getInstance(clazz);
        EnvironmentContext context = EnvironmentFactory.getContext();
        Future<?> task = context.doTask(() -> {
            recommender.prepare(configuration, dataModule, dataSpace);
            recommender.practice();
        });
        task.get();
        return recommender;
    }

    @Bean
    public HashMap<String, Recommender> getRecommenders(DataSpace dataSpace, DataModule dataModule) throws Exception {
        HashMap<String, Recommender> recommenders = new HashMap<>();
        recommenders.put("AssociationRule", getRecommender(AssociationRuleRecommender.class, dataSpace, dataModule));
        recommenders.put("BPR", getRecommender(BPRRecommender.class, dataSpace, dataModule));
        recommenders.put("ItemKNN", getRecommender(ItemKNNRankingRecommender.class, dataSpace, dataModule));
        recommenders.put("LDA", getRecommender(LDARecommender.class, dataSpace, dataModule));
        recommenders.put("MostPopular", getRecommender(MostPopularRecommender.class, dataSpace, dataModule));
        recommenders.put("Random", getRecommender(RandomGuessRecommender.class, dataSpace, dataModule));
        recommenders.put("UserKNN", getRecommender(UserKNNRankingRecommender.class, dataSpace, dataModule));
        recommenders.put("WRMF", getRecommender(WRMFRecommender.class, dataSpace, dataModule));

        return recommenders;
    }

}
