package codingchica.demo.test.dropwizard.api.commands;

import codingchica.demo.test.dropwizard.core.config.DropwizardTestDemoConfiguration;
import codingchica.demo.test.dropwizard.core.model.external.Employee;
import codingchica.demo.test.dropwizard.service.EmployeeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import io.dropwizard.Application;
import io.dropwizard.cli.EnvironmentCommand;
import io.dropwizard.setup.Environment;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;
import org.apache.commons.lang3.StringUtils;

/** CLI command to retrieve an employee by ID. */
public class GetEmployeeCommand extends EnvironmentCommand<DropwizardTestDemoConfiguration> {
  /**
   * The key that will be used to store/retrieve the GUID of the employee to retrieve when
   * interacting with the namespace.
   */
  private static final String NAMESPACE_KEY_GUID = "guid";

  /** The employee service that will be providing the business logic for people. */
  private final EmployeeService employeeService = new EmployeeService();

  /**
   * Constructor for the Get Employee CLI command.
   *
   * @param application The dropwizard application which will be running the command.
   * @param name The name of the command on the CLI.
   * @param description A description of the command for the CLI help.
   */
  public GetEmployeeCommand(
      Application<DropwizardTestDemoConfiguration> application, String name, String description) {
    super(application, name, description);
  }

  /**
   * Get an existing employee by the ID.
   *
   * @param guid The global unique identifier of the employee to retrieve.
   * @return The existing Employee object to return on the response.
   */
  private Employee getEmployee(String guid) {
    return employeeService.getEmployeeByGuid(guid);
  }

  /**
   * Run the command from the CLI call.
   *
   * @param environment The environment setup within the application running this command.
   * @param namespace The namespace were input
   * @param dropwizardTestDemoConfiguration The application's configuration.
   * @throws Exception When there is an issue during the employee retrieval.
   */
  @Override
  protected void run(
      Environment environment,
      Namespace namespace,
      DropwizardTestDemoConfiguration dropwizardTestDemoConfiguration)
      throws Exception {
    Preconditions.checkNotNull(namespace, "namespace must not be null");
    Preconditions.checkNotNull(environment, "environment must not be null");
    Preconditions.checkNotNull(
        dropwizardTestDemoConfiguration, "dropwizardTestDemoConfiguration must not be null");

    String guid = namespace.getString(NAMESPACE_KEY_GUID);
    Preconditions.checkArgument(!StringUtils.isBlank(guid), "guid must not be blank");

    Employee employee = getEmployee(guid);
    ObjectMapper mapper = environment.getObjectMapper();
    Preconditions.checkNotNull(mapper, "mapper must not be null");
    System.out.println(mapper.writeValueAsString(employee));
  }

  /**
   * Configure the CLI help/user interface based upon how we expect the inputs to be configured.
   *
   * @param subparser The parser that will be responsible for consuming and interpreting the
   *     command's configuration.
   */
  @Override
  public void configure(Subparser subparser) {
    Preconditions.checkNotNull(subparser, "subparser must not be null");
    // Consume a configuration file input, confirm subparser is not null
    super.configure(subparser);
    subparser
        .addArgument("--guid")
        .dest(NAMESPACE_KEY_GUID)
        .type(String.class)
        .required(true)
        .help("The global unique identifier (GUID) of the employee to retrieve.");
  }
}
