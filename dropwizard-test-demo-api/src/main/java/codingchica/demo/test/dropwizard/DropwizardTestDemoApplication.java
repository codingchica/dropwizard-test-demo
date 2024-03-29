package codingchica.demo.test.dropwizard;

import codingchica.demo.test.dropwizard.api.commands.GetEmployeeCommand;
import codingchica.demo.test.dropwizard.api.resources.EmployeeResource;
import codingchica.demo.test.dropwizard.core.config.DropwizardTestDemoConfiguration;
import codingchica.demo.test.dropwizard.service.EmployeeService;
import io.dropwizard.core.Application;
import io.dropwizard.core.setup.Bootstrap;
import io.dropwizard.core.setup.Environment;
import org.apache.commons.lang3.ArrayUtils;

/** The main DropWizard application / controller. */
public class DropwizardTestDemoApplication extends Application<DropwizardTestDemoConfiguration> {
  /**
   * Entry point from the command line when starting up the DropWizard application.
   *
   * @param args Command line arguments.
   * @throws Exception If the application is unable to start up.
   */
  public static void main(final String[] args) throws Exception {
    String[] arguments = ArrayUtils.nullToEmpty(args);
    new DropwizardTestDemoApplication().run(arguments);
  }

  /**
   * Retrieve the name of the application. Mostly used for the command-line interface.
   *
   * @return The application name.
   */
  @Override
  public String getName() {
    return "DropwizardTestDemo";
  }

  /**
   * Initialize the application with the provided bootstrap configuration. This is where you would
   * add bundles, or commands
   *
   * @param bootstrap The configuration to use to bootstrap the application during startup.
   * @see <a
   *     href="https://www.dropwizard.io/en/latest/manual/core.html#man-core-bundles">Bundles</a>
   * @see <a
   *     href="https://www.dropwizard.io/en/latest/manual/core.html#man-core-commands">Commands</a>
   */
  @Override
  public void initialize(final Bootstrap<DropwizardTestDemoConfiguration> bootstrap) {
    // Enable variable substitution with environment variables
    //    bootstrap.setConfigurationSourceProvider(
    //        new SubstitutingSourceProvider(
    //            bootstrap.getConfigurationSourceProvider(), new
    // EnvironmentVariableSubstitutor()));

    bootstrap.addCommand(getEmployeeCommand());
  }

  /**
   * Construct a new EmployeeService object. Exposing this as a separate method, even though the
   * logic is simple, in case we need to modify the behavior during any testing.
   *
   * @return An Employee Service object.
   */
  public EmployeeService employeeService() {
    return new EmployeeService();
  }

  /**
   * Construct a new EmployeeResource object. Exposing this as a separate method, even though the
   * logic is simple, in case we need to modify the behavior during any testing.
   *
   * @param employeeService An EmployeeService to use within the EmployeeResource.
   * @return A EmployeeResource object.
   */
  public EmployeeResource employeeResource(EmployeeService employeeService) {
    return new EmployeeResource(employeeService);
  }

  /**
   * Construct a new GetEmployeeCommand object. Exposing this as a separate method, even though the
   * logic is simple, in case we need to modify the behavior during any testing.
   *
   * @return A GetEmployeeCommand object.
   */
  public GetEmployeeCommand getEmployeeCommand() {
    return new GetEmployeeCommand(this, "GetEmployee", "Retrieve an employee by ID.");
  }

  /**
   * Execute the DropWizard application with the specified configuration and environment settings.
   * This is where you would add filters, health checks, health, Jersey providers, Managed Objects,
   * servlets, and tasks.
   *
   * <ul>
   *   <li><a href="https://www.dropwizard.io/en/latest/manual/core.html#man-core-health">Health</a>
   *   <li><a
   *       href="https://www.dropwizard.io/en/latest/manual/core.html#man-core-healthchecks">Health
   *       Checks</a>
   *   <li><a href="https://www.dropwizard.io/en/latest/manual/core.html#man-core-managed">Managed
   *       Objects</a>
   *   <li><a
   *       href="https://www.dropwizard.io/en/latest/manual/core.html#man-core-resources">Resources</a>
   *   <li><a href="https://www.dropwizard.io/en/latest/manual/core.html#man-core-tasks">Tasks</a>
   * </ul>
   *
   * @param configuration POJO representing configuration file provided during application launch.
   * @param environment Environment setup to work within.
   */
  @Override
  public void run(
      final DropwizardTestDemoConfiguration configuration, final Environment environment) {
    // Resources that will be used by the application.
    environment.jersey().register(employeeResource(employeeService()));
  }
}
