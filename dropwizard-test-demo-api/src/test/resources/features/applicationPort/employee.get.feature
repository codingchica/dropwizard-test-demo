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

    @Component
    Scenario Outline: GUID - Non-Alpha-numeric characters
      Given that my request contains header Accept = application/json
      And that my request goes to endpoint employees/<id>
      And that my request uses the GET method
      When I submit the request
      Then the response code is 400
      And the error response body contains JSON data
        | errors | ["path param guid Must contain only alphanumeric characters."] |
      Examples:
        | id        |
        | abc%20123 |
        | abc%21123 |
        | abc%22123 |
        | abc%23123 |
        | abc%24123 |
        | abc%25123 |
        | abc%26123 |
        | abc%27123 |
        | abc%28123 |
        | abc%29123 |
        | abc%2A123 |
        | abc%2B123 |
        | abc%2C123 |
        | abc%2D123 |
        | abc%2E123 |
        | abc%2F123 |
        | abc%3A123 |
        | abc%3B123 |
        | abc%3C123 |
        | abc%3D123 |
        | abc%3E123 |
        | abc%3F123 |
        | abc%40123 |
        | abc%5B123 |
        | abc%5C123 |
        | abc%5D123 |
        | abc%5E123 |
        | abc%5F123 |
        | abc%60123 |
        | abc%7B123 |
        | abc%7C123 |
        | abc%7D123 |
        | abc%7E123 |
        | abc%7F123 |

    @Component
    Scenario Outline: GUID - Blank
      Given that my request contains header Accept = application/json
      And that my request goes to endpoint employees/<id>
      And that my request uses the GET method
      When I submit the request
      Then the response code is 400
      And the error response body contains JSON data
        # Multiple errors will be returned - select the expected one
        | errors[?(@ contains 'alphanumeric')] | ["path param guid Must contain only alphanumeric characters."] |
      Examples:
        | id     |
        | %20%20 |

    @Component
    Scenario Outline: GUID - Length
      Given that my request contains header Accept = application/json
      And that my request goes to endpoint employees/<id>
      And that my request uses the GET method
      When I submit the request
      Then the response code is 400
      And the error response body contains JSON data
        | errors | ["path param guid length must be less than or equal to 50"] |
      Examples:
        | id                                                  |
        | 123456789012345678901234567890123456789012345678901 |

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
