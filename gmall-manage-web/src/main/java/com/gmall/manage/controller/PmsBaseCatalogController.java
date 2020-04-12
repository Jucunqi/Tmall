package com.gmall.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.gmall.bean.PmsBaseCatalog1;
import com.gmall.bean.PmsBaseCatalog2;
import com.gmall.bean.PmsBaseCatalog3;
import com.gmall.service.PmsBaseCatalogService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@CrossOrigin   //开启跨域请求
@Controller
public class PmsBaseCatalogController {

    @Reference
    private PmsBaseCatalogService pmsBaseCatalogService;

    /**
     * 查询所有一级菜单
     * @return
     */
    @RequestMapping("getCatalog1")
    @ResponseBody
    public List<PmsBaseCatalog1> getCatalog1(){
        return pmsBaseCatalogService.selAllCataLog1();
    }

    /**
     * 根据一级菜单id查询所有二级菜单
     * @param catalog1Id
     * @return
     */
    @RequestMapping("getCatalog2")
    @ResponseBody
    public List<PmsBaseCatalog2> getCatalog2(String catalog1Id){
        return pmsBaseCatalogService.selAllCatalog2ByCatalog1Id(catalog1Id);
    }

    /**
     * 根据一级菜单id查询所有二级菜单
     * @param catalog2Id
     * @return
     */
    @RequestMapping("getCatalog3")
    @ResponseBody
    public List<PmsBaseCatalog3> getCatalog3(String catalog2Id){
        return pmsBaseCatalogService.selAllCatalog3ByCatalog2Id(catalog2Id);
    }

}
