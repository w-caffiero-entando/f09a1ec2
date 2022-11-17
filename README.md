# Entando app-engine

This multi-module Maven project contains all the Entando core modules needed to build the app-engine war file.

To run the war file locally:

```
mvn clean install
cd webapp/
mvn package jetty:run-war -Pjetty-local -Dspring.profiles.active=swagger -DskipTests -DskipLicenseDownload -Pderby -Pkeycloak
```

The application will be available at http://localhost:8080/entando-de-app/

More information are available on [webapp README](webapp/README.md).

To execute all the tests:

```
mvn clean test -Ppre-deployment-verification
```

