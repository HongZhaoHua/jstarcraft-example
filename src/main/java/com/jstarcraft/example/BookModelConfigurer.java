package com.jstarcraft.example;

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
public class BookModelConfigurer {

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

    private Model getModel(Class<? extends Model> clazz, DataSpace dataSpace, DataModule dataModule) throws Exception {
        Model model = ReflectionUtility.getInstance(clazz);
        EnvironmentContext context = EnvironmentFactory.getContext();
        Future<?> task = context.doTask(() -> {
            model.prepare(configuration, dataModule, dataSpace);
            model.practice();
        });
        task.get();
        return model;
    }

    @Bean("bookModels")
    public ConcurrentMap<String, Model> getBookModels(DataSpace bookDataSpace, DataModule bookDataModule) throws Exception {
        ConcurrentMap<String, Model> models = new ConcurrentHashMap<>();
        models.put("AssociationRule", getModel(AssociationRuleModel.class, bookDataSpace, bookDataModule));
        models.put("BPR", getModel(BPRModel.class, bookDataSpace, bookDataModule));
        models.put("ItemKNN", getModel(ItemKNNRankingModel.class, bookDataSpace, bookDataModule));
        models.put("LDA", getModel(LDAModel.class, bookDataSpace, bookDataModule));
        models.put("MostPopular", getModel(MostPopularModel.class, bookDataSpace, bookDataModule));
        models.put("Random", getModel(RandomGuessModel.class, bookDataSpace, bookDataModule));
        models.put("UserKNN", getModel(UserKNNRankingModel.class, bookDataSpace, bookDataModule));
        models.put("WRMF", getModel(WRMFModel.class, bookDataSpace, bookDataModule));
        return models;
    }

}
