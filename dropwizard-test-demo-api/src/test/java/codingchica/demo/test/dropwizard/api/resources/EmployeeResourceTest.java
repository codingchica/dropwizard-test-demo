package codingchica.demo.test.dropwizard.api.resources;

import static org.junit.jupiter.api.Assertions.*;

import codingchica.demo.test.dropwizard.core.model.external.Employee;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class EmployeeResourceTest {
  private final EmployeeResource employeeResource = new EmployeeResource();

  @Nested
  class GetEmployeeTest {

    /**
     * Most input validation is done by the underlying Dropwizard framework ahead of invoking the
     * API, so these results will differ from what actually happens when the API is invoked.
     */
    @Nested
    class InputValidationTest {
      @ParameterizedTest
      @NullAndEmptySource
      @ValueSource(
          strings = {
            "abc/123",
            "abc-123",
            "abc_123",
            ";",
            "",
            "123456789012345678901234567890123456789012345678901"
          })
      void validationDoneByDropwizardFramework(String value) {
        // Setup

        // Execution
        Employee employee = employeeResource.getEmployee(value);

        // Validation
        // No exceptions thrown in the method's own code.
        assertNotNull(employee);
      }
    }

    @Test
    void hardcodedResponse() {
      // Setup
      String guid = "abc-123";
      final Employee expectedEmployee =
          Employee.builder()
              .guid("abc-123")
              .firstName("John")
              .lastName("Doe")
              .nickName("Johnny")
              .build();

      // Execution
      Employee employee = employeeResource.getEmployee(guid);

      // Validation
      assertEquals(expectedEmployee, employee, "employee");
    }
  }
}
