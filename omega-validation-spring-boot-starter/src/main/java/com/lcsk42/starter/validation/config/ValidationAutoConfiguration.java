package com.lcsk42.starter.validation.config;

import jakarta.annotation.PostConstruct;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.BaseHibernateValidatorConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

/**
 * JSR 303 校验器自动配置
 *
 * @author Charles7c
 * @since 2.3.0
 */
@Slf4j
@AutoConfigureBefore
public class ValidationAutoConfiguration {

    /**
     * Validator 失败立即返回模式配置
     *
     * <p>
     * 默认情况下会校验完所有字段，然后才抛出异常。
     * </p>
     */
    @Bean
    public Validator validator(MessageSource messageSource) {
        try (LocalValidatorFactoryBean factoryBean = new LocalValidatorFactoryBean()) {
            // 国际化
            factoryBean.setValidationMessageSource(messageSource);
            // 快速失败
            factoryBean.getValidationPropertyMap()
                    .put(BaseHibernateValidatorConfiguration.FAIL_FAST, Boolean.TRUE.toString());
            factoryBean.afterPropertiesSet();
            return factoryBean.getValidator();
        }
    }

    @PostConstruct
    public void postConstruct() {
        log.debug("[Omega Starter] - Auto Configuration 'Validation' completed initialization.");
    }
}
