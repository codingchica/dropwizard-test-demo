package codingchica.demo.test.dropwizard.config;

import static org.junit.jupiter.api.Assertions.*;

import ch.qos.logback.access.spi.IAccessEvent;
import ch.qos.logback.classic.spi.ILoggingEvent;
import codingchica.demo.test.dropwizard.core.config.DropwizardTestDemoConfiguration;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.dropwizard.configuration.*;
import io.dropwizard.core.server.DefaultServerFactory;
import io.dropwizard.core.server.ServerFactory;
import io.dropwizard.core.setup.AdminFactory;
import io.dropwizard.core.setup.HealthCheckConfiguration;
import io.dropwizard.health.HealthFactory;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.jersey.validation.Validators;
import io.dropwizard.jetty.ConnectorFactory;
import io.dropwizard.jetty.GzipHandlerFactory;
import io.dropwizard.jetty.HttpConnectorFactory;
import io.dropwizard.jetty.ServerPushFilterFactory;
import io.dropwizard.logging.common.AppenderFactory;
import io.dropwizard.logging.common.ConsoleAppenderFactory;
import io.dropwizard.logging.common.DefaultLoggingFactory;
import io.dropwizard.logging.common.LoggingFactory;
import io.dropwizard.metrics.common.MetricsFactory;
import io.dropwizard.metrics.common.ReporterFactory;
import io.dropwizard.request.logging.LogbackAccessRequestLogFactory;
import io.dropwizard.request.logging.RequestLogFactory;
import io.dropwizard.servlets.tasks.TaskConfiguration;
import io.dropwizard.util.DataSize;
import io.dropwizard.util.Duration;
import jakarta.validation.Validator;
import java.io.File;
import java.io.IOException;
import java.util.*;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.http.CookieCompliance;
import org.eclipse.jetty.http.HttpCompliance;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Spy;

/**
 * Tests to enforce that all application configuration files contain the expected contents, once
 * deserialized into POJO(s). See also: <br>
 * https://www.dropwizard.io/en/latest/manual/testing.html#testing-configurations
 *
 * <p>When running this test locally, you must set the expected environment variables to match the
 * Maven build.
 */
public class ConfigurationFileContentsTest {

  private static final ObjectMapper objectMapper = Jackson.newObjectMapper();
  private static final Validator validator = Validators.newValidator();

  @Spy
  private final EnvironmentVariableSubstitutor environmentVariableSubstitutor =
      new EnvironmentVariableSubstitutor(true);

  private final YamlConfigurationFactory<DropwizardTestDemoConfiguration> factory =
      new YamlConfigurationFactory<>(
          DropwizardTestDemoConfiguration.class, validator, objectMapper, "dw");

  private final ResourceConfigurationSourceProvider resourceConfigurationSourceProvider =
      new ResourceConfigurationSourceProvider();

  private static List<String> configFiles = null;

  /**
   * These are setup in the Maven pom.xml. If you are running the test locally in an IDE, you must
   * also set these values in your run configuration.
   */
  @BeforeAll
  public static void enforceEnvironmentSetup() {
    Map<String, String> expectedEnvironmentVariables = new TreeMap<>();

    expectedEnvironmentVariables.put("LOG_LEVEL_MAIN", "DEBUG");

    expectedEnvironmentVariables.forEach(
        (key, value) ->
            assertEquals(
                value, System.getenv(key), key + " is not setup in environment variables"));

    objectMapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
    objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
  }

  public static List<String> provideConfigFiles() {
    if (configFiles == null) {
      File configFolder = new File("src/main/resources/appConfig");
      assertTrue(configFolder.exists(), configFolder.getPath() + " does not exist");
      assertTrue(configFolder.isDirectory(), configFolder.getPath() + " is not a directory");
      System.out.println("Made it to here");
      configFiles =
          Arrays.stream(
                  Objects.requireNonNull(
                      configFolder.listFiles(
                          (dir, name) ->
                              StringUtils.endsWithIgnoreCase(name, ".yml")
                                  || StringUtils.endsWithIgnoreCase(name, ".yaml"))))
              // Classloader is used at runtime to retrieve file contents.
              .map(
                  (item) ->
                      StringUtils.substring(
                          item.getPath(), StringUtils.indexOf(item.getPath(), "appConfig")))
              .toList();
    }
    return configFiles;
  }

