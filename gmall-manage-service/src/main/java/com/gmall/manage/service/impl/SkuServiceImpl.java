package com.gmall.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.gmall.bean.*;
import com.gmall.manage.mapper.*;
import com.gmall.service.SkuService;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.io.PipedInputStream;
import java.io.PipedReader;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class SkuServiceImpl implements SkuService {

    @Autowired
    private PmsSkuInfoMapper pmsSkuInfoMapper;
    @Autowired
    private PmsSkuAttrValueMapper pmsSkuAttrValueMapper;
    @Autowired
    private PmsSkuImageMapper pmsSkuImageMapper;
    @Autowired
    private PmsSkuSaleAttrValueMapper pmsSkuSaleAttrValueMapper;
    @Autowired
    private PmsProductSaleAttrMapper pmsProductSaleAttrMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 新增商品sku信息
     * @param pmsSkuInfo
     * @return
     */
    @Transactional
    @Override
    public String insPmsSkuInfo(PmsSkuInfo pmsSkuInfo) {
        //新增info表信息
        pmsSkuInfoMapper.insert(pmsSkuInfo);

        //新增pms_sku_attr_valu表信息
        List<PmsSkuAttrValue> pmsSkuAttrValueList = pmsSkuInfo.getSkuAttrValueList();
        for (PmsSkuAttrValue pmsSkuAttrValue : pmsSkuAttrValueList) {
            pmsSkuAttrValue.setSkuId(pmsSkuInfo.getId());
            pmsSkuAttrValueMapper.insert(pmsSkuAttrValue);


        }

        //新增skuImage表
        List<PmsSkuImage> pmsSkuImageList = pmsSkuInfo.getSkuImageList();
        for (PmsSkuImage pmsSkuImage : pmsSkuImageList) {
            pmsSkuImage.setSkuId(pmsSkuInfo.getId());
            pmsSkuImageMapper.insert(pmsSkuImage);
        }

        //新增pms_sku_sale_attr_value
        List<PmsSkuSaleAttrValue> pmsSkuSaleAttrValueList = pmsSkuInfo.getSkuSaleAttrValueList();
        for (PmsSkuSaleAttrValue pmsSkuSaleAttrValue : pmsSkuSaleAttrValueList) {
            pmsSkuSaleAttrValue.setSkuId(pmsSkuInfo.getId());
            pmsSkuSaleAttrValueMapper.insert(pmsSkuSaleAttrValue);
        }
        return "success";
    }

    /**
     * 通过id查询商品sku信息，通过缓存
     * @param skuId
     * @return
     */
    @Override
    public PmsSkuInfo selSkuInfoById(String skuId) {

        //创建缓存对象的key   格式为"sku:skuid:info"
        String key = "sku:" + skuId + ":info";
        //先到缓存中查询对象是否存在
        String pmsSkuInfoStr =  stringRedisTemplate.opsForValue().get(key);
        //将字符串转为对象
        PmsSkuInfo pmsSkuInfo = JSON.parseObject(pmsSkuInfoStr, PmsSkuInfo.class);
        if(pmsSkuInfo!=null&&!pmsSkuInfo.equals("")) {
            //如果对象存在直接返回
            return pmsSkuInfo;
        }else{
            //那nx分布式锁
            Boolean aBoolean = redisTemplate.opsForValue().setIfAbsent("lock", "lock", 10, TimeUnit.SECONDS);
            if(aBoolean){
                //如果拿到锁，到数据库中查询
                //如果对象不存在到数据库中查询
                PmsSkuInfo pmsSkuInfo1 = selSkuInfoByIdFormDb(skuId);
                if (pmsSkuInfo1 != null && !pmsSkuInfo1.equals("")) {
                    //将查询到的结果添加到缓存中
                    redisTemplate.opsForValue().set(key,pmsSkuInfo1);
                    //释放锁
                    redisTemplate.delete("lock");
                    return pmsSkuInfo1;
                }else{
                    //如果数据库中没有该数据，则给缓存中存放一个空数据，并且设置一个较短的有效时间
                    redisTemplate.opsForValue().set(key, null, 60, TimeUnit.SECONDS);
                    //释放锁
                    redisTemplate.delete("lock");
                }
            }else{
                //如果没拿到锁，自旋
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return selSkuInfoById(skuId);
            }


            }

        return null;
    }

    /**
     * 通过id查询商品sku信息,到数据库中查询
     * @param skuId
     * @return
     */
    public  PmsSkuInfo selSkuInfoByIdFormDb(String skuId) {

        //查询商品sku信息
        PmsSkuInfo pmsSkuInfo = pmsSkuInfoMapper.selectByPrimaryKey(skuId);
        //非空判断
        if(pmsSkuInfo==null||pmsSkuInfo.equals("")){
            return null;
        }
        //查询商品sku图片信息
        Example example = new Example(PmsSkuImage.class);
        example.createCriteria().andEqualTo("skuId", skuId);
        List<PmsSkuImage> pmsSkuImages = pmsSkuImageMapper.selectByExample(example);
        //非空判断
        if(pmsSkuImages!=null&&pmsSkuImages.size()>0){

            pmsSkuInfo.setSkuImageList(pmsSkuImages);
            pmsSkuInfo.setSkuDefaultImg(pmsSkuImages.get(0).getImgUrl());
            return pmsSkuInfo;
        }else{
            System.out.println("没有找到相应数据");
            return null;
        }
    }
    /**
     * 查询所有sku信息
     * @param skuId
     * @param spuId
     * @return
     */
    @Override
    public List<PmsProductSaleAttr> spuSaleAttrListCheckBySku(String skuId, String spuId) {
        List<PmsProductSaleAttr> pmsProductSaleAttrs = pmsProductSaleAttrMapper.spuSaleAttrListCheckBySku(skuId, spuId);

        return pmsProductSaleAttrs;
    }

    /**
     * 通过spuId查询出同组的sku信息
     * @param spuId
     * @return
     */
    @Override
    public List<PmsSkuInfo> selSkuSaleValueListBySpu(String spuId) {

        List<PmsSkuInfo> pmsSkuInfos = pmsSkuInfoMapper.selSkuSaleValueListBySpu(spuId);
        return pmsSkuInfos;
    }

    /**
     * 查询所有sku，存放于es中
     * @return
     */
    @Override
    public List<PmsSkuInfo> selAllSku() {

        List<PmsSkuInfo> pmsSkuInfos = pmsSkuInfoMapper.selectAll();
        for (PmsSkuInfo pmsSkuInfo : pmsSkuInfos) {
            String id = pmsSkuInfo.getId();
            Example example = new Example(PmsSkuAttrValue.class);
            example.createCriteria().andEqualTo("skuId", id);
            List<PmsSkuAttrValue> pmsSkuAttrValues = pmsSkuAttrValueMapper.selectByExample(example);
            pmsSkuInfo.setSkuAttrValueList(pmsSkuAttrValues);
        }
        return pmsSkuInfos;
    }

    /**
     * 校验价格
     * @param skuId
     * @param price
     * @return
     */
    @Override
    public boolean checkPrice(String skuId, BigDecimal price) {
        boolean flag = false;

        //通过skuId查询出商品对象  获得sku表中商品价格
        Example example = new Example(PmsSkuInfo.class);
        example.createCriteria().andEqualTo("id", skuId);
        PmsSkuInfo pmsSkuInfo = pmsSkuInfoMapper.selectOneByExample(example);
        BigDecimal price1 = pmsSkuInfo.getPrice();
        //Bigdecimal的比较方法，如果相同返回0
        if (price.compareTo(price) == 0) {
            flag = true;
        }
        return flag;
    }
}
