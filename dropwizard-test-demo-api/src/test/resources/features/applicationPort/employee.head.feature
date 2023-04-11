# See Gherkin syntax reference: https://cucumber.io/docs/gherkin/reference/
@employee
Feature: Employee API - Head - Success

  Background:
    Given that my request uses the http protocol
    And that my request goes to the application port

  Rule:  When successful, the expected response should be returned.

    @Component
    Scenario Outline: Success.
      Given that my request uses the <HTTPMethod> method
      And that my request goes to endpoint employees/1
      When I submit the request
      Then the response code is 200
      And the response body is completely empty
      Examples:
        | HTTPMethod |
        | HEAD       |


