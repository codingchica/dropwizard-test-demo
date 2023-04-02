package codingchica.demo.test.dropwizard.component.steps;

import static org.junit.jupiter.api.Assertions.*;

import codingchica.demo.test.dropwizard.component.ComponentTestServerFactory;
import codingchica.demo.test.dropwizard.component.model.APICallWorld;
import codingchica.demo.test.dropwizard.config.DropwizardTestDemoConfiguration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import io.cucumber.core.options.CurlOption;
import io.cucumber.java.AfterAll;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.ParameterType;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.dropwizard.testing.junit5.DropwizardAppExtension;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

/** Cucumber steps that can be used for generic API calls to the Dropwizard server. */
public class GenericAPISteps {

  private static DropwizardAppExtension<DropwizardTestDemoConfiguration> DROP_WIZARD_SERVER;

  /**
   * State storage between steps. A separate copy will be created for each test / scenario, but not
   * each step.
   */
  private final APICallWorld world = new APICallWorld();

  /**
   * A custom step to map a port name to a Dropwizard API port, in case we support dynamic ports in
   * the future.
   *
   * @param portName The name of the port as indicated in the feature file. One of
   *     admin|application.
   * @return The corresponding port from the Dropwizard server in use for the component testing.
   */
  @ParameterType("admin|application")
  public int portName(String portName) {
    switch (portName) {
      case "admin":
        return DROP_WIZARD_SERVER.getAdminPort();
      case "application":
        return DROP_WIZARD_SERVER.getLocalPort();
      default:
        throw new IllegalArgumentException("Unexpected portName: " + portName);
    }
  }

  @BeforeAll
  public static void beforeAll() throws Exception {
    DROP_WIZARD_SERVER = ComponentTestServerFactory.startServer();
  }

  @AfterAll
  public static void afterAll() throws InterruptedException {
    ComponentTestServerFactory.stopServer();
  }

  @Given("that my request uses the {string} protocol")
  public void that_my_request_uses_the_protocol(String protocol) {
    world.protocol = protocol;
  }

  @Given("that my request goes to endpoint {string}")
  public void setPath(String path) {
    world.path = path;
  }

  @Given("that my request goes to the {portName} port")
  public void setPort(int portName) {
    world.port = portName;
  }

  @Given("that my request contains header {word} = {word}")
  public void setContentTypeRequestHeader(String header, String value) {
    world.requestHeaders.put(header, value);
  }

  @Given("that my request uses the {word} method")
  public void setHttpMethod(String methodName) throws ProtocolException {
    world.httpMethod = CurlOption.HttpMethod.valueOf(methodName);
  }

  @When("I submit the request")
  public void sendRequest() throws IOException, URISyntaxException {
    // Setup
    world.endpoint =
        String.format("%s://%s:%s/%s", world.protocol, world.server, world.port, world.path);
    world.url = new URI(world.endpoint).toURL();
    world.connection = (HttpURLConnection) world.url.openConnection();
    world.requestHeaders.forEach(
        (header, value) -> world.connection.setRequestProperty(header, value));
    world.connection.setRequestMethod(world.httpMethod.name());
    world.connection.setDoOutput(true);

    // Execution
    world.connection.connect();
  }

  private String getResponseBody() throws IOException {
    String responseReceived = null;
    try (BufferedReader br =
        new BufferedReader(
            new InputStreamReader(world.connection.getInputStream(), StandardCharsets.UTF_8))) {
      StringBuilder response = new StringBuilder();
      String responseLine = null;
      while ((responseLine = br.readLine()) != null) {
        response.append(responseLine.trim());
      }
      responseReceived = response.toString();
    }
    return responseReceived;
  }

  @Then("the response code is {int}")
  public void theResponseCodeMatches(int expectedResponseCode) throws IOException {
    // Validation
    // String body = getResponseBody();
    assertEquals(
        expectedResponseCode,
        world.connection.getResponseCode(),
        "http status code mismatch calling "
            + world.endpoint
            + ": "
            + world.connection.getResponseMessage());
  }

  @Then("the response body is {word}")
  public void theResponseBodyEquals(String responseBody) throws IOException {
    assertEquals(responseBody, getResponseBody(), "mismatch mismatch calling " + world.endpoint);
  }

  @Then("the response body contains JSON data")
  public void theResponseBodyMatchesPattern(Map<String, String> expectedResponseData)
      throws IOException {
    String responseBody = getResponseBody();
    DocumentContext jsonBody = JsonPath.parse(responseBody);
    assertNotNull(expectedResponseData, "expectedResponseData");
    assertNotNull(responseBody, "responseBody");
    expectedResponseData.forEach(
        (path, value) -> {
          try {
            assertEquals(
                jsonBody.read(path).toString(),
                value,
                "Mismatch on '" + path + "' in response = " + responseBody);
          } catch (Throwable t) {
            System.out.println("Response: " + responseBody);
            throw t;
          }
        });
  }

  @Then("the response body contains String data")
  public void theResponseBodyMatchesPattern(List<String> expectedResponseSnippets)
      throws IOException {
    String responseBody = getResponseBody();
    assertNotNull(expectedResponseSnippets, "expectedResponseSnippets");
    assertNotNull(responseBody, "responseBody");
    expectedResponseSnippets.forEach(
        (value) -> {
          try {
            assertTrue(StringUtils.contains(responseBody, value));
          } catch (Throwable t) {
            throw new IllegalArgumentException(
                "No hit for '" + value + "' in response:\n" + responseBody, t);
          }
        });
  }

  @Then("the response body is completely empty")
  public void theResponseBodyIsNotSet() throws IOException {
    assertEquals("", getResponseBody(), "mismatch mismatch calling " + world.endpoint);
  }
}
