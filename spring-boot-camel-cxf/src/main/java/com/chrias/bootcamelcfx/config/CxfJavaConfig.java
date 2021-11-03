package com.chrias.bootcamelcfx.config;

import javax.xml.ws.Endpoint;
import com.chrias.bootcamelcfx.endpoint.EmployeeServiceEndpoint;
import org.apache.camel.component.cxf.CxfEndpoint;
import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.jaxws.EndpointImpl;
import org.apache.cxf.transport.servlet.CXFServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/***************************************************************
* These imports are needed for the HTTPConduitFactory Java config
***************************************************************/
// import org.springframework.core.io.ClassPathResource;
// import org.springframework.core.io.Resource;
// import org.apache.cxf.transport.http.HTTPConduit;
// import org.apache.cxf.transport.http.HTTPConduitFactory;
// import org.apache.cxf.transport.http.HTTPTransportFactory;
// import org.apache.cxf.transport.http.URLConnectionHTTPConduit;
// import org.apache.cxf.service.model.EndpointInfo;
// import org.apache.cxf.ws.addressing.EndpointReferenceType;
// import org.apache.cxf.configuration.jsse.TLSClientParameters;
// import java.io.IOException;
// import java.security.KeyStore;
// import javax.net.ssl.KeyManager;
// import javax.net.ssl.KeyManagerFactory;
// import javax.net.ssl.TrustManager;
// import javax.net.ssl.TrustManagerFactory;
// import org.apache.cxf.Bus;

/**
 * To use this Java config you need to enable the javaconfig Spring profile (e.g. --spring.profiles.active=javaconfig)
 */
@Configuration
@Profile("javaconfig")
public class CxfJavaConfig {

    Logger log = LoggerFactory.getLogger(CxfJavaConfig.class);
    
    /**
     * This will map all CXF traffic to http://localhost:8080/soap/*
     */
    @Bean
    public ServletRegistrationBean<CXFServlet> cxfServlet() {
        return new ServletRegistrationBean<CXFServlet>(new CXFServlet(), "/soap/*");
    }

    /**
     * This is required for the CXF Java config. I could have also named the method
     * cxf() instead of explicity naming the bean.
     */
    @Bean(name="cxf", destroyMethod = "shutdown")
    public SpringBus springBus() {
        return new SpringBus();
    }

    /**
     * This is a JAX-WS CXF SOAP webservice endpoint. JAX-WS endpoints are used to bind a web service endpoint with a Java
     * implementation class. This shouldn't be confused with a CxfEndpoint which is used to to bind a webservice endpoint
     * with a Camel route.
     * 
     * You can use the EmployeeServices.wsdl with SOAP UI to consume this endpoint at http://localhost:8080/soap/JaxWsEmployeeService
     */
    @Bean
    public Endpoint employeeService(SpringBus bus, EmployeeServiceEndpoint employeeServiceEndpoint) {
        EndpointImpl endpoint = new EndpointImpl(bus, employeeServiceEndpoint);
        endpoint.publish("/JaxWsEmployeeService");
        endpoint.setWsdlLocation("EmployeeServices.wsdl");
        return endpoint;
    }

    /**
     * This creates a remote SOAP webservice proxy endpoint that can be bound to a Camel route. A webservice
     * client could hit this endpoint, the Camel route could update the SOAP request, and forward it to the remote
     * webservice. In this example, it would forward the request to the CxfEndpoint below. This would be considered
     * a consumer CxfEndpoint according to the <a href="https://camel.apache.org/components/3.12.x/cxf-component.html">CXF Camel Component</a>
     * since it is bound to the from() endpoint DSL.
     * <br/><br/>
     * You can use the EmployeeServices.wsdl with SOAP UI to consume this endpoint at http://localhost:8080/soap/CxfEmployeeServiceProxy
     * <br/><br/>
     * @see com.chrias.bootcamelcfx.route.CfxSoapServiceProxyRoute
     */
    @Bean
    public CxfEndpoint cxfEmployeeServiceProxy() throws ClassNotFoundException {
        CxfEndpoint endpoint = new CxfEndpoint();
        endpoint.setAddress("/CxfEmployeeServiceProxy");
        endpoint.setServiceClass("com.chrias.bootcamelcfx.endpoint.EmployeeServiceEndpoint");
        return endpoint;
    }

    /**
     * This CxfEndpoint invokes a remote SOAP webservice. This would be considered a producer CxfEndpoint according to the
     * <a href="https://camel.apache.org/components/3.12.x/cxf-component.html">CXF Camel Component</a> since it is bound to 
     * the to() endpoint DSL.
     * <br/><br/>
     * @see com.chrias.bootcamelcfx.route.CfxSoapServiceProxyRoute
     */
    @Bean
    public CxfEndpoint cxfEmployeeServiceBackend() throws ClassNotFoundException {
        CxfEndpoint endpoint = new CxfEndpoint();
        endpoint.setAddress("https://localhost:8043/soap/service/employee");
        endpoint.setServiceClass("com.chrias.bootcamelcfx.endpoint.EmployeeServiceEndpoint");
        return endpoint;
    }

    /**
     * This creates a CXF HTTP conduit for handling SSL mutual authentication.
     * <br/><br/>
     * This is the Java config that matches the XML config in META-INF/cxf/camel-config.xml. It can be
     * excluded and mutual authentication will still work with remote web services because the Spring Boot
     * camel-cxf-starter dependency has configuration options for SSL. That config can be found in application.properties.
     * If you excluded the Spring Boot starter config in application.properties and enable this code everything will still work.
     */
    /*@Bean
    public HTTPConduitFactory httpConduit() {
        return new HTTPConduitFactory() {
            @Override
            public HTTPConduit createConduit(HTTPTransportFactory f, Bus b, EndpointInfo localInfo, EndpointReferenceType target) throws IOException {
                String keystorePassword = "changeit";
                Resource keyStoreResource = new ClassPathResource("client-app.jks");
                
                TLSClientParameters tlsClientParams = new TLSClientParameters();
                // tlsClientParams.setDisableCNCheck(true);
                tlsClientParams.setCertAlias("client-app");

                HTTPConduit conduit = new URLConnectionHTTPConduit(b, localInfo);

                try {
                    KeyStore keyStore = KeyStore.getInstance("PKCS12");
                    keyStore.load(keyStoreResource.getInputStream(), keystorePassword.toCharArray());
    
                    TrustManagerFactory trustFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                    trustFactory.init(keyStore);
    
                    TrustManager[] trustManagers = trustFactory.getTrustManagers();
                    tlsClientParams.setTrustManagers(trustManagers);
    
                    KeyManagerFactory keyFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
                    keyFactory.init(keyStore, keystorePassword.toCharArray());

                    KeyManager[] keyManagers = keyFactory.getKeyManagers();
                    tlsClientParams.setKeyManagers(keyManagers);
                    
                    conduit.setTlsClientParameters(tlsClientParams);
                } catch (Exception e) {
                    log.error("Error creating custom HTTPConduit", e);
                }
                
                return conduit;
            }
        };
    }*/
}
