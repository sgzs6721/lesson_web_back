package com.lesson.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

/**
 * 分页查询结果
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "分页查询结果")
public class PageResult<T> {
    /**
     * 总记录数
     */
    @Schema(description = "总记录数", example = "100")
    private long total;

    /**
     * 当前页码
     */
    @Schema(description = "当前页码", example = "1")
    private int pageNum;

    /**
     * 每页大小
     */
    @Schema(description = "每页记录数", example = "10")
    private int pageSize;

    /**
     * 总页数
     */
    @Schema(description = "总页数", example = "10")
    private int pages;

    /**
     * 数据列表
     */
    @Schema(description = "当前页数据列表")
    private List<T> list;

    /**
     * 创建分页结果
     *
     * @param total 总记录数
     * @param list  数据列表
     * @param <T>   数据类型
     * @return 分页结果
     */
    public static <T> PageResult<T> of(List<T> list, long total, int pageNum, int pageSize) {
        PageResult<T> result = new PageResult<>();
        result.setList(list);
        result.setTotal(total);
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);
        result.setPages((int) Math.ceil((double) total / pageSize));
        return result;
    }

    public static <T> PageResult<T> empty() {
        PageResult<T> result = new PageResult<>();
        result.setTotal(0L);
        result.setList(Collections.emptyList());
        return result;
    }
} 