  /**
   * Ensure that the <em>metrics</em> section of the configuration is setup as expected.
   *
   * @param configFilePath The path to the configuration file under test.
   * @throws ConfigurationException If the configuration is not valid.
   * @throws IOException If there are issues retrieving the configuration file.
   * @see <a href="https://www.dropwizard.io/en/latest/manual/configuration.html#metrics">Dropwizard
   *     Config Reference: Metrics</a>
   */
  @ParameterizedTest(name = "{0}")
  @MethodSource("provideConfigFiles")
  public void testContents_metrics(String configFilePath)
      throws ConfigurationException, IOException {
    // Setup
    SubstitutingSourceProvider substitutingSourceProvider =
        new SubstitutingSourceProvider(
            resourceConfigurationSourceProvider, environmentVariableSubstitutor);
    DropwizardTestDemoConfiguration configPOJO =
        factory.build(substitutingSourceProvider, configFilePath);

    // Execution
    MetricsFactory metrics = configPOJO.getMetricsFactory();

    // Validation
    assertNotNull(metrics, "metrics");
    assertAll(
        () -> assertEquals(Duration.minutes(1), metrics.getFrequency(), "frequency"),
        () -> assertEquals(new ArrayList<ReporterFactory>(), metrics.getReporters(), "reporters"),
        () -> assertFalse(metrics.isReportOnStop(), "reportOnStop"));
  }

  /**
   * Ensure that the <em>metrics</em> section of the configuration is setup as expected.
   *
   * @param configFilePath The path to the configuration file under test.
   * @throws ConfigurationException If the configuration is not valid.
   * @throws IOException If there are issues retrieving the configuration file.
   * @see <a href="https://www.dropwizard.io/en/latest/manual/configuration.html">Dropwizard Config
   *     Reference</a>
   */
  @ParameterizedTest(name = "{0}")
  @MethodSource("provideConfigFiles")
  public void testContents_admin(String configFilePath) throws ConfigurationException, IOException {
    // Setup
    SubstitutingSourceProvider substitutingSourceProvider =
        new SubstitutingSourceProvider(
            resourceConfigurationSourceProvider, environmentVariableSubstitutor);
    DropwizardTestDemoConfiguration configPOJO =
        factory.build(substitutingSourceProvider, configFilePath);

    // Execution
    AdminFactory admin = configPOJO.getAdminFactory();

    // Validation
    System.out.println(objectMapper.writeValueAsString(admin));
    assertNotNull(admin, "admin");

    validateContents_admin_healthChecks(admin.getHealthChecks());
    validateContents_admin_tasks(admin.getTasks());
  }

  /**
   * Ensure that the <em>healthchecks</em> section of the configuration is setup as expected.
   *
   * @param healthChecks The object to validate.
   * @see <a
   *     href="https://www.dropwizard.io/en/latest/manual/configuration.html#health-checks">Dropwizard
   *     Config Reference: Health checks</a>
   */
  private void validateContents_admin_healthChecks(HealthCheckConfiguration healthChecks) {
    assertNotNull(healthChecks, "healthChecks");
    assertAll(
        () -> assertEquals(1, healthChecks.getMinThreads(), "healthChecks.minThreads"),
        () -> assertEquals(4, healthChecks.getMaxThreads(), "healthChecks.maxThreads"),
        () -> assertEquals(1, healthChecks.getWorkQueueSize(), "healthChecks.workQueueSize"));
  }

  /**
   * Ensure that the <em>tasks</em> section of the configuration is setup as expected.
   *
   * @param tasks The object to validate.
   * @see <a href="https://www.dropwizard.io/en/latest/manual/configuration.html#tasks">Dropwizard
   *     Config Reference: Tasks</a>
   */
  private void validateContents_admin_tasks(TaskConfiguration tasks) {
    assertNotNull(tasks, "tasks");
    assertFalse(tasks.isPrintStackTraceOnError(), "tasks.printStackTraceOnError");
  }

