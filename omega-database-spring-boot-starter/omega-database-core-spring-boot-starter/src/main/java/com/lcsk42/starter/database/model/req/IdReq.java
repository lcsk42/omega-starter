package com.lcsk42.starter.database.model.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ID 请求参数
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IdReq {
    /**
     * ID
     */
    @Schema(description = "ID", example = "1")
    @NotNull(message = "ID 不能为空")
    private Long id;
}
