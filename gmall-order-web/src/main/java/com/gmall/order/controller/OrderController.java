package com.gmall.order.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.gmall.annotations.LoginRequired;
import com.gmall.bean.OmsCartItem;
import com.gmall.bean.OmsOrder;
import com.gmall.bean.OmsOrderItem;
import com.gmall.bean.UmsMemberReceiveAddress;
import com.gmall.service.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.jws.WebParam;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class OrderController {

    @Reference
    private UmsMemberReceiveAddressService umsMemberReceiveAddressService;
    @Reference
    private CartService cartService;
    @Reference
    private OrderService orderService;
    @Reference
    private SkuService skuService;



    @LoginRequired(mustSuccess = true)
    @RequestMapping("toTrade")
    public String toTrade(HttpServletRequest request, Model model) {
        String memberId = (String) request.getAttribute("memberId");
        String nickname = (String) request.getAttribute("nickname");

        //用户收件地址集合
        List<UmsMemberReceiveAddress> umsMemberReceiveAddresses = umsMemberReceiveAddressService.selByMid(memberId);

        //根据用户memberId查询出购物车集合
        List<OmsCartItem> omsCartItems = cartService.selAllItemByUserId(memberId);

        //创建订单列表集合
        List<OmsOrderItem> omsOrderItems = new ArrayList<>();

        //将购物车中的数据整合到等单列表中
        for (OmsCartItem omsCartItem : omsCartItems) {
            OmsOrderItem omsOrderItem = new OmsOrderItem();
            omsOrderItem.setProductPic(omsCartItem.getProductPic());
            omsOrderItem.setProductName(omsCartItem.getProductName());
            omsOrderItem.setProductQuantity(omsCartItem.getQuantity());
            omsOrderItem.setProductPrice(omsCartItem.getTotalPrice());
            omsOrderItems.add(omsOrderItem);
        }

        model.addAttribute("omsOrderItems", omsOrderItems);
        model.addAttribute("userAddressList", umsMemberReceiveAddresses);

        //计算应支付的总额
        BigDecimal totalAmount = getTotalAmount(omsCartItems);
        model.addAttribute("totalAmount",totalAmount);

        //创建一个唯一的订单校验码，并传递给页面
        String tradeCode = orderService.genTradeCode(memberId);
        model.addAttribute("tradeCode", tradeCode);

        return "trade";
    }

    @RequestMapping("submitOrder")
    @LoginRequired(mustSuccess = true)
    public ModelAndView submitOrder(String addrId, BigDecimal totalAmount, String tradeCode, HttpServletRequest request) {
        //从请求域中获取用户的id和name
        String memberId = (String) request.getAttribute("memberId");
        String nickname = (String) request.getAttribute("nickname");
        //检查用户写单的订单校验码，只有验证成功才可执行后续操作
        String success = orderService.checkTradeCode(memberId, tradeCode);
        if (StringUtils.isNotBlank(success) && success.equals("success")) {

            //创建订单对象
            OmsOrder omsOrder = new OmsOrder();
            omsOrder.setAutoConfirmDay(7);
            omsOrder.setConfirmStatus(0);
//            omsOrder.setCouponId();
//            omsOrder.setCouponAmount(null);   优惠金额
            omsOrder.setCreateTime(new Date());
            omsOrder.setDeleteStatus(0);
            omsOrder.setDiscountAmount(null);
            omsOrder.setFreightAmount(null);  //运费金额
            omsOrder.setMemberId(memberId);
            omsOrder.setMemberUsername(nickname);
//            omsOrder.setModifyTime();     修改时间
            omsOrder.setNote("快点发货");   //备注，不知道为什么老师直接写死了
            //创建订单号
            String orderSn = "gmall" + System.currentTimeMillis();
            omsOrder.setOrderSn(orderSn);      //订单号
            omsOrder.setOrderType(0);       //0->正常订单；1->秒杀订单',
            omsOrder.setPayAmount(totalAmount);
//            omsOrder.setPaymentTime();    //支付时间
//            omsOrder.setPayType();        //支付类型
            //查询用户收货地址信息
            UmsMemberReceiveAddress umsMemberReceiveAddress = umsMemberReceiveAddressService.selAddressInfoByAddressId(addrId);
            omsOrder.setReceiverCity(umsMemberReceiveAddress.getCity());
            omsOrder.setReceiverDetailAddress(umsMemberReceiveAddress.getDetailAddress());
            omsOrder.setReceiverName(umsMemberReceiveAddress.getName());
            omsOrder.setReceiverPhone(umsMemberReceiveAddress.getPhoneNumber());
            omsOrder.setReceiverPostCode(umsMemberReceiveAddress.getPostCode());
            omsOrder.setReceiverProvince(umsMemberReceiveAddress.getProvince());
            omsOrder.setReceiverRegion(umsMemberReceiveAddress.getRegion());
//            omsOrder.setSourceType();     //订单来源、
            omsOrder.setStatus(0);          //订单状态

            //创建订单详情集合
            List<OmsOrderItem> omsOrderItems = new ArrayList<>();

            //从购物车中查询出需要结算的列表
            List<OmsCartItem> omsCartItems = cartService.selAllItemByUserId(memberId);
            for (OmsCartItem omsCartItem : omsCartItems) {
                if (omsCartItem.getIsChecked().equals("1")) {
                    //验价
//                    boolean b = skuService.checkPrice(omsCartItem.getProductSkuId(), omsCartItem.getPrice());
                    //如果验价成功则进行后续操作，但是我为了少开一个服务，没有开启此功能

                    //创建订单详情对象
                    OmsOrderItem omsOrderItem = new OmsOrderItem();
                    omsOrderItem.setProductName(omsCartItem.getProductName());
                    omsOrderItem.setProductPrice(omsCartItem.getPrice());
                    omsOrderItem.setProductPic(omsCartItem.getProductPic());
//                    omsOrderItem.setCouponAmount(null);   优惠券分解价格

                    omsOrderItem.setOrderSn(orderSn);
                    omsOrderItem.setProductCategoryId(omsCartItem.getProductCategoryId());
                    omsOrderItem.setProductQuantity(omsCartItem.getQuantity());
                    omsOrderItem.setProductSkuId(omsCartItem.getProductSkuId());
                    omsOrderItem.setRealAmount(omsCartItem.getPrice());
                    omsOrderItem.setProductId(omsCartItem.getProductId());
                    omsOrderItem.setProductSkuCode("1111111111");
                    omsOrderItem.setProductSn("商品的仓库编号");
                    omsOrderItems.add(omsOrderItem);
                }

            }

            omsOrder.setOmsOrderItems(omsOrderItems);
            //将订单数据写入数据库
            omsOrder = orderService.insOrder(omsOrder);

            //删除购物车中的商品,为了以后测试还要向购物车中添加数据，暂时先不删除了
//            cartService.delCartInfo(memberId);
            //重定向到支付系统,并传递参数
            ModelAndView mv = new ModelAndView("redirect:http://192.168.1.6:8087/index");
            mv.addObject("outOrderSn", orderSn);
            mv.addObject("totalAmount", totalAmount);
            mv.addObject("orderId", omsOrder.getId());
            return mv;
        } else {
            //用户交易码校验失败
            //跳转到支付失败页面
            ModelAndView mv = new ModelAndView("redirect:tradeFail.html");
            return mv;
        }

    }



    //工具类，但是因为懒没有提取出来
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



}
