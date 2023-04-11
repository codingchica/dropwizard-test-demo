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

/** Unit tests for the internal employee class * */
class EmployeeTest {
  final Employee employee =
      Employee.builder().id(1).guid("abc-123").first("John").last("Doe").nickName("Johnny").build();

  @Nested
  class ToStringTest {
    /** Ensure toString output would be helpful for debugging. */
    @Test
    void employeeToString() {
      // Setup
      // Keep this one at default constructor values to minimize brittleness
      final Employee employee = Employee.builder().build();

      // Execute
      String result = employee.toString();

      // Validation
      assertEquals("Employee(id=0, guid=null, first=null, last=null, nickName=null)", result);
    }

    /** Ensure toString output would be helpful for debugging. */
    @Test
    void builderToString() {
      // Setup
      // Keep this one at default constructor values to minimize brittleness
      final Employee.EmployeeBuilder employeeBuilder = Employee.builder();

      // Execute
      String result = employeeBuilder.toString();

      // Validation
      assertEquals(
          "Employee.EmployeeBuilder(id=0, guid=null, first=null, last=null, nickName=null)",
          result);
    }
  }

  /**
   * Ensure that Lombok annotations are set up as expected. Confirm that annotations for validation
   * are not part of generated setters.
   */
  @Nested
  class IdTest {
    private final Method getter = Employee.class.getDeclaredMethod("getId");
    private final Method setter = Employee.class.getDeclaredMethod("setId", int.class);

    IdTest() throws NoSuchMethodException {}

    @ParameterizedTest
    @ValueSource(ints = {-1, -5, -100, 0, 1, 5, 100})
    void testGetterSetter(int value) throws InvocationTargetException, IllegalAccessException {
      valueRetrievedCorrectly(employee, value, getter, setter);
    }

    @Test
    void testWithId()
        throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
      final Method withMethod = Employee.class.getDeclaredMethod("withId", int.class);
      verifyWithValueCopyBehavior(employee, employee.getId(), -5, withMethod);
    }

