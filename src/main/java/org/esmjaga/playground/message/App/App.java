package org.esmjaga.playground.message.App;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@SpringBootApplication
@EnableAsync
public class App {


  public static void main(String[] args) throws IOException, TimeoutException {
    ApplicationContext ctx = SpringApplication.run(App.class, args);
    //stub stb= ctx.getBean(stub.class);
    //stb.initialize();
    //stb.send();
    ctx.getBean(Sender.class).initialize();
    ctx.getBean(Sender.class).send("hello");
    ctx.getBean(NackSender.class).initialize();
    ctx.getBean(NackSender.class).send();
    ctx.getBean(quorumSender.class).initialize();
    ctx.getBean(quorumSender.class).send();
    ctx.getBean(Reciever.class).initialize();
    ctx.getBean(Reciever2.class).initialize();
    ctx.getBean(quorumReciever.class).initialize();
    ctx.getBean(Reciever.class).recieve();
    ctx.getBean(Reciever2.class).recieve();
    ctx.getBean(quorumReciever.class).recieve();
    // demonstrate exclusive queue
    ctx.getBean(Reciever2.class).del();

  }

 /* @Component

  private class stub {
    @Autowired
    NackSender nackSender;
    @Autowired
    Sender sender;
    @Autowired
    Reciever reciever;
    @Autowired
    Reciever2 reciever2;

    stub() {

    }

    void initialize() {
      try {
        nackSender.initialize();
        sender.initialize();
        reciever.initialize();
        reciever2.initialize();
      } catch (Exception e) {

      }
    }
    void send()
    {
      try {
        sender.send("text_msg");
        nackSender.send();
        reciever.recieve();
        reciever2.recieve();
      }
      catch(Exception e)
      {

      }
    }
  }*/
}
