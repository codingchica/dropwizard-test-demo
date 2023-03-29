package codingchica.demo.test.dropwizard.config;

/** A factory for generating happy-path configurations to use in testing. */
public class ConfigFactory {

  /**
   * Construct and populate a valid DropwizardTestDemoConfiguration that can be used for happy-path
   * testing.
   *
   * @return A populated DropwizardTestDemoConfiguration object, setup for validation happy-path.
   */
  public static DropwizardTestDemoConfiguration dropwizardTestDemoConfiguration() {
    return new DropwizardTestDemoConfiguration();
  }
}
