package com.chrias.springbootcxf.springbootcxf.config;

import com.chrias.springbootcxf.springbootcxf.endpoint.EmployeeEndpoint;

import org.apache.cxf.Bus;
import org.apache.cxf.jaxws.EndpointImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import javax.xml.ws.Endpoint;

@Configuration
public class CxfConfig {
    
    private Bus bus;

    public CxfConfig(Bus bus) {
        this.bus = bus;
    }

    @Bean
    public Endpoint endpoint(EmployeeEndpoint employeeEndpoint) {
        EndpointImpl endpoint = new EndpointImpl(bus, employeeEndpoint);
        endpoint.publish("/service/employee");
        return endpoint;
    }

}
