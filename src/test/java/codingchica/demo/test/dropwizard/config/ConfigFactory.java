package codingchica.demo.test.dropwizard.config;

/** A factory for generating happy-path configurations to use in testing. */
public class ConfigFactory {
  public static DropwizardTestDemoConfiguration dropwizardTestDemoConfiguration() {
    return new DropwizardTestDemoConfiguration();
  }
}
