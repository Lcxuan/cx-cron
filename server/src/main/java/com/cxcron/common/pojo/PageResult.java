package com.cxcron.common.pojo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * 分页查询结果。
 */
@Data
@AllArgsConstructor
@Schema(description = "分页查询结果")
public class PageResult<T> {

    /**
     * 当前页数据。
     */
    @Schema(description = "当前页数据")
    private List<T> list;

    /**
     * 总记录数。
     */
    @Schema(description = "总记录数")
    private long total;
}
