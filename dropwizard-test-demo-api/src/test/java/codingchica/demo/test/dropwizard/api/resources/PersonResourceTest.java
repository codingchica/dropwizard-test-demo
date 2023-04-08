package codingchica.demo.test.dropwizard.api.resources;

import static org.junit.jupiter.api.Assertions.*;

import codingchica.demo.test.dropwizard.core.model.external.Person;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class PersonResourceTest {
  private final PersonResource personResource = new PersonResource();

  @Nested
  class GetPersonTest {

    @Test
    void hardcodedResponse() {
      // Setup
      final Person expectedPerson =
          Person.builder().id(1).firstName("John").lastName("Doe").nickName("Johnny").build();

      // Execution
      Person person = personResource.getPerson(1);

      // Validation
      assertEquals(expectedPerson, person, "person");
    }
  }
}
