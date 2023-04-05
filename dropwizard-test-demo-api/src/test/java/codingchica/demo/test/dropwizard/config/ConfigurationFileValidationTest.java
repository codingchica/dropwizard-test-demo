package codingchica.demo.test.dropwizard.config;

import static codingchica.demo.test.dropwizard.util.AnnotationValidationUtils.*;
import static org.junit.jupiter.api.Assertions.*;

import codingchica.demo.test.dropwizard.core.config.DropwizardTestDemoConfiguration;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.configuration.*;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.jersey.validation.Validators;
import java.io.File;
import java.io.IOException;
import java.util.*;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Spy;

/**
 * Tests to enforce that all application configuration files are valid. See also: <br>
 * https://www.dropwizard.io/en/latest/manual/testing.html#testing-configurations
 *
 * <p>When running this test locally, you must set the expected environment variables to match the
 * Maven build.
 */
public class ConfigurationFileValidationTest {
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
  }

  public static List<String> provideConfigFiles() {
    File configFolder = new File("src/main/resources/appConfig");
    assertTrue(configFolder.exists(), configFolder.getPath() + " does not exist");
    assertTrue(configFolder.isDirectory(), configFolder.getPath() + " is not a directory");
    System.out.println("Made it to here");
    return Arrays.stream(
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

  @ParameterizedTest(name = "{0}")
  @MethodSource("provideConfigFiles")
  public void configFileIsValid(String configFilePath) throws ConfigurationException, IOException {
    // Setup
    SubstitutingSourceProvider substitutingSourceProvider =
        new SubstitutingSourceProvider(
            resourceConfigurationSourceProvider, environmentVariableSubstitutor);
    DropwizardTestDemoConfiguration configPOJO =
        factory.build(substitutingSourceProvider, configFilePath);

    // Execution
    Set<ConstraintViolation<DropwizardTestDemoConfiguration>> violations =
        validator.validate(configPOJO);

    // Validation
    assertEmpty(violations);
  }
}
