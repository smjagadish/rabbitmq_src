package org.esmjaga.playground.message.App;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.impl.AMQBasicProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.HashMap;
import java.util.Map;

public class AzureSender {
    @Autowired
    @Qualifier("remoteconn")
    Connection conn;
    private Channel ch;
    private Logger logger = LoggerFactory.getLogger(AzureSender.class);

    AzureSender()
    {

    }
    void initialize()
    {
        try
        {
            ch = conn.createChannel();
            ch.exchangeDeclare("primary_ex", BuiltinExchangeType.DIRECT, true,false,false,null);
            ch.queueDeclare("primary_queue",true,false,false,null);
            ch.queueBind("primary_queue","primary_ex","primary_queue_rk");
            ch.exchangeDeclare("topic_ex",BuiltinExchangeType.TOPIC,true,false,false,null);
            Map<String,Object> qmap = new HashMap<>();
            qmap.put("x-queue-type","quorum");
            qmap.put("x-message-ttl",100000);
            ch.queueDeclare("topic_q1",true,false,false,qmap);
            ch.queueDeclare("topic_q2",true,false,false,qmap);
            // binds topic_ex to a queue called topic_q1 with routing key consisting of 3 words
            ch.queueBind("topic_q1","topic_ex","*.*.*");
            // binds topic_ex to a queue called topic_q2 with routing key starting with word topic_router and o or more words after
            ch.queueBind("topic_q2","topic_ex","topic_router.#");
            ch.confirmSelect();
            ch.addConfirmListener(new RemoteConfirmAck());


        }
        catch(Exception e)
        {

        }
    }
    void send()
    {
        try {
              String msg="send to azure direct from local";
              ch.basicPublish("primary_ex","primary_queue_rk",null,msg.getBytes());
              logger.info("message sent");
              String msg_q="send to quorun queue";
            AMQP.BasicProperties amqBasicProperties = new AMQP.BasicProperties()
                    .builder()
                    .appId("esmjaga-rmq-sender")
                    .contentType("text/plain")
                    .contentEncoding("UTF-8")
                    .expiration("100000")
                    .build();
              ch.basicPublish("topic_ex","my.first.routingkey",amqBasicProperties,msg_q.getBytes());
        }
        catch(Exception e)
        {
                System.out.println(e.getMessage());
        }
    }
}
