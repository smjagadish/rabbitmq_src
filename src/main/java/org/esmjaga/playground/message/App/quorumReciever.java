package org.esmjaga.playground.message.App;

import com.rabbitmq.client.Connection;
import org.springframework.beans.factory.annotation.Autowired;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class quorumReciever {
    @Autowired
    @Qualifier("localconn")
    Connection connection;
    private Channel channel;
    quorumReciever()
    {

    }
    void initialize()
    {
        try {
            channel = connection.createChannel();
        }
        catch(Exception e)
        {

        }
    }
    void recieve()
    {
        try {
            //recieving from quorum queue
            boolean basic_ack = true;
            channel.basicConsume("quorum_q",basic_ack,new MessageConsumer(channel));
        }
        catch(Exception e)
        {

        }
    }
}
