package com.gmall.order.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.gmall.bean.OmsOrder;
import com.gmall.bean.OmsOrderItem;
import com.gmall.order.mapper.OmsOrderItemMapper;
import com.gmall.order.mapper.OmsOrderMapper;
import com.gmall.service.OrderService;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import tk.mybatis.mapper.entity.Example;

import javax.jms.*;
import java.util.List;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private OmsOrderItemMapper omsOrderItemMapper;
    @Autowired
    private OmsOrderMapper omsOrderMapper;


    /**
     * 创建交易码
     *
     * @param memberId
     * @return
     */
    @Override
    public String genTradeCode(String memberId) {

        String tradeCode = UUID.randomUUID().toString();
        stringRedisTemplate.opsForValue().set("user:" + memberId + ":tradeCode", tradeCode);
        return tradeCode;
    }

    /**
     * 校验交易码
     *
     * @param memberId
     * @param tradeCode
     * @return
     */
    @Override
    public String checkTradeCode(String memberId, String tradeCode) {

        String tradeCodeFormCache = stringRedisTemplate.opsForValue().get("user:" + memberId + ":tradeCode");
        if (StringUtils.isNotBlank(tradeCodeFormCache) && tradeCodeFormCache.equals(tradeCode)) {

            //校验成功,并将缓存中的交易码删除
            stringRedisTemplate.delete("user:" + memberId + ":tradeCode");//为保证安全，可以在删除的时候先查询一下。为保持原子性可以使用lua脚本实现
            System.out.println("操作成功");
            return "success";
        }
        System.out.println("操作失败");
        return "fail";
    }

    /**
     * 新增订单和订单详情信息
     *
     * @param omsOrder
     */
    @Override
    public OmsOrder insOrder(OmsOrder omsOrder) {

        //获得订单详情集合
        List<OmsOrderItem> omsOrderItems = omsOrder.getOmsOrderItems();

        //新增订单信息
        omsOrderMapper.insertSelective(omsOrder);

        //遍历订单详情集合，进行新增操作
        for (OmsOrderItem omsOrderItem : omsOrderItems) {
            omsOrderItem.setOrderId(omsOrder.getId());
            omsOrderItemMapper.insertSelective(omsOrderItem);
        }
        return omsOrder;
    }

    @Override
    public void updOrderInfo(String orderSn) {


        //修改订单系统状态
        Example example = new Example(OmsOrder.class);
        example.createCriteria().andEqualTo("orderSn", orderSn);
        OmsOrder omsOrder = new OmsOrder();
        omsOrder.setStatus(1);
        omsOrderMapper.updateByExampleSelective(omsOrder,example);

        /**
         * 自己自己写的接收消息，然后修改订单状态
         * 后来发现不能实现，因为这样不能实现监听的功能，方法不能启动
         */
        /*//接收支付系统发送的消息
        ConnectionFactory connectionFactory = null;
        Session session = null;
        Connection connection = null;
        connectionFactory = new ActiveMQConnectionFactory("tcp://192.168.91.129:61616");
        try {
            connection = connectionFactory.createConnection();
            connection.start();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination queue = session.createQueue("PAYMENT_RESULT_CHECK_QUEUE");
            MessageConsumer consumer = session.createConsumer(queue);
            //创建消息监听对象
            consumer.setMessageListener(new MessageListener() {
                @Override
                public void onMessage(Message message) {
                    if (message instanceof MapMessage) {
                        try {
                            //接收消息
                            Object orderSn = ((MapMessage) message).getObject("orderSn");
                            if (orderSn!=null&&!orderSn.equals("")) {
                               String sn = (String) orderSn;
                               //修改订单系统状态
                                Example example = new Example(OmsOrder.class);
                                example.createCriteria().andEqualTo("orderSn", sn);
                                OmsOrder omsOrder = new OmsOrder();
                                omsOrder.setStatus(1);
                                omsOrderMapper.updateByExampleSelective(omsOrder,example);
                            }
                        } catch (JMSException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        } catch (JMSException e) {
            e.printStackTrace();
        }finally {
            try {
                if (connection!=null) {
                    connection.close();
                }
            } catch (JMSException e) {
                e.printStackTrace();
            }

        }*/

    }
}
