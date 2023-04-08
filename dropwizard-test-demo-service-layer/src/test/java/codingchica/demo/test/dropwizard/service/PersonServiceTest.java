package codingchica.demo.test.dropwizard.service;

import static org.junit.jupiter.api.Assertions.*;

import codingchica.demo.test.dropwizard.core.model.external.Person;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PersonServiceTest {
  private final PersonService personService = new PersonService();

  @Nested
  class InternalToExternalPersonMappingTest {
    @Test
    void testPopulated() {
      // Setup
      final int id = 1;

      // Execution
      Person person = personService.getPersonById(id);

      // Validation
      assertNotNull(person, "person");
      assertAll(
          () -> assertEquals("John", person.getFirstName()),
          () -> assertEquals("Doe", person.getLastName()),
          () -> assertEquals("Johnny", person.getNickName()),
          () -> assertEquals(id, person.getId()));
    }
  }
}
