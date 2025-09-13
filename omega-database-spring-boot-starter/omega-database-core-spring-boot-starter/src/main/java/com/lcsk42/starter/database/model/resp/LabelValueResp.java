package com.lcsk42.starter.database.model.resp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "键值对响应参数")
public class LabelValueResp<T> {
    /**
     * 标签
     */
    @Schema(description = "标签", example = "男")
    private String label;

    /**
     * 值
     */
    @Schema(description = "值", example = "1")
    private T value;

    /**
     * 是否禁用
     */
    @Schema(description = "是否禁用", example = "false")
    private Boolean disabled;

    /**
     * 额外数据
     */
    @Schema(description = "额外数据")
    private Object extra;
}
