package com.gmall.manage.mapper;

import com.gmall.bean.PmsProductSaleAttr;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface PmsProductSaleAttrMapper extends Mapper<PmsProductSaleAttr> {

    /**
     * 查询商品sku全部属性值
     * @param skuId
     * @param spuId
     * @return
     */
    List<PmsProductSaleAttr> spuSaleAttrListCheckBySku(@Param("skuId") String skuId, @Param("spuId") String spuId);
}
