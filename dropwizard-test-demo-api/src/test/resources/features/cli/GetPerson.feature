# See Gherkin syntax reference: https://cucumber.io/docs/gherkin/reference/
# If running this feature file directly (not part of the Maven build) you will need to
# set the following env/system properties that normally come from the Maven build:
# project.artifactId=${project.artifactId}
# project.version=${project.version}

@version
Feature: CLI GetPerson

  Rule: Inputs should be validated.

    @Component
    Scenario Outline: Missing ID / Value
      Given that my cli call includes the arguments
        | java                                                |
        | -jar                                                |
        | target/${project.artifactId}-${project.version}.jar |
        | GetPerson                                           |
        | <argument>                                          |
        | src/test/resources/appConfig/test-component.yml     |
      When I run the CLI command until it stops
      Then the cli exit code is 1
      And CLI standard error is empty
      And CLI standard output contains the partial line 'GetPerson --id ID [-h]'
      And CLI standard output contains the partial line '--id ID                The  unique  identifier  (ID)  of  the  person  to'
      And CLI standard output contains the partial line 'retrieve.'
      Examples:
        | argument |
        |          |
        | --id     |

    @Component
    Scenario Outline: ID not Positive
      Given that my cli call includes the arguments
        | java                                                |
        | -jar                                                |
        | target/${project.artifactId}-${project.version}.jar |
        | GetPerson                                           |
        | --id                                                |
        | <id>                                                |
        | src/test/resources/appConfig/test-component.yml     |
      When I run the CLI command until it stops
      Then the cli exit code is 1
      And CLI standard error is empty
      And CLI standard output contains the partial line 'java.lang.IllegalArgumentException: id must be positive'
      Examples:
        | id          |
        | -2147483648 |
        | -1          |
        | 0           |


    @Component
    Scenario Outline: ID not an int
      Given that my cli call includes the arguments
        | java                                                |
        | -jar                                                |
        | target/${project.artifactId}-${project.version}.jar |
        | GetPerson                                           |
        | --id                                                |
        | <id>                                                |
        | src/test/resources/appConfig/test-component.yml     |
      When I run the CLI command until it stops
      Then the cli exit code is 1
      And CLI standard error is empty
      And CLI standard output contains the partial line 'GetPerson --id ID [-h]'
      And CLI standard output contains the partial line '--id ID                The  unique  identifier  (ID)  of  the  person  to'
      And CLI standard output contains the partial line 'retrieve.'
      Examples:
        | id          |
        | -2147483649 |
        | 2147483648  |
        | hello       |

  Rule: Successfully retrieve an existing person.

    @Component
    Scenario: Successful
      Given that my cli call includes the arguments
        | java                                                |
        | -jar                                                |
        | target/${project.artifactId}-${project.version}.jar |
        | GetPerson                                           |
        | --id                                                |
        | 1                                                   |
        | src/test/resources/appConfig/test-component.yml     |
      When I run the CLI command until it stops
      Then the cli exit code is 0
      And CLI standard error is empty
      And CLI standard output contains the partial line '{"id":1,"firstName":"John","lastName":"Doe","nickName":"Johnny"}'