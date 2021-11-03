package com.chrias.accountcamelrouting.bean;

import javax.annotation.PostConstruct;

import org.apache.camel.opentelemetry.OpenTelemetryTracer;
import org.springframework.stereotype.Component;

// @Component
public class HelloTelemetry {

    private OpenTelemetryTracer tracer;

    public HelloTelemetry(OpenTelemetryTracer tracer) {
        this.tracer = tracer;
    }

    @PostConstruct
    public void stop() {
        System.out.println("x");
    }
    
}
