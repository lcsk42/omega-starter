package com.lcsk42.starter.web;

import com.lcsk42.starter.core.constant.CustomHttpHeaderConstants;
import com.lcsk42.starter.core.model.R;
import com.lcsk42.starter.json.jackson.util.JacksonUtil;
import com.lcsk42.starter.web.annotation.CompatibleOutput;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@RestControllerAdvice
public class GlobalResultHandler implements ResponseBodyAdvice<Object> {

    /**
     * 判断是否应处理响应体。此方法检查：
     * 1. 检查类或方法是否具有 @CompatibleOutput 注解
     * 2. 检查返回值是否为 Result 类型
     * 3. 检查请求 URI 是否以 "/api" 开头
     */
    @Override
    public boolean supports(final MethodParameter returnType,
                            @NonNull final Class<? extends HttpMessageConverter<?>> converterType) {
        // 如果类上有 @CompatibleOutput 注解，不处理
        Class<?> controllerClass = returnType.getContainingClass();
        boolean hasClassAnnotation = AnnotationUtils.findAnnotation(controllerClass, CompatibleOutput.class) != null;
        if (hasClassAnnotation) {
            return false;
        }

        // 如果方法上有 @CompatibleOutput 注解，不处理
        boolean hasMethodAnnotation = returnType.getMethodAnnotation(CompatibleOutput.class) != null;
        if (hasMethodAnnotation) {
            return false;
        }

        // 如果返回类型已经比 R.class，不处理
        boolean isResultType = returnType.getParameterType().equals(R.class);
        if (isResultType) {
            return false;
        }

        // 如果请求不是以 "/api" 开头，不处理
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return false;
        }
        HttpServletRequest request = attributes.getRequest();
        String requestUri = request.getRequestURI();

        return StringUtils.startsWith(requestUri, "/api");
    }

    /**
     * 在响应体写入响应前对其进行修改。
     * 1. 若响应体不是字符串类型，则将其包装为 R 对象
     * 2. 若为字符串类型，则转换为 JSON 格式的 R 对象
     */
    @Override
    public Object beforeBodyWrite(Object body,
                                  @NonNull MethodParameter returnType,
                                  @NonNull MediaType selectedContentType,
                                  @NonNull Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  @NonNull ServerHttpRequest request,
                                  @NonNull ServerHttpResponse response) {

        String requestId = request.getHeaders().getFirst(CustomHttpHeaderConstants.REQUEST_ID);

        if (StringUtils.isBlank(requestId)) {
            // 如果 request ID 是空的，生成一个新的
            requestId = CustomHttpHeaderConstants.getReturnRequestId();
        }

        // 若返回类型是字符串，将其转换为 JSON 格式的 Result 对象
        if (returnType.getParameterType().isAssignableFrom(String.class)) {
            String json = JacksonUtil.toJSON(R.ok(body).withRequestId(requestId));
            // 设置响应内容类型为 application/json
            // 由于 returnType.getParameterType() 是 String 类型，默认内容类型会是 text/plain
            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            return ObjectUtils.defaultIfNull(json, body.toString());
        }

        // 其他情况下，直接将响应体包装为 Result 对象
        return R.ok(body).withRequestId(requestId);
    }
}
