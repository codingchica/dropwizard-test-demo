package codingchica.demo.test.dropwizard.api.resources;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;

import codingchica.demo.test.dropwizard.core.model.external.Employee;
import codingchica.demo.test.dropwizard.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EmployeeResourceTest {
  @Mock private EmployeeService employeeService;

  private EmployeeResource employeeResource;

  private final Employee employee =
      Employee.builder()
          .guid("abc123")
          .firstName("John")
          .lastName("Doe")
          .nickName("Johnny")
          .build();

  @BeforeEach
  void setup() {
    employeeResource = new EmployeeResource(employeeService);

    lenient().when(employeeService.getEmployeeByGuid(anyString())).thenReturn(employee);
    lenient().when(employeeService.getEmployeeByGuid(null)).thenReturn(employee);
  }

  @Nested
  class ConstructorTest {
    @Test
    void nullInput() {
      // Setup

      // Execution
      Executable executable = () -> new EmployeeResource(null);

      // Validation
      NullPointerException exception = assertThrows(NullPointerException.class, executable);
      assertEquals("employeeService must not be null", exception.getMessage());
    }
  }

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
      String guid = "abc123";
      final Employee expectedEmployee =
          Employee.builder()
              .guid("abc123")
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
