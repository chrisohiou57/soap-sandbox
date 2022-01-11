package com.chrias.accountcamelrouting.route;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.chrias.accountcamelrouting.processor.InvestmentCreateAccountRequestMessageProcessor;
import com.chrias.accountcamelrouting.processor.InvestmentCreateAccountResponseMessageProcessor;
import com.chrias.accountcamelrouting.processor.RetailCreateAccountRequestMessageProcessor;
import com.chrias.accountcamelrouting.processor.RetailCreateAccountResponseMessageProcessor;
import com.chrias.accountcamelrouting.security.WsSecuritySoapHeaderRemovalProcessor;
import com.chrias.camelsoapmodel.account.Account;
import com.chrias.camelsoapmodel.account.AccountType;
import com.chrias.camelsoapmodel.account.CreateAccountRequest;
import com.chrias.camelsoapmodel.account.CreateAccountResponse;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.aws2.s3.AWS2S3Constants;
import org.apache.camel.component.cxf.common.message.CxfConstants;
import org.apache.camel.model.dataformat.JaxbDataFormat;
import org.apache.cxf.message.MessageContentsList;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AccountCreationRoute extends RouteBuilder {

    @Value("${jms.queue.account.creation}")
    private String accountCreationQueue;

    @Value("${sqs.account.creation.arn}")
    private Object sqsAccountCreationArn;

    @Value("${s3.account.creation.arn}")
    private String s3AccountCreationArn;

    private JaxbDataFormat jaxbDataFormat;

    private static List<AccountType> retailAccountTypes = Arrays.asList(
        AccountType.CHECKING, AccountType.SAVINGS, AccountType.CERTIFICATE_OF_DEPOSIT
    );

    private static final String ACCOUNT_FILE_NAME_HEADER = "accountFileName";

    public AccountCreationRoute(JaxbDataFormat jaxbDataFormat) {
        this.jaxbDataFormat = jaxbDataFormat;
    }

    @Override
    public void configure() throws Exception {
        // TODO add routeId(s)
        from("cxf:bean:cxfCreateAccountService?dataFormat=POJO")            
            .onCompletion().onCompleteOnly()
                .to("direct:publishAccountCreation")
            .end()
            .onCompletion().onFailureOnly()
                .to("micrometer:counter:camelcreateAccountApiFailureCounter")
            .end()
            // Here we begin the normal route. The EIP above executes depending on the result below.
            .to("micrometer:counter:camelcreateAccountApiCounter")
            .process(new WsSecuritySoapHeaderRemovalProcessor())
            .choice()
                .when(exchange -> {
                    MessageContentsList payload = exchange.getIn().getBody(MessageContentsList.class);
                    AccountType accountType = ((CreateAccountRequest) payload.get(0)).getAccount().getAccountType();
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
            .multicast().parallelProcessing()
                .to("direct:accountCreationToArtemis")
                .to("direct:accountCreationToSQS")
                .to("direct:accountCreationToAmazonMQ")
                .to("direct:accountCreationToS3")
        .end();

        from("direct:accountCreationToArtemis").routeId("publish-acct-creation-artemis")
            // Setting these JAXB headers is a way we can handle multiple fragments. The application.properties file has a config for
            // it since we are only dealing with one fragment in this demo. This could all be configured with multiple JaxbDataFormat
            // beans that are injected where needed as well.
            // .setHeader(JaxbConstants.JAXB_PART_NAMESPACE, simple("http://www.chrias.com/account"))
            // .setHeader(JaxbConstants.JAXB_PART_CLASS, simple("com.chrias.camelsoapmodel.account.Account"))
            .marshal(jaxbDataFormat)
            .log("Publishing account creation message to Artemis in XML format: ${body}")
            .to(String.format("jms:%s?jmsMessageType=Text", accountCreationQueue))
        .end();

        from("direct:accountCreationToSQS").routeId("publish-acct-creation-sqs")
            .marshal().json()
            .log("Publishing account creation message to AWS SQS in JSON format: ${body}")
            .to(String.format("aws2-sqs://%s:%s", sqsAccountCreationArn, accountCreationQueue))
        .end();

        from("direct:accountCreationToAmazonMQ").routeId("publish-acct-creation-awsmq")
            .marshal().json()
            .log("Publishing account creation message to AmazonMQ in JSON format: ${body}")
            .to(String.format("amqp:%s?jmsMessageType=Text", accountCreationQueue))
        .end();

        from("direct:accountCreationToS3").routeId("publish-acct-creation-s3")
            .log("Publishing account creation file to AWS S3 in CSV format: ${body}")
            .process(exchange -> {
                Account account = (Account) exchange.getIn().getBody();

                Map<String, Object> csvValues = new HashMap<>();
                csvValues.put("customerId", account.getCustomerId());
                csvValues.put("accountNumber", account.getAccountNumber());
                csvValues.put("accountNickname", account.getAccountNickname());
                csvValues.put("accountType", account.getAccountType().toString());

                exchange.getMessage().setBody(csvValues);
                exchange.getMessage().setHeader(ACCOUNT_FILE_NAME_HEADER, String.format("%s.csv", account.getAccountNumber()));
            })
            .marshal().csv()
            .setHeader(AWS2S3Constants.KEY, header(ACCOUNT_FILE_NAME_HEADER))
            .to(String.format("aws2-s3://%s", s3AccountCreationArn))
        .end();
    }
    
}
