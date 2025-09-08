package com.lcsk42.starter.json.jackson.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.lcsk42.starter.core.enums.BaseEnum;

import java.io.IOException;

/**
 * 枚举接口 BaseEnum 序列化器
 */
@JacksonStdImpl
public class BaseEnumSerializer extends JsonSerializer<BaseEnum> {

    /**
     * 静态实例
     */
    public static final BaseEnumSerializer INSTANCE = new BaseEnumSerializer();

    @Override
    public void serialize(BaseEnum value, JsonGenerator generator, SerializerProvider serializers) throws IOException {
        generator.writeObject(value.getValue());
    }
}