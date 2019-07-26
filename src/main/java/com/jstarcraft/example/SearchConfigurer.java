package com.jstarcraft.example;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.jstarcraft.core.orm.identification.IdentityDefinition;
import com.jstarcraft.example.common.event.EventBus;

/**
 * 搜索配置器
 * 
 * @author Birdy
 *
 */
@Configuration
public class SearchConfigurer {

    @Bean
    IdentityDefinition getIdentityDefinition() {
        IdentityDefinition identityDefinition = new IdentityDefinition(10, 53);
        return identityDefinition;
    }

    @Bean
    EventBus getEventBus() {
        EventBus eventBus = new EventBus(1000, 1, 60);
        return eventBus;
    }

}
