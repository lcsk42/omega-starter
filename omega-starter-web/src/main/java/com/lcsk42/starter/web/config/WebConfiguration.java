package com.lcsk42.starter.web.config;

import com.lcsk42.starter.web.converter.BaseEnumConverterFactory;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Slf4j
public class WebConfiguration implements WebMvcConfigurer {

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.addPathPrefix("/api", clazz -> clazz.isAnnotationPresent(RestController.class)
                && !clazz.getPackageName().startsWith("org.springdoc")
                && !clazz.getPackageName().startsWith("springfox.documentation"));
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverterFactory(new BaseEnumConverterFactory());
    }

    @PostConstruct
    public void postConstruct() {
        log.debug("[Omega Starter] - Auto Configuration 'Web MVC' completed initialization.");
    }
}