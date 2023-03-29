# dropwizard-test-demo

How to start the dropwizard-test-demo application
---

1. Run `mvn clean install` to build your application
1. Start application with `java -jar target/dropwizard-test-demo-0.1-SNAPSHOT.jar server appConfig/test-component.yml`
1. To check that your application is running enter url `http://localhost:8080`

Health Check
---

To see your application's health enter url `http://localhost:8081/healthcheck`
