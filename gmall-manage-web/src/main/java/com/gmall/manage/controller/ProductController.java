package com.gmall.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.gmall.bean.PmsBaseSaleAttr;
import com.gmall.bean.PmsProductImage;
import com.gmall.bean.PmsProductInfo;
import com.gmall.bean.PmsProductSaleAttr;
import com.gmall.service.ProductService;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@CrossOrigin
@Controller
public class ProductController {

    @Reference
    private ProductService productService;
    //注入FastDFS客户端
    @Autowired
    private FastFileStorageClient storageClient;
    /**
     * 根据商品类目id查询商品Spu信息
     *
     * @param catalog3Id
     * @return
     */
    @RequestMapping("spuList")
    @ResponseBody
    public List<PmsProductInfo> spuList(String catalog3Id) {
        return productService.selSpuByCatalog3Id(catalog3Id);
    }


    /**
     * 查询所有基本属性
     *
     * @return
     */
    @RequestMapping("baseSaleAttrList")
    @ResponseBody
    public List<PmsBaseSaleAttr> baseSaleAttrList() {
        return productService.selAllBaseAttr();
    }

    /**
     * 新增商品Spu信息
     *
     * @return
     */
    @RequestMapping("saveSpuInfo")
    @ResponseBody
    public String saveSpuInfo(@RequestBody PmsProductInfo pmsProductInfo) {
        productService.insProduct(pmsProductInfo);
        return "success";
    }

    /**
     * 测试文件上传
     * @param file
     * @return
     */
    @RequestMapping("/fileUpload")
    @ResponseBody
    public String fileUpload(MultipartFile file) throws IOException {
        StorePath storePath = storageClient.uploadFile(file.getInputStream(), file.getSize(), FilenameUtils.getExtension(file.getOriginalFilename()), null);
        System.out.println(storePath);
        String fileUrl = "http://192.168.91.129/"+storePath.getFullPath();
        return fileUrl;

    }

    /**
     * 根据商品id查询商品属性值
     * @param productId
     * @return
     */
    @RequestMapping("spuSaleAttrList")
    @ResponseBody
    public List<PmsProductSaleAttr> spuSaleAttrList(@RequestParam("spuId") String productId){
        return productService.selProductAttrByProductId(productId);
    }

    /**
     * 根据商品id查询所有商品图片集合
     * @param productId
     * @return
     */
    @RequestMapping("spuImageList")
    @ResponseBody
    public List<PmsProductImage> spuImageList(@RequestParam("spuId") String productId) {
        return productService.selImagesByProductId(productId);
    }

}
