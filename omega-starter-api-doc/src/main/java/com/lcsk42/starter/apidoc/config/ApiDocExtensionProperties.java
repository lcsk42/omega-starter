package com.lcsk42.starter.apidoc.config;

import io.swagger.v3.oas.models.Components;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * API 文档扩展配置属性
 */
@Setter
@Getter
@ConfigurationProperties("springdoc")
public class ApiDocExtensionProperties {

    /**
     * 组件配置（包括鉴权配置等）
     */
    @NestedConfigurationProperty
    private Components components;

}
