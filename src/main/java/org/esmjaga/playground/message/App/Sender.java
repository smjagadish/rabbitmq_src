package org.esmjaga.playground.message.App;

import com.rabbitmq.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

public class Sender {
    private final static String QUEUE_NAME_1 = "queue1";
    private final static String QUEUE_NAME_2 = "queue2";
    private final static Logger logger = LoggerFactory.getLogger(Sender.class);

    //private Connection connection;
    //private ConnectionFactory factory;
     Channel channel;
    @Autowired
    Connection connection;
    private String trans_q;
    Sender() {

    }

     void initialize() {
        logger.info("creating rabbitmq connection");
        try {
            channel = connection.createChannel();
            channel.exchangeDeclare("mainEx","direct");
            boolean durable = true;
            // marking queue as durable so that it can survive broker restart/crash
            // the queues are also non-exclusive and non auto-delete
            // the queues do not declare any special property (like message ttl , priorities etc.)
            channel.queueDeclare(QUEUE_NAME_1, durable, false, false, null);
            channel.queueDeclare(QUEUE_NAME_2, durable, false, false, null);
            // example for a queue that is durable , non-exclusive , non-auto delete and declares properties
            Map<String,Object> map = new HashMap<>();
            map.put("x-message-ttl",1000);
            map.put("x-max-priority",10);
            map.put("x-max-length",100);
            // assigning a dead letter ex
            // be sure to create the dead_ex before binding a queue
            map.put("x-dead-letter-exchange","dead_ex");
            map.put("x-dead-letter-routing-key","dlx");
            channel.queueDeclare("customqueue",true,false,false,map);
            channel.exchangeDeclare("dead_ex","direct",true);
            channel.queueDeclare("dead_queue",true,false,false,null);
            channel.queueBind("dead_queue","dead_ex","dlx");
            // exclusive queue . This queue is later used in Reciever2.java
            // the queue is also auto-delete
            trans_q = channel.queueDeclare().getQueue();

        }
        catch (Exception e)
        {

        }
    }
    void send(String msg)
    {
        try {
            channel.basicPublish("", QUEUE_NAME_1, null, msg.getBytes());
            System.out.println(" [x] Sent to " + QUEUE_NAME_1  + " with:" + msg);
            channel.basicPublish("", QUEUE_NAME_2, null, msg.getBytes());
            System.out.println(" [x] Sent to " + QUEUE_NAME_2  + " with:" + msg);
            String pp ="police";
            channel.basicPublish("",trans_q,null, pp.getBytes());
            System.out.println(" [x] Sent to " + trans_q  + " with:" + pp);

        }
        catch(Exception e)
        {

        }
    }
}
