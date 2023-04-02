package codingchica.demo.test.dropwizard.component.steps;

import static org.junit.jupiter.api.Assertions.*;

import codingchica.demo.test.dropwizard.component.model.CLIWorld;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

public class GenericCLISteps {
  private CLIWorld world = new CLIWorld();
  /**
   * The working directory for the java process should remain the build root, so that the JaCoCo
   * java agent's relative path does not change between Maven's JUnit execution and the new
   * processes being spawned.
   */
  private File workingDirectory = new File(System.getProperty("user.dir"));

  private String replaceSystemProperties(String stringValue) {
    String returnValue = stringValue;
    if (returnValue != null && StringUtils.contains(returnValue, "${")) {
      for (Map.Entry<Object, Object> entry : System.getProperties().entrySet()) {
        if (entry.getKey() instanceof String prop && entry.getValue() instanceof String value) {
          String keyword = String.format("${%s}", prop);
          if (StringUtils.contains(returnValue, keyword)) {
            System.out.printf("Replacing %s with %s in %s%n", keyword, value, returnValue);
            returnValue = StringUtils.replace(returnValue, keyword, value);
          }
        } else {
          returnValue = stringValue;
        }
      }
    }

    if (returnValue != null && StringUtils.contains(returnValue, "${")) {
      for (Map.Entry<String, String> entry : System.getenv().entrySet()) {
        String prop = entry.getKey();
        String value = entry.getValue();
        String keyword = String.format("${%s}", prop);
        if (StringUtils.contains(stringValue, keyword)) {
          System.out.printf("Replacing %s with %s in %s%n", keyword, value, returnValue);
          returnValue = StringUtils.replace(returnValue, keyword, value);
        }
      }
    }
    assertFalse(
        StringUtils.contains(returnValue, "${"),
        String.format(
            "Found unexpected substitution variable in %s.  If running outside of the Maven build,"
                + " please add this as an environment variable.",
            returnValue));
    return returnValue;
  }

  @Given("that my cli call includes the argument {word}")
  public void addArgument(String argument) {
    if (StringUtils.equals("java", argument)) {

      for (Map.Entry<String, String> entry : System.getenv().entrySet()) {
        String prop = entry.getKey();
        String value = entry.getValue();
        if (StringUtils.contains(prop, "JAVA")) {
          System.out.printf("Replacing %s with %s%n", prop, value);
        }
      }

      world.arguments.add(String.format("%s/bin/java", System.getenv("JAVA_HOME")));

      List<String> jvmInputs = ManagementFactory.getRuntimeMXBean().getInputArguments();
      for (String entry : jvmInputs) {
        if (StringUtils.contains(entry, "javaagent")) {
          System.out.println(String.format("Passing along Java agent: %s", entry));
          world.arguments.add(entry);
        }
      }
    } else {
      world.arguments.add(replaceSystemProperties(argument));
    }
  }

  @Given("that my cli call includes the arguments")
  public void addArguments(List<String> arguments) {
    if (arguments != null) {
      arguments.forEach(this::addArgument);
    }
  }

  private List<String> getNewLinesFromBuffer(
      BufferedReader bufferedReader,
      Process process,
      boolean stopWhenKeywordInLogs,
      String ouputLogSnippetForShutdown)
      throws IOException {
    List<String> lines = new ArrayList<>();
    String nextLine = null;
    do {
      nextLine = bufferedReader.readLine();
      System.out.println(nextLine);
      if (nextLine != null) {
        world.outputLines.add(nextLine);
        if (stopWhenKeywordInLogs && StringUtils.contains(nextLine, ouputLogSnippetForShutdown)) {
          System.out.println("Server started successfully - stopping server");
          process.destroyForcibly();
          break;
        }
      }
    } while (nextLine != null);
    return lines;
  }

  @When("I run the CLI command until it stops")
  public void runCommandUntilItStops()
      throws IOException, InterruptedException, ExecutionException, TimeoutException {
    runCommand(false, null);
  }

  @When("I run the CLI command until output contains: {string}")
  public void runCommandUntilKeyword(String outputShutdownSnippet)
      throws IOException, InterruptedException {
    runCommand(true, outputShutdownSnippet);
  }

