package com.test.aibackend.config;

import com.test.aibackend.util.MessageFormatter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public MessageFormatter messageFormatter() {
        return new MessageFormatter();
    }
}
