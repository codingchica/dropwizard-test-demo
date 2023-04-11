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
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@ExtendWith(DropwizardExtensionsSupport.class)
class GetEmployeeCommandTest {
  @Nested
  class POJOTests {
    final String name = "getEmployee";
    final String description = "Retrieve an existing employee";

    @Spy
    private DropwizardTestDemoConfiguration dropwizardTestDemoConfiguration =
        ConfigFactory.dropwizardTestDemoConfiguration();

    private Application<DropwizardTestDemoConfiguration> application;
    @Mock private Subparser subparser;
    @Mock private Argument argument;
    private GetEmployeeCommand getEmployeeCommand =
        new GetEmployeeCommand(application, name, description);
    @Mock private Namespace namespace;
    @Mock private Environment environment;
    @Spy private ObjectMapper objectMapper = newObjectMapper();

    @Test
    void constructor_nulls() {
      // Setup

      // Execution
      GetEmployeeCommand getEmployeeCommand = new GetEmployeeCommand(null, null, null);

      // Validation
      assertNotNull(getEmployeeCommand);
      assertNull(getEmployeeCommand.getConfiguration());
      assertNull(getEmployeeCommand.getDescription());
      assertNull(getEmployeeCommand.getName());
    }

    @Test
    void constructor_populated() {
      // Setup

      // Execution
      GetEmployeeCommand getEmployeeCommand =
          new GetEmployeeCommand(application, name, description);

      // Validation
      assertNotNull(getEmployeeCommand, "getEmployeeCommand");
      assertNull(getEmployeeCommand.getConfiguration(), "configuration");
      assertEquals(description, getEmployeeCommand.getDescription(), "description");
      assertEquals(name, getEmployeeCommand.getName(), "name");
    }

    @Test
    void configure_null() {
      // Setup

      // Execution
      Executable executable = () -> getEmployeeCommand.configure(null);

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
      when(argument.type(String.class)).thenReturn(argument);
      when(argument.required(anyBoolean())).thenReturn(argument);
      when(argument.help(any(String.class))).thenReturn(argument);
      when(argument.nargs("?")).thenReturn(argument);

      // Execution
      getEmployeeCommand.configure(subparser);

      // Validation
      verify(subparser).addArgument("--guid");
      verify(subparser).addArgument("file");
      verify(argument).help("application configuration file");
      verify(argument).dest("guid");
      verify(argument).type(String.class);
      verify(argument).required(true);
      verify(argument).help("The global unique identifier (GUID) of the employee to retrieve.");
      verify(argument).nargs("?");
      verifyNoMoreInteractions(argument);
      verifyNoMoreInteractions(subparser);
    }

    @Test
    void run_environmentNull() {
      // Setup

      // Execution
      Executable executable =
          () -> getEmployeeCommand.run(null, namespace, dropwizardTestDemoConfiguration);

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
          () -> getEmployeeCommand.run(environment, null, dropwizardTestDemoConfiguration);

      // Validation
      NullPointerException nullPointerException =
          assertThrows(NullPointerException.class, executable);
      assertEquals("namespace must not be null", nullPointerException.getMessage());
    }

    @Test
    void run_configNull() {
      // Setup

      // Execution
      Executable executable = () -> getEmployeeCommand.run(environment, namespace, null);

      // Validation
      NullPointerException nullPointerException =
          assertThrows(NullPointerException.class, executable);
      assertEquals(
          "dropwizardTestDemoConfiguration must not be null", nullPointerException.getMessage());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    void run_idNotPositive(String value) {
      // Setup
      when(namespace.getString("guid")).thenReturn(value);

      // Execution
      Executable executable =
          () -> getEmployeeCommand.run(environment, namespace, dropwizardTestDemoConfiguration);

      // Validation
      IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, executable);
      assertEquals("guid must not be blank", exception.getMessage());
    }

    @Test
    void run_mapperNull() {
      // Setup
      when(environment.getObjectMapper()).thenReturn(null);
      when(namespace.getString("guid")).thenReturn("1");

      // Execution
      Executable executable =
          () -> getEmployeeCommand.run(environment, namespace, dropwizardTestDemoConfiguration);

      // Validation
      NullPointerException nullPointerException =
          assertThrows(NullPointerException.class, executable);
      assertEquals("mapper must not be null", nullPointerException.getMessage());
    }

    @Test
    void run_populated() throws Exception {
      // Setup
      when(environment.getObjectMapper()).thenReturn(objectMapper);
      when(namespace.getString("guid")).thenReturn("1");

      // Execution
      getEmployeeCommand.run(environment, namespace, dropwizardTestDemoConfiguration);

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
    private String cliCommand = "GetEmployee";
    private static final String CONFIG_FILE = "src/test/resources/appConfig/test-component.yml";

    @BeforeEach
    void setUp() throws Exception {
      // Setup necessary mock
      when(jarLocation.getVersion()).thenReturn(Optional.of("0.1-SNAPSHOT"));

      // Add commands you want to test
      bootstrap.addCommand(
          new GetEmployeeCommand(application, cliCommand, "Retrieve a particular employee"));

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
    void retrievesEmployeeByID() {
      // Setup
      String expectedJSON =
          "{\"guid\":\"abc42\",\"firstName\":\"John\",\"lastName\":\"Doe\",\"nickName\":\"Johnny\"}";

      // Execution
      final Optional<Throwable> exception = cli.run(cliCommand, "--guid", "abc42", CONFIG_FILE);

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
