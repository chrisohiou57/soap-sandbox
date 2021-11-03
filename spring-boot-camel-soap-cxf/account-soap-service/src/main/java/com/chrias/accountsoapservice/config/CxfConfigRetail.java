package com.chrias.accountsoapservice.config;

import javax.xml.ws.Endpoint;

import com.chrias.accountsoapservice.service.RetailAccountRetrievalService;
import com.chrias.accountsoapservice.webservice.RetailAccountWebService;

import org.apache.cxf.Bus;
import org.apache.cxf.jaxws.EndpointImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("retail")
public class CxfConfigRetail {

    private Bus bus;

    public CxfConfigRetail(Bus bus) {
        this.bus = bus;
    }

    @Bean
    public Endpoint retailAccountServiceEndpoint(RetailAccountWebService retailAccountWebService) {
        EndpointImpl endpoint = new EndpointImpl(bus, retailAccountWebService);
        endpoint.publish("/account");
        return endpoint;
    }

    @Bean
    public RetailAccountRetrievalService retailAccountRetrievalService() {
        return new RetailAccountRetrievalService();
    }

    @Bean
    public RetailAccountWebService retailAccountWebService(RetailAccountRetrievalService retailAccountRetrievalService) {
        return new RetailAccountWebService(retailAccountRetrievalService);
    }
    
}
