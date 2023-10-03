package org.esmjaga.playground.message.App;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class AzureReciever {
    @Autowired
    @Qualifier("remoteconn")
    Connection conn;
    private Channel ch;
    private Logger logger = LoggerFactory.getLogger(AzureReciever.class);

}
