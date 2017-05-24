package com.activemq.topic.lvcr;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

/**
 * 消息的消费者（接受者）(非监听)
 * @author liang
 *
 */

public class TopicJMSConsumer1 {

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
        connectionFactory = new ActiveMQConnectionFactory(TopicJMSConsumer1.USERNAME, TopicJMSConsumer1.PASSWORD, TopicJMSConsumer1.BROKEURL);

        try {
            //2、通过连接工厂获取连接connection, 注意连接默认是关闭的，因此需要start开启
            connection = connectionFactory.createConnection();
            //启动连接
            connection.start();
            //3、创建session，前面2部的操作就是为了创建session（上下文环境对象）
            //创建session的一些配置参数 比如是否启用事务，签收模式（这里先设置为自动签收auto）
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            //4、通过session创建一个destination对象，这里创建一个名称为HelloWorld的消息队列，在ptp模式是queue，在pub/sub模式下是topic
            destination = session.createTopic("TopicMessage");
            //5、通过session创建  发送消息的生产者/接受消息的消费者  这里创建的是消费者
            messageConsumer = session.createConsumer(destination);

            messageConsumer.setMessageListener(new TopicMyJmsListener("TopicJMSConsumer1"));

        } catch (JMSException e) {
            e.printStackTrace();
        }

    }
}