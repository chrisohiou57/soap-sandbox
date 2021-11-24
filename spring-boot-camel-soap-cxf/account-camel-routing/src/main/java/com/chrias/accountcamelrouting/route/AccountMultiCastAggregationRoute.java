package com.chrias.accountcamelrouting.route;

import com.chrias.accountcamelrouting.aggregation.AccountAggregationStrategy;
import com.chrias.accountcamelrouting.bean.InvestmentAccountRequestMessageConverter;
import com.chrias.accountcamelrouting.bean.RetailAccountResponseMessageConverter;
import com.chrias.accountcamelrouting.processor.RetailAccountRequestMessageProcessor;
import com.chrias.accountcamelrouting.processor.RetailAccountResponseMessageProcessor;
import com.chrias.accountcamelrouting.security.WsSecuritySoapHeaderRemovalProcessor;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.cxf.common.message.CxfConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class AccountMultiCastAggregationRoute extends RouteBuilder {

    Logger log = LoggerFactory.getLogger(AccountMultiCastAggregationRoute.class);

    @Override
    public void configure() throws Exception {
        /*
        TODO add fail on exception. It tripped me up when I was creating the investment AccountType incorrectly. It was still making it to the aggregator, but not with the body I was expecting.
        TODO how can I unit test the object mapping?
        TODO should there be a counter for invocation and succesful calls?
        */
        from("cxf:bean:cxfGetAccountService?dataFormat=POJO")
            .to("micrometer:counter:camelGetAccountApiCounter")
            .process(new WsSecuritySoapHeaderRemovalProcessor())
            .multicast(new AccountAggregationStrategy()).parallelProcessing()
                .to("direct:callRetailAccountService")
                .to("direct:callInvestmentAccountService")
            .end()
            .log("Aggregated Response: ${body}")
            .end();

        from("direct:callRetailAccountService")
            .log("Forwarding Request to Retail Backend: ${body}")
            .process(new RetailAccountRequestMessageProcessor())
            .setHeader(CxfConstants.OPERATION_NAME, constant("getAccounts"))
            .setHeader(CxfConstants.OPERATION_NAMESPACE, constant("http://www.chrias.com/retailaccount"))
            .to("cxf:bean:retailAccountServiceBackend?dataFormat=POJO")
            .process(new RetailAccountResponseMessageProcessor())
            .log("Retail Backend Response: ${body}")
            .end();

        from("direct:callInvestmentAccountService")
            .log("Forwarding Request to Investment Backend: ${body}")
            .bean(InvestmentAccountRequestMessageConverter.class, "convertRequestMessage")
            .setHeader(CxfConstants.OPERATION_NAME, constant("getAccounts"))
            .setHeader(CxfConstants.OPERATION_NAMESPACE, constant("http://www.chrias.com/investmentaccount"))
            .to("cxf:bean:investmentAccountServiceBackend?dataFormat=POJO")
            .bean(RetailAccountResponseMessageConverter.class, "convertResponseMessage")
            .log("Investment Backend Response: ${body}")
            .end();
    }
    
}
