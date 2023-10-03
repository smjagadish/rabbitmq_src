package org.esmjaga.playground.message.App;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class RemoteApp {
    public static void main(String[] args)
    {
        ApplicationContext ctx = SpringApplication.run(RemoteApp.class,args);
        ctx.getBean(AzureSender.class).initialize();
        ctx.getBean(AzureSender.class).send();
    }
}
