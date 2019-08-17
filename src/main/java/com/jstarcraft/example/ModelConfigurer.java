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
public class ModelConfigurer {

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

    @Bean
    public HashMap<String, Model> getModels(DataSpace dataSpace, DataModule dataModule) throws Exception {
        HashMap<String, Model> models = new HashMap<>();
        models.put("AssociationRule", getModel(AssociationRuleModel.class, dataSpace, dataModule));
        models.put("BPR", getModel(BPRModel.class, dataSpace, dataModule));
        models.put("ItemKNN", getModel(ItemKNNRankingModel.class, dataSpace, dataModule));
        models.put("LDA", getModel(LDAModel.class, dataSpace, dataModule));
        models.put("MostPopular", getModel(MostPopularModel.class, dataSpace, dataModule));
        models.put("Random", getModel(RandomGuessModel.class, dataSpace, dataModule));
        models.put("UserKNN", getModel(UserKNNRankingModel.class, dataSpace, dataModule));
        models.put("WRMF", getModel(WRMFModel.class, dataSpace, dataModule));

        return models;
    }

}
