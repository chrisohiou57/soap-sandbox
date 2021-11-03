This Spring Boot app uses Apache CXF for the SOAP web service endpoints. It is configured to use mutual authentication with self-signed certificates. The CLI commands used to generate the SSL artifacts found in ```src/main/resources/cert``` are documented below.

You can use the ```client-app.jks``` as the keystore and truststore in another application to consume the web services in this app using mutual authentication.

## IMPORTANT:
For mutual authentication to work properly it is important that you use the same ```CN``` value of ```localhost``` when executing these commands. If you don't, you will get CN validation errors during the SSL handshake. For these commands, you provide the value of ```localhost``` when asked for your first and last name.

##### Create certificate for server application:
```keytool -genkeypair -alias server-app -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore server-app.jks -validity 3650```

##### Create self signed certificate for client:
```keytool -genkeypair -alias client-app -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore client-app.jks -validity 3650```

##### Create public certificate file from client app certificate:
```keytool -export -alias client-app -file client-app.crt -keystore client-app.jks```

##### Create public certificate file from server app certificate:
```keytool -export -alias server-app -file server-app.crt -keystore server-app.jks```

##### Import server’s certificate into client keystore:
```keytool -import -trustcacerts -alias server-app -file server-app.crt -keystore client-app.jks```

##### Import client’s certificate to server keystore:
```keytool -import -trustcacerts -alias client-app -file client-app.crt -keystore server-app.jks```

##### List certificates in the keystores:
```keytool -list -keystore server-app.jks -storepass changeit```

```keytool -list -keystore client-app.jks -storepass changeit```