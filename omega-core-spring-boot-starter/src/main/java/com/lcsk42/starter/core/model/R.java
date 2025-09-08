package com.lcsk42.starter.core.model;

import com.lcsk42.starter.core.exception.base.AbstractException;
import com.lcsk42.starter.core.exception.errorcode.BaseErrorCode;
import lombok.Builder;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.util.Optional;

@Getter
@Builder
public class R<T> implements Serializable {
    /**
     * 表示操作成功的常量。
     */
    public static final String SUCCESS_CODE = "200";

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 表示结果状态的响应码。
     */
    private String code;

    /**
     * 结果中返回的实际数据。
     */
    @SuppressWarnings("squid:S1948")
    private T data;

    /**
     * 提供结果更多上下文信息的可读消息。
     */
    private String message;

    /**
     * 用于请求追踪的唯一标识符。
     */
    private String requestId;

    /**
     * 构造一个表示无附加数据操作成功的响应。
     *
     * @return 无数据的成功结果对象。
     */
    public static R<Void> ok() {
        return R.<Void>builder()
                .code(SUCCESS_CODE)
                .build();
    }

    /**
     * 构造一个带有指定数据的操作成功响应。
     *
     * @param data 结果中要返回的数据。
     * @return 包含数据的成功结果对象。
     */
    public static <T> R<T> ok(T data) {
        return R.<T>builder()
                .code(SUCCESS_CODE)
                .data(data)
                .build();
    }

    /**
     * 使用预定义服务错误构造一个默认失败响应。
     *
     * @return 包含标准错误码和消息的失败结果对象。
     */
    public static R<Void> fail() {
        return R.<Void>builder()
                .code(BaseErrorCode.SERVICE_ERROR.getCode())
                .message(BaseErrorCode.SERVICE_ERROR.getMessage())
                .build();
    }

    /**
     * 使用自定义错误消息构造一个失败响应。
     *
     * @param errorMessage 结果中包含的错误消息。
     * @return 包含指定错误消息的失败结果对象。
     */
    public static R<Void> fail(String errorMessage) {
        return R.<Void>builder()
                .code(BaseErrorCode.SERVICE_ERROR.getCode())
                .message(errorMessage)
                .build();
    }

    /**
     * 基于自定义平台异常构造一个失败响应。
     *
     * @param abstractException 包含错误详情的异常对象。
     * @return 从异常中获取错误码和消息的失败结果对象。
     */
    public static R<Void> fail(AbstractException abstractException) {
        String errorCode = Optional.ofNullable(abstractException.getErrorCode())
                .orElse(BaseErrorCode.SERVICE_ERROR.getCode());
        String errorMessage = Optional.ofNullable(abstractException.getErrorMessage())
                .orElse(BaseErrorCode.SERVICE_ERROR.getMessage());
        return R.<Void>builder()
                .code(errorCode)
                .message(errorMessage)
                .build();
    }

    /**
     * 使用自定义错误码和消息构造一个失败响应。
     *
     * @param errorCode    错误码。
     * @param errorMessage 错误消息。
     * @return 包含指定错误信息的失败结果对象。
     */
    public static R<Void> fail(String errorCode, String errorMessage) {
        return R.<Void>builder()
                .code(errorCode)
                .message(errorMessage)
                .build();
    }

    /**
     * 判断响应是否成功。
     *
     * @return 当响应码等于 SUCCESS_CODE 时返回 true，否则返回 false。
     */
    public boolean isSucceed() {
        return SUCCESS_CODE.equals(code);
    }

    /**
     * 设置响应标识符。
     *
     * @return 当前 Result 实例用于方法链式调用。
     */
    public R<T> withRequestId(String requestId) {
        this.requestId = requestId;
        return this;
    }
}
