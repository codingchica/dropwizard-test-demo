package codingchica.demo.test.dropwizard.component;

import codingchica.demo.test.dropwizard.DropwizardTestDemoApplication;
import codingchica.demo.test.dropwizard.config.DropwizardTestDemoConfiguration;
import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit5.DropwizardAppExtension;

/**
 * A class to aid in the starting and stopping of the dropwizard server during component testing.
 */
public class ComponentTestServerFactory {

  private static final DropwizardAppExtension<DropwizardTestDemoConfiguration> DROP_WIZARD_SERVER =
      new DropwizardAppExtension<DropwizardTestDemoConfiguration>(
          DropwizardTestDemoApplication.class,
          ResourceHelpers.resourceFilePath("test-component.yml"));

  /** How many tests are currently using the running server. */
  private static Integer activeTestCount = 0;

  /**
   * Start the Dropwizard server, if necessary. Register a new test using the running server. Tests
   * calling this method must later call the related method:
   *
   * @see #stopServer()
   * @return An object representing the running server.
   * @throws Exception If the server cannot be started.
   */
  public static DropwizardAppExtension<DropwizardTestDemoConfiguration> startServer()
      throws Exception {
    synchronized (activeTestCount) {
      if (activeTestCount == 0) {
        DROP_WIZARD_SERVER.before();
      }
      activeTestCount = activeTestCount + 1;
    }
    return DROP_WIZARD_SERVER;
  }

  /**
   * Indicate that a test that was previously registered with startServer() is now done running and
   * no longer needs access to the running server instance.
   *
   * @see #startServer()
   * @throws InterruptedException If the thread is interrupted during the sleep prior to shutdown.
   */
  public static void stopServer() throws InterruptedException {
    synchronized (activeTestCount) {
      activeTestCount = activeTestCount - 1;
      if (activeTestCount == 0) {
        // For good measure in case anything goes wrong with the count.
        Thread.sleep(1000);
        DROP_WIZARD_SERVER.after();
      }
    }
  }
}
