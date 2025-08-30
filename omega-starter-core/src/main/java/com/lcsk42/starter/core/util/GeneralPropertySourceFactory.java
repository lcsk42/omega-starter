package com.lcsk42.starter.core.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.DefaultPropertySourceFactory;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.lang.Nullable;

import java.io.IOException;

/**
 * 通用配置文件读取工厂
 * <p>
 * DefaultPropertySourceFactory 仅支持 properties
 * 配置文件读取，详见：<ahref="https://docs.spring.io/spring-boot/docs/2.0.6.RELEASE/reference/html/boot-features-external-config.html#boot-features-external-config-yaml-shortcomings">YAMLShortcomings</a>
 * </p>
 */
public class GeneralPropertySourceFactory extends DefaultPropertySourceFactory {

    @Override
    public PropertySource<?> createPropertySource(@Nullable String name,
                                                  EncodedResource encodedResource) throws IOException {
        Resource resource = encodedResource.getResource();
        String resourceName = resource.getFilename();
        if (StringUtils.isNotBlank(resourceName) && StringUtils.endsWithAny(resourceName, ".yml", ".yaml")) {
            return new YamlPropertySourceLoader().load(resourceName, resource).getFirst();
        }
        return super.createPropertySource(name, encodedResource);
    }
}