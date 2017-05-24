package com.activemq.topic.lvcr;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

public class TopicJMSProducer {

    //默认连接用户名
    private static final String USERNAME = ActiveMQConnection.DEFAULT_USER;
    //默认连接密码
    private static final String PASSWORD = ActiveMQConnection.DEFAULT_PASSWORD;
    //默认连接地址
    private static final String BROKEURL = ActiveMQConnection.DEFAULT_BROKER_URL;
    //发送的消息数量
    private static final int SENDNUM = 10;

    public static void main(String[] args) {
        //连接工厂
        ConnectionFactory connectionFactory;
        //连接
        Connection connection = null;
        //会话 接受或者发送消息的线程
        Session session;
        //消息的目的地
        Destination destination;
        //消息生产者
        MessageProducer messageProducer;
        //1、实例化连接工厂connectionFactory，需要username,password,brokerurl
        connectionFactory = new ActiveMQConnectionFactory(TopicJMSProducer.USERNAME, TopicJMSProducer.PASSWORD, TopicJMSProducer.BROKEURL);

        try {
            //2、通过连接工厂获取连接connection, 注意连接默认是关闭的，因此需要start开启
            connection = connectionFactory.createConnection();
            //启动连接
            connection.start();
            //3、创建session，前面2部的操作就是为了创建session（上下文环境对象）
            //创建session的一些配置参数 比如是否启用事务，签收模式（这里先设置为自动签收quto）
            session = connection.createSession(Boolean.FALSE, Session.AUTO_ACKNOWLEDGE);
            //4、通过session创建一个destination对象，这里创建一个名称为HelloWorld的消息队列，在ptp模式是queue，在pub/sub模式下是topic
            destination = session.createTopic("TopicMessage");
            //5、通过session创建  发送消息的生产者/接受消息的消费者  这里创建的是生产者
            messageProducer = session.createProducer(destination);
            //6、设置持久化/非持久化特性（默认是非持久） 如果是非持久，那么意味着mq的重启会导致消息丢失（这里设置为非持久化）
            //如果持久化到kahdb、leveldb、jdbc的方式的话，意味着消息持久化
            messageProducer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
            //7、定义JMS规范的消息类型，这里先使用简单的TextMessage
            //需要session进行创建
            TextMessage message = session.createTextMessage();
            //设置消息内容
            message.setText("Hello Activemq");
            //通过消息生产者发送消息
            messageProducer.send(message);
            System.out.println("消息已发送。。。。。。");
           // sendMessage(session, messageProducer);
            //第3步没有设置事务，故这里不需要提交事务
            //session.commit();

        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            if(connection != null){
                try {
                    connection.close();
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        }

    }
    /**
     * 发送消息
     * @param session
     * @param messageProducer  消息生产acer
     *                         者
     * @throws Exception
     */
    public static void sendMessage(Session session,MessageProducer messageProducer) throws Exception{
        for (int i = 0; i < TopicJMSProducer.SENDNUM; i++) {
            //创建一条文本消息
            TextMessage message = session.createTextMessage("ActiveMQ 发送消息" +i);
            System.out.println("发送消息：Activemq 发送消息" + i);
            //通过消息生产者发出消息
            messageProducer.send(message);
        }

    }
}