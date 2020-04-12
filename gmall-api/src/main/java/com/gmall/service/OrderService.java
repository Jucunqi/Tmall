package com.gmall.service;

import com.gmall.bean.OmsOrder;

/**
 * 订单业务接口
 */
public interface OrderService {

    /**
     * 根据用户id生成一个随机的交易码
     * @param memberId
     * @return
     */
    String genTradeCode(String memberId);

    /**
     * 校验用户的交易码
     * @param memberId
     * @param tradeCode
     * @return
     */
    String checkTradeCode(String memberId, String tradeCode);

    /**
     * 新增订单和订单详情信息
     * @param omsOrder
     */
    OmsOrder insOrder(OmsOrder omsOrder);

    /**
     * 接收消息，并修改订单状态
     * @return
     */
    void updOrderInfo(String orderSn);

}
