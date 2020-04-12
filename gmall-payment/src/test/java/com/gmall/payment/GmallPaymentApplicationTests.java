package com.gmall.payment;

import org.apache.activemq.command.ActiveMQQueue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.jms.core.JmsTemplate;

import javax.jms.Queue;

@SpringBootTest
public class GmallPaymentApplicationTests {

    @Autowired
    private JmsMessagingTemplate messagingTemplate;

    @Test
    void testActiveMq() {
        Queue queue = new ActiveMQQueue("hello");
        messagingTemplate.convertAndSend(queue,"hello everyBody");
    }
}
