package codingchica.demo.test.dropwizard.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for the POJO methods within the DropwizardTestDemoConfiguration class.
 *
 * @see DropwizardTestDemoConfigurationValidationTest
 */
class DropwizardTestDemoConfigurationPOJOTest {

  @Test
  void constructor_whenInvoked_returnsObject() {
    // Setup

    // Execution
    DropwizardTestDemoConfiguration dropwizardTestDemoConfiguration =
        new DropwizardTestDemoConfiguration();

    // Validation
    assertNotNull(dropwizardTestDemoConfiguration);
  }
}
