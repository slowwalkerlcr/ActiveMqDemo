package com.activemq.topic.lvcr;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

/**
 * 消息的消费者（接受者）(非监听)
 * @author liang
 *
 * 1、这个消费者在topic模式下设置了持久化监听，但是在mq的管理页面的Messages Dequeued  数量并不会增加
 * 2、只要消费者启动过一次，不管之后是启动状态还是停止状态，mq管理页面的Number Of Consumers 都存在此消费者
 *
 *
 */

public class TopicJMSConsumer3 {

    private static final String USERNAME = ActiveMQConnection.DEFAULT_USER;//默认连接用户名
    private static final String PASSWORD = ActiveMQConnection.DEFAULT_PASSWORD;//默认连接密码
    private static final String BROKEURL = ActiveMQConnection.DEFAULT_BROKER_URL;//默认连接地址

    public static void main(String[] args) {
        ConnectionFactory connectionFactory;//连接工厂
        Connection connection = null;//连接

        Session session;//会话 接受或者发送消息的线程
        Destination destination;//消息的目的地

        MessageConsumer messageConsumer;//消息的消费者

        //1、实例化连接工厂connectionFactory，需要username,password,brokerurl
        connectionFactory = new ActiveMQConnectionFactory(TopicJMSConsumer3.USERNAME, TopicJMSConsumer3.PASSWORD, TopicJMSConsumer3.BROKEURL);

        try {
            //2、通过连接工厂获取连接connection, 注意连接默认是关闭的，因此需要start开启
            connection = connectionFactory.createConnection();
            //设置clientID，在topic模式下面，如果设置了clientID，如果消息已经发送，消费者还没启动，这是后消费者启动在消息发送之后，也可以监听到消息(即持久化消息)
            //同时需要在第5步创建消费者的时候指定 clientId
            connection.setClientID("TopicJMSConsumer3");
            //启动连接
            connection.start();
            //3、创建session，前面2部的操作就是为了创建session（上下文环境对象）
            //创建session的一些配置参数 比如是否启用事务，签收模式（这里先设置为自动签收auto）
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            //4、通过session创建一个destination对象，这里创建一个名称为HelloWorld的消息队列，在ptp模式是queue，在pub/sub模式下是topic
            destination = session.createTopic("TopicMessage");
            //5、通过session创建  发送消息的生产者/接受消息的消费者  这里创建的是消费者
            messageConsumer = session.createDurableSubscriber((Topic) destination,"TopicJMSConsumer3");

            messageConsumer.setMessageListener(new TopicMyJmsListener("TopicJMSConsumer3"));

        } catch (JMSException e) {
            e.printStackTrace();
        }

    }
}