package org.esmjaga.playground.message.App;

import com.rabbitmq.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ReplyToSender {

    private Logger logger = LoggerFactory.getLogger(ReplyToSender.class);
    @Autowired
    Connection connection;
    Channel channel;
    final String cid ;

    ReplyToSender()
    {
       cid = "X3465TG";
    }
    void initialize()
    {
        try {
            channel = connection.createChannel();
            // purpose built exchange for this sender
            // the ex will be a direct one with durability enabled and exclusivity, auto-delete disabled
            channel.exchangeDeclare("rpc_ex",BuiltinExchangeType.DIRECT,true,false,false,null);
            // associated dlx ex
            channel.exchangeDeclare("rpc_dlx",BuiltinExchangeType.DIRECT);
            // the queue bound to the ex
            // the queue will have dlx setup as well
            Map<String,Object> map = new HashMap<>();
            map.put("x-dead-letter-exchange","dlx_rpc");
            map.put("x-dead-letter-routing-key","rpc_dlx_routing");
            // durable but non-exclusive and non-auto delete
            channel.queueDeclare("rpc_queue",true,false,false,map);
            // the corresponding dlx queue
            channel.queueDeclare("rpc_dlx_queue",true,false,false,null);
            // now declaring the 'replyTo' queue which must be used by the consumers to publish and this class to consume
            channel.queueDeclare("rpc_reply_queue",true,false,false,null);
            // lets bind all together
            channel.queueBind("rpc_queue","rpc_ex","rpc_queue_routing");
            channel.queueBind("rpc_dlx_queue","rpc_dlx","rpc_dlx_routing");
            channel.queueBind("rpc_reply_queue","rpc_ex","rpc_reply_queue_routing");
        }
        catch(Exception e)
        {

        }
    }

    // this method will have to be async as i will block on the future to retrieve result from replyto queue
    @Async
    void send()
    {
        try {
            String msg ="this message is an rpc call";

            // publish the message with replyTo and correlationID set
            AMQP.BasicProperties prop = new AMQP.BasicProperties()
                    .builder()
                    .appId("my_rpc")
                    .contentType("application/text")
                    .replyTo("rpc_reply_queue")
                    .correlationId(cid)
                    .build();
            // the mandatory property is set to true
            // may help if i want to add a return listener to catch the routing errors if any
            logger.info("publish to rpc queue with data:"+msg);
            channel.basicPublish("rpc_ex","rpc_queue_routing", true,prop,msg.getBytes(StandardCharsets.UTF_8));
            // this is the code where we consume from the 'replyTo' queue
            CompletableFuture<String> response = new CompletableFuture<>();
            MessageConsumer mcons = new MessageConsumer(channel)
            {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    if(properties.getCorrelationId().equals(cid)) {
                        // id matches
                        // process the result
                        logger.info("message correlated and consumed from the reply queue");
                        String result = new String(body);
                        logger.info("content is:"+ result);
                        // ack message and no multiple ack
                        channel.basicAck(envelope.getDeliveryTag(),false);
                        response.complete(result);
                    }
                    else {
                        // correlation id match failed
                        // nack the message and do not requeue it
                        // no multiple nacks either
                        channel.basicNack(envelope.getDeliveryTag(),false,false);
                    }
                }
            };
            // consuming from the replyTo queue
            // auto ack is false
            channel.basicConsume("rpc_reply_queue",false,mcons);
            // blocks here until the response is sent by reciever on the reply queue
            String output = response.get();
            logger.info("all done and now i quit");

        }
        catch(Exception e)
        {

        }
    }
}
