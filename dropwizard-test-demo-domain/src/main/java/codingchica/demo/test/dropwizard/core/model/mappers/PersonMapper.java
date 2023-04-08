package codingchica.demo.test.dropwizard.core.model.mappers;

import codingchica.demo.test.dropwizard.core.model.internal.Person;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/** A mapper interface to translate a Person object. */
@Mapper
public interface PersonMapper {

  /**
   * Convert an internal person to an external person representation.
   *
   * @return A Person representation appropriate for external responses.
   */
  @Mapping(target = "firstName", source = "first")
  @Mapping(target = "lastName", source = "last")
  // Fields with matching names need not be explicitly defined in the mappings.
  codingchica.demo.test.dropwizard.core.model.external.Person internalToExternalPersonMapping(
      Person person);
}
