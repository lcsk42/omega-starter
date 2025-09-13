package com.lcsk42.starter.database.model.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * ID 列表请求参数
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IdsReq {
    /**
     * ID
     */
    @Schema(description = "ID", example = "[1,2]")
    @NotEmpty(message = "ID 不能为空")
    private List<Long> ids;
}
