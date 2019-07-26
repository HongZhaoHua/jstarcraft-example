package com.jstarcraft.example;

import java.util.HashMap;
import java.util.concurrent.Future;

import org.nd4j.linalg.factory.Nd4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.jstarcraft.ai.data.DataModule;
import com.jstarcraft.ai.data.DataSpace;
import com.jstarcraft.ai.environment.EnvironmentContext;
import com.jstarcraft.rns.configure.Configurator;
import com.jstarcraft.rns.recommend.Recommender;
import com.jstarcraft.rns.recommend.benchmark.ranking.MostPopularRecommender;
import com.jstarcraft.rns.recommend.collaborative.ranking.ItemKNNRankingRecommender;
import com.jstarcraft.rns.recommend.collaborative.ranking.UserKNNRankingRecommender;

/**
 * 推荐配置器
 * 
 * @author Birdy
 *
 */
@Configuration
public class RecommendConfigurer {

    private Configurator configuration = Configurator.valueOf();

    @Bean("mostPopularRecommender")
    public Recommender getMostPopularRecommender(DataSpace dataSpace, DataModule dataModule) throws Exception {
        Recommender recommender = new MostPopularRecommender();
        EnvironmentContext context = Nd4j.getAffinityManager().getClass().getSimpleName().equals("CpuAffinityManager") ? EnvironmentContext.CPU : EnvironmentContext.GPU;
        Future<?> task = context.doTask(() -> {
            recommender.prepare(configuration, dataModule, dataSpace);
            recommender.practice();
        });
        task.get();
        return recommender;
    }

    @Bean("itemKnnRecommender")
    public Recommender getItemKnnRecommender(DataSpace dataSpace, DataModule dataModule) throws Exception {
        Recommender recommender = new ItemKNNRankingRecommender();
        EnvironmentContext context = Nd4j.getAffinityManager().getClass().getSimpleName().equals("CpuAffinityManager") ? EnvironmentContext.CPU : EnvironmentContext.GPU;
        Future<?> task = context.doTask(() -> {
            recommender.prepare(configuration, dataModule, dataSpace);
            recommender.practice();
        });
        task.get();
        return recommender;
    }

    @Bean("userKnnRecommender")
    public Recommender getUserKnnRecommender(DataSpace dataSpace, DataModule dataModule) throws Exception {
        Recommender recommender = new UserKNNRankingRecommender();
        EnvironmentContext context = Nd4j.getAffinityManager().getClass().getSimpleName().equals("CpuAffinityManager") ? EnvironmentContext.CPU : EnvironmentContext.GPU;
        Future<?> task = context.doTask(() -> {
            recommender.prepare(configuration, dataModule, dataSpace);
            recommender.practice();
        });
        task.get();
        return recommender;
    }

    @Bean
    public HashMap<String, Recommender> getRecommenders(Recommender mostPopularRecommender, Recommender itemKnnRecommender, Recommender userKnnRecommender) {
        HashMap<String, Recommender> recommenders = new HashMap<>();
        recommenders.put("MostPopular", mostPopularRecommender);
        recommenders.put("ItemKNN", itemKnnRecommender);
        recommenders.put("UserKNN", userKnnRecommender);
        return recommenders;
    }

}
