package codingchica.demo.test.dropwizard.config;

import static codingchica.demo.test.dropwizard.util.GetterSetterTester.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import codingchica.demo.test.dropwizard.core.config.DropwizardTestDemoConfiguration;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the POJO methods within the DropwizardTestDemoConfiguration class.
 *
 * @see DropwizardTestDemoConfigurationValidationTest
 */
class DropwizardTestDemoConfigurationPOJOTest {
  private final DropwizardTestDemoConfiguration dropwizardTestDemoConfiguration =
      new DropwizardTestDemoConfiguration();

  @Test
  void constructor_whenInvoked_returnsObject() {
    // Setup

    // Execution
    DropwizardTestDemoConfiguration dropwizardTestDemoConfiguration =
        new DropwizardTestDemoConfiguration();

    // Validation
    assertNotNull(dropwizardTestDemoConfiguration);
  }

  /** Ensure toString output would be helpful for debugging. */
  @Test
  void toString_whenInvoked_includesAllExpectedFields() {
    // Setup
    // Keep this one at default constructor values to minimize brittleness
    final DropwizardTestDemoConfiguration dropwizardTestDemoConfiguration =
        new DropwizardTestDemoConfiguration();

    // Execute
    String result = dropwizardTestDemoConfiguration.toString();

    // Validation
    assertEquals("DropwizardTestDemoConfiguration(testValue=null)", result);
  }

  /** Ensure that Lombok annotations are set up as expected. */
  @Nested
  class TestValueTest {
    private final Method getter =
        DropwizardTestDemoConfiguration.class.getDeclaredMethod("getTestValue");
    private final Method setter =
        DropwizardTestDemoConfiguration.class.getDeclaredMethod("setTestValue", String.class);

    TestValueTest() throws NoSuchMethodException {}

    @Test
    void testNull() throws InvocationTargetException, IllegalAccessException {
      nullValueRetrievedCorrectly(dropwizardTestDemoConfiguration, (String) null, getter, setter);
    }

    @Test
    void testPopulated() throws InvocationTargetException, IllegalAccessException {
      populatedValueRetrievedCorrectly(
          dropwizardTestDemoConfiguration, "some value goes here", getter, setter);
    }
  }
}
