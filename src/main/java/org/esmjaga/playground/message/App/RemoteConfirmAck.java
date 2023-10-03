package org.esmjaga.playground.message.App;

import com.rabbitmq.client.ConfirmListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class RemoteConfirmAck implements ConfirmListener {
    private static Logger logger = LoggerFactory.getLogger(RemoteConfirmAck.class);
    @Override
    public void handleAck(long l, boolean b) throws IOException {
        logger.info("rmq cluster recieved the transmitted message");
    }

    @Override
    public void handleNack(long l, boolean b) throws IOException {
       logger.info("rmq cluster did not process the transmitted message");
    }
}
