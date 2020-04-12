package com.gmall.service;

import com.gmall.bean.PaymentInfo;
import tk.mybatis.mapper.common.Mapper;

public interface PaymentService {

    /**
     * 新增支付信息
     * @param paymentInfo
     */
    void insPaymentInfo(PaymentInfo paymentInfo);

    /**
     * 更新支付信息
     * @param paymentInfo
     */
    void updPaymentInfo(PaymentInfo paymentInfo);

    /**
     * 发送延迟队列消息，确定用户支付情况
     * @param orderSn
     */
    void sendDelayPaymentResult(String orderSn);

}
