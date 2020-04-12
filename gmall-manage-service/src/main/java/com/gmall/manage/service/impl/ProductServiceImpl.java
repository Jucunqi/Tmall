package com.gmall.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.gmall.bean.*;
import com.gmall.manage.mapper.*;
import com.gmall.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private PmsProductInfoMapper pmsProductInfoMapper;
    @Autowired
    private PmsBaseSaleAttrMapper pmsBaseSaleAttrMapper;
    @Autowired
    private PmsBaseAttrValueMapper pmsBaseAttrValueMapper;
    @Autowired
    private PmsProductSaleAttrMapper pmsProductSaleAttrMapper;
    @Autowired
    private PmsProductSaleAttrValueMapper pmsProductSaleAttrValueMapper;
    @Autowired
    private PmsProductImageMapper pmsProductImageMapper;


    /**
     * 根据商品类目id查询商品Spu信息
     * @param catalog3Id
     * @return
     */
    @Override
    public List<PmsProductInfo> selSpuByCatalog3Id(String catalog3Id) {

        Example example = new Example(PmsProductInfo.class);
        example.createCriteria().andEqualTo("catalog3Id", catalog3Id);
        return pmsProductInfoMapper.selectByExample(example);
    }

    /**
     * 查询所有基本属性
     * @return
     */
    @Override
    public List<PmsBaseSaleAttr> selAllBaseAttr() {
        return pmsBaseSaleAttrMapper.selectAll();
    }


    /**
     * 新增商品信息
     * @param pmsProductInfo
     * @return
     */
    @Override
    public String insProduct(PmsProductInfo pmsProductInfo) {

        //新增info表
        pmsProductInfoMapper.insertSelective(pmsProductInfo);
        String id = pmsProductInfo.getId();
        System.out.println("新增商品的id为："+id);

        //新增图片信息
        List<PmsProductImage> spuImageList = pmsProductInfo.getSpuImageList();
        for (PmsProductImage pmsProductImage : spuImageList) {
            pmsProductImage.setProductId(id);
            pmsProductImageMapper.insert(pmsProductImage);
        }

        //新增attr表
        List<PmsProductSaleAttr> spuSaleAttrList = pmsProductInfo.getSpuSaleAttrList();
        for (PmsProductSaleAttr pmsProductSaleAttr : spuSaleAttrList) {
            pmsProductSaleAttr.setProductId(id);
            pmsProductSaleAttrMapper.insertSelective(pmsProductSaleAttr);

            //新增attrValue表
            List<PmsProductSaleAttrValue> spuSaleAttrValueList = pmsProductSaleAttr.getSpuSaleAttrValueList();
            for (PmsProductSaleAttrValue pmsProductSaleAttrValue : spuSaleAttrValueList) {
                pmsProductSaleAttrValue.setProductId(id);
                pmsProductSaleAttrValueMapper.insertSelective(pmsProductSaleAttrValue);
            }

        }

        return "success";
    }

    /**
     * 根据商品id查询商品属性列表
     * @param productId
     * @return
     */
    @Override
    public List<PmsProductSaleAttr> selProductAttrByProductId(String productId) {
        //根据商品id查询出所有商品属性列表
        Example example = new Example(PmsProductSaleAttr.class);
        example.createCriteria().andEqualTo("productId", productId);
        List<PmsProductSaleAttr> pmsProductSaleAttrs = pmsProductSaleAttrMapper.selectByExample(example);

        //遍历属性列表，给属性赋值
        for (PmsProductSaleAttr pmsProductSaleAttr : pmsProductSaleAttrs) {
            Example example1 = new Example(PmsProductSaleAttrValue.class);
            example1.createCriteria().andEqualTo("saleAttrId", pmsProductSaleAttr.getSaleAttrId())
                                        .andEqualTo("productId",productId);

            //将查询到的属性值，赋给属性列表
            List<PmsProductSaleAttrValue> pmsProductSaleAttrValues = pmsProductSaleAttrValueMapper.selectByExample(example1);
            pmsProductSaleAttr.setSpuSaleAttrValueList(pmsProductSaleAttrValues);
        }
        return pmsProductSaleAttrs;
    }

    /**
     * 根据商品id查询商品图片集合
     * @param productId
     * @return
     */
    @Override
    public List<PmsProductImage> selImagesByProductId(String productId) {
        Example example = new Example(PmsProductImage.class);
        example.createCriteria().andEqualTo("productId", productId);
        return pmsProductImageMapper.selectByExample(example);
    }


}
