package com.lcsk42.starter.core.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 应用配置属性
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties("application")
public class ApplicationProperties {

    /**
     * ID
     */
    private String id;

    /**
     * 名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 版本
     */
    private String version;

    /**
     * URL
     */
    private String url;

    /**
     * 基本包
     */
    private String basePackage;

    /**
     * 联系人
     */
    private Contact contact;

    /**
     * 许可协议
     */
    private License license;

    /**
     * 是否为生产环境
     */
    private boolean production = false;

    /**
     * 联系人配置属性
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Contact {
        /**
         * 名称
         */
        private String name;

        /**
         * 邮箱
         */
        private String email;

        /**
         * URL
         */
        private String url;
    }

    /**
     * 许可协议配置属性
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class License {
        /**
         * 名称
         */
        private String name;

        /**
         * URL
         */
        private String url;
    }
}
