package com.jstarcraft.example.common.configurer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.jstarcraft.core.common.lockable.LockableAspect;

/**
 * 数据配置器
 * 
 * @author Birdy
 *
 */
@Configuration
public class LockableAspectConfigurer {

    /**
     * 装配锁切面
     * 
     * @return
     */
    @Bean("lockableAspect")
    LockableAspect getLockableAspect() throws Exception {
        LockableAspect aspect = new LockableAspect();
        return aspect;
    }

}
