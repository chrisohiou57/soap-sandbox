package com.chrias.accountcamelrouting.route;

import java.util.Arrays;
import java.util.List;

import com.chrias.accountcamelrouting.processor.InvestmentCreateAccountRequestMessageProcessor;
import com.chrias.accountcamelrouting.processor.InvestmentCreateAccountResponseMessageProcessor;
import com.chrias.accountcamelrouting.processor.RetailCreateAccountRequestMessageProcessor;
import com.chrias.accountcamelrouting.processor.RetailCreateAccountResponseMessageProcessor;
import com.chrias.camelsoapmodel.account.AccountType;
import com.chrias.camelsoapmodel.account.CreateAccountResponse;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.cxf.common.message.CxfConstants;
import org.apache.cxf.message.MessageContentsList;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AccountCreationRoute extends RouteBuilder {

    @Value("${jms.queue.account.creation}")
    private String accountCreationQueue;

    private static List<AccountType> retailAccountTypes = Arrays.asList(
        AccountType.CHECKING, AccountType.SAVINGS, AccountType.CERTIFICATE_OF_DEPOSIT
    );

    @Override
    public void configure() throws Exception {
        from("cxf:bean:cxfCreateAccountService?dataFormat=POJO")            
            .onCompletion().onCompleteOnly()
                .to("direct:publishAccountCreation")
            .end()
            .onCompletion().onFailureOnly()
                .to("micrometer:counter:camelcreateAccountApiFailureCounter")
            .end()
            // Here we begin the normal route. The EIP above executes depending on the result below.
            .to("micrometer:counter:camelcreateAccountApiCounter")
            .choice()
                .when(exchange -> {
                    MessageContentsList payload = exchange.getIn().getBody(MessageContentsList.class);
                    AccountType accountType = ((com.chrias.camelsoapmodel.account.CreateAccountRequest) payload.get(0)).getAccount().getAccountType();
                    return retailAccountTypes.contains(accountType);
                })
                    .to("direct:callRetailCreateAccountService")
                .otherwise()
                    .to("direct:callInvestmentCreateAccountService")
            .endChoice()
        .end();

        from("direct:callRetailCreateAccountService")
            .log("Forwarding Account Creation Request to Retail Backend: ${body}")
            .to("micrometer:counter:camelcreateRetailAccountApiCounter")
            .process(new RetailCreateAccountRequestMessageProcessor())
            .setHeader(CxfConstants.OPERATION_NAME, constant("createAccount"))
            .setHeader(CxfConstants.OPERATION_NAMESPACE, constant("http://www.chrias.com/retailaccount"))
            .to("cxf:bean:retailAccountServiceBackend?dataFormat=POJO")
            .process(new RetailCreateAccountResponseMessageProcessor())
            .log("Retail Backend Response: ${body}")
            .end();

        from("direct:callInvestmentCreateAccountService")
            .log("Forwarding Account Creation Request to Investment Backend: ${body}")
            .to("micrometer:counter:camelcreateInvestmentAccountApiCounter")
            .process(new InvestmentCreateAccountRequestMessageProcessor())
            .setHeader(CxfConstants.OPERATION_NAME, constant("createAccount"))
            .setHeader(CxfConstants.OPERATION_NAMESPACE, constant("http://www.chrias.com/investmentaccount"))
            .to("cxf:bean:investmentAccountServiceBackend?dataFormat=POJO")
            .process(new InvestmentCreateAccountResponseMessageProcessor())
            .log("Investment Backend Response: ${body}")
            .end();

        from("direct:publishAccountCreation")
            .process(exchange -> {
                CreateAccountResponse response = (CreateAccountResponse) exchange.getIn().getBody();
                exchange.getMessage().setBody(response.getAccount());
            })
            .marshal().json()
            .log("Publishing account creation message in JSON format: ${body}")
            .to(String.format("jms:%s?jmsMessageType=Text", accountCreationQueue))
            .end();
    }
    
}
