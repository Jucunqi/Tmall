package com.gmall.service;

import com.gmall.bean.PmsProductSaleAttr;
import com.gmall.bean.PmsSkuInfo;

import java.math.BigDecimal;
import java.util.List;

public interface SkuService {

    /**
     * 新增商品sku信息
     * @param pmsSkuInfo
     * @return
     */
    String insPmsSkuInfo(PmsSkuInfo pmsSkuInfo);

    /**
     * 通过id查询所有sku信息
     * @param skuId
     * @return
     */
    PmsSkuInfo selSkuInfoById(String skuId);

    /**
     * 查询页面显示商品sku信息
     * @param skuId
     * @param spuId
     * @return
     */
    List<PmsProductSaleAttr> spuSaleAttrListCheckBySku(String skuId, String spuId);

    /**
     * 通过skuId查询出同组的sku信息
     * @param spuId
     * @return
     */
    List<PmsSkuInfo> selSkuSaleValueListBySpu(String spuId);

    /**
     * 查询所有商品Sku信息，用户存放es中
     * @return
     */
    List<PmsSkuInfo> selAllSku();

    /**
     * 校验价格
     * @return
     */
    boolean checkPrice(String skuId, BigDecimal price);


}
