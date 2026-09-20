package com.cxcron.common.pojo;

import lombok.Getter;
import lombok.Setter;

/**
 * 分页查询参数。
 */
@Getter
@Setter
public class PageParam {

    /**
     * 默认页码。
     */
    public static final long DEFAULT_PAGE_NO = 1L;

    /**
     * 默认每页数量。
     */
    public static final long DEFAULT_PAGE_SIZE = 10L;

    /**
     * 页码，从 1 开始。
     */
    private Long pageNo = DEFAULT_PAGE_NO;

    /**
     * 每页数量。
     */
    private Long pageSize = DEFAULT_PAGE_SIZE;

    public void setPageNo(Long pageNo) {
        this.pageNo = pageNo == null || pageNo < 1 ? DEFAULT_PAGE_NO : pageNo;
    }

    public void setPageSize(Long pageSize) {
        this.pageSize = pageSize == null || pageSize < 1 ? DEFAULT_PAGE_SIZE : pageSize;
    }
}
