package codingchica.demo.test.dropwizard.core.model.mappers;

import static org.junit.jupiter.api.Assertions.*;

import codingchica.demo.test.dropwizard.core.model.internal.Employee;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class EmployeeMapperImplTest {
  private final EmployeeMapperImpl employeeMapperImpl = new EmployeeMapperImpl();

  @Nested
  public class InternalToExternalEmployeeMappingTest {
    @Test
    void testNull() {
      // Setup

      // Execution
      codingchica.demo.test.dropwizard.core.model.external.Employee externalEmployee =
          employeeMapperImpl.internalToExternalEmployeeMapping(null);

      // Validation
      assertNull(externalEmployee);
    }

    @Test
    void testPopulated() {
      // Setup
      Employee internalEmployee =
          Employee.builder().id(1).first("John").last("Doe").nickName("Johnny").build();

      // Execution
      codingchica.demo.test.dropwizard.core.model.external.Employee externalEmployee =
          employeeMapperImpl.internalToExternalEmployeeMapping(internalEmployee);

      // Validation
      assertAll(
          () -> assertEquals(internalEmployee.getGuid(), externalEmployee.getGuid()),
          () -> assertEquals(internalEmployee.getFirst(), externalEmployee.getFirstName()),
          () -> assertEquals(internalEmployee.getLast(), externalEmployee.getLastName()),
          () -> assertEquals(internalEmployee.getNickName(), externalEmployee.getNickName()));
    }
  }
}
