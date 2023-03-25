# See Gherkin syntax reference: https://cucumber.io/docs/gherkin/reference/
@Component
Feature: Health Check API

  Scenario: Health check API call returns successful response.
    When I invoke the health check API
    Then the response contains true