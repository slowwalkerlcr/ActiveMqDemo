package com.activemq.topic.lvcr;

import javax.jms.*;

/**
 * Created by Administrator on 2017/5/24.
 */
public class TopicMyJmsListener implements MessageListener{

    private String consumer;

    public TopicMyJmsListener() {
    }

    public TopicMyJmsListener(String consumer) {
        this.consumer = consumer;
    }

    public void onMessage(Message m) {
        try{
                TextMessage message = (TextMessage)m;
                System.out.println(consumer+"收到文本消息"+message.getText());
        }catch(JMSException e){
            e.printStackTrace();
        }
    }
}
