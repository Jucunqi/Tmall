package com.gmall.service;

import com.gmall.bean.OmsCartItem;

import java.util.List;

public interface CartService {

    /**
     * 判断购物车中是否已存在该商品
     * @param productSkuId
     * @param memberId
     * @return
     */
    OmsCartItem existItem(String productSkuId,String memberId);

    /**
     * 修改购物车中商品信息
     * @param omsCartItem
     * @return
     */
    int updCartItem(OmsCartItem omsCartItem);

    /**
     * 新增购物车中商品信息
     * @param omsCartItem
     * @return
     */
    int insCartItem(OmsCartItem omsCartItem);

    /**
     * 实现缓存同步
     * @param memberId
     */
    void cacheFlush(String memberId);


    /**
     * 缓存中查询用户购物车信息
     * @param userId  用户id  也是memberId    
     * @return
     */
    List<OmsCartItem> selAllItemByUserId(String userId);

    /**
     * 根据memberId删除用户购物车中的数据
     * @param memberId
     */
    void delCartInfo(String memberId);




}
