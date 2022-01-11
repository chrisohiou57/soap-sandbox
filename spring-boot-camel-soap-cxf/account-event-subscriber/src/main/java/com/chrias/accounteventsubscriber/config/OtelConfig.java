package com.chrias.accounteventsubscriber.config;

import org.apache.camel.CamelContext;
import org.apache.camel.opentelemetry.OpenTelemetryTracer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OtelConfig {

    @Bean
    public OpenTelemetryTracer openTelemetryTracer(CamelContext context) {
        OpenTelemetryTracer tracer = new OpenTelemetryTracer();
        tracer.init(context);
        return tracer;
    }

}
