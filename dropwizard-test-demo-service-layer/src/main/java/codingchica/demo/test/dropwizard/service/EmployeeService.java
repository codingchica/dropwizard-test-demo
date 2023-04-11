package codingchica.demo.test.dropwizard.service;

import codingchica.demo.test.dropwizard.core.model.internal.Employee;
import codingchica.demo.test.dropwizard.core.model.mappers.EmployeeMapper;
import codingchica.demo.test.dropwizard.core.model.mappers.EmployeeMapperImpl;

/** The internal business layer logic for handling of an employee. */
public class EmployeeService {
  /** A mapper to translate between different layer's representations of an employee. */
  private EmployeeMapper employeeMapper = new EmployeeMapperImpl();

  /**
   * Retrieve an existing employee by guid.
   *
   * @param guid The global unique identifier for the employee.
   * @return The existing employee that can be returned to the client, if present.
   */
  public codingchica.demo.test.dropwizard.core.model.external.Employee getEmployeeByGuid(
      String guid) {
    // TODO retrieve this employee from the DB
    Employee employee =
        Employee.builder().guid(guid).first("John").last("Doe").nickName("Johnny").build();

    return employeeMapper.internalToExternalEmployeeMapping(employee);
  }
}
