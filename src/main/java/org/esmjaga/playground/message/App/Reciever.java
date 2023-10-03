package org.esmjaga.playground.message.App;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class Reciever {
    private final static String QUEUE_NAME = "queue1";
    //private ConnectionFactory factory;
    //private Connection connection;
    @Autowired
    @Qualifier("localconn")
    Connection connection;
    private Channel channel;
    private String cons_tag;
    Reciever()
    {
      cons_tag=null;
    }
    void initialize() throws IOException, TimeoutException {

        channel = connection.createChannel();
        boolean durable = true;
        // marking queue as durable so that it can survive broker restart/crash
        channel.queueDeclare(QUEUE_NAME, durable, false, false, null);


    }
    public void recieve()
    {
        try{
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), "UTF-8");
                //getting the consumer tag ans assigning it to an instance variable
                // the cons_tag can be used with channel.basicCancel(cons_tag) to perform a cancel of this consumer
                // note that other cons using the same channel will not be impacted
                cons_tag = consumerTag;
                System.out.println("cons_tag="+cons_tag);
                System.out.println(" [x] Received in R1 '" + message + "'"+Thread.currentThread().getName());
            };
            //turning on explicit ack
            boolean autoAck = false;
            // i'm making this consumer as the highest prio consumer
            // as long this cons is not blocked , it will be preferred instead of R2 (since R2 doesnt have prio set)
            // the prio doesnt depend on the underlying channel , so r1 and r2 can use same channel or diff channel
            Map<String,Object> map = new HashMap<>();
            map.put("x-priority",10);
            channel.basicConsume("topic_q1", autoAck, map, deliverCallback, consumerTag -> { });
            channel.basicConsume(QUEUE_NAME, autoAck,  deliverCallback, consumerTag -> { });
            //No ack sent back, so the messages will be re-delivered when the consumer restarts (assuming restart is less than 30 mins from the time of message getting stored in the queue)
            System.out.println("this is a test msg to check if basic consume blocks");

        }
        catch(Exception e)
        {

        }
        System.out.println("cccc");
    }

}
