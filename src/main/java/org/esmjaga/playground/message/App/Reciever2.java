package org.esmjaga.playground.message.App;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeoutException;


public class Reciever2 {
    private final static String QUEUE_NAME = "queue2";
    //private ConnectionFactory factory;
    //private Connection connection;
    @Autowired
    Connection connection;
    @Autowired
    Sender snd;
    private Channel channel;
    Reciever2()
    {

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
                System.out.println(" [x] Received in R2 '" + message + "'"+Thread.currentThread().getName());
            };
            // turning on explicit ack
            boolean autoAck = false;
            channel.basicConsume(QUEUE_NAME, autoAck, deliverCallback, consumerTag -> { });
            //No ack sent back, so the messages will be re-delivered when the consumer restarts (assuming restart is less than 30 mins from the time of message getting stored in the queue)

        }
        catch(Exception e)
        {

        }
    }
    // The below method illustrates use of exclusive queues
    // 'channel' used in Sender.java which is used to create the ex.queue is used here
    // any other channel wont be able to consume (or) write  from/to this queue
    public void del()
    {
        Channel ch = snd.channel;
        try{
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), "UTF-8");
                System.out.println(" [x] Received in R2 '" + message + "'"+Thread.currentThread().getName());
            };
            // turning on explicit ack
            boolean autoAck = true;
            ch.basicConsume("", autoAck, deliverCallback, consumerTag -> { });
            //No ack sent back, so the messages will be re-delivered when the consumer restarts (assuming restart is less than 30 mins from the time of message getting stored in the queue)

        }
        catch(Exception e)
        {

        }
    }

}
