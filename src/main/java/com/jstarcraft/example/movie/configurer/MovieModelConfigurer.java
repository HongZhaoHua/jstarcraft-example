package com.jstarcraft.example.movie.configurer;

import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Future;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.jstarcraft.ai.data.DataModule;
import com.jstarcraft.ai.data.DataSpace;
import com.jstarcraft.ai.environment.EnvironmentContext;
import com.jstarcraft.ai.environment.EnvironmentFactory;
import com.jstarcraft.core.common.configuration.MapOption;
import com.jstarcraft.core.common.configuration.Option;
import com.jstarcraft.core.common.reflection.ReflectionUtility;
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
public class MovieModelConfigurer {

    private Option option;

    {
        try {
            Properties keyValues = new Properties();
            keyValues.load(this.getClass().getResourceAsStream("/data.properties"));
            option = new MapOption(keyValues);
        } catch (Exception exception) {
            throw new RuntimeException();
        }
    }

    private Model getModel(Class<? extends Model> clazz, DataSpace dataSpace, DataModule dataModule) throws Exception {
        Model model = ReflectionUtility.getInstance(clazz);
        EnvironmentContext context = EnvironmentFactory.getContext();
        Future<?> task = context.doTask(() -> {
            model.prepare(option, dataModule, dataSpace);
            model.practice();
        });
        task.get();
        return model;
    }

    private ConcurrentMap<String, Model> models = new ConcurrentHashMap<>();

    public void refreshModels(DataSpace movieDataSpace, DataModule movieDataModule) throws Exception {
        models.put("AssociationRule", getModel(AssociationRuleModel.class, movieDataSpace, movieDataModule));
        models.put("BPR", getModel(BPRModel.class, movieDataSpace, movieDataModule));
        models.put("ItemKNN", getModel(ItemKNNRankingModel.class, movieDataSpace, movieDataModule));
        models.put("LDA", getModel(LDAModel.class, movieDataSpace, movieDataModule));
        models.put("MostPopular", getModel(MostPopularModel.class, movieDataSpace, movieDataModule));
        models.put("Random", getModel(RandomGuessModel.class, movieDataSpace, movieDataModule));
        models.put("UserKNN", getModel(UserKNNRankingModel.class, movieDataSpace, movieDataModule));
        models.put("WRMF", getModel(WRMFModel.class, movieDataSpace, movieDataModule));
    }

    @Bean("movieModels")
    public ConcurrentMap<String, Model> getMovieModels(DataSpace movieDataSpace, DataModule movieDataModule) throws Exception {
        refreshModels(movieDataSpace, movieDataModule);
        return models;
    }

}
