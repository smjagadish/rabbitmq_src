package org.esmjaga.playground.message.App;

import com.rabbitmq.client.*;
import com.rabbitmq.client.impl.AMQBasicProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Sender {
    private final static String QUEUE_NAME_1 = "queue1";
    private final static String QUEUE_NAME_2 = "queue2";
    private final static Logger logger = LoggerFactory.getLogger(Sender.class);

    //private Connection connection;
    //private ConnectionFactory factory;
     Channel channel;
     Channel channel2;
    @Autowired
    @Qualifier("localconn")
    Connection connection;
     String trans_q;
    Sender() {

    }

     void initialize() {
        logger.info("creating rabbitmq connection");
        try {
            channel = connection.createChannel();
            channel2 = connection.createChannel();
            channel.exchangeDeclare("mainEx","direct");
            boolean durable = true;
            // marking queue as durable so that it can survive broker restart/crash
            // the queues are also non-exclusive and non auto-delete
            // the queues do not declare any special property (like message ttl , priorities etc.)
            channel.queueDeclare(QUEUE_NAME_1, durable, false, false, null);
            channel.queueDeclare(QUEUE_NAME_2, durable, false, false, null);
            // example for a queue that is durable , non-exclusive , non-auto delete and declares properties
            Map<String,Object> map = new HashMap<>();
            map.put("x-message-ttl",1000);
            map.put("x-max-priority",10);
            map.put("x-max-length",100);
            // assigning a dead letter ex
            // be sure to create the dead_ex before binding a queue
            map.put("x-dead-letter-exchange","dead_ex");
            map.put("x-dead-letter-routing-key","dlx");
            channel.queueDeclare("customqueue",true,false,false,map);
            channel.exchangeDeclare("dead_ex","direct",true);
            channel.queueDeclare("dead_queue",true,false,false,null);
            channel.queueBind("dead_queue","dead_ex","dlx");
            // exclusive queue . This queue is later used in Reciever2.java
            // the queue is also auto-delete
            trans_q = channel.queueDeclare().getQueue();
            // sample code for routing error
            // exchange and queue declared but no binding
            channel.exchangeDeclare("unreachable_ex","direct");
            channel.queueDeclare("unreachable_queue",true,false,false,null);
            // sample code for a topic exchange and bound queues
            // can be a good use case for binding queues based on wildcards/pattern-matching
            channel.exchangeDeclare("topic_ex",BuiltinExchangeType.TOPIC);
            channel.queueDeclare("topic_q1",true,false,false,null);
            channel.queueDeclare("topic_q2",true,false,false,null);
            // topic_q1 will match messages consisting of 3 words de-limited by a '.' and the middle word must be q1_msg
            channel.queueBind("topic_q1","topic_ex","*.q1_msg.*");
            // topic_q2 will match messages consisting of any num of words , but the first word must be q2_msg
            channel.queueBind("topic_q2","topic_ex","q2_msg.#");
            // finally adding a routing error call back impl for the channel
            channel.addReturnListener(new RoutingErrorCallback());
            // if producer confirms (i.e. broker ack is needed , uncomment below lines
            // similar to consumer acks, broker may ack one msg at a time or for a group of msg
            // if the message couldn't be processed by the broker, it may do a nack
            // onus is on producer to re-attempt delivery if a nack is sent by broker
            // of course , makes sense to turn on producer confirms before any message is written to the queue
            // results processed asynchronously in the provided callback
            //channel.confirmSelect();
           /* channel.addConfirmListener(new ConfirmListener() {
                @Override
                public void handleAck(long l, boolean b) throws IOException {

                }

                @Override
                public void handleNack(long l, boolean b) throws IOException {

                }
            });*/
        }
        catch (Exception e)
        {

        }
    }
    void send(String msg)
    {
        try {
            // to default built-in exchange
            // default exchange routes to queues based on queue name
            // so routing key (2nd arg) is the queue name
            channel.basicPublish("", QUEUE_NAME_1, null, msg.getBytes());
            System.out.println(" [x] Sent to " + QUEUE_NAME_1  + " with:" + msg);
            // again to default built-in exchange
            channel.basicPublish("", QUEUE_NAME_2, null, msg.getBytes());
            System.out.println(" [x] Sent to " + QUEUE_NAME_2  + " with:" + msg);
            String pp ="police";
            // to the exclusive queue
            channel.basicPublish("",trans_q,null, pp.getBytes());
            System.out.println(" [x] Sent to " + trans_q  + " with:" + pp);
            //routing error case
            //publish to exchange which doesnt have queue bound to it
            // the 'true' sets mandatory to enabled, which is a must for the return listener to be invoked
            channel.basicPublish("unreachable_ex","unreachable_queue", true,null,msg.getBytes());
            // example for a message publish with  properties explicitly specified
            AMQP.BasicProperties basicProperties = new AMQP.BasicProperties().builder()
                    .appId("esmjaga-app") // any custom app-id
                    .contentEncoding("UTF-8") // for msg extrapolation
                    .contentType("application/json") // for msg extrapolation
                    .priority(5) // if prio-queue is used
                    .expiration("30") // not TTL, but expiration at individual msg level. if queue level TTL is low, that takes precedence
                    .build(); // gives the property object
            channel.basicPublish("",QUEUE_NAME_1,basicProperties,msg.getBytes());
            // now publishing to a topic exchange and routing is working on the basis of binding rules
            String topic_msg="this is from topic";
            channel.basicPublish("topic_ex","this.q1_msg.works_now",null,topic_msg.getBytes());
            channel.basicPublish("topic_ex","this.q1_msg.works_now_again",null,topic_msg.getBytes());
            // another sample of publishing to a topic exchange
            channel.basicPublish("topic_ex","q2_msg.this string doesn't matter",null,topic_msg.getBytes());
            // publish to topic exchange, but msg will get dropped as it doesn't match any patterns
            channel.basicPublish("topic_ex","non matching.msg",null,topic_msg.getBytes());

        }
        catch(Exception e)
        {

        }
    }
}
