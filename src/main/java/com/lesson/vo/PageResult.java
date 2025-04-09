package com.lesson.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

/**
 * 分页结果
 *
 * @param <T> 结果类型
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "分页结果")
public class PageResult<T> {
    /**
     * 总记录数
     */
    @Schema(description = "总记录数")
    private Long total;

    /**
     * 数据列表
     */
    @Schema(description = "数据列表")
    private List<T> list;

    /**
     * 创建分页结果
     *
     * @param total 总记录数
     * @param list  数据列表
     * @param <T>   数据类型
     * @return 分页结果
     */
    public static <T> PageResult<T> of(Long total, List<T> list) {
        PageResult<T> result = new PageResult<>();
        result.setTotal(total);
        result.setList(list);
        return result;
    }

    public static <T> PageResult<T> empty() {
        PageResult<T> result = new PageResult<>();
        result.setTotal(0L);
        result.setList(Collections.emptyList());
        return result;
    }
} 