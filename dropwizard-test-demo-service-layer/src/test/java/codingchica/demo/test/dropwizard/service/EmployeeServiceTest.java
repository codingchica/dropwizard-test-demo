package codingchica.demo.test.dropwizard.service;

import static org.junit.jupiter.api.Assertions.*;

import codingchica.demo.test.dropwizard.core.model.external.Employee;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {
  private final EmployeeService employeeService = new EmployeeService();

  @Nested
  class InternalToExternalEmployeeMappingTest {
    @Test
    void testPopulated() {
      // Setup
      final String guid = "abc-123";

      // Execution
      Employee employee = employeeService.getEmployeeByGuid(guid);

      // Validation
      assertNotNull(employee, "employee");
      assertAll(
          () -> assertEquals("John", employee.getFirstName()),
          () -> assertEquals("Doe", employee.getLastName()),
          () -> assertEquals("Johnny", employee.getNickName()),
          () -> assertEquals(guid, employee.getGuid()));
    }
  }
}