  /**
   * Ensure that the <em>health</em> section of the configuration is setup as expected.
   *
   * @param configFilePath The path to the configuration file under test.
   * @throws ConfigurationException If the configuration is not valid.
   * @throws IOException If there are issues retrieving the configuration file.
   * @see <a href="https://www.dropwizard.io/en/latest/manual/configuration.html#health">Dropwizard
   *     Config Reference: Health</a>
   */
  @ParameterizedTest(name = "{0}")
  @MethodSource("provideConfigFiles")
  public void testContents_health(String configFilePath)
      throws ConfigurationException, IOException {
    // Setup
    SubstitutingSourceProvider substitutingSourceProvider =
        new SubstitutingSourceProvider(
            resourceConfigurationSourceProvider, environmentVariableSubstitutor);
    DropwizardTestDemoConfiguration configPOJO =
        factory.build(substitutingSourceProvider, configFilePath);

    // Execution
    Optional<HealthFactory> health = configPOJO.getHealthFactory();

    // Validation
    System.out.println(objectMapper.writeValueAsString(health));
    assertTrue(health.isEmpty(), "health");
  }

  /**
   * Ensure that the <em>testValue</em> section of the configuration is setup as expected.
   *
   * @param configFilePath The path to the configuration file under test.
   * @throws ConfigurationException If the configuration is not valid.
   * @throws IOException If there are issues retrieving the configuration file.
   * @see DropwizardTestDemoConfiguration
   */
  @ParameterizedTest(name = "{0}")
  @MethodSource("provideConfigFiles")
  public void testContents_testValue(String configFilePath)
      throws ConfigurationException, IOException {
    // Setup
    SubstitutingSourceProvider substitutingSourceProvider =
        new SubstitutingSourceProvider(
            resourceConfigurationSourceProvider, environmentVariableSubstitutor);
    DropwizardTestDemoConfiguration configPOJO =
        factory.build(substitutingSourceProvider, configFilePath);

    // Execution
    String testValue = configPOJO.getTestValue();

    // Validation
    System.out.println(objectMapper.writeValueAsString(testValue));
    assertNull(testValue, "testValue");
  }

  /**
   * Ensure that the <em>logging</em> section of the configuration is setup as expected.
   *
   * @param configFilePath The path to the configuration file under test.
   * @throws ConfigurationException If the configuration is not valid.
   * @throws IOException If there are issues retrieving the configuration file.
   * @see <a href="https://www.dropwizard.io/en/latest/manual/configuration.html#logging">Dropwizard
   *     Config Reference: Logging</a>
   */
  @ParameterizedTest(name = "{0}")
  @MethodSource("provideConfigFiles")
  public void testContents_logging(String configFilePath)
      throws ConfigurationException, IOException {
    // Setup
    SubstitutingSourceProvider substitutingSourceProvider =
        new SubstitutingSourceProvider(
            resourceConfigurationSourceProvider, environmentVariableSubstitutor);
    DropwizardTestDemoConfiguration configPOJO =
        factory.build(substitutingSourceProvider, configFilePath);

    // Execution
    LoggingFactory logging = configPOJO.getLoggingFactory();

    // Validation
    System.out.println(objectMapper.writeValueAsString(logging));
    assertNotNull(logging, "logging");
    assertTrue(
        logging instanceof DefaultLoggingFactory, "logging instanceof DefaultLoggingFactory");

    DefaultLoggingFactory defaultLoggingFactory = (DefaultLoggingFactory) logging;
    assertEquals("DEBUG", defaultLoggingFactory.getLevel(), "level");

    validateContents_logging_loggers(defaultLoggingFactory.getLoggers());
    validateContents_logging_appenders(defaultLoggingFactory.getAppenders());
  }

  /**
   * Ensure that the <em>loggers</em> section of the configuration is setup as expected.
   *
   * @param loggers The object to validate.
   * @see <a href="https://www.dropwizard.io/en/latest/manual/configuration.html#logging">Dropwizard
   *     Config Reference: Logging</a>
   */
  private void validateContents_logging_loggers(Map<String, JsonNode> loggers) {
    assertNotNull(loggers, "loggers");
    assertEquals(1, loggers.size(), "loggers.size");

    JsonNode codingChicaLogger = loggers.get("codingchica");
    assertNotNull(codingChicaLogger, "codingChicaLogger");
    assertEquals("DEBUG", codingChicaLogger.asText(), "codingChicaLogger.level");
  }

