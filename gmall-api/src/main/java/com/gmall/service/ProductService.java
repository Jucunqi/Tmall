package com.gmall.service;

import com.gmall.bean.PmsBaseSaleAttr;
import com.gmall.bean.PmsProductImage;
import com.gmall.bean.PmsProductInfo;
import com.gmall.bean.PmsProductSaleAttr;

import java.util.List;

public interface ProductService {

    /**
     * 根据商品类目id查询Spu信息
     * @param catalog3Id
     * @return
     */
    List<PmsProductInfo> selSpuByCatalog3Id(String catalog3Id);

    /**
     * 查询所有基本属性信息
     * @return
     */
    List<PmsBaseSaleAttr> selAllBaseAttr();

    /**
     * 新增商品信息
     * @param pmsProductInfo
     * @return
     */
    String insProduct(PmsProductInfo pmsProductInfo);

    /**
     * 根据商品id查询商品属性列表
     * @param productId
     * @return
     */
    List<PmsProductSaleAttr> selProductAttrByProductId(String productId);

    /**
     * 根据商品id查询商品图片集合
     * @param productId
     * @return
     */
    List<PmsProductImage> selImagesByProductId(String productId);
}
