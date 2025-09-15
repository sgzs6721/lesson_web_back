package com.lesson.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * 批量删除请求VO
 */
@Data
@Schema(description = "批量删除请求")
public class BatchDeleteRequest {
    
    @NotEmpty(message = "删除ID列表不能为空")
    @Size(min = 1, max = 100, message = "批量删除数量必须在1-100之间")
    @Schema(description = "要删除的ID列表", example = "[1, 2, 3]", required = true)
    private List<Long> ids;
}
