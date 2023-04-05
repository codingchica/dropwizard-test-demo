package codingchica.demo.test.dropwizard.component;

import codingchica.demo.test.dropwizard.DropwizardTestDemoApplication;
import codingchica.demo.test.dropwizard.core.config.DropwizardTestDemoConfiguration;
import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit5.DropwizardAppExtension;
import lombok.Synchronized;

/**
 * A class to aid in the starting and stopping of the dropwizard server during component testing.
 */
public class ComponentTestServerFactory {

  private static final DropwizardAppExtension<DropwizardTestDemoConfiguration> DROP_WIZARD_SERVER =
      new DropwizardAppExtension<>(
          DropwizardTestDemoApplication.class,
          ResourceHelpers.resourceFilePath("appConfig/test-component.yml"));

  /** How many tests are currently using the running server. */
  private static Integer activeTestCount = 0;

  /**
   * Start the Dropwizard server, if necessary. Register a new test using the running server. Tests
   * calling this method must later call the related method:
   *
   * @return An object representing the running server.
   * @throws Exception If the server cannot be started.
   * @see #stopServer()
   */
  @Synchronized
  public static DropwizardAppExtension<DropwizardTestDemoConfiguration> startServer()
      throws Exception {
    if (activeTestCount == 0) {
      DROP_WIZARD_SERVER.before();
    }
    activeTestCount = activeTestCount + 1;
    return DROP_WIZARD_SERVER;
  }

  /**
   * Indicate that a test that was previously registered with startServer() is now done running and
   * no longer needs access to the running server instance.
   *
   * @see #startServer()
   */
  @Synchronized
  public static void stopServer() {
    activeTestCount = activeTestCount - 1;
    if (activeTestCount == 0) {
      DROP_WIZARD_SERVER.after();
    }
  }
}
