package codingchica.demo.test.dropwizard.core.model.mappers;

import codingchica.demo.test.dropwizard.core.model.internal.Employee;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/** A mapper interface to translate a Employee object. */
@Mapper
public interface EmployeeMapper {

  /**
   * Convert an internal employee to an external employee representation.
   *
   * @return A Employee representation appropriate for external responses.
   */
  @Mapping(target = "firstName", source = "first")
  @Mapping(target = "lastName", source = "last")
  // Fields with matching names need not be explicitly defined in the mappings.
  codingchica.demo.test.dropwizard.core.model.external.Employee internalToExternalEmployeeMapping(
      Employee employee);
}
