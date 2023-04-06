# See Gherkin syntax reference: https://cucumber.io/docs/gherkin/reference/
@person
Feature: Person API - Unsupported Methods

  Background:
    Given that my request uses the http protocol
    And that my request goes to the application port

  @Component
  Scenario Outline: 405 Method Not Supported
    Given that my request uses the <HTTPMethod> method
    And that my request goes to endpoint people/1
    When I submit the request
    Then the response code is 405
    Examples:
      | HTTPMethod |
      | POST       |
      | PUT        |
      | TRACE      |
      | DELETE     |

