package com.activemq.seletor.lvcr;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

/**
 * 消息的消费者（接受者）(非监听)
 * @author Edison
 *
 */

public class JMSConsumerSelector {

    private static final String USERNAME = ActiveMQConnection.DEFAULT_USER;//默认连接用户名
    private static final String PASSWORD = ActiveMQConnection.DEFAULT_PASSWORD;//默认连接密码
    private static final String BROKEURL = ActiveMQConnection.DEFAULT_BROKER_URL;//默认连接地址
    private static final String condition="age>=25";

    public static void main(String[] args) {
        ConnectionFactory connectionFactory;//连接工厂
        Connection connection = null;//连接

        Session session;//会话 接受或者发送消息的线程
        Destination destination;//消息的目的地

        MessageConsumer messageConsumer;//消息的消费者

        //1、实例化连接工厂connectionFactory，需要username,password,brokerurl
        connectionFactory = new ActiveMQConnectionFactory(JMSConsumerSelector.USERNAME, JMSConsumerSelector.PASSWORD, JMSConsumerSelector.BROKEURL);

        try {
            //2、通过连接工厂获取连接connection, 注意连接默认是关闭的，因此需要start开启
            connection = connectionFactory.createConnection();
            //启动连接
            connection.start();
            //3、创建session，前面2部的操作就是为了创建session（上下文环境对象）
            //创建session的一些配置参数 比如是否启用事务，签收模式（这里先设置为自动签收auto）
            session = connection.createSession(Boolean.FALSE, Session.CLIENT_ACKNOWLEDGE);

            //4、通过session创建一个destination对象，这里创建一个名称为HelloWorld的消息队列，在ptp模式是queue，在pub/sub模式下是topic
            destination = session.createQueue("mapMessage");
            //5、通过session创建  发送消息的生产者/接受消息的消费者  这里创建的是消费者
            messageConsumer = session.createConsumer(destination,condition);

            while (true) {
                MapMessage mapMessage = (MapMessage) messageConsumer.receive(100000);
                if(mapMessage != null){
                    System.out.println("收到的消息，来自姓名:" + mapMessage.getString("name")+"==>年龄："+mapMessage.getIntProperty("age"));
                    //如果第3步创建 签收模式是客户端手动签收，这里需要调用消息的acknowledge()进行手动签收，否在消息服务器里的消息不会出队列，即消息服务器里的消息不会消费掉
                    //如果是这是的auto自动签收，调用receive()方法即自动签收了，不需要再次手动签收
                    mapMessage.acknowledge();
                }else {
                    break;
                }

            }
        } catch (JMSException e) {
            e.printStackTrace();
        }

    }
}