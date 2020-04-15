package com.gmall.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.gmall.bean.OmsCartItem;
import com.gmall.cart.mapper.OmsCartItemMapper;
import com.gmall.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisAccessor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.*;


@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private OmsCartItemMapper omsCartItemMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 判断购物车中是否已存在该商品
     * @param productSkuId
     * @param memberId
     * @return
     */
    @Override
    public OmsCartItem existItem(String productSkuId, String memberId) {

        OmsCartItem omsCartItem = new OmsCartItem();
        omsCartItem.setProductSkuId(productSkuId);
        omsCartItem.setMemberId(memberId);
        return omsCartItemMapper.selectOne(omsCartItem);

    }
    /**
     * 修改购物车中商品信息
     * @param omsCartItem
     * @return
     */
    @Override
    public int updCartItem(OmsCartItem omsCartItem) {

        Example example = new Example(OmsCartItem.class);
        example.createCriteria().andEqualTo("productSkuId", omsCartItem.getProductSkuId())
                .andEqualTo("memberId", omsCartItem.getMemberId());
        return omsCartItemMapper.updateByExampleSelective(omsCartItem, example);

    }
    /**
     * 新增购物车中商品信息
     * @param omsCartItem
     * @return
     */
    @Override
    public int insCartItem(OmsCartItem omsCartItem) {
        return omsCartItemMapper.insert(omsCartItem);

    }
    /**
     * 实现缓存同步
     * @param memberId
     */
    @Override
    public void cacheFlush(String memberId) {

        //查询出用户数据库购物车中所有信息
        Example example = new Example(OmsCartItem.class);
        example.createCriteria().andEqualTo("memberId",memberId);
        List<OmsCartItem> omsCartItems = omsCartItemMapper.selectByExample(example);

        //放入缓存中
        Map<String, String> cartMap = new HashMap<>();
        for (OmsCartItem cartItem : omsCartItems) {
            cartMap.put(cartItem.getProductSkuId(), JSON.toJSONString(cartItem));
        }

        redisTemplate.delete("user:" + memberId + ":cart");
        redisTemplate.opsForValue().set("user:"+memberId+":cart",cartMap);

    }

    /**
     * 从缓存中查询购物车信息
     * @param userId  用户id  也是memberId    
     * @return
     */
    @Override
    public List<OmsCartItem> selAllItemByUserId(String userId) {

        //创建返回值对象
        List<OmsCartItem> omsCartItems = new ArrayList<>();
        //从缓存中查询出map集合
//        String cacheJson = stringRedisTemplate.opsForValue().get("user:" + userId + ":cart");
//        Map<String,String> map = JSON.parseObject(cacheJson, Map.class);
//        Set<String> skuIds = map.keySet();
//        for (String skuId : skuIds) {
//            String omsCartItemStr = map.get(skuId);
//            //取出购物车信息
//            OmsCartItem omsCartItem = JSON.parseObject(omsCartItemStr, OmsCartItem.class);
//            omsCartItem.setTotalPrice(omsCartItem.getPrice().multiply(new BigDecimal(omsCartItem.getQuantity())));
//            System.out.println("缓存中取出的对象:"+omsCartItem);
//            omsCartItems.add(omsCartItem);
//        }
        //从缓存中查询出map集合
        Map<String,String> map = (LinkedHashMap<String,String>) redisTemplate.opsForValue().get("user:" + userId + ":cart");
        Set<String> skuIds = map.keySet();
        for (String skuId : skuIds) {
            String cartItemStr = map.get(skuId);
            OmsCartItem omsCartItem = JSON.parseObject(cartItemStr, OmsCartItem.class);
            omsCartItem.setTotalPrice(omsCartItem.getPrice().multiply(new BigDecimal(omsCartItem.getQuantity())));
            omsCartItems.add(omsCartItem);
        }
        //返回数据
        return omsCartItems;
    }

    @Override
    public void delCartInfo(String memberId) {

        Example example = new Example(OmsCartItem.class);
        example.createCriteria().andEqualTo("memberId", memberId);
        omsCartItemMapper.deleteByExample(example);
    }
}
