package com.chrias.bootcamelcfx.route;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class CfxSoapServiceProxyRoute extends RouteBuilder {

@Override
    public void configure() throws Exception {

        from("cxf:bean:cxfEmployeeServiceProxy")
            .log("Forwarding Request to Backend: ${body}")
            .to("cxf:bean:cxfEmployeeServiceBackend")
            .log("Backend Response: ${body}");

    }
    
}
