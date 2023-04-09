package codingchica.demo.test.dropwizard.api.commands;

import static io.dropwizard.jackson.Jackson.newObjectMapper;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import codingchica.demo.test.dropwizard.DropwizardTestDemoApplication;
import codingchica.demo.test.dropwizard.config.ConfigFactory;
import codingchica.demo.test.dropwizard.core.config.DropwizardTestDemoConfiguration;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.Application;
import io.dropwizard.cli.Cli;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import io.dropwizard.util.JarLocation;
import java.io.*;
import java.util.Optional;
import javax.annotation.concurrent.NotThreadSafe;
import net.sourceforge.argparse4j.inf.Argument;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@ExtendWith(DropwizardExtensionsSupport.class)
class GetPersonCommandTest {
  @Nested
  class POJOTests {
    final String name = "getPerson";
    final String description = "Retrieve an existing person";

    @Spy
    private DropwizardTestDemoConfiguration dropwizardTestDemoConfiguration =
        ConfigFactory.dropwizardTestDemoConfiguration();

    private Application<DropwizardTestDemoConfiguration> application;
    @Mock private Subparser subparser;
    @Mock private Argument argument;
    private GetPersonCommand getPersonCommand =
        new GetPersonCommand(application, name, description);
    @Mock private Namespace namespace;
    @Mock private Environment environment;
    @Spy private ObjectMapper objectMapper = newObjectMapper();

    @Test
    void constructor_nulls() {
      // Setup

      // Execution
      GetPersonCommand getPersonCommand = new GetPersonCommand(null, null, null);

      // Validation
      assertNotNull(getPersonCommand);
      assertNull(getPersonCommand.getConfiguration());
      assertNull(getPersonCommand.getDescription());
      assertNull(getPersonCommand.getName());
    }

    @Test
    void constructor_populated() {
      // Setup

      // Execution
      GetPersonCommand getPersonCommand = new GetPersonCommand(application, name, description);

      // Validation
      assertNotNull(getPersonCommand, "getPersonCommand");
      assertNull(getPersonCommand.getConfiguration(), "configuration");
      assertEquals(description, getPersonCommand.getDescription(), "description");
      assertEquals(name, getPersonCommand.getName(), "name");
    }

    @Test
    void configure_null() {
      // Setup

      // Execution
      Executable executable = () -> getPersonCommand.configure(null);

      // Validation
      NullPointerException nullPointerException =
          assertThrows(NullPointerException.class, executable);
      assertEquals("subparser must not be null", nullPointerException.getMessage());
    }

    @Test
    void configure_populated() {
      // Setup
      when(subparser.addArgument(any())).thenReturn(argument);
      when(argument.dest(any())).thenReturn(argument);
      when(argument.type(int.class)).thenReturn(argument);
      when(argument.required(anyBoolean())).thenReturn(argument);
      when(argument.help(any(String.class))).thenReturn(argument);
      when(argument.nargs("?")).thenReturn(argument);

      // Execution
      getPersonCommand.configure(subparser);

      // Validation
      verify(subparser).addArgument("--id");
      verify(subparser).addArgument("file");
      verify(argument).help("application configuration file");
      verify(argument).dest("id");
      verify(argument).type(int.class);
      verify(argument).required(true);
      verify(argument).help("The unique identifier (ID) of the person to retrieve.");
      verify(argument).nargs("?");
      verifyNoMoreInteractions(argument);
      verifyNoMoreInteractions(subparser);
    }

    @Test
    void run_environmentNull() {
      // Setup

      // Execution
      Executable executable =
          () -> getPersonCommand.run(null, namespace, dropwizardTestDemoConfiguration);

      // Validation
      NullPointerException nullPointerException =
          assertThrows(NullPointerException.class, executable);
      assertEquals("environment must not be null", nullPointerException.getMessage());
    }

    @Test
    void run_namespaceNull() {
      // Setup

      // Execution
      Executable executable =
          () -> getPersonCommand.run(environment, null, dropwizardTestDemoConfiguration);

      // Validation
      NullPointerException nullPointerException =
          assertThrows(NullPointerException.class, executable);
      assertEquals("namespace must not be null", nullPointerException.getMessage());
    }

