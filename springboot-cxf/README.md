This Spring Boot app uses Apache CXF for the SOAP web service endpoints. It is configured to use mutual authentication with self-signed certificates. The CLI commands used to generate the SSL artifacts found in ```src/main/resources``` are documented below.

##### Create certificate for server application:
```keytool -genkeypair -alias server-app -keyalg RSA -keysize 2048 -storetype JKS -keystore server-app.jks -validity 3650```

##### Create self signed certificate for client:
```keytool -genkeypair -alias client-app -keyalg RSA -keysize 2048 -storetype JKS -keystore client-app.jks -validity 3650```

##### Create public certificate file from client app certificate:
```keytool -export -alias client-app -file client-app.crt -keystore client-app.jks```

##### Create public certificate file from server app certificate:
```keytool -export -alias server-app -file server-app.crt -keystore server-app.jks```

##### Import server’s certificate into client keystore:
```keytool -import -alias server-app -file server-app.crt -keystore client-app.jks```

##### Import client’s certificate to server keystore:
```keytool -import -alias client-app -file client-app.crt -keystore server-app.jks```

##### List certificates in the keystores:
```keytool -list -keystore server-app.jks -storepass changeit```

```keytool -list -keystore client-app.jks -storepass changeit```