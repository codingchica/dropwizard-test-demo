# See Gherkin syntax reference: https://cucumber.io/docs/gherkin/reference/
@employee
Feature: Employee API - Get

  Background:
    Given that my request uses the http protocol
    And that my request goes to the application port

  Rule:  Input validation should be performed on all inputs consumed.

    @Component
    Scenario Outline: Failures - Unsupported Response Content Types
      Given that my request contains header Accept = <MIMEType>
      And that my request goes to endpoint employees/1
      And that my request uses the GET method
      When I submit the request
      Then the response code is 406
      And the error response body contains JSON data
        | code    | 406                     |
        | message | HTTP 406 Not Acceptable |
      Examples:
        | MIMEType              |
        | application/ld+json   |
        | application/html      |
        | application/xhtml+xml |
        | application/zip       |
        | text/plain            |

  Rule:  When successful, the expected response should be returned.

    @Component
    Scenario Outline: Various Request Content Types
      # Get doesn't consume a request body, so it accepts the request, regardless of which content type header is provided.
      Given that my request contains header Content-Type = <MIMEType>
      And that my request contains header Accept = application/json
      And that my request goes to endpoint employees/1
      And that my request uses the GET method
      When I submit the request
      Then the response code is 200
      And the response body contains JSON data
        | guid      | 1      |
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
