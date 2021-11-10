package com.chrias.accountsoapservice.config;

import javax.xml.ws.Endpoint;

import com.chrias.accountsoapservice.service.InvestmentAccountService;
import com.chrias.accountsoapservice.webservice.InvestmentAccountWebService;

import org.apache.cxf.Bus;
import org.apache.cxf.jaxws.EndpointImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("investment")
public class CxfConfigInvestment {
    
    private Bus bus;

    public CxfConfigInvestment(Bus bus) {
        this.bus = bus;
    }

    @Bean
    public Endpoint retailAccountServiceEndpoint(InvestmentAccountWebService retailAccountWebService) {
        EndpointImpl endpoint = new EndpointImpl(bus, retailAccountWebService);
        endpoint.publish("/account");
        return endpoint;
    }

    @Bean
    public InvestmentAccountService investmentAccountRetrievalService() {
        return new InvestmentAccountService();
    }

    @Bean
    public InvestmentAccountWebService retailAccountWebService(InvestmentAccountService investmentAccountRetrievalService) {
        return new InvestmentAccountWebService(investmentAccountRetrievalService);
    }
    
}