  /**
   * Ensure that the <em>appenders</em> section of the configuration is setup as expected.
   *
   * @param appenders The object to validate.
   * @see <a href="https://www.dropwizard.io/en/latest/manual/configuration.html#logging">Dropwizard
   *     Config Reference: Logging</a>
   */
  private void validateContents_logging_appenders(List<AppenderFactory<ILoggingEvent>> appenders) {
    assertNotNull(appenders, "appenders");
    assertEquals(1, appenders.size(), "appenders.size");

    AppenderFactory<ILoggingEvent> appenderFactory = appenders.get(0);
    System.out.println(appenderFactory.getClass());
    assertNotNull(appenders, "appenderFactory");
    assertTrue(
        appenderFactory instanceof ConsoleAppenderFactory,
        "appenderFactory instanceof ConsoleAppenderFactory");

    ConsoleAppenderFactory<ILoggingEvent> consoleAppenderFactory =
        (ConsoleAppenderFactory) appenderFactory;
    assertAll(
        () ->
            assertEquals(
                "ALL", consoleAppenderFactory.getThreshold(), "consoleAppenderFactory.threshold"),
        () -> assertNull(consoleAppenderFactory.getLogFormat(), "consoleAppenderFactory.logFormat"),
        () -> assertNull(consoleAppenderFactory.getLayout(), "consoleAppenderFactory.layout"),
        () ->
            assertEquals(
                TimeZone.getTimeZone("UTC"),
                consoleAppenderFactory.getTimeZone(),
                "consoleAppenderFactory.timeZone"),
        () ->
            assertEquals(
                256, consoleAppenderFactory.getQueueSize(), "consoleAppenderFactory.queueSize"),
        () ->
            assertEquals(
                -1,
                consoleAppenderFactory.getDiscardingThreshold(),
                "consoleAppenderFactory.discardingThreshold"),
        () ->
            assertNull(
                consoleAppenderFactory.getMessageRate(), "consoleAppenderFactory.messageRate"),
        () ->
            assertFalse(
                consoleAppenderFactory.isIncludeCallerData(),
                "consoleAppenderFactory.includeCallerData"),
        () ->
            assertEquals(
                new ArrayList<>(),
                consoleAppenderFactory.getFilterFactories(),
                "consoleAppenderFactory.filterFactories"),
        () ->
            assertEquals(
                ConsoleAppenderFactory.ConsoleStream.STDOUT,
                consoleAppenderFactory.getTarget(),
                "consoleAppenderFactory.target"));
  }

