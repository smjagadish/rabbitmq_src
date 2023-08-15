package org.esmjaga.playground.message.App;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RestController;

@SpringBootConfiguration
public class Configuration {
    @Bean
    public Sender sendBean()
    {
        return new Sender();
    }
    @Bean
    public Reciever recieveBean()
    {
        return new Reciever();
    }
}
