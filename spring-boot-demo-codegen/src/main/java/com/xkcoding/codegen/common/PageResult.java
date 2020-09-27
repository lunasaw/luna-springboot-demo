package com.xkcoding.codegen.common;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * <p>
 * 分页结果集
 * </p>
 *
 * @package: com.xkcoding.codegen.common
 * @description: 分页结果集
 * @author: yangkai.shen
 * @date: Created in 2019-03-22 11:24
 * @copyright: Copyright (c) 2019
 * @version: V1.0
 * @modified: yangkai.shen
 */
public class PageResult<T> {
    /**
     * 总条数
     */
    private Long total;

    /**
     * 页码
     */
    private int pageNumber;

    /**
     * 每页结果数
     */
    private int pageSize;

    /**
     * 结果集
     */
    private List<T> list;

    public PageResult() {
    }

    public PageResult(Long total, int pageNumber, int pageSize, List<T> list) {
        this.total = total;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.list = list;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }
}
