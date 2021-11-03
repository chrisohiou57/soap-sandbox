#### Create certificates and keystore/trustore for mutual SSL handshakes

##### Create self signed certificate for retail SOAP webservice app:
keytool -genkeypair -alias account-soap-app -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore account-soap-app.jks -dname "CN=localhost, OU=Account Mgmt, O=Chrias Corp, L=Columbus, S=Ohio, C=US" -storepass accountpw -keypass accountpw -validity 3650

##### Create public certificate from retail app certificate:
keytool -export -alias account-soap-app -file account-soap-app.crt -keystore account-soap-app.jks

##### Create self signed certificate for Camel app:
keytool -genkeypair -alias camel-app -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore camel-app.jks -dname "CN=localhost, OU=Middleware, O=Chrias Corp, L=Columbus, S=Ohio, C=US" -storepass camelpw -keypass camelpw -validity 3650

##### Create public certificate from Camel app certificate:
keytool -export -alias camel-app -file camel-app.crt -keystore camel-app.jks

##### Create self signed certificate for client app:
keytool -genkeypair -alias client-app -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore client-app.jks -dname "CN=localhost, OU=Online Banking, O=Chrias Corp, L=Columbus, S=Ohio, C=US" -storepass clientpw -keypass clientpw -validity 3650

##### Create public certificate from client app certificate:
keytool -export -alias client-app -file client-app.crt -keystore client-app.jks

#### Import certaificates on both sides for mutual SSL handshakes

Use the password of the keystore you importing into (e.g. the value used for --keystore)

##### Import account-soap-app certificate into camel-app and client-app keystore:
keytool -import -trustcacerts -alias account-soap-app -file account-soap-app.crt -keystore camel-app.jks
keytool -import -trustcacerts -alias account-soap-app -file account-soap-app.crt -keystore client-app.jks

##### Import camel-app and client-app certificate into account-soap-app keystore:
keytool -import -trustcacerts -alias camel-app -file camel-app.crt -keystore account-soap-app.jks
keytool -import -trustcacerts -alias client-app -file client-app.crt -keystore account-soap-app.jks

##### Import camel-app certificate into client-app keystore:
keytool -import -trustcacerts -alias camel-app -file camel-app.crt -keystore client-app.jks

##### Import client-app certificate into camel-app keystore:
keytool -import -trustcacerts -alias client-app -file client-app.crt -keystore camel-app.jks