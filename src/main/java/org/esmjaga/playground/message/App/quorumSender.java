package org.esmjaga.playground.message.App;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class quorumSender {
    private final Logger logger = LoggerFactory.getLogger(quorumSender.class);
    private Channel channel;
    @Autowired
    @Qualifier("localconn")
    Connection connection;
    quorumSender()
    {

    }
    void initialize()
    {
        try {
            channel = connection.createChannel();
            // an exchange that will be used for routing to quorum queues
            channel.exchangeDeclare("quorum_ex", BuiltinExchangeType.DIRECT);
            // the dlx exchange for usage by quorum queues
            channel.exchangeDeclare("dlx_quorum",BuiltinExchangeType.DIRECT);
            Map<String,Object> map = new HashMap<>();
            // this prop turns a queue into a quorum queue
            map.put("x-queue-type","quorum");
            // to set the dlx properties
            map.put("x-dead-letter-exchange","dlx_quorum");
            map.put("x-dead-letter-routing-key","quorumdlx");
            // queue declaration
            channel.queueDeclare("quorum_q",true,false,false,map);
            // dlx queue declaration
            channel.queueDeclare("dlx_q",true,false,false,null);
            // binding quorum q to the ex
            // the routing key used is "quorum"
            channel.queueBind("quorum_q","quorum_ex","quorum");
            // binding dlx q for quorum to the ex
            channel.queueBind("dlx_q","dlx_quorum","quorumdlx");
            // publisher confirms to make sense out of the quorum queues
            channel.confirmSelect();
            channel.addConfirmListener(new quorumPublishAck());
        }
        catch(Exception e)
        {

        }
    }

    void send() {
        try {
            // publish first message to the quorum queue
            String msg = "message bound for quorum";
            channel.basicPublish("quorum_ex", "quorum", null, msg.getBytes(StandardCharsets.UTF_8));
        }
        catch(Exception e)
        {

        }
    }
}