  /**
   * Ensure that the <em>server</em> section of the configuration is setup as expected.
   *
   * @param configFilePath The path to the configuration file under test.
   * @throws ConfigurationException If the configuration is not valid.
   * @throws IOException If there are issues retrieving the configuration file.
   * @see <a
   *     href="https://www.dropwizard.io/en/latest/manual/configuration.htmll#servers">Dropwizard
   *     Config Reference: Server</a>
   */
  @ParameterizedTest(name = "{0}")
  @MethodSource("provideConfigFiles")
  public void testContents_server(String configFilePath)
      throws ConfigurationException, IOException {
    // Setup
    SubstitutingSourceProvider substitutingSourceProvider =
        new SubstitutingSourceProvider(
            resourceConfigurationSourceProvider, environmentVariableSubstitutor);
    DropwizardTestDemoConfiguration configPOJO =
        factory.build(substitutingSourceProvider, configFilePath);
    Set<String> allowedMethods = new HashSet<>();
    allowedMethods.add("HEAD");
    allowedMethods.add("DELETE");
    allowedMethods.add("POST");
    allowedMethods.add("GET");
    allowedMethods.add("OPTIONS");
    allowedMethods.add("PUT");
    allowedMethods.add("PATCH");

    // Execution
    ServerFactory serverFactory = configPOJO.getServerFactory();

    // Validation
    System.out.println(objectMapper.writeValueAsString(serverFactory));
    assertNotNull(serverFactory, "serverFactory");
    assertTrue(
        serverFactory instanceof DefaultServerFactory,
        "serverFactory instanceof DefaultServerFactory");

    DefaultServerFactory defaultServerFactory = (DefaultServerFactory) serverFactory;
    assertAll(
        () ->
            assertEquals(
                1024, defaultServerFactory.getMaxThreads(), "defaultServerFactory.maxThreads"),
        () ->
            assertEquals(
                8, defaultServerFactory.getMinThreads(), "defaultServerFactory.minThreads"),
        () ->
            assertEquals(
                1024,
                defaultServerFactory.getMaxQueuedRequests(),
                "defaultServerFactory.maxQueuedRequests"),
        () ->
            assertEquals(
                Duration.minutes(1),
                defaultServerFactory.getIdleThreadTimeout(),
                "defaultServerFactory.idleThreadTimeout"),
        () ->
            assertNull(
                defaultServerFactory.getNofileSoftLimit(), "defaultServerFactory.nofileSoftLimit"),
        () ->
            assertNull(
                defaultServerFactory.getNofileHardLimit(), "defaultServerFactory.nofileHardLimit"),
        () -> assertNull(defaultServerFactory.getGid(), "defaultServerFactory.gid"),
        () -> assertNull(defaultServerFactory.getUid(), "defaultServerFactory.uid"),
        () -> assertNull(defaultServerFactory.getUser(), "defaultServerFactory.user"),
        () -> assertNull(defaultServerFactory.getGroup(), "defaultServerFactory.group"),
        () -> assertNull(defaultServerFactory.getUmask(), "defaultServerFactory.umask"),
        () ->
            assertNull(defaultServerFactory.getStartsAsRoot(), "defaultServerFactory.startsAsRoot"),
        () ->
            assertTrue(
                defaultServerFactory.getRegisterDefaultExceptionMappers(),
                "defaultServerFactory.registerDefaultExceptionMappers"),
        () ->
            assertFalse(
                defaultServerFactory.getDetailedJsonProcessingExceptionMapper(),
                "defaultServerFactory.detailedJsonProcessingExceptionMapper"),
        () ->
            assertEquals(
                Duration.seconds(30),
                defaultServerFactory.getShutdownGracePeriod(),
                "defaultServerFactory.shutdownGracePeriod"),
        () ->
            assertEquals(
                allowedMethods,
                defaultServerFactory.getAllowedMethods(),
                "defaultServerFactory.allowedMethods"),
        () ->
            assertTrue(
                defaultServerFactory.getEnableThreadNameFilter(),
                "defaultServerFactory.enableThreadNameFilter"),
        () ->
            assertFalse(
                defaultServerFactory.getDumpAfterStart(), "defaultServerFactory.dumpAfterStart"),
        () ->
            assertFalse(
                defaultServerFactory.getDumpBeforeStop(), "defaultServerFactory.dumpBeforeStop"),
        () ->
            assertEquals(
                64,
                defaultServerFactory.getAdminMaxThreads(),
                "defaultServerFactory.adminMaxThreads"),
        () ->
            assertEquals(
                1,
                defaultServerFactory.getAdminMinThreads(),
                "defaultServerFactory.adminMinThreads"),
        () ->
            assertEquals(
                "/",
                defaultServerFactory.getApplicationContextPath(),
                "defaultServerFactory.applicationContextPath"),
        () ->
            assertEquals(
                "/",
                defaultServerFactory.getAdminContextPath(),
                "defaultServerFactory.adminContextPath"),
        () ->
            assertEquals(
                "/",
                defaultServerFactory.getAdminContextPath(),
                "defaultServerFactory.adminContextPath"),
        () ->
            assertTrue(
                defaultServerFactory.getJerseyRootPath().isEmpty(),
                "defaultServerFactory.rootPath"),
        () ->
            assertTrue(
                defaultServerFactory.getJerseyRootPath().isEmpty(),
                "defaultServerFactory.rootPath"));

    validateContents_server_applicationConnectors(defaultServerFactory.getApplicationConnectors());
    validateContents_server_adminConnectors(defaultServerFactory.getAdminConnectors());
    validateContents_server_serverPush(defaultServerFactory.getServerPush());
    validateContents_server_requestLogFactory(defaultServerFactory.getRequestLogFactory());
    validateContents_server_gzip(defaultServerFactory.getGzipFilterFactory());
  }

