package com.chrias.accountcamelrouting.config;

import org.apache.camel.CamelContext;
import org.apache.camel.opentelemetry.OpenTelemetryTracer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenTelementryConfig {

    Logger log = LoggerFactory.getLogger(OpenTelementryConfig.class);

    // @Bean
    // public OpenTelemetryTracer openTelemetryTracer(CamelContext camelContext) {
    //     log.debug("Initializing the OpenTelemetryTracer");
    //     OpenTelemetryTracer tracer = new OpenTelemetryTracer();
    //     tracer.init(camelContext);
    //     return tracer;
    // }
    
}
