package org.esmjaga.playground.message.App;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class RemoteMessageConsumer extends DefaultConsumer {
    private static Logger logger = LoggerFactory.getLogger(RemoteMessageConsumer.class);
    private Channel channel;
    private String ctag;
    public RemoteMessageConsumer(Channel channel) {
        super(channel);
        this.channel = channel;
    }

    @Override
    public void handleConsumeOk(String consumerTag) {
        super.handleConsumeOk(consumerTag);
    }

    @Override
    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
        logger.info("ack'ing message consumption");
        ctag = consumerTag;
        channel.basicAck(envelope.getDeliveryTag(), false);
    }

    @Override
    public void handleCancel(String consumerTag) throws IOException {
        super.handleCancel(consumerTag);
    }

    @Override
    public void handleCancelOk(String consumerTag) {
        super.handleCancelOk(consumerTag);
    }
}
