package com.activemq.delay.lvcr;

import javax.jms.*;

/**
 * Created by Administrator on 2017/5/24.
 */
public class MyDelayJmsListener implements MessageListener{
    public void onMessage(Message m) {
        try{
                TextMessage message = (TextMessage)m;
                System.out.println("文本消息"+message.getText());
        }catch(JMSException e){
            e.printStackTrace();
        }
    }
}
