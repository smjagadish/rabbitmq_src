package org.esmjaga.playground.message.App;

import com.rabbitmq.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;
public class ReplyToReciever {
    private Logger logger = LoggerFactory.getLogger(ReplyToReciever.class);
    @Autowired
    @Qualifier("localconn")
    Connection connection;
    Channel channel;
    String cid;
    String reply_queue_name;

    ReplyToReciever()
    {
        cid = null;
        reply_queue_name = null;
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
            // consuming from the rpc queue
            MessageConsumer mcons = new MessageConsumer(channel)
            {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    cid = properties.getCorrelationId();
                    reply_queue_name = properties.getReplyTo();
                    String data = new String(body);
                    logger.info("consuming from rpc queue with data:"+data);
                    // acking the msg first, no multiple ack
                    channel.basicAck(envelope.getDeliveryTag(), false);
                    // now publish to the reply_to queue
                    String msg = "rpc consumer got the data and acking explciitly";
                    AMQP.BasicProperties prop = new AMQP.BasicProperties()
                            .builder()
                                    .correlationId(cid)
                                            .build();
                    logger.info("populating the response to reply queue");
                    channel.basicPublish("rpc_ex","rpc_reply_queue_routing",false,prop,msg.getBytes(StandardCharsets.UTF_8));
                }
            };
            channel.basicConsume("rpc_queue",false,mcons);
        }
        catch(Exception e)
        {

        }
    }

}
