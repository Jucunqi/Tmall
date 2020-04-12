package com.gmall.bean;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * 封装页面搜索信息的实体类
 */
public class PmsSearchParam implements Serializable {

    private String id;
    private String keyword;
    private String catalog3Id;
    private String[] valueId;

    public String[] getValueId() {
        return valueId;
    }

    public void setValueId(String[] valueId) {
        this.valueId = valueId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getCatalog3Id() {
        return catalog3Id;
    }

    public void setCatalog3Id(String catalog3Id) {
        this.catalog3Id = catalog3Id;
    }
}
