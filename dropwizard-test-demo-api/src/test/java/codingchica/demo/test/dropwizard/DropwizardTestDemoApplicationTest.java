package codingchica.demo.test.dropwizard;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

import codingchica.demo.test.dropwizard.api.commands.GetEmployeeCommand;
import codingchica.demo.test.dropwizard.api.resources.EmployeeResource;
import codingchica.demo.test.dropwizard.core.config.DropwizardTestDemoConfiguration;
import io.dropwizard.core.setup.Bootstrap;
import io.dropwizard.core.setup.Environment;
import io.dropwizard.jersey.setup.JerseyEnvironment;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

/** Unit tests for the DropwizardTestDemoApplication class. */
@ExtendWith(MockitoExtension.class)
class DropwizardTestDemoApplicationTest {
  @Spy
  private DropwizardTestDemoApplication dropwizardTestDemoApplication =
      new DropwizardTestDemoApplication();

  @Mock private DropwizardTestDemoConfiguration dropwizardTestDemoConfiguration;
  @Mock private Environment environment;
  @Mock private JerseyEnvironment jerseyEnvironment;
  @Mock private Bootstrap<DropwizardTestDemoConfiguration> bootstrap;

  /** Unit tests for the run method. */
  @Nested
  class RunTest {
    @Test
    void whenRunInvoked_thenSetupAsExpected() {
      // Setup
      when(environment.jersey()).thenReturn(jerseyEnvironment);

      // Execution
      dropwizardTestDemoApplication.run(dropwizardTestDemoConfiguration, environment);

      // Validation
      verifyNoInteractions(dropwizardTestDemoConfiguration);

      verify(environment).jersey();
      verifyNoMoreInteractions(environment);

      verify(jerseyEnvironment).register(any(EmployeeResource.class));
      verifyNoMoreInteractions(jerseyEnvironment);
    }
  }

  /** Unit tests for the initialize method. */
  @Nested
  class InitializeTest {
    @Test
    void whenInitializeInvoked_thenSuccess() {
      // Setup
      ArgumentCaptor<GetEmployeeCommand> addCommandCaptor =
          ArgumentCaptor.forClass(GetEmployeeCommand.class);

      // Execution
      dropwizardTestDemoApplication.initialize(bootstrap);

      // Validation
      verify(bootstrap).addCommand(addCommandCaptor.capture());
      GetEmployeeCommand getEmployeeCommand = addCommandCaptor.getValue();
      assertEquals("GetEmployee", getEmployeeCommand.getName(), "name");
      assertEquals(
          "Retrieve an employee by ID.", getEmployeeCommand.getDescription(), "description");
      verifyNoMoreInteractions(bootstrap);
    }
  }

  /** Unit tests for the getName method. */
  @Nested
  class GetNameTest {
    @Test
    void whenGetNameInvoked_thenExpectedValueReturned() {
      // Setup

      // Execution
      String result = dropwizardTestDemoApplication.getName();

      // Validation
      assertEquals("DropwizardTestDemo", result, "result");
    }
  }

  /** Unit tests for the main method. */
  @Nested
  class MainTest {
    private final ByteArrayOutputStream systemOutput = new ByteArrayOutputStream();
    private final String usageInfo =
        StringUtils.normalizeSpace(
            """
usage: java -jar project.jar [-h] [-v] {server,check,GetEmployee} ...

positional arguments:
  {server,check,GetEmployee}         available commands

named arguments:
  -h, --help             show this help message and exit
  -v, --version          show the application version and exit
""");

    private final String versionInfoWhenError =
        StringUtils.normalizeSpace(
            """
No application version detected. Add a Implementation-Version entry to your JAR's manifest to enable this.
""");

    private void assertOutputPrinted(String expectedOutput) {
      String output = systemOutput.toString();
      assertNotNull(output);
      // Guard against EOL differences between systems.
      output = StringUtils.normalizeSpace(output);
      assertEquals(expectedOutput.trim(), output);
    }

    /** Setup to perform prior to each test case. */
    @BeforeEach
    void setup() {
      System.setOut(new PrintStream(systemOutput));
    }

    @Test
    void whenMainInvokedWithNullArguments_thenUsageInfoAndExit() throws Exception {
      // Setup

      // Execution
      DropwizardTestDemoApplication.main(null);

      // Validation
      // No exception thrown.  Immediately terminates.
      assertOutputPrinted(usageInfo);
    }

    @Test
    void whenMainInvokedWithEmptyArguments_thenUsageInfoAndExit() throws Exception {
      // Setup
      String[] args = new String[0];

      // Execution
      DropwizardTestDemoApplication.main(args);

      // Validation
      // No exception thrown.  Immediately terminates.
      assertOutputPrinted(usageInfo);
    }

    @ParameterizedTest
    @ValueSource(strings = {"-v", "--version"})
    void whenMainInvokedWithVersionArgs_thenVersionInfoAndExit(String parameterValue)
        throws Exception {
      // Setup
      String[] args = new String[1];
      args[0] = parameterValue;

      // Execution
      DropwizardTestDemoApplication.main(args);

      // Validation
      // No exception thrown.  Immediately terminates.
      // No jar provided on CLI args, so no manifest from which to pull version information.
      assertOutputPrinted(versionInfoWhenError);
    }

    @ParameterizedTest
    @ValueSource(strings = {"-h", "--help"})
    void whenMainInvokedWithHelpArgs_thenUsageInfoAndExit(String parameterValue) throws Exception {
      // Setup
      String[] args = new String[1];
      args[0] = parameterValue;

      // Execution
      DropwizardTestDemoApplication.main(args);

      // Validation
      // No exception thrown.  Immediately terminates.
      assertOutputPrinted(usageInfo);
    }
  }
}
