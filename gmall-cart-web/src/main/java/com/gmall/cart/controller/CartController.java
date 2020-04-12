package com.gmall.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.gmall.annotations.LoginRequired;
import com.gmall.bean.OmsCartItem;
import com.gmall.bean.PmsSkuInfo;
import com.gmall.service.CartService;
import com.gmall.service.SkuService;
import com.gmall.util.CookieUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.persistence.Id;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class CartController {

    @Reference
    private SkuService skuService;
    @Reference
    private CartService cartService;


    @LoginRequired(mustSuccess = true)
    @RequestMapping("checkCart")
    public String checkCart(String skuId,String isChecked,Model model,HttpServletRequest request) {

        String memberId = (String) request.getAttribute("memberId");
        String nickname = (String) request.getAttribute("nickname");
        OmsCartItem omsCartItem = new OmsCartItem();
        omsCartItem.setMemberId(memberId);
        omsCartItem.setProductSkuId(skuId);
        omsCartItem.setIsChecked(isChecked);

        //修改数据库中商品选中属性
        cartService.updCartItem(omsCartItem);

        //更新缓存
        cartService.cacheFlush(memberId);

        //将最新的数据从缓存中查出
        List<OmsCartItem> omsCartItems = cartService.selAllItemByUserId(memberId);
        model.addAttribute("cartList", omsCartItems);
        //将被选中的需要结算的总额传递给页面
        BigDecimal totalAmount = getTotalAmount(omsCartItems);
        model.addAttribute("totalAmount",totalAmount);
        return "cartListInner";
    }

    @LoginRequired(mustSuccess = true)
    @RequestMapping("cartList")
    public String cartList(HttpServletRequest request, Model model){
        String userId = (String) request.getAttribute("memberId");
        System.out.println("userid为："+userId);
        String nickname = (String) request.getAttribute("nickname");

        List<OmsCartItem> omsCartItems = new ArrayList<>();
        //判断用户是否登录
        if (!StringUtils.isBlank(userId)) {
            //已经登录查询缓存中的购物车信息
            omsCartItems = cartService.selAllItemByUserId(userId);
        } else {
            //未登录查询Cookie中的数据
            String cartListCookie = CookieUtil.getCookieValue(request, "cartListCookie", true);
            omsCartItems = JSON.parseArray(cartListCookie, OmsCartItem.class);
        }

        model.addAttribute("cartList", omsCartItems);
        //将被选中的需要结算的总额传递给页面
        BigDecimal totalAmount = getTotalAmount(omsCartItems);
        model.addAttribute("totalAmount",totalAmount);
        return "cartList";
    }

    //计算出以选中的需要结算的最终价格
    public BigDecimal getTotalAmount(List<OmsCartItem> omsCartItems){

        BigDecimal bigDecimal = new BigDecimal("0");
        for (OmsCartItem omsCartItem : omsCartItems) {
            if(omsCartItem.getIsChecked().equals("1")){

                bigDecimal = bigDecimal.add(omsCartItem.getTotalPrice());
            }
        }
        return bigDecimal;
    }


    @LoginRequired(mustSuccess = true)
    @RequestMapping("addToCart")
    public String addToCart(String skuId, Integer quantity, HttpServletRequest request, HttpServletResponse response) {

        //根据skuId查询商品Sku信息
        PmsSkuInfo pmsSkuInfo = skuService.selSkuInfoById(skuId);

        //创建购物车信息对象
        OmsCartItem omsCartItem = new OmsCartItem();
        omsCartItem.setQuantity(quantity);
        omsCartItem.setCreateDate(new Date());
        omsCartItem.setPrice(pmsSkuInfo.getPrice());
        omsCartItem.setProductCategoryId(pmsSkuInfo.getCatalog3Id());
        omsCartItem.setProductName(pmsSkuInfo.getSkuName());
        omsCartItem.setProductSkuId(pmsSkuInfo.getId());
        omsCartItem.setProductId(pmsSkuInfo.getSpuId());

        String memberId = (String) request.getAttribute("memberId");
        String nickname = (String) request.getAttribute("nickname");
        //判断用户是否登录
        if (StringUtils.isBlank(memberId)) {
            //如果用户未登录
            //获取Cookie中数据并判断
            String cartListCookie = CookieUtil.getCookieValue(request, "cartListCookie", true);
            if (!StringUtils.isBlank(cartListCookie)) {
                //如果Cookie中有数据
                List<OmsCartItem> omsCartItems = JSON.parseArray(cartListCookie, OmsCartItem.class);
                //判断是否已经有相同数据
//            boolean exist = ifExist(omsCartItems, omsCartItem);
                for (OmsCartItem cartItem : omsCartItems) {

                    if (cartItem.getProductSkuId().equals(omsCartItem.getProductSkuId())) {
                        //如果有相同数据,新增商品数量
                        cartItem.setQuantity(cartItem.getQuantity() + omsCartItem.getQuantity());
                        //覆盖Cookie
                        CookieUtil.setCookie(request, response, "cartListCookie", JSON.toJSONString(omsCartItems), 60 * 60 * 24, true);
                        return "redirect:/success.html";
                    }
                }
                //如果没有相同数据，新增商品
                omsCartItems.add(omsCartItem);
                //覆盖Cookie
                CookieUtil.setCookie(request, response, "cartListCookie", JSON.toJSONString(omsCartItems), 60 * 60 * 24, true);
            } else {
                //如果Cookie中没有数据,直接向Cookie中添加商品
                List<OmsCartItem> omsCartItems = new ArrayList<>();
                omsCartItems.add(omsCartItem);
                CookieUtil.setCookie(request, response, "cartListCookie", JSON.toJSONString(omsCartItems), 60 * 60 * 24, true);
            }
        } else {

            //如果用户已登录
                //查看购物车中是否已经有该商品
                OmsCartItem omsCartItemFromDB = cartService.existItem(omsCartItem.getProductSkuId(), memberId);
                if (omsCartItemFromDB != null) {
                    //如果已经有，更改商品数量
                    omsCartItemFromDB.setQuantity(omsCartItemFromDB.getQuantity() + quantity);
                    cartService.updCartItem(omsCartItemFromDB);
                } else {
                //如果没有，新增数据库信息
                omsCartItem.setMemberId(memberId);
                omsCartItem.setIsChecked("1");
                System.out.println(omsCartItem);
                cartService.insCartItem(omsCartItem);
            }
            //同步缓存
            cartService.cacheFlush(memberId);

        }

        return "redirect:/success.html";
    }







    @LoginRequired(mustSuccess = false)
    @RequestMapping("hello")
    public String eee(HttpServletRequest request) {

        Object memberId = request.getAttribute("memberId");
        if (memberId != null && !memberId.equals("")) {
            String id = memberId.toString();
            System.out.println("id为："+id);
        }

        Object nickName = request.getAttribute("nickName");
        if (nickName != null && !nickName.equals("")) {
            String name = nickName.toString();
            System.out.println("name为："+name);
        }


        return "redirect:/success.html";
    }


//    /**
//     * 判断Cookie中的购物车是否包含即将提交的商品
//     * @param omsCartItems
//     * @param omsCartItem
//     * @return
//     */
//    private boolean ifExist(List<OmsCartItem> omsCartItems, OmsCartItem omsCartItem) {
//
//        for (OmsCartItem cartItem : omsCartItems) {
//            if(cartItem.getProductSkuId().equals(omsCartItem.getProductSkuId())){
//                return true;
//            }
//        }
//        return false;
//    }
}
