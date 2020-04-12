package com.gmall.item.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.gmall.bean.PmsProductSaleAttr;
import com.gmall.bean.PmsSkuAttrValue;
import com.gmall.bean.PmsSkuInfo;
import com.gmall.bean.PmsSkuSaleAttrValue;
import com.gmall.service.SkuService;
import net.minidev.json.JSONUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ItemControlller {

    @Reference
    private SkuService skuService;

    /**
     * 通过id查询sku信息
     * @param skuId
     * @param model
     * @return
     */
    @RequestMapping("{skuId}.html")
    public String showItem(@PathVariable String skuId, Model model) {
        //查出商品spu信息
        PmsSkuInfo pmsSkuInfo = skuService.selSkuInfoById(skuId);
        model.addAttribute("skuInfo", pmsSkuInfo);
        //查询商品sku信息及被选中的属性
        List<PmsProductSaleAttr> pmsProductSaleAttrs = skuService.spuSaleAttrListCheckBySku(skuId, pmsSkuInfo.getSpuId());
        model.addAttribute("spuSaleAttrListCheckBySku", pmsProductSaleAttrs);

        //查询商品skuId与属性id的组合Map
        List<PmsSkuInfo> pmsSkuInfos = skuService.selSkuSaleValueListBySpu(pmsSkuInfo.getSpuId());
        Map<String, String> skuInfoMap = new HashMap<>();
        //遍历skuId
        for (PmsSkuInfo skuInfo : pmsSkuInfos) {
            String v = skuInfo.getId();
            String k = "";
            //遍历saleAttrValueId
            List<PmsSkuSaleAttrValue> skuSaleAttrValueList = skuInfo.getSkuSaleAttrValueList();
            for (PmsSkuSaleAttrValue pmsSkuSaleAttrValue : skuSaleAttrValueList) {
                String saleAttrValueId = pmsSkuSaleAttrValue.getSaleAttrValueId();
                k += saleAttrValueId + "|";
            }

            //将结果放入到Map中
            skuInfoMap.put(k, v);
        }

        //将得到的map集合转化成json字符串发送给前端页面
        String skuMapJson = JSON.toJSONString(skuInfoMap);
        System.out.println(skuMapJson);
        model.addAttribute("skuMapJson", skuMapJson);
        return "item";
    }
}