  /**
   * Ensure that the <em>applicationConnectors</em> section of the configuration is setup as
   * expected.
   *
   * @param applicationConnectors The object to validate.
   * @see <a href="https://www.dropwizard.io/en/latest/manual/configuration.html#http">Dropwizard
   *     Config Reference: HTTP connector</a>
   */
  private void validateContents_server_applicationConnectors(
      List<ConnectorFactory> applicationConnectors) {
    validateContents_server_httpConnectorFactory(8080, applicationConnectors);
  }

  /**
   * Ensure that the <em>applicationConnectors</em> section of the configuration is setup as
   * expected.
   *
   * @param applicationConnectors The object to validate.
   * @see <a href="https://www.dropwizard.io/en/latest/manual/configuration.html#http">Dropwizard
   *     Config Reference: HTTP connector</a>
   */
  private void validateContents_server_adminConnectors(
      List<ConnectorFactory> applicationConnectors) {
    validateContents_server_httpConnectorFactory(8081, applicationConnectors);
  }

  /**
   * Ensure that the <em>applicationConnectors</em> section of the configuration is setup as
   * expected.
   *
   * @param applicationConnectors The object to validate.
   * @see <a href="https://www.dropwizard.io/en/latest/manual/configuration.html#http">Dropwizard
   *     Config Reference: HTTP connector</a>
   */
  private void validateContents_server_httpConnectorFactory(
      int port, List<ConnectorFactory> applicationConnectors) {
    assertNotNull(applicationConnectors, "applicationConnectors");
    assertEquals(1, applicationConnectors.size(), "applicationConnectors.size");

    ConnectorFactory connectorFactory = applicationConnectors.get(0);
    assertNotNull(connectorFactory, "applicationConnectors.http");

    HttpConnectorFactory httpConnectorFactory = (HttpConnectorFactory) connectorFactory;
    assertAll(
        () -> assertEquals(port, httpConnectorFactory.getPort(), "httpConnectorFactory.port"),
        () -> assertNull(httpConnectorFactory.getBindHost(), "httpConnectorFactory.bindHost"),
        () ->
            assertFalse(
                httpConnectorFactory.isInheritChannel(), "httpConnectorFactory.inheritChannel"),
        () ->
            assertEquals(
                DataSize.bytes(512),
                httpConnectorFactory.getHeaderCacheSize(),
                "httpConnectorFactory.headerCacheSize"),
        () ->
            assertEquals(
                DataSize.kibibytes(32),
                httpConnectorFactory.getOutputBufferSize(),
                "httpConnectorFactory.outputBufferSize"),
        () ->
            assertEquals(
                DataSize.kibibytes(8),
                httpConnectorFactory.getMaxRequestHeaderSize(),
                "httpConnectorFactory.maxRequestHeaderSize"),
        () ->
            assertEquals(
                DataSize.kibibytes(8),
                httpConnectorFactory.getMaxResponseHeaderSize(),
                "httpConnectorFactory.maxResponseHeaderSize"),
        () ->
            assertEquals(
                DataSize.kibibytes(8),
                httpConnectorFactory.getInputBufferSize(),
                "httpConnectorFactory.inputBufferSize"),
        () ->
            assertEquals(
                DataSize.kibibytes(8),
                httpConnectorFactory.getInputBufferSize(),
                "httpConnectorFactory.inputBufferSize"),
        () ->
            assertEquals(
                Duration.seconds(30),
                httpConnectorFactory.getIdleTimeout(),
                "httpConnectorFactory.idleTimeout"),
        () ->
            assertEquals(
                DataSize.bytes(0),
                httpConnectorFactory.getMinRequestDataPerSecond(),
                "httpConnectorFactory.minRequestDataPerSecond"),
        () ->
            assertEquals(
                DataSize.bytes(0),
                httpConnectorFactory.getMinResponseDataPerSecond(),
                "httpConnectorFactory.minResponseDataPerSecond"),
        () ->
            assertEquals(
                DataSize.bytes(64),
                httpConnectorFactory.getMinBufferPoolSize(),
                "httpConnectorFactory.minBufferPoolSize"),
        () ->
            assertEquals(
                DataSize.bytes(1024),
                httpConnectorFactory.getBufferPoolIncrement(),
                "httpConnectorFactory.bufferPoolIncrement"),
        () ->
            assertEquals(
                DataSize.kibibytes(64),
                httpConnectorFactory.getMaxBufferPoolSize(),
                "httpConnectorFactory.maxBufferPoolSize"),
        () ->
            assertTrue(
                httpConnectorFactory.getAcceptorThreads().isEmpty(),
                "httpConnectorFactory.acceptorThreads"),
        () ->
            assertTrue(
                httpConnectorFactory.getSelectorThreads().isEmpty(),
                "httpConnectorFactory.selectorThreads"),
        () ->
            assertNull(
                httpConnectorFactory.getAcceptQueueSize(), "httpConnectorFactory.acceptQueueSize"),
        () ->
            assertTrue(httpConnectorFactory.isReuseAddress(), "httpConnectorFactory.reuseAddress"),
        () ->
            assertFalse(
                httpConnectorFactory.isUseServerHeader(), "httpConnectorFactory.useServerHeader"),
        () ->
            assertTrue(
                httpConnectorFactory.isUseDateHeader(), "httpConnectorFactory.useDateHeader"),
        () ->
            assertFalse(
                httpConnectorFactory.isUseForwardedHeaders(),
                "httpConnectorFactory.useForwardedHeaders"),
        () ->
            assertFalse(
                httpConnectorFactory.isUseProxyProtocol(), "httpConnectorFactory.useProxyProtocol"),
        () ->
            assertEquals(
                HttpCompliance.RFC7230,
                httpConnectorFactory.getHttpCompliance(),
                "httpConnectorFactory.httpCompliance"),
        () ->
            assertEquals(
                CookieCompliance.RFC6265,
                httpConnectorFactory.getRequestCookieCompliance(),
                "httpConnectorFactory.requestCookieCompliance"),
        () ->
            assertEquals(
                CookieCompliance.RFC6265,
                httpConnectorFactory.getResponseCookieCompliance(),
                "httpConnectorFactory.responseCookieCompliance"));
  }

