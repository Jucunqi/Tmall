package com.gmall.service;

import com.gmall.bean.PmsSearchParam;
import com.gmall.bean.PmsSearchSkuInfo;

import java.util.List;

public interface SearchService {

    /**
     * 搜索信息
     * @return
     */
    List<PmsSearchSkuInfo> selPmsSearchInfo(PmsSearchParam pmsSearchParam);
}
