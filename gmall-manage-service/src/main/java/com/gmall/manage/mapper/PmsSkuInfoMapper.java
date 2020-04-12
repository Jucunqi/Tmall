package com.gmall.manage.mapper;

import com.gmall.bean.PmsSkuInfo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface PmsSkuInfoMapper extends Mapper<PmsSkuInfo> {
    /**
     * 通过skuId查询出同组的sku信息
     * @param spuId
     * @return
     */
    List<PmsSkuInfo> selSkuSaleValueListBySpu(@Param("spuId") String spuId);


}
