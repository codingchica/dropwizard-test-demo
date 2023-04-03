package codingchica.demo.test.dropwizard.config;

import codingchica.demo.test.dropwizard.core.config.DropwizardTestDemoConfiguration;
import io.dropwizard.validation.BaseValidator;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the DropwizardTestDemoConfiguration class's validation logic.
 *
 * @see DropwizardTestDemoConfigurationPOJOTest See also:
 *     https://github.com/dropwizard/dropwizard/blob/master/dropwizard-configuration/src/test/java/io/dropwizard/configuration/ConfigurationValidationExceptionTest.java
 */
class DropwizardTestDemoConfigurationValidationTest {
  private final DropwizardTestDemoConfiguration dropwizardTestDemoConfiguration =
      ConfigFactory.dropwizardTestDemoConfiguration();

  private final Validator validator = BaseValidator.newValidator();

  private final AnnotationValidationUtils<DropwizardTestDemoConfiguration> validationUtils =
      new AnnotationValidationUtils<>();

  /** Generic happy-path scenario */
  @Test
  void happyPath() {
    // Setup
    // See ConfigFactory for setup.

    // Execution
    final Set<ConstraintViolation<DropwizardTestDemoConfiguration>> violations =
        validator.validate(dropwizardTestDemoConfiguration);

    // Validation
    validationUtils.assertEmpty(violations);
  }
}
