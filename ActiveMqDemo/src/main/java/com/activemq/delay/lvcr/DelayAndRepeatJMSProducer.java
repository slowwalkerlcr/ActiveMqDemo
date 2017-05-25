package com.activemq.delay.lvcr;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ScheduledMessage;

import javax.jms.*;

/**
 * 消息延时推送机制
 * AMQ_SCHEDULED_DELAY	long	延迟投递的时间
   AMQ_SCHEDULED_PERIOD	long	重复投递的时间间隔
   AMQ_SCHEDULED_REPEAT	int	重复投递次数
   AMQ_SCHEDULED_CRON	String	Cron表达式


     定时任务
     CRON表达式的优先级高于另外三个参数，如果在设置了CRON的同时，也有repeat和period参数，则会在每次CRON执行的时候，重复投递repeat次，每次间隔为period。就是说设置是叠加的效果。例如每小时都会发生消息被投递10次，延迟1秒开始，每次间隔1秒:
     MessageProducer producer = session.createProducer(destination);
     TextMessage message = session.createTextMessage("test msg");
     message.setStringProperty(ScheduledMessage.AMQ_SCHEDULED_CRON, "0 * * * *");
     message.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY, 1000);
     message.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_PERIOD, 1000);
     message.setIntProperty(ScheduledMessage.AMQ_SCHEDULED_REPEAT, 9);
     producer.send(message);
 */
public class DelayAndRepeatJMSProducer {

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
        connectionFactory = new ActiveMQConnectionFactory(DelayAndRepeatJMSProducer.USERNAME, DelayAndRepeatJMSProducer.PASSWORD, DelayAndRepeatJMSProducer.BROKEURL);

        try {
            //2、通过连接工厂获取连接connection, 注意连接默认是关闭的，因此需要start开启
            connection = connectionFactory.createConnection();
            //启动连接
            connection.start();
            //3、创建session，前面2部的操作就是为了创建session（上下文环境对象）
            //创建session的一些配置参数 比如是否启用事务，签收模式（这里先设置为自动签收quto）
            session = connection.createSession(Boolean.FALSE, Session.AUTO_ACKNOWLEDGE);
            //4、通过session创建一个destination对象，这里创建一个名称为HelloWorld的消息队列，在ptp模式是queue，在pub/sub模式下是topic
            destination = session.createQueue("DelayAndRepeatMessageQueue");
            //5、通过session创建  发送消息的生产者/接受消息的消费者  这里创建的是生产者
            messageProducer = session.createProducer(destination);
            //6、设置持久化/非持久化特性（默认是非持久） 如果是非持久，那么意味着mq的重启会导致消息丢失（这里设置为非持久化）
            //如果持久化到kahdb、leveldb、jdbc的方式的话，意味着消息持久化
            messageProducer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
            //7、定义JMS规范的消息类型，这里先使用简单的TextMessage
            //需要session进行创建
            TextMessage message = session.createTextMessage();
            //设置消息内容
            message.setText("Hello Delay Repeat Activemq");
            //消息的延时推送  broker端消息延时调度机制
            long delay = 30 * 1000;
            long period = 10 * 1000;
            int repeat = 9;
            message.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_PERIOD,period);
            message.setIntProperty(ScheduledMessage.AMQ_SCHEDULED_REPEAT,repeat);
            message.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY, delay);
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
}