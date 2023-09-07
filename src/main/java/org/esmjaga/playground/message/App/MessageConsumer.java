package org.esmjaga.playground.message.App;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class MessageConsumer extends DefaultConsumer {
    private final Logger logger = LoggerFactory.getLogger(MessageConsumer.class);
    public MessageConsumer(Channel channel) {
        super(channel);
    }

    @Override
    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
        String st = new String(body,"UTF-8");
        logger.info("message consumed with data:"+st);
        long tag = envelope.getDeliveryTag();
        //ack'ing each message one at a time
        //if i do true, then it'll be multi-ack
        getChannel().basicAck(tag,false);
    }

    @Override
    public void handleCancel(String consumerTag) throws IOException {
        super.handleCancel(consumerTag);
    }
}
