package com.chrias.accountcamelrouting.aggregation;

import com.chrias.camelsoapmodel.account.GetAccountsResponse;

import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;

public class AccountAggregationStrategy implements AggregationStrategy {

    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        // If we don't have both exchanges yet return the newExchange and aggregate() will be called again.
        if (oldExchange == null) {
            return newExchange;
        }
        GetAccountsResponse oldResponse = oldExchange.getIn().getBody(GetAccountsResponse.class);
        GetAccountsResponse newResponse = newExchange.getIn().getBody(GetAccountsResponse.class);
        oldResponse.getAccount().addAll(newResponse.getAccount());
        oldExchange.getIn().setBody(oldResponse);
        return oldExchange;
    }
    
}
