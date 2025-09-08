package com.lcsk42.starter.web.feign;

import com.lcsk42.starter.core.exception.base.ServiceException;
import com.lcsk42.starter.core.exception.errorcode.BaseErrorCode;
import com.lcsk42.starter.core.model.R;
import feign.Response;
import feign.codec.Decoder;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * 自定义 Feign 响应解码器，用于解包 R<T> 响应包装器
 * 当 API 响应表示失败时抛出异常
 */
@RequiredArgsConstructor
public class FeignResultDecoder implements Decoder {
    // Feign 使用的原始解码器（通常是 SpringDecoder）
    private final Decoder decoder;

    @Override
    public Object decode(Response response, Type type)
            throws IOException {
        // 从 Feign 请求模板获取方法元数据
        final Method method = response.request().requestTemplate().methodMetadata().method();

        // 检查声明的返回类型是否为 R 以外的类型（即需要从 R<T> 解包）
        final boolean isResult = method.getReturnType() != R.class;
        if (isResult) {
            // 先将响应解码为 R 对象
            R<?> R = (R<?>) this.decoder.decode(response, R.class);
            // 如果响应表示成功，返回实际数据
            if (BooleanUtils.isTrue(R.isSucceed())) {
                return R.getData();
            } else {
                // 否则抛出自定义异常表示失败
                throw new ServiceException(R.getMessage(), BaseErrorCode.SERVICE_FEIGN_ERROR);
            }
        }
        // 如果方法本身返回 R 对象，则直接解码
        return this.decoder.decode(response, type);
    }
}