  public void runCommand(boolean stopOnceFullyStarted, String outputShutdownSnippet)
      throws IOException, InterruptedException {
    System.out.println(world.arguments);

    ProcessBuilder builder = new ProcessBuilder();
    builder.command(world.arguments);
    builder.directory(workingDirectory);
    Process process = builder.start();
    try (InputStreamReader outputReader = new InputStreamReader(process.getInputStream());
        InputStreamReader errorReader = new InputStreamReader(process.getErrorStream());
        BufferedReader outputBuffer = new BufferedReader(outputReader);
        BufferedReader errorBuffer = new BufferedReader(errorReader); ) {

      int sleepIntervalMilliSec = 10;
      int pollCount = 0;
      int maxPollCount = 900;
      do {
        // Read std out logs as we go, so buffers don't fill up.
        pollCount++;
        System.out.println("Retrieving logs while polling for status");
        Thread.sleep(sleepIntervalMilliSec);

        world.outputLines.addAll(
            getNewLinesFromBuffer(
                outputBuffer, process, stopOnceFullyStarted, outputShutdownSnippet));

      } while (process.isAlive() && pollCount <= maxPollCount);

      world.exitCode = process.exitValue();

      world.errorOutputLines.addAll(getNewLinesFromBuffer(errorBuffer, process, false, null));
    }
  }

  @Then("the cli exit code is {int}")
  public void confirmExitCode(int expectedCode) {
    assertEquals(expectedCode, world.exitCode, "Exit code");
  }

  private boolean containsFullLineMatch(String expectedOutput, List<String> actualOutput) {

    boolean match = false;
    if (world.outputLines != null) {

      match =
          world.outputLines.stream()
              .anyMatch(
                  (item) ->
                      StringUtils.equals(
                          StringUtils.normalizeSpace(item),
                          StringUtils.normalizeSpace(expectedOutput)));
    }
    return match;
  }

  private boolean containsPartialMatch(String expectedOutput, List<String> actualOutput) {

    boolean match = false;
    if (world.outputLines != null) {

      match =
          world.outputLines.stream()
              .anyMatch(
                  (item) ->
                      StringUtils.contains(
                          StringUtils.normalizeSpace(item),
                          StringUtils.normalizeSpace(expectedOutput)));
    }
    return match;
  }

  @Then("CLI standard output contains the normalized line {string}")
  public void outputContainsFullLine(String expectedOutput) {
    String interpretedOutput = replaceSystemProperties(expectedOutput);
    assertTrue(
        containsFullLineMatch(interpretedOutput, world.outputLines),
        "No match for line '" + interpretedOutput + "' in standard output:  " + world.outputLines);
  }

  @Then("CLI standard output contains the partial line {string}")
  public void outputContainsPartialLine(String expectedOutput) {
    String interpretedOutput = replaceSystemProperties(expectedOutput);
    assertTrue(
        containsPartialMatch(interpretedOutput, world.outputLines),
        "No partial match for line '"
            + interpretedOutput
            + "' in standard output:  "
            + world.outputLines);
  }

  @Then("CLI standard error contains the normalized line {word}")
  public void errorContainsFullLine(String expectedOutput) {
    String interpretedOutput = replaceSystemProperties(expectedOutput);
    assertTrue(
        containsFullLineMatch(interpretedOutput, world.errorOutputLines),
        "No match for line '"
            + interpretedOutput
            + "' in standard error:  "
            + world.errorOutputLines);
  }

  @Then("CLI standard error contains the partial line {word}")
  public void errorContainsPartialLine(String expectedOutput) {
    String interpretedOutput = replaceSystemProperties(expectedOutput);
    assertTrue(
        containsPartialMatch(interpretedOutput, world.errorOutputLines),
        "No partial match for line '"
            + interpretedOutput
            + "' in standard error:  "
            + world.errorOutputLines);
  }

  @And("CLI standard error is empty")
  public void thenCLIStandardErrorIsEmpty() {
    assertTrue(
        CollectionUtils.isEmpty(world.errorOutputLines),
        "Expected empty error output, but saw: " + world.errorOutputLines);
  }
}
