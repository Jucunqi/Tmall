package com.gmall.search.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.gmall.annotations.LoginRequired;
import com.gmall.bean.*;
import com.gmall.service.AttrService;
import com.gmall.service.SearchService;
import org.apache.commons.collections.map.PredicatedSortedMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.*;

@Controller
public class IndexController {

    @Reference
    private SearchService searchService;
    @Reference
    private AttrService attrService;

    /**
     * 跳转到搜索结果页面
     * @param pmsSearchParam
     * @param model
     * @return
     */
    @RequestMapping("/list.html")
    public String list(PmsSearchParam pmsSearchParam, Model model){

        List<PmsSearchSkuInfo> pmsSearchSkuInfos = searchService.selPmsSearchInfo(pmsSearchParam);

        //定义Set接收去重的SkuValueId
        Set<String> skuValueIdSet = new HashSet<>();
        for (PmsSearchSkuInfo pmsSearchSkuInfo : pmsSearchSkuInfos) {
            List<PmsSkuAttrValue> skuAttrValueList = pmsSearchSkuInfo.getSkuAttrValueList();
            for (PmsSkuAttrValue pmsSkuAttrValue : skuAttrValueList) {
                skuValueIdSet.add(pmsSkuAttrValue.getValueId());
            }
        }

        //通过skuValueIdSet查询出所有分类信息
        String valuAttrIds = StringUtils.join(skuValueIdSet, ",");
        System.out.println("组装字符串"+valuAttrIds);
        List<PmsBaseAttrInfo> attrList = attrService.getAttrList(valuAttrIds);
        model.addAttribute("attrList", attrList);

        model.addAttribute("skuLsInfoList", pmsSearchSkuInfos);

        //实现点击分类信息删除所在属性组的功能
        String[] valueId = pmsSearchParam.getValueId();
        //定义面包屑集合
        List<PmsSearchCrumb> pmsSearchCrumbs = new ArrayList<>();
        if(valueId!=null&&!valueId.equals("")){
            for (String id : valueId) {
                //创建面包屑对象
                PmsSearchCrumb pmsSearchCrumb = new PmsSearchCrumb();
                pmsSearchCrumb.setValueId(id);
                pmsSearchCrumb.setUrlParam(getUrlParam4Del(pmsSearchParam,id));
                //迭代遍历删除属性列表
                Iterator<PmsBaseAttrInfo> iterator = attrList.iterator();
                while (iterator.hasNext()) {
                    //得到需要遍历的容器对象
                    PmsBaseAttrInfo pmsBaseAttrInfo = iterator.next();
                    List<PmsBaseAttrValue> attrValueList = pmsBaseAttrInfo.getAttrValueList();
                    for (PmsBaseAttrValue attrValue : attrValueList) {

                            if(id.equals(attrValue.getId())){
                                pmsSearchCrumb.setValueName(attrValue.getValueName());
                                iterator.remove();
                            }
                        }
                    }
                pmsSearchCrumbs.add(pmsSearchCrumb);
            }
        }

        //用于页面面包屑和筛选条件url
        String urlParam = getUrlParam(pmsSearchParam);
        model.addAttribute("urlParam",urlParam);

        //将面包屑集合传递给页面
        model.addAttribute("attrValueSelectedList", pmsSearchCrumbs);

        //传递搜索参数
        String keyword = pmsSearchParam.getKeyword();
        if(keyword!=null&&!keyword.equals("")){

            model.addAttribute("keyword", keyword);
        }
        return "list";
    }


    private String getUrlParam4Del(PmsSearchParam pmsSearchParam,String delValueId){
        String urlParam = "";
        String keyword = pmsSearchParam.getKeyword();
        if(keyword!=null&&!keyword.equals("")){
            if (!urlParam.equals("")) {
                urlParam = urlParam+"&"+"keyword="+keyword;
            }
            urlParam = urlParam+"keyword="+keyword;
        }
        String catalog3Id = pmsSearchParam.getCatalog3Id();
        if (catalog3Id!=null&&!catalog3Id.equals("")) {
            if (!urlParam.equals("")) {
                urlParam = urlParam+"&"+"catalog3Id="+catalog3Id;
            }
            urlParam = urlParam+"catalog3Id="+catalog3Id;
        }

        String[] valueId = pmsSearchParam.getValueId();
        if (valueId!=null) {

            for (String s : valueId) {

                    if (!delValueId.equals(s)) {
                         urlParam = urlParam + "&" + "valueId=" + s;
                    }

            }
        }

        return urlParam;

    }

    private String getUrlParam(PmsSearchParam pmsSearchParam) {

        String urlParam = "";
        String keyword = pmsSearchParam.getKeyword();
        if(keyword!=null&&!keyword.equals("")){
            if (!urlParam.equals("")) {
                urlParam = urlParam+"&"+"keyword="+keyword;
            }
            urlParam = urlParam+"keyword="+keyword;
        }
        String catalog3Id = pmsSearchParam.getCatalog3Id();
        if (catalog3Id!=null&&!catalog3Id.equals("")) {
            if (!urlParam.equals("")) {
                urlParam = urlParam+"&"+"catalog3Id="+catalog3Id;
            }
            urlParam = urlParam+"catalog3Id="+catalog3Id;
        }

        String[] valueId = pmsSearchParam.getValueId();
        if (valueId!=null) {

            for (String s : valueId) {
                urlParam = urlParam + "&" + "valueId=" + s;
            }
        }

        return urlParam;
    }

    /**
     * 显示门面
     * @return
     */
    @LoginRequired(mustSuccess = false)
    @RequestMapping("/index")
    public String show(){
        return "index";
    }
}
