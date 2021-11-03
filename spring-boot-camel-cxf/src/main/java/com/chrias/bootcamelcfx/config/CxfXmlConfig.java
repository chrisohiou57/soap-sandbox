package com.chrias.bootcamelcfx.config;

import org.apache.cxf.transport.servlet.CXFServlet;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Profile;

/**
 * To use this Java config you need to enable the javaconfig Spring profile (e.g. --spring.profiles.active=xmlconfig)
 */
@Configuration
@ImportResource(locations = {"classpath:META-INF/cxf/camel-config.xml"})
@Profile("xmlconfig")
public class CxfXmlConfig {
    
    /**
     * This bean could also have been defined in camel-config.xml. However, this code
     * demonstrates that you can combine XML and Java configs.
     */
    @Bean
    public ServletRegistrationBean<CXFServlet> cxfServlet() {
        return new ServletRegistrationBean<CXFServlet>(new CXFServlet(), "/soap/*");
    }
    
}
