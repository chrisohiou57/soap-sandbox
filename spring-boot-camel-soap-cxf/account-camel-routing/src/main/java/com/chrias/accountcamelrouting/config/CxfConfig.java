package com.chrias.accountcamelrouting.config;

import org.apache.camel.component.cxf.CxfEndpoint;
import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.transport.servlet.CXFServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CxfConfig {

    private static Logger log = LoggerFactory.getLogger(CxfConfig.class);

    @Value("${api.uri.scheme}")
    private String apiUriScheme;

    @Value("${retail.account.api.svc.service.host}")
    private String retailSoapApiHost;

    @Value("${retail.account.api.svc.service.port}")
    private String retailSoapApiPort;

    @Value("${investment.acct.api.svc.service.host}")
    private String investmentSoapApiHost;

    @Value("${investment.acct.api.svc.service.port}")
    private String investmentSoapApiPort;

    @Bean
    public ServletRegistrationBean<CXFServlet> cxfServlet() {
        return new ServletRegistrationBean<CXFServlet>(new CXFServlet(), "/camel/*");
    }

    @Bean(name="cxf", destroyMethod = "shutdown")
    public SpringBus springBus() {
        return new SpringBus();
    }

    @Bean
    public CxfEndpoint cxfGetAccountService() throws ClassNotFoundException {
        CxfEndpoint endpoint = new CxfEndpoint();
        endpoint.setAddress("/getAccounts");
        endpoint.setServiceClass("com.chrias.camelsoapmodel.account.AccountsPort");
        endpoint.setDefaultOperationName("getAccounts");
        return endpoint;
    }

    @Bean
    public CxfEndpoint cxfCreateAccountService() throws ClassNotFoundException {
        CxfEndpoint endpoint = new CxfEndpoint();
        endpoint.setAddress("/createAccount");
        endpoint.setServiceClass("com.chrias.camelsoapmodel.account.AccountsPort");
        endpoint.setDefaultOperationName("createAccount");
        return endpoint;
    }

    @Bean
    public CxfEndpoint retailAccountServiceBackend() throws ClassNotFoundException {
        log.debug("Configuring retail SOAP API backend with uri: {}://{}:{}", apiUriScheme, retailSoapApiHost, retailSoapApiPort);
        CxfEndpoint endpoint = new CxfEndpoint();
        endpoint.setAddress(String.format("%s://%s:%s/retail/account", apiUriScheme, retailSoapApiHost, retailSoapApiPort));
        endpoint.setServiceClass("com.chrias.soapmodel.retail.RetailAccountsPort");
        return endpoint;
    }

    @Bean
    public CxfEndpoint investmentAccountServiceBackend() throws ClassNotFoundException {
        log.debug("Configuring investment SOAP API backend with uri: {}://{}:{}", apiUriScheme, investmentSoapApiHost, investmentSoapApiPort);
        CxfEndpoint endpoint = new CxfEndpoint();
        endpoint.setAddress(String.format("%s://%s:%s/investment/account", apiUriScheme, investmentSoapApiHost, investmentSoapApiPort));
        endpoint.setServiceClass("com.chrias.soapmodel.investment.InvestmentAccountsPort");
        return endpoint;
    }

    // @Bean
    // public CxfEndpoint retailCreateAccountServiceBackend() throws ClassNotFoundException {
    //     log.debug("Configuring retail account creation SOAP API backend with uri: {}://{}:{}", apiUriScheme, retailSoapApiHost, retailSoapApiPort);
    //     CxfEndpoint endpoint = new CxfEndpoint();
    //     endpoint.setAddress(String.format("%s://%s:%s/retail/account", apiUriScheme, retailSoapApiHost, retailSoapApiPort));
    //     endpoint.setServiceClass("com.chrias.soapmodel.retail.RetailAccountsPort");
    //     return endpoint;
    // }

    // @Bean
    // public CxfEndpoint investmentCreateAccountServiceBackend() throws ClassNotFoundException {
    //     log.debug("Configuring investment account creation SOAP API backend with uri: {}://{}:{}", apiUriScheme, investmentSoapApiHost, investmentSoapApiPort);
    //     CxfEndpoint endpoint = new CxfEndpoint();
    //     endpoint.setAddress(String.format("%s://%s:%s/investment/account", apiUriScheme, investmentSoapApiHost, investmentSoapApiPort));
    //     endpoint.setServiceClass("com.chrias.soapmodel.investment.InvestmentAccountsPort");
    //     return endpoint;
    // }
    
}
