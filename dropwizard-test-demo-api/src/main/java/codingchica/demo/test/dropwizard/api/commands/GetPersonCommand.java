package codingchica.demo.test.dropwizard.api.commands;

import codingchica.demo.test.dropwizard.core.config.DropwizardTestDemoConfiguration;
import codingchica.demo.test.dropwizard.core.model.external.Person;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import io.dropwizard.Application;
import io.dropwizard.cli.EnvironmentCommand;
import io.dropwizard.setup.Environment;
import lombok.NonNull;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;

/** CLI command to retrieve a person by ID. */
public class GetPersonCommand extends EnvironmentCommand<DropwizardTestDemoConfiguration> {
  /**
   * The key that will be used to store/retrieve the ID of the person to retrieve when interacting
   * with the namespace.
   */
  private static final String NAMESPACE_KEY_ID = "id";

  /**
   * Constructor for the Get Person CLI command.
   *
   * @param application The dropwizard application which will be running the command.
   * @param name The name of the command on the CLI.
   * @param description A description of the command for the CLI help.
   */
  public GetPersonCommand(
      Application<DropwizardTestDemoConfiguration> application, String name, String description) {
    super(application, name, description);
  }

  /**
   * Get an existing person by the ID.
   *
   * @param id The ID of the person to retrieve.
   * @return The existing Person object to return on the response.
   */
  private Person getPerson(int id) {
    return Person.builder().id(id).firstName("John").lastName("Doe").nickName("Johnny").build();
  }

  /**
   * Run the command from the CLI call.
   *
   * @param environment The environment setup within the application running this command.
   * @param namespace The namespace were input
   * @param dropwizardTestDemoConfiguration The application's configuration.
   * @throws Exception When there is an issue during the person retrieval.
   */
  @Override
  protected void run(
      Environment environment,
      Namespace namespace,
      DropwizardTestDemoConfiguration dropwizardTestDemoConfiguration)
      throws Exception {
    // TODO for now this is hard coded, but will need to hook this up to persistence for retrieval.
    Preconditions.checkNotNull(namespace, "namespace must not be null");
    int id = namespace.getInt(NAMESPACE_KEY_ID);

    Preconditions.checkNotNull(environment, "environment must not be null");
    Preconditions.checkNotNull(
        dropwizardTestDemoConfiguration, "dropwizardTestDemoConfiguration must not be null");
    Preconditions.checkArgument(0 < id, "id must be positive");

    Person person = getPerson(id);
    ObjectMapper mapper = environment.getObjectMapper();
    Preconditions.checkNotNull(mapper, "mapper must not be null");
    System.out.println(mapper.writeValueAsString(person));
  }

  /**
   * Configure the CLI help/user interface based upon how we expect the inputs to be configured.
   *
   * @param subparser The parser that will be responsible for consuming and interpreting the
   *     command's configuration.
   */
  @Override
  public void configure(@NonNull Subparser subparser) {
    Preconditions.checkNotNull(subparser, "subparser must not be null");
    subparser
        .addArgument("--id")
        .dest(NAMESPACE_KEY_ID)
        .type(int.class)
        .required(true)
        .help("The unique identifier (ID) of the person to retrieve.");
  }
}
