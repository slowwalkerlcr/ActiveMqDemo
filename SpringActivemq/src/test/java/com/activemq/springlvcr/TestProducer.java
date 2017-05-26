package com.activemq.springlvcr;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class TestProducer extends BaseJunit4Test {

    @Autowired
    private ActiveMQProducer activeMQProducer;

    @Test
    public void send(){
        this.activeMQProducer.sendMessage("the message come from Spring!");
    }
}
