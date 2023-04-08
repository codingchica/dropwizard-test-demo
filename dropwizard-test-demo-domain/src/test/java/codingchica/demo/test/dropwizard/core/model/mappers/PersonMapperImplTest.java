package codingchica.demo.test.dropwizard.core.model.mappers;

import static org.junit.jupiter.api.Assertions.*;

import codingchica.demo.test.dropwizard.core.model.internal.Person;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class PersonMapperImplTest {
  private final PersonMapperImpl personMapperImpl = new PersonMapperImpl();

  @Nested
  public class InternalToExternalPersonMappingTest {
    @Test
    void testNull() {
      // Setup

      // Execution
      codingchica.demo.test.dropwizard.core.model.external.Person externalPerson =
          personMapperImpl.internalToExternalPersonMapping(null);

      // Validation
      assertNull(externalPerson);
    }

    @Test
    void testPopulated() {
      // Setup
      Person internalPerson =
          Person.builder().id(1).first("John").last("Doe").nickName("Johnny").build();

      // Execution
      codingchica.demo.test.dropwizard.core.model.external.Person externalPerson =
          personMapperImpl.internalToExternalPersonMapping(internalPerson);

      // Validation
      assertAll(
          () -> assertEquals(internalPerson.getId(), externalPerson.getId()),
          () -> assertEquals(internalPerson.getFirst(), externalPerson.getFirstName()),
          () -> assertEquals(internalPerson.getLast(), externalPerson.getLastName()),
          () -> assertEquals(internalPerson.getNickName(), externalPerson.getNickName()));
    }
  }
}
