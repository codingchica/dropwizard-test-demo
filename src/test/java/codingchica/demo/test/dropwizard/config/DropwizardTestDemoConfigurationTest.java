package codingchica.demo.test.dropwizard.config;

import static org.junit.jupiter.api.Assertions.assertTrue;

import io.dropwizard.validation.BaseValidator;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the DropwizardTestDemoConfiguration class. See also:
 * https://github.com/dropwizard/dropwizard/blob/master/dropwizard-configuration/src/test/java/io/dropwizard/configuration/ConfigurationValidationExceptionTest.java
 */
class DropwizardTestDemoConfigurationTest {
  private DropwizardTestDemoConfiguration dropwizardTestDemoConfiguration =
      ConfigFactory.dropwizardTestDemoConfiguration();

  final Validator validator = BaseValidator.newValidator();

  @Test
  void happyPath() {
    // Setup

    // Execution
    final Set<ConstraintViolation<DropwizardTestDemoConfiguration>> violations =
        validator.validate(dropwizardTestDemoConfiguration);

    // Validation
    assertTrue(violations.isEmpty());
  }
}
