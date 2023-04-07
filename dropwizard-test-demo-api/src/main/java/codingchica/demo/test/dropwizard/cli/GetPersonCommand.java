package codingchica.demo.test.dropwizard.cli;

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

public class GetPersonCommand extends EnvironmentCommand<DropwizardTestDemoConfiguration> {
  public GetPersonCommand(
      Application<DropwizardTestDemoConfiguration> application, String name, String description) {
    super(application, name, description);
  }

  private Person getPerson(int id) {
    return Person.builder().id(id).firstName("John").lastName("Doe").nickName("Johnny").build();
  }

  @Override
  protected void run(
      Environment environment,
      Namespace namespace,
      DropwizardTestDemoConfiguration dropwizardTestDemoConfiguration)
      throws Exception {
    // TODO for now this is hard coded, but will need to hook this up to persistence for retrieval.
    Preconditions.checkNotNull(namespace, "namespace must not be null");
    int id = namespace.getInt("id");

    Preconditions.checkNotNull(environment, "environment must not be null");
    Preconditions.checkNotNull(
        dropwizardTestDemoConfiguration, "dropwizardTestDemoConfiguration must not be null");
    Preconditions.checkArgument(0 < id, "id must be positive");

    Person person = getPerson(id);
    ObjectMapper mapper = environment.getObjectMapper();
    Preconditions.checkNotNull(mapper, "mapper must not be null");
    System.out.println(mapper.writeValueAsString(person));
  }

  @Override
  public void configure(@NonNull Subparser subparser) {
    Preconditions.checkNotNull(subparser, "subparser must not be null");
    subparser
        .addArgument("--id")
        .dest("id")
        .type(int.class)
        .required(true)
        .help("The unique identifier (ID) of the person to retrieve.");
  }
}