    @Test
    void testEqualsAndHashCodeDoNotMatch() {
      verifyEqualsCanEqualAndHashCodeForNotMatch(employee.withId(-2), employee);
      verifyEqualsCanEqualAndHashCodeForNotMatch(employee, employee.withId(200));
    }
  }

  @Nested
  class GuidTest {
    private final Method getter = Employee.class.getDeclaredMethod("getGuid");
    private final Method setter = Employee.class.getDeclaredMethod("setGuid", String.class);

    GuidTest() throws NoSuchMethodException {}

    @ParameterizedTest
    @ValueSource(strings = {"some value goes here"})
    @NullAndEmptySource
    void testGetterSetter(String value) throws InvocationTargetException, IllegalAccessException {
      valueRetrievedCorrectly(employee, value, getter, setter);
    }

    @Test
    void testWithGuid()
        throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
      final Method withMethod = Employee.class.getDeclaredMethod("withGuid", String.class);
      verifyWithValueCopyBehavior(employee, employee.getGuid(), "some other value", withMethod);
    }

    @Test
    void testEqualsAndHashCodeDoNotMatch() {
      verifyEqualsCanEqualAndHashCodeForNotMatch(employee.withGuid(null), employee);
      verifyEqualsCanEqualAndHashCodeForNotMatch(employee, employee.withGuid(null));
      verifyEqualsCanEqualAndHashCodeForNotMatch(
          employee, employee.withGuid("Some other last name"));
    }
  }

  /**
   * Ensure that Lombok annotations are set up as expected. Confirm that annotations for validation
   * are not part of generated setters.
   */
  @Nested
  class FirstNameTest {
    private final Method getter = Employee.class.getDeclaredMethod("getFirst");
    private final Method setter = Employee.class.getDeclaredMethod("setFirst", String.class);

    FirstNameTest() throws NoSuchMethodException {}

    @ParameterizedTest
    @ValueSource(strings = {"some value goes here"})
    @NullAndEmptySource
    void testGetterSetter(String value) throws InvocationTargetException, IllegalAccessException {
      valueRetrievedCorrectly(employee, value, getter, setter);
    }

    @Test
    void testWithFirstName()
        throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
      final Method withMethod = Employee.class.getDeclaredMethod("withFirst", String.class);
      verifyWithValueCopyBehavior(employee, employee.getFirst(), "some other value", withMethod);
    }

    @Test
    void testEqualsAndHashCodeDoNotMatch() {
      verifyEqualsCanEqualAndHashCodeForNotMatch(employee.withFirst(null), employee);
      verifyEqualsCanEqualAndHashCodeForNotMatch(employee, employee.withFirst(null));
      verifyEqualsCanEqualAndHashCodeForNotMatch(
          employee, employee.withFirst("Some other first name"));
    }
  }

  /**
   * Ensure that Lombok annotations are set up as expected. Confirm that annotations for validation
   * are not part of generated setters.
   */
  @Nested
  class LastNameTest {
    private final Method getter = Employee.class.getDeclaredMethod("getLast");
    private final Method setter = Employee.class.getDeclaredMethod("setLast", String.class);

    LastNameTest() throws NoSuchMethodException {}

    @ParameterizedTest
    @ValueSource(strings = {"some value goes here"})
    @NullAndEmptySource
    void testGetterSetter(String value) throws InvocationTargetException, IllegalAccessException {
      valueRetrievedCorrectly(employee, value, getter, setter);
    }

    @Test
    void testWithLastName()
        throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
      final Method withMethod = Employee.class.getDeclaredMethod("withLast", String.class);
      verifyWithValueCopyBehavior(employee, employee.getLast(), "some other value", withMethod);
    }

    @Test
    void testEqualsAndHashCodeDoNotMatch() {
      verifyEqualsCanEqualAndHashCodeForNotMatch(employee.withLast(null), employee);
      verifyEqualsCanEqualAndHashCodeForNotMatch(employee, employee.withLast(null));
      verifyEqualsCanEqualAndHashCodeForNotMatch(
          employee, employee.withLast("Some other last name"));
    }
  }

  /**
   * Ensure that Lombok annotations are set up as expected. Confirm that annotations for validation
   * are not part of generated setters.
   */
  @Nested
  class NickNameTest {
    private final Method getter = Employee.class.getDeclaredMethod("getNickName");
    private final Method setter = Employee.class.getDeclaredMethod("setNickName", String.class);

    NickNameTest() throws NoSuchMethodException {}

    @ParameterizedTest
    @ValueSource(strings = {"some value goes here"})
    @NullAndEmptySource
    void testGetterSetter(String value) throws InvocationTargetException, IllegalAccessException {
      valueRetrievedCorrectly(employee, value, getter, setter);
    }

    @Test
    void testWithNickName()
        throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
      final Method withMethod = Employee.class.getDeclaredMethod("withNickName", String.class);
      verifyWithValueCopyBehavior(employee, employee.getNickName(), "some other value", withMethod);
    }

    @Test
    void testEqualsAndHashCodeDoNotMatch() {
      verifyEqualsCanEqualAndHashCodeForNotMatch(employee.withNickName(null), employee);
      verifyEqualsCanEqualAndHashCodeForNotMatch(employee, employee.withNickName(null));
      verifyEqualsCanEqualAndHashCodeForNotMatch(
          employee, employee.withNickName("Some other last name"));
    }
  }

  @Nested
  class EqualAndHashCodeTester {

    @Test
    void testEqualsAndHashCodeMatch() {
      // Force duplication
      Employee matchingEmployee = employee.withFirst("new name");
      // Reset to original value
      matchingEmployee.setFirst(employee.getFirst());

      assertTrue((new Employee()).equals(new Employee()));
      verifyEqualsCanEqualAndHashCodeForMatch(employee, employee);
      verifyEqualsCanEqualAndHashCodeForMatch(employee, matchingEmployee);
    }

    @Test
    void testEqualsAndHashCodeDoNotMatch() {
      verifyEqualsForOtherScenarios(employee);

      verifyEqualsCanEqualAndHashCodeForNotMatch(employee.withId(-2), employee);
      verifyEqualsCanEqualAndHashCodeForNotMatch(employee, employee.withId(200));

      verifyEqualsCanEqualAndHashCodeForNotMatch(employee.withFirst(null), employee);
      verifyEqualsCanEqualAndHashCodeForNotMatch(employee, employee.withFirst(null));
      verifyEqualsCanEqualAndHashCodeForNotMatch(
          employee, employee.withFirst("Some other first name"));

      verifyEqualsCanEqualAndHashCodeForNotMatch(employee.withLast(null), employee);
      verifyEqualsCanEqualAndHashCodeForNotMatch(employee, employee.withLast(null));
      verifyEqualsCanEqualAndHashCodeForNotMatch(
          employee, employee.withLast("Some other last name"));

      verifyEqualsCanEqualAndHashCodeForNotMatch(employee.withNickName(null), employee);
      verifyEqualsCanEqualAndHashCodeForNotMatch(employee, employee.withNickName(null));
      verifyEqualsCanEqualAndHashCodeForNotMatch(
          employee, employee.withNickName("Some other last name"));
    }
  }

  @Nested
  class SerializationTest {
    private static final ObjectMapper MAPPER = newObjectMapper();
    private final Employee fixtureEmployee =
        MAPPER.readValue(
            getClass().getResource("/fixtures/internal/employee.json"), Employee.class);

    SerializationTest() throws IOException {}

    @Test
    void serializeToJSON() throws Exception {
      // Setup
      final String expected = MAPPER.writeValueAsString(fixtureEmployee);
      String[] expectedSnippets =
          new String[] {
            "\"id\":1", "\"first\":\"John\"", "\"last\":\"Doe\"", "\"nickName\":\"Johnny\""
          };

      // Execution
      String result = MAPPER.writeValueAsString(employee);

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
      assertEquals(fixtureEmployee, employee);
      assertEquals(1, fixtureEmployee.getId());
      assertEquals("John", fixtureEmployee.getFirst());
      assertEquals("Doe", fixtureEmployee.getLast());
      assertEquals("Johnny", fixtureEmployee.getNickName());
    }
  }
}
