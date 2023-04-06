# See Gherkin syntax reference: https://cucumber.io/docs/gherkin/reference/
@person
Feature: Person API - Get - Success

  Background:
    Given that my request uses the http protocol
    And that my request goes to the application port

  @Component
  Scenario Outline: Various Request Content Types
    # Get doesn't consume a request body, so it accepts the request, regardless of which content type header is provided.
    Given that my request contains header Content-Type = <MIMEType>
    And that my request contains header Accept = application/json
    And that my request goes to endpoint people/1
    And that my request uses the GET method
    When I submit the request
    Then the response code is 200
    And the response body contains JSON data
      | id        | 1      |
      | firstName | John   |
      | lastName  | Doe    |
      | nickName  | Johnny |
    Examples:
      | MIMEType              |
      | application/ld+json   |
      | application/html      |
      | application/json      |
      | application/xhtml+xml |
      | application/zip       |
      | text/plain            |
