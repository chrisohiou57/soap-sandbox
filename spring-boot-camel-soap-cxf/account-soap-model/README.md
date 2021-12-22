# Overview
This project is used to generate JAX-WS artifacts for our SOAP services using the [cxf-codegen-plugin Maven plugin](https://cxf.apache.org/docs/maven-cxf-codegen-plugin-wsdl-to-java.html). When you build the project it will generate the JAX-WS artifcats and package them in a JAR. Packaging these classes in a JAR is useful as it can be used for the Camel API Gateway and account SOAP back-end service implementations.

You will notice that the configuration for the cxf-codegen-plugin maps the targetNamespace from our WSDLs to the package name of our choosing.
```
<wsdlOptions>
    <wsdlOption>
        <wsdl>${basedir}/src/main/resources/wsdl/retail/RetailAccount.wsdl</wsdl>
        <extraargs>
            <extraarg>-p</extraarg>
            <extraarg>http://www.chrias.com/retailaccount=com.chrias.soapmodel.retail</extraarg>
        </extraargs>
    </wsdlOption>
    <wsdlOption>
        <wsdl>${basedir}/src/main/resources/wsdl/investment/InvestmentAccount.wsdl</wsdl>
        <extraargs>
            <extraarg>-p</extraarg>
            <extraarg>http://www.chrias.com/investmentaccount=com.chrias.soapmodel.investment</extraarg>
        </extraargs>
    </wsdlOption>
</wsdlOptions>
```

## Building the JAR 
To build the JAR containing the JAX-WS artifacts you will need Maven installed, or you can use the Maven wrapper checked in with the project. The command below will generate the JAR and install it in your local Maven repository. It can then be referenced as a Maven dependency by the other project that need it.
```
mvnw clean install
```