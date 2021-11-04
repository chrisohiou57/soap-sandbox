package com.chrias.accountsoapservice.config;

import org.apache.cxf.bus.spring.SpringBus;
import org.springframework.context.annotation.Bean;
import io.opentelemetry.javaagent.instrumentation.cxf.TracingEndInInterceptor;
import io.opentelemetry.javaagent.instrumentation.cxf.TracingOutFaultInterceptor;
import io.opentelemetry.javaagent.instrumentation.cxf.TracingStartInInterceptor;

public class CxfConfig {

    @Bean
    public SpringBus cxf() {
        SpringBus bus = new SpringBus();
        bus.getInInterceptors().add(new TracingStartInInterceptor());
        bus.getOutInterceptors().add(new TracingEndInInterceptor());
        bus.getOutFaultInterceptors().add(new TracingOutFaultInterceptor());
        return bus;
    }
    
}
