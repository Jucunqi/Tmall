package com.gmall.service;

import com.gmall.bean.PmsBaseAttrInfo;
import com.gmall.bean.PmsBaseAttrValue;

import java.util.List;

/**
 * 处理商品属性信息的接口
 */
public interface AttrService {

    /**
     * 根据三级类目id查询商品属性信息
     *
     * @param catalog3Id
     * @return
     */
    List<PmsBaseAttrInfo> selAttrInfoByCatalog3Id(String catalog3Id);

    /**
     * 新增商品信息
     *
     * @param pmsBaseAttrInfo
     * @return
     */
    int insAttr(PmsBaseAttrInfo pmsBaseAttrInfo);

    /**
     * 通过商品属性id查询商品属性值
     *
     * @param attrId
     * @return
     */
    List<PmsBaseAttrValue> selAttrValueByAttrId(String attrId);

    /**
     * 通过attrValueId查询attr信息
     * @param valudId
     * @return
     */
    List<PmsBaseAttrInfo> getAttrList(String valudId);


}
