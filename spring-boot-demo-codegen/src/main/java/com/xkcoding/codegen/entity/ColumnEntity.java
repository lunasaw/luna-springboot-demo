package com.xkcoding.codegen.entity;

import lombok.Data;

/**
 * <p>
 * 列属性： https://blog.csdn.net/lkforce/article/details/79557482
 * </p>
 *
 * @package: com.xkcoding.codegen.entity
 * @description: 列属性： https://blog.csdn.net/lkforce/article/details/79557482
 * @author: yangkai.shen
 * @date: Created in 2019-03-22 09:46
 * @copyright: Copyright (c) 2019
 * @version: V1.0
 * @modified: yangkai.shen
 */
public class ColumnEntity {
    /**
     * 列表
     */
    private String columnName;
    /**
     * 数据类型
     */
    private String dataType;
    /**
     * 备注
     */
    private String comments;
    /**
     * 驼峰属性
     */
    private String caseAttrName;
    /**
     * 普通属性
     */
    private String lowerAttrName;
    /**
     * 属性类型
     */
    private String attrType;
    /**
     * 其他信息
     */
    private String extra;

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getCaseAttrName() {
        return caseAttrName;
    }

    public void setCaseAttrName(String caseAttrName) {
        this.caseAttrName = caseAttrName;
    }

    public String getLowerAttrName() {
        return lowerAttrName;
    }

    public void setLowerAttrName(String lowerAttrName) {
        this.lowerAttrName = lowerAttrName;
    }

    public String getAttrType() {
        return attrType;
    }

    public void setAttrType(String attrType) {
        this.attrType = attrType;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }
}
