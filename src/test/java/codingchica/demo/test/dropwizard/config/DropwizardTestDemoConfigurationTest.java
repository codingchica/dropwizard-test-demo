package codingchica.demo.test.dropwizard.config;

import io.dropwizard.validation.BaseValidator;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the DropwizardTestDemoConfiguration class.
 * See also: https://github.com/dropwizard/dropwizard/blob/master/dropwizard-configuration/src/test/java/io/dropwizard/configuration/ConfigurationValidationExceptionTest.java
 */
class DropwizardTestDemoConfigurationTest {
    private DropwizardTestDemoConfiguration dropwizardTestDemoConfiguration = ConfigFactory.dropwizardTestDemoConfiguration();

    final Validator validator = BaseValidator.newValidator();


    @Test
    void happyPath() {
        // Setup

        // Execution
        final Set<ConstraintViolation<DropwizardTestDemoConfiguration>> violations = validator.validate(dropwizardTestDemoConfiguration);

        // Validation
        assertTrue(violations.isEmpty());
    }
}