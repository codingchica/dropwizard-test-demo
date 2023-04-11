# See Gherkin syntax reference: https://cucumber.io/docs/gherkin/reference/
# If running this feature file directly (not part of the Maven build) you will need to
# set the following env/system properties that normally come from the Maven build:
# project.artifactId=${project.artifactId}
# project.version=${project.version}

@version
Feature: CLI GetEmployee

  Rule: Inputs should be validated.

    @Component
    Scenario: Missing Key
      Given that my cli call includes the arguments
        | java                                                |
        | -jar                                                |
        | target/${project.artifactId}-${project.version}.jar |
        | GetEmployee                                         |
        | src/test/resources/appConfig/test-component.yml     |
      When I run the CLI command until it stops
      Then the cli exit code is 1
      And CLI standard error is empty
      And CLI standard output contains the partial line 'GetEmployee --guid GUID [-h]'
      And CLI standard output contains the partial line '--guid GUID            The  global  unique   identifier   (GUID)  of  the'
      And CLI standard output contains the partial line 'employee to retrieve.'

    @Component
    Scenario: Missing Value
      Given that my cli call includes the arguments
        | java                                                |
        | -jar                                                |
        | target/${project.artifactId}-${project.version}.jar |
        | GetEmployee                                         |
        | --guid                                              |
        |                                                     |
        | src/test/resources/appConfig/test-component.yml     |
      When I run the CLI command until it stops
      Then the cli exit code is 1
      And CLI standard error is empty
      And CLI standard output contains the partial line 'java.lang.IllegalArgumentException: guid must not be blank'


  Rule: Successfully retrieve an existing employee.

    @Component
    Scenario: Successful
      Given that my cli call includes the arguments
        | java                                                |
        | -jar                                                |
        | target/${project.artifactId}-${project.version}.jar |
        | GetEmployee                                         |
        | --guid                                              |
        | 1                                                |
        | src/test/resources/appConfig/test-component.yml     |
      When I run the CLI command until it stops
      Then the cli exit code is 0
      And CLI standard error is empty
      And CLI standard output contains the partial line '{"guid":"1","firstName":"John","lastName":"Doe","nickName":"Johnny"}'