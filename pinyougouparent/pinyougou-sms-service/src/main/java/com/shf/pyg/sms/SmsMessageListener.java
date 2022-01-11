package com.shf.pyg.sms;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;

public class SmsMessageListener implements MessageListener {
    @Override
    public void onMessage(Message message) {
        MapMessage mapMessage=(MapMessage)message;
        try {
            String mobile = mapMessage.getString("mobile");
            String smscode = mapMessage.getString("smscode");
            System.out.println("从MQ中提取出消息---手机号:"+mobile+"，验证码："+smscode);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
