package org.esmjaga.playground.message.App;

import com.rabbitmq.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class NackSender {
    @Autowired
    private Connection connection;
    private Channel ch;

    private static final Logger logger = LoggerFactory.getLogger(NackSender.class);
    NackSender()
    {

    }

    void initialize()  {
        try {
            logger.info("initializing nackSender and creating the exchange ");
            ch = connection.createChannel();
            ch.exchangeDeclare("nackEx", "fanout");
        }
        catch(Exception e)
        {

        }
    }
    // this sender's send method will run async in a background thread for 100 iterations
    // the message published to the exchange 'nackEx' wont be routed as long as a queue binds to it
    @Async
    public void send() {
        try {
            logger.info("publishing to an unnamed queue tied to the fanout exchange . wont work until any queue isn't bound to the exch");
            for(int i=0;i<100;i++)
            {
                ch.basicPublish("nackEx", "", null, new String("Wont get through").getBytes());
                Thread.sleep(10000);
            }
        }
        catch(Exception e)
        {

        }
    }
}
