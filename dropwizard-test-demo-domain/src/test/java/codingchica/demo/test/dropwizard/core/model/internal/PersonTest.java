package codingchica.demo.test.dropwizard.core.model.internal;

import static codingchica.demo.test.dropwizard.util.EqualsHashCodeTester.*;
import static codingchica.demo.test.dropwizard.util.GetterSetterTester.valueRetrievedCorrectly;
import static codingchica.demo.test.dropwizard.util.WithTester.verifyWithValueCopyBehavior;
import static io.dropwizard.jackson.Jackson.newObjectMapper;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class PersonTest {
  final Person person = Person.builder().id(1).first("John").last("Doe").nickName("Johnny").build();

  @Nested
  class ToStringTest {
    /** Ensure toString output would be helpful for debugging. */
    @Test
    void personToString() {
      // Setup
      // Keep this one at default constructor values to minimize brittleness
      final Person person = Person.builder().build();

      // Execute
      String result = person.toString();

      // Validation
      assertEquals("Person(id=0, first=null, last=null, nickName=null)", result);
    }

    /** Ensure toString output would be helpful for debugging. */
    @Test
    void builderToString() {
      // Setup
      // Keep this one at default constructor values to minimize brittleness
      final Person.PersonBuilder personBuilder = Person.builder();

      // Execute
      String result = personBuilder.toString();

      // Validation
      assertEquals("Person.PersonBuilder(id=0, first=null, last=null, nickName=null)", result);
    }
  }

  /**
   * Ensure that Lombok annotations are set up as expected. Confirm that annotations for validation
   * are not part of generated setters.
   */
  @Nested
  class IdTest {
    private final Method getter = Person.class.getDeclaredMethod("getId");
    private final Method setter = Person.class.getDeclaredMethod("setId", int.class);

    IdTest() throws NoSuchMethodException {}

    @ParameterizedTest
    @ValueSource(ints = {-1, -5, -100, 0, 1, 5, 100})
    void testGetterSetter(int value) throws InvocationTargetException, IllegalAccessException {
      valueRetrievedCorrectly(person, value, getter, setter);
    }
  }

  /**
   * Ensure that Lombok annotations are set up as expected. Confirm that annotations for validation
   * are not part of generated setters.
   */
  @Nested
  class FirstNameTest {
    private final Method getter = Person.class.getDeclaredMethod("getFirst");
    private final Method setter = Person.class.getDeclaredMethod("setFirst", String.class);

    FirstNameTest() throws NoSuchMethodException {}

    @ParameterizedTest
    @ValueSource(strings = {"some value goes here"})
    @NullAndEmptySource
    void testGetterSetter(String value) throws InvocationTargetException, IllegalAccessException {
      valueRetrievedCorrectly(person, value, getter, setter);
    }
  }

  /**
   * Ensure that Lombok annotations are set up as expected. Confirm that annotations for validation
   * are not part of generated setters.
   */
  @Nested
  class LastNameTest {
    private final Method getter = Person.class.getDeclaredMethod("getLast");
    private final Method setter = Person.class.getDeclaredMethod("setLast", String.class);

    LastNameTest() throws NoSuchMethodException {}

    @ParameterizedTest
    @ValueSource(strings = {"some value goes here"})
    @NullAndEmptySource
    void testGetterSetter(String value) throws InvocationTargetException, IllegalAccessException {
      valueRetrievedCorrectly(person, value, getter, setter);
    }
  }

  /**
   * Ensure that Lombok annotations are set up as expected. Confirm that annotations for validation
   * are not part of generated setters.
   */
  @Nested
  class NickNameTest {
    private final Method getter = Person.class.getDeclaredMethod("getNickName");
    private final Method setter = Person.class.getDeclaredMethod("setNickName", String.class);

    NickNameTest() throws NoSuchMethodException {}

    @ParameterizedTest
    @ValueSource(strings = {"some value goes here"})
    @NullAndEmptySource
    void testGetterSetter(String value) throws InvocationTargetException, IllegalAccessException {
      valueRetrievedCorrectly(person, value, getter, setter);
    }
  }

  @Nested
  class EqualAndHashCodeTester {

    @Test
    void testEqualsAndHashCodeMatch() {
      // Force duplication
      Person matchingPerson = person.withFirst("new name");
      // Reset to original value
      matchingPerson.setFirst(person.getFirst());

      verifyEqualsCanEqualAndHashCodeForMatch(person, person);
      verifyEqualsCanEqualAndHashCodeForMatch(person, matchingPerson);
    }

    @Test
    void testEqualsAndHashCodeDoNotMatch() {
      verifyEqualsForOtherScenarios(person);

      verifyEqualsCanEqualAndHashCodeForNotMatch(person.withId(-2), person);
      verifyEqualsCanEqualAndHashCodeForNotMatch(person, person.withId(200));

      verifyEqualsCanEqualAndHashCodeForNotMatch(person.withFirst(null), person);
      verifyEqualsCanEqualAndHashCodeForNotMatch(person, person.withFirst(null));
      verifyEqualsCanEqualAndHashCodeForNotMatch(person, person.withFirst("Some other first name"));

      verifyEqualsCanEqualAndHashCodeForNotMatch(person.withLast(null), person);
      verifyEqualsCanEqualAndHashCodeForNotMatch(person, person.withLast(null));
      verifyEqualsCanEqualAndHashCodeForNotMatch(person, person.withLast("Some other last name"));

      verifyEqualsCanEqualAndHashCodeForNotMatch(person.withNickName(null), person);
      verifyEqualsCanEqualAndHashCodeForNotMatch(person, person.withNickName(null));
      verifyEqualsCanEqualAndHashCodeForNotMatch(
          person, person.withNickName("Some other last name"));
    }
  }

  @Nested
  class WithTest {
    @Test
    void testWithBehaviorId()
        throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
      final Method withMethod = Person.class.getDeclaredMethod("withId", int.class);
      verifyWithValueCopyBehavior(person, person.getId(), -5, withMethod);
    }

    @Test
    void testWithFirstName()
        throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
      final Method withMethod = Person.class.getDeclaredMethod("withFirst", String.class);
      verifyWithValueCopyBehavior(person, person.getFirst(), "some other value", withMethod);
    }

    @Test
    void testWithLastName()
        throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
      final Method withMethod = Person.class.getDeclaredMethod("withLast", String.class);
      verifyWithValueCopyBehavior(person, person.getLast(), "some other value", withMethod);
    }

    @Test
    void testWithNickName()
        throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
      final Method withMethod = Person.class.getDeclaredMethod("withNickName", String.class);
      verifyWithValueCopyBehavior(person, person.getNickName(), "some other value", withMethod);
    }
  }

  @Nested
  class SerializationTest {
    private static final ObjectMapper MAPPER = newObjectMapper();
    private final Person fixturePerson =
        MAPPER.readValue(getClass().getResource("/fixtures/internal/person.json"), Person.class);

    SerializationTest() throws IOException {}

    @Test
    void serializeToJSON() throws Exception {
      // Setup
      final String expected = MAPPER.writeValueAsString(fixturePerson);
      String[] expectedSnippets =
          new String[] {
            "\"id\":1", "\"first\":\"John\"", "\"last\":\"Doe\"", "\"nickName\":\"Johnny\""
          };

      // Execution
      String result = MAPPER.writeValueAsString(person);

      // Validation
      assertEquals(expected, result);
      Arrays.stream(expectedSnippets)
          .forEach(
              (expectedValue) -> {
                assertTrue(
                    StringUtils.contains(result, expectedValue),
                    String.format("%s should contain %s", result, expectedValue));
              });
    }

    @Test
    public void deserializeFromJSON() {
      // Setup

      // Execution

      // Validation
      assertEquals(fixturePerson, person);
      assertEquals(1, fixturePerson.getId());
      assertEquals("John", fixturePerson.getFirst());
      assertEquals("Doe", fixturePerson.getLast());
      assertEquals("Johnny", fixturePerson.getNickName());
    }
  }
}
