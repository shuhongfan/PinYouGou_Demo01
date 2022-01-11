package com.shf.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.jms.Destination;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath:applicationContext-jms-producer.xml")
public class TestJms {
    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private Destination queueTextDestination;

    @Autowired
    private Destination topicTextDestination;

    @Test
    public void sendQueueTest(){
        jmsTemplate.convertAndSend(queueTextDestination,"这是一个点对点消息");
    }


    @Test
    public void sendTopicTest(){
        jmsTemplate.convertAndSend(topicTextDestination,"这是一个发布订阅消息");
    }
}
