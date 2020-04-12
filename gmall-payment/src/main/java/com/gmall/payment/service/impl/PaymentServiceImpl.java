package com.gmall.payment.service.impl;

import ch.qos.logback.core.joran.action.ActionUtil;
import com.alibaba.dubbo.config.annotation.Service;
import com.gmall.bean.PaymentInfo;
import com.gmall.mq.ActiveMQConfig;
import com.gmall.mq.ActiveMQUtil;
import com.gmall.payment.mapper.PaymentInfoMapper;
import com.gmall.service.PaymentService;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ScheduledMessage;
import org.apache.activemq.command.ActiveMQMapMessage;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import tk.mybatis.mapper.entity.Example;

import javax.jms.*;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentInfoMapper paymentInfoMapper;

    @Override
    public void insPaymentInfo(PaymentInfo paymentInfo) {
        paymentInfoMapper.insertSelective(paymentInfo);
    }

    /**
     * 更新支付信息
     * @param paymentInfo
     */
    @Override
    public void updPaymentInfo(PaymentInfo paymentInfo) {
        Example example = new Example(PaymentInfo.class);
        example.createCriteria().andEqualTo("orderSn",paymentInfo.getOrderSn());
        paymentInfoMapper.updateByExampleSelective(paymentInfo,example);

        //并发送消息，提示订单系统，已经支付成功
        ConnectionFactory connectionFactory = null;
        Session session = null;
        Connection connection = null;
        MessageProducer producer = null;
        try {
            connectionFactory = new ActiveMQConnectionFactory("tcp://192.168.91.129:61616");
            connection = connectionFactory.createConnection();
            connection.start();
            //创建session对象并开启事务支持
            session = connection.createSession(true, Session.SESSION_TRANSACTED);
            Queue queue = new ActiveMQQueue("PAYMENT_RESULT_CHECK_QUEUE");
            producer = session.createProducer(queue);
            MapMessage mapMessage = new ActiveMQMapMessage();
            mapMessage.setObject("orderSn",paymentInfo.getOrderSn());

            producer.send(mapMessage);
            session.commit();
        } catch (Exception e) {
            try {
                session.rollback();
            } catch (JMSException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        }finally {
            try {
                if (connection!=null) {
                    connection.close();
                }
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void sendDelayPaymentResult(String orderSn) {
        Connection connection = null;
        Session session = null;
        Queue queue = null;
        ConnectionFactory connectionFactory = null;
        try {
            connectionFactory = new ActiveMQConnectionFactory("tcp://192.168.91.129:61616");
            connection = connectionFactory.createConnection();
            connection.start();
            session = connection.createSession(true, Session.SESSION_TRANSACTED);
            queue = session.createQueue("PAYMENT_RESULT_CHECK_QUEUE");
            MessageProducer producer = session.createProducer(queue);
            MapMessage mapMessage = new ActiveMQMapMessage();
            mapMessage.setObject("orderSn", orderSn);
            //设置延迟队列的时间属性
            mapMessage.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY,1000*30);   //10秒
            producer.send(mapMessage);
            session.commit();
        } catch (Exception e) {
            try {
                session.rollback();
            } catch (JMSException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        }finally {
            try {
                if (connection!=null) {
                    connection.close();
                }
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }
}
