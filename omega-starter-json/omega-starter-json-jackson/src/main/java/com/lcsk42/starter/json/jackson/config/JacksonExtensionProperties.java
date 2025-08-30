package com.lcsk42.starter.json.jackson.config;

import com.lcsk42.starter.json.enums.BigNumberSerializeMode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Jackson 扩展配置属性
 */
@Setter
@Getter
@ConfigurationProperties("spring.jackson")
public class JacksonExtensionProperties {

    /**
     * 大数值序列化模式
     */
    private BigNumberSerializeMode bigNumberSerializeMode = BigNumberSerializeMode.FLEXIBLE;
}