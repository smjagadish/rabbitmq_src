package org.esmjaga.playground.message.App;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Sender {
    private final static String QUEUE_NAME = "hello";
    private final static Logger logger = LoggerFactory.getLogger(Sender.class);

    Sender() {

    }

    void initialize() {
        logger.info("creating rabbitmq connection");
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try {
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            String message = "Hello World!";
            channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
            System.out.println(" [x] Sent '" + message + "'");

        }
        catch (Exception e)
        {

        }
    }
}
