package com.chrias.accountcamelrouting.security;

import java.util.List;
import java.util.stream.Collectors;

import javax.xml.namespace.QName;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.cxf.binding.soap.SoapHeader;
import org.apache.cxf.headers.Header;
import org.apache.wss4j.common.WSS4JConstants;

public class WsSecuritySoapHeaderRemovalProcessor implements Processor {
    private QName WSSE_SECURITY_HEADER = new QName(WSS4JConstants.WSSE_NS, "Security");

    @Override
    public void process(Exchange exchange) throws Exception {
        List<SoapHeader> soapHeaders = (List<SoapHeader>) exchange.getIn().getHeader(Header.HEADER_LIST);

        List<SoapHeader> filteredHeaders = soapHeaders
                .stream()
                .filter(header -> !header.getName().equals(WSSE_SECURITY_HEADER))
                .collect(Collectors.toList());

        exchange.getIn().setHeader(Header.HEADER_LIST, filteredHeaders);
    }
}
