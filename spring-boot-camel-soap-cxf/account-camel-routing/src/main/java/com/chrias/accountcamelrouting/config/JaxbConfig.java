package com.chrias.accountcamelrouting.config;

import org.apache.camel.model.dataformat.JaxbDataFormat;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JaxbConfig {

    @Bean
    public JaxbDataFormat jaxbDataFormat() {
        return new JaxbDataFormat();
    }
    
}
