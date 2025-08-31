package com.lcsk42.starter.core.config;

import com.lcsk42.starter.core.ApplicationContextHolder;
import com.lcsk42.starter.core.designpattern.chain.AbstractChainContext;
import com.lcsk42.starter.core.designpattern.strategy.AbstractStrategyChoose;
import com.lcsk42.starter.core.init.ApplicationContentPostProcessor;
import com.lcsk42.starter.core.threadpool.build.ThreadPoolBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.util.concurrent.Executor;

public class CoreAutoConfiguration {

    @Bean
    public ApplicationProperties applicationProperties() {
        return new ApplicationProperties();
    }

    /**
     * 创建 ApplicationContextHolder bean（当不存在时）。
     *
     * @return 新的 ApplicationContextHolder 实例
     */
    @Bean
    @ConditionalOnMissingBean
    public ApplicationContextHolder applicationContextHolder() {
        return new ApplicationContextHolder();
    }

    /**
     * 创建 ApplicationContentPostProcessor bean（当不存在时）。
     *
     * @param applicationContext Spring 应用上下文
     * @return 新的 ApplicationContentPostProcessor 实例
     */
    @Bean
    @ConditionalOnMissingBean
    public ApplicationContentPostProcessor applicationContentPostProcessor(ApplicationContext applicationContext) {
        return new ApplicationContentPostProcessor(applicationContext);
    }

    /**
     * 创建主任务执行器 bean。
     * 配置默认线程池包含以下特性：
     * - 线程名前缀 "default-pool-"
     * - 非守护线程
     *
     * @return 配置好的 ThreadPoolExecutor 实例
     */
    @Bean
    @Primary
    public Executor taskExecutor() {
        return ThreadPoolBuilder.builder()
                .threadFactory("default-pool-", false)
                .build();
    }

    /**
     * 策略模式选择器
     */
    @Bean
    public AbstractStrategyChoose abstractStrategyChoose() {
        return new AbstractStrategyChoose();
    }

    /**
     * 责任链模式上下文
     */
    @Bean
    public AbstractChainContext abstractChainContext() {
        return new AbstractChainContext();
    }

}