    @Test
    void run_configNull() {
      // Setup

      // Execution
      Executable executable = () -> getPersonCommand.run(environment, namespace, null);

      // Validation
      NullPointerException nullPointerException =
          assertThrows(NullPointerException.class, executable);
      assertEquals(
          "dropwizardTestDemoConfiguration must not be null", nullPointerException.getMessage());
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 0})
    void run_idNotPositive(int value) {
      // Setup
      when(namespace.getInt("id")).thenReturn(value);

      // Execution
      Executable executable =
          () -> getPersonCommand.run(environment, namespace, dropwizardTestDemoConfiguration);

      // Validation
      IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, executable);
      assertEquals("id must be positive", exception.getMessage());
    }

    @Test
    void run_mapperNull() {
      // Setup
      when(environment.getObjectMapper()).thenReturn(null);
      when(namespace.getInt("id")).thenReturn(1);

      // Execution
      Executable executable =
          () -> getPersonCommand.run(environment, namespace, dropwizardTestDemoConfiguration);

      // Validation
      NullPointerException nullPointerException =
          assertThrows(NullPointerException.class, executable);
      assertEquals("mapper must not be null", nullPointerException.getMessage());
    }

    @Test
    void run_populated() throws Exception {
      // Setup
      when(environment.getObjectMapper()).thenReturn(objectMapper);
      when(namespace.getInt("id")).thenReturn(1);

      // Execution
      getPersonCommand.run(environment, namespace, dropwizardTestDemoConfiguration);

      // Validation
      // No exception thrown
    }
  }

  /** See https://www.dropwizard.io/en/latest/manual/testing.html#testing-commands */
  @Nested
  @NotThreadSafe
  class CLITest {
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;
    private final InputStream originalIn = System.in;

    private final ByteArrayOutputStream stdOut = new ByteArrayOutputStream();
    private final ByteArrayOutputStream stdErr = new ByteArrayOutputStream();

    private DropwizardTestDemoApplication application = new DropwizardTestDemoApplication();

    @Mock JarLocation jarLocation;
    @Spy Bootstrap<DropwizardTestDemoConfiguration> bootstrap = new Bootstrap<>(application);

    private Cli cli = null;
    private String cliCommand = "GetPerson";
    private static final String CONFIG_FILE = "src/test/resources/appConfig/test-component.yml";

    @BeforeEach
    void setUp() throws Exception {
      // Setup necessary mock
      when(jarLocation.getVersion()).thenReturn(Optional.of("0.1-SNAPSHOT"));

      // Add commands you want to test
      bootstrap.addCommand(
          new GetPersonCommand(application, cliCommand, "Retrieve a particular person"));

      // Redirect stdout and stderr to our byte streams
      System.setOut(new PrintStream(stdOut));
      System.setErr(new PrintStream(stdErr));

      // Set up the CLI that will process the command.
      cli = new Cli(jarLocation, bootstrap, stdOut, stdErr);
    }

    @AfterEach
    void teardown() {
      System.setOut(originalOut);
      System.setErr(originalErr);
      System.setIn(originalIn);
    }

    @Test
    void retrievesPersonByID() {
      // Setup
      String expectedJSON =
          "{\"id\":1,\"firstName\":\"John\",\"lastName\":\"Doe\",\"nickName\":\"Johnny\"}";

      // Execution
      final Optional<Throwable> exception = cli.run(cliCommand, "--id", "1", CONFIG_FILE);

      // Validation
      String stdOutContents = stdOut.toString();
      String stdErrContents = stdErr.toString();
      assertTrue(exception.isEmpty(), "No exception returned in:" + exception);
      assertTrue(
          StringUtils.contains(stdOutContents, expectedJSON),
          String.format("expected %n%s%nin stdout: %s%n", expectedJSON, stdOutContents));
      assertEquals("", stdErr.toString(), "stdOut");
    }
  }
}