  /**
   * Ensure that the <em>applicationConnectors</em> section of the configuration is setup as
   * expected.
   *
   * @param serverPush The object to validate.
   * @see <a
   *     href="https://www.dropwizard.io/en/latest/manual/configuration.html#server-push">Dropwizard
   *     Config Reference: Server Push</a>
   */
  private void validateContents_server_serverPush(ServerPushFilterFactory serverPush) {
    assertNotNull(serverPush, "serverPush");
    assertAll(
        () -> assertFalse(serverPush.isEnabled(), "serverPush.enabled"),
        () ->
            assertEquals(
                Duration.seconds(4), serverPush.getAssociatePeriod(), "serverPush.associatePeriod"),
        () -> assertEquals(16, serverPush.getMaxAssociations(), "serverPush.maxAssociations"),
        () -> assertNull(serverPush.getRefererHosts(), "serverPush.refererHosts"),
        () -> assertNull(serverPush.getRefererPorts(), "serverPush.refererPorts"));
  }

  /**
   * Ensure that the <em>request log</em> section of the configuration is setup as expected.
   *
   * @param requestLogFactory The object to validate.
   * @see <a
   *     href="https://www.dropwizard.io/en/latest/manual/configuration.html#request-log">Dropwizard
   *     Config Reference: Request Log</a>
   */
  private void validateContents_server_requestLogFactory(RequestLogFactory<?> requestLogFactory) {
    assertNotNull(requestLogFactory, "requestLogFactory");
    assertTrue(
        requestLogFactory instanceof LogbackAccessRequestLogFactory,
        "requestLogFactory instanceof LogbackAccessRequestLogFactory");

    LogbackAccessRequestLogFactory logbackAccessRequestLogFactory =
        (LogbackAccessRequestLogFactory) requestLogFactory;
    assertNotNull(logbackAccessRequestLogFactory, "logbackAccessRequestLogFactory");
    assertTrue(
        logbackAccessRequestLogFactory.isEnabled(), "logbackAccessRequestLogFactory.enabled");

    List<AppenderFactory<IAccessEvent>> appenders = logbackAccessRequestLogFactory.getAppenders();
    assertNotNull(appenders, "logbackAccessRequestLogFactory.appenders");
    assertEquals(1, appenders.size(), "logbackAccessRequestLogFactory.appenders.size");

    AppenderFactory<IAccessEvent> appenderFactory = appenders.get(0);
    assertNotNull(appenderFactory, "logbackAccessRequestLogFactory.appenderFactory");

    System.out.println(appenderFactory.getClass());
    assertTrue(
        appenderFactory instanceof ConsoleAppenderFactory,
        "appenderFactory instanceof ConsoleAppenderFactory");

    ConsoleAppenderFactory<IAccessEvent> consoleAppenderFactory =
        (ConsoleAppenderFactory<IAccessEvent>) appenderFactory;
    assertAll(
        () ->
            assertEquals(
                "ALL", consoleAppenderFactory.getThreshold(), "consoleAppenderFactory.threshold"),
        () -> assertNull(consoleAppenderFactory.getLogFormat(), "consoleAppenderFactory.logFormat"),
        () -> assertNull(consoleAppenderFactory.getLayout(), "consoleAppenderFactory.layout"),
        () ->
            assertEquals(
                TimeZone.getTimeZone("UTC"),
                consoleAppenderFactory.getTimeZone(),
                "consoleAppenderFactory.timeZone"),
        () ->
            assertEquals(
                256, consoleAppenderFactory.getQueueSize(), "consoleAppenderFactory.queueSize"),
        () ->
            assertEquals(
                -1,
                consoleAppenderFactory.getDiscardingThreshold(),
                "consoleAppenderFactory.discardingThreshold"),
        () ->
            assertNull(
                consoleAppenderFactory.getMessageRate(), "consoleAppenderFactory.messageRate"),
        () ->
            assertFalse(
                consoleAppenderFactory.isIncludeCallerData(),
                "consoleAppenderFactory.includeCallerData"),
        () ->
            assertEquals(
                new ArrayList<>(),
                consoleAppenderFactory.getFilterFactories(),
                "consoleAppenderFactory.filterFactories"),
        () ->
            assertEquals(
                ConsoleAppenderFactory.ConsoleStream.STDOUT,
                consoleAppenderFactory.getTarget(),
                "consoleAppenderFactory.target"));
  }

