package com.gmall.payment;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayRequest;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.gmall.annotations.LoginRequired;
import com.gmall.bean.PaymentInfo;
import com.gmall.payment.config.AlipayConfig;
import com.gmall.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.rmi.MarshalledObject;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
public class PaymentController {

    @Autowired
    private AlipayClient alipayClient;
    @Reference
    private PaymentService paymentService;


    @LoginRequired(mustSuccess = true)
    @RequestMapping("index")
    public String index(String orderId,String outOrderSn, BigDecimal totalAmount, HttpServletRequest request, Model model) {

        String memberId = (String) request.getAttribute("memberId");
        String nickname = (String) request.getAttribute("nickname");

        //将数据传递给前端页面
        model.addAttribute("outOrderSn", outOrderSn);
        model.addAttribute("totalAmount", totalAmount);
        model.addAttribute("nickname", nickname);
        model.addAttribute("orderId", orderId);
        return "index";
    }

    @LoginRequired(mustSuccess = true)
    @RequestMapping("alipay/submit")
    public String alipaySubmit(BigDecimal totalAmount,String outOrderSn,String orderId,HttpServletRequest request,Model model) {

        String nickname = (String) request.getAttribute("nickname");


        String subject = "琚存祺的商品标题";
        String company = "谷粒商城有限公司";

        //创建支付信息对象
        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setCreateTime(new Date());
        paymentInfo.setOrderId(orderId);
        paymentInfo.setOrderSn(outOrderSn);
        paymentInfo.setPaymentStatus("未支付");
        paymentInfo.setSubject(subject);
        paymentInfo.setTotalAmount(totalAmount);
        paymentService.insPaymentInfo(paymentInfo);

        model.addAttribute("nickname", nickname);
        model.addAttribute("outOrderSn", outOrderSn);
        model.addAttribute("totalAmount", totalAmount);
        model.addAttribute("company",company);
        model.addAttribute("subject", subject);

        //发送延迟队列消息到支付宝，主动确认用户支付情况
        paymentService.sendDelayPaymentResult(outOrderSn);
        return "payConfirm";



//        String form = null;
        //调用支付宝支付功能失败，被安全拦截
        //自己写一个意思一下把
        /*try {
            AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
            alipayRequest.setReturnUrl(AlipayConfig.return_payment_url);
            alipayRequest.setNotifyUrl(AlipayConfig.notify_payment_url);

            Map<String, Object> map = new HashMap<>();
            map.put("out_trade_no", outOrderSn);
            map.put("product_code", "FAST_INSTANT_TRADE_PAY");
            map.put("total_amount", totalAmount);
            map.put("subject", "琚存祺的标题");

            String param = JSON.toJSONString(map);
            alipayRequest.setBizContent(param);

            form = alipayClient.execute(alipayRequest).getBody();
            System.out.println(form);
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
*/
    }


    @LoginRequired(mustSuccess = true)
    @RequestMapping("alipay/paymentReturn")
    public String paymentReturn(String alipayTradeNo,String orderSn) {

        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setPaymentStatus("已支付");
        paymentInfo.setCallbackTime(new Date());
        paymentInfo.setOrderSn(orderSn);
        paymentInfo.setAlipayTradeNo(alipayTradeNo);

        paymentService.updPaymentInfo(paymentInfo);
        return "finish";
    }



}
