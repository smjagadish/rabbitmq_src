package org.esmjaga.playground.message.App;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@SpringBootApplication
public class App {


    public static void main(String[] args) throws IOException, TimeoutException {
      ApplicationContext ctx= SpringApplication.run(App.class,args);

      Sender sender = ctx.getBean(Sender.class);
      sender.initialize();
      Reciever rcv = ctx.getBean(Reciever.class);
      rcv.initialize();

    }
}
