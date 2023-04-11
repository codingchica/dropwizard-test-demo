package codingchica.demo.test.dropwizard.api.resources;

import static org.junit.jupiter.api.Assertions.*;

import codingchica.demo.test.dropwizard.core.model.external.Employee;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class EmployeeResourceTest {
  private final EmployeeResource employeeResource = new EmployeeResource();

  @Nested
  class GetEmployeeTest {

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
