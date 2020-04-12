package com.gmall.payment.testmq;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.springframework.jms.connection.ConnectionFactoryUtils;

import javax.jms.*;

public class Provider {

    public static void main(String[] args) {
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("admin","admin","tcp://192.168.91.129:61616");
        Connection connection = null;
        Session session = null;
        try {
            connection = connectionFactory.createConnection();
            connection.start();
            //第一个值表示是否开启事务，如果选择true，第二个值相当于选择0
            session = connection.createSession(true,Session.SESSION_TRANSACTED);
            //创建队列对象，并起名为drink
            Queue queue = session.createQueue("drink");
            //创建消息生产者
            MessageProducer producer = session.createProducer(queue);

            TextMessage textMessage = new ActiveMQTextMessage();
            textMessage.setText("I'm thirsty,give me a coup of coffee please!");
            producer.setDeliveryMode(DeliveryMode.PERSISTENT);
            producer.send(textMessage);

            session.commit();
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
        }
    }
}


