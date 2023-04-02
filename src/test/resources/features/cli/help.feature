# See Gherkin syntax reference: https://cucumber.io/docs/gherkin/reference/
# If running this feature file directly (not part of the Maven build) you will need to
# set the following env/system properties that normally come from the Maven build:
# project.artifactId=${project.artifactId}
# project.version=${project.version}

@version
Feature: CLI Help

  @Component
  Scenario Outline: CLI Help successful
    Given that my cli call includes the arguments
      | java                                                |
      | -jar                                                |
      | target/${project.artifactId}-${project.version}.jar |
      | <Argument>                                          |
    When I run the CLI command until it stops
    Then the cli exit code is 0
    And CLI standard error is empty
    And CLI standard output contains the normalized line 'usage: java -jar dropwizard-test-demo-0.1-SNAPSHOT.jar'
    And CLI standard output contains the normalized line '       [-h] [-v] {server,check} ...'
    And CLI standard output contains the normalized line 'positional arguments:'
    And CLI standard output contains the normalized line '  {server,check}         available commands'
    And CLI standard output contains the normalized line 'named arguments:'
    And CLI standard output contains the normalized line '  -h, --help             show this help message and exit'
    And CLI standard output contains the normalized line '  -v, --version          show the application version and exit'
    Examples:
      | Argument |
      | -h       |
      | --help   |