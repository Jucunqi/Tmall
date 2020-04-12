package com.gmall.payment.testmq;

import org.apache.activemq.ActiveMQConnectionFactory;
import javax.jms.*;

public class Consumer {
    public static void main(String[] args) {
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("admin", "admin", "tcp://192.168.91.129:61616");

        Connection connection = null;
        Session session = null;

        try {
            connection = connectionFactory.createConnection();
            connection.start();
            session = connection.createSession(false,Session.AUTO_ACKNOWLEDGE);
            Destination queue = session.createQueue("drink");
            MessageConsumer consumer = session.createConsumer(queue);
           consumer.setMessageListener(new MessageListener() {
                @Override
                public void onMessage(Message message) {
                    if (message instanceof TextMessage) {
                        try {
                            String text = ((TextMessage) message).getText();
                            System.out.println(text);
                        } catch (JMSException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

        } catch (JMSException e) {
            e.printStackTrace();
        }

    }
}
