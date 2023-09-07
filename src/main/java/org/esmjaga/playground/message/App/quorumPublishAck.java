package org.esmjaga.playground.message.App;

import com.rabbitmq.client.ConfirmListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class quorumPublishAck implements ConfirmListener {
    private Logger logger = LoggerFactory.getLogger(quorumPublishAck.class);
    @Override
    public void handleAck(long l, boolean b) throws IOException {
        logger.info("publisher confirm in place for msg send to quorum queue");
    }

    @Override
    public void handleNack(long l, boolean b) throws IOException {
        logger.info("publisher confirm failed for msg send to quorum queue");
    }
}
