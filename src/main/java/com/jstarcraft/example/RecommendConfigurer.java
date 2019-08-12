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
import com.jstarcraft.rns.model.Model;
import com.jstarcraft.rns.model.benchmark.RandomGuessModel;
import com.jstarcraft.rns.model.benchmark.ranking.MostPopularModel;
import com.jstarcraft.rns.model.collaborative.ranking.BPRModel;
import com.jstarcraft.rns.model.collaborative.ranking.ItemKNNRankingModel;
import com.jstarcraft.rns.model.collaborative.ranking.LDAModel;
import com.jstarcraft.rns.model.collaborative.ranking.UserKNNRankingModel;
import com.jstarcraft.rns.model.collaborative.ranking.WRMFModel;
import com.jstarcraft.rns.model.extend.ranking.AssociationRuleModel;

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

    private Model getRecommender(Class<? extends Model> clazz, DataSpace dataSpace, DataModule dataModule) throws Exception {
        Model recommender = ReflectionUtility.getInstance(clazz);
        EnvironmentContext context = EnvironmentFactory.getContext();
        Future<?> task = context.doTask(() -> {
            recommender.prepare(configuration, dataModule, dataSpace);
            recommender.practice();
        });
        task.get();
        return recommender;
    }

    @Bean
    public HashMap<String, Model> getRecommenders(DataSpace dataSpace, DataModule dataModule) throws Exception {
        HashMap<String, Model> recommenders = new HashMap<>();
        recommenders.put("AssociationRule", getRecommender(AssociationRuleModel.class, dataSpace, dataModule));
        recommenders.put("BPR", getRecommender(BPRModel.class, dataSpace, dataModule));
        recommenders.put("ItemKNN", getRecommender(ItemKNNRankingModel.class, dataSpace, dataModule));
        recommenders.put("LDA", getRecommender(LDAModel.class, dataSpace, dataModule));
        recommenders.put("MostPopular", getRecommender(MostPopularModel.class, dataSpace, dataModule));
        recommenders.put("Random", getRecommender(RandomGuessModel.class, dataSpace, dataModule));
        recommenders.put("UserKNN", getRecommender(UserKNNRankingModel.class, dataSpace, dataModule));
        recommenders.put("WRMF", getRecommender(WRMFModel.class, dataSpace, dataModule));

        return recommenders;
    }

}