  /**
   * Ensure that the <em>applicationConnectors</em> section of the configuration is setup as
   * expected.
   *
   * @param gzipFilterFactory The object to validate.
   * @see <a href="https://www.dropwizard.io/en/latest/manual/configuration.html#gzip">Dropwizard
   *     Config Reference: GZip</a>
   */
  private void validateContents_server_gzip(GzipHandlerFactory gzipFilterFactory) {
    assertNotNull(gzipFilterFactory, "gzipFilterFactory");
    assertAll(
        () -> assertTrue(gzipFilterFactory.isEnabled(), "gzipFilterFactory.enabled"),
        () ->
            assertEquals(
                DataSize.bytes(256),
                gzipFilterFactory.getMinimumEntitySize(),
                "gzipFilterFactory.minimumEntitySize"),
        () ->
            assertEquals(
                DataSize.kibibytes(8),
                gzipFilterFactory.getBufferSize(),
                "gzipFilterFactory.bufferSize"),
        () ->
            assertNull(
                gzipFilterFactory.getCompressedMimeTypes(),
                "gzipFilterFactory.compressedMimeTypes"),
        () ->
            assertNull(
                gzipFilterFactory.getExcludedMimeTypes(), "gzipFilterFactory.excludedMimeTypes"),
        () ->
            assertNull(gzipFilterFactory.getIncludedMethods(), "gzipFilterFactory.includedMethods"),
        () -> assertNull(gzipFilterFactory.getExcludedPaths(), "gzipFilterFactory.excludedPaths"),
        () -> assertNull(gzipFilterFactory.getIncludedPaths(), "gzipFilterFactory.includedPaths"),
        () ->
            assertEquals(
                -1,
                gzipFilterFactory.getDeflateCompressionLevel(),
                "gzipFilterFactory.deflateCompressionLevel"),
        () -> assertFalse(gzipFilterFactory.isSyncFlush(), "gzipFilterFactory.syncFlush"));
  }
}
