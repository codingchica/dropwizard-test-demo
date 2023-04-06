# See Gherkin syntax reference: https://cucumber.io/docs/gherkin/reference/
@person
Feature: Person API - Get - Input Validation

  Background:
    Given that my request uses the http protocol
    And that my request goes to the application port

  @Component
  Scenario Outline: Failures - ID Not an Int
    Given that my request contains header Content-Type = application/json
    And that my request contains header Accept = application/json
    And that my request goes to endpoint people/<id>
    And that my request uses the GET method
    When I submit the request
    # Dropwizard can't map it to our method, which consumes an int, so it fails with a 404
    Then the response code is 404
    And the error response body contains JSON data
      | code    | 404            |
      | message | <errorMessage> |
    Examples:
      | id          | errorMessage       |
      | -2147483649 | HTTP 404 Not Found |
      | 2147483648  | HTTP 404 Not Found |
      | hello       | HTTP 404 Not Found |

  @Component
  Scenario Outline: Failures - ID Not Positive
    Given that my request contains header Content-Type = application/json
    And that my request contains header Accept = application/json
    And that my request goes to endpoint people/<id>
    And that my request uses the GET method
    When I submit the request
    Then the response code is 400
    And the error response body contains JSON data
      | errors | <errorMessage> |
    Examples:
      | id          | errorMessage                             |
      | -2147483648 | ["path param id must be greater than 0"] |
      | -1          | ["path param id must be greater than 0"] |
      | 0           | ["path param id must be greater than 0"] |


  @Component
  Scenario Outline: Failures - Unsupported Response Content Types
    Given that my request contains header Accept = <MIMEType>
    And that my request goes to endpoint people/1
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

