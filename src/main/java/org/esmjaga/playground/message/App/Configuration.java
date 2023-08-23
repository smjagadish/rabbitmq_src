package org.esmjaga.playground.message.App;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.context.properties.bind.Name;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeoutException;

@SpringBootConfiguration
public class Configuration {
    @Bean
    public Sender sendBean()
    {
        return new Sender();
    }

   @Bean(name = "r1")
    public Reciever recieveBean()
    {
        return new Reciever();
    }

    @Bean(name = "r2")
    public Reciever2 recieveBean2()
    {
        return new Reciever2();
    }

    @Bean
    public Connection getConnection(ConnectionFactory cf) throws IOException, TimeoutException {
        // also possible to set username,password
        cf.setHost("localhost");
        return cf.newConnection();
    }
    @Bean
    public ConnectionFactory getCF()
    {
        return new ConnectionFactory();
    }

    @Bean
    public NackSender nackSender()
    {
        return new NackSender();
    }
}
