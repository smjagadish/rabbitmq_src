package org.esmjaga.playground.message.App;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.ReturnListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.Charset;

public class RoutingErrorCallback implements ReturnListener {
    private Logger logger = LoggerFactory.getLogger(RoutingErrorCallback.class);
    @Override
    public void handleReturn(int i, String s, String s1, String s2, AMQP.BasicProperties basicProperties, byte[] bytes) throws IOException {
        logger.info("sorry, the message cannot be routed");
        String st = new String(bytes, Charset.defaultCharset());
        logger.info("lost message content is:"+st);
    }
}
