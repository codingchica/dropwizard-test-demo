package codingchica.demo.test.dropwizard.core.model.external;

import static codingchica.demo.test.dropwizard.util.AnnotationValidationUtils.assertEmpty;
import static codingchica.demo.test.dropwizard.util.AnnotationValidationUtils.assertOneViolation;
import static codingchica.demo.test.dropwizard.util.EqualsHashCodeTester.*;
import static codingchica.demo.test.dropwizard.util.GetterSetterTester.valueRetrievedCorrectly;
import static codingchica.demo.test.dropwizard.util.WithTester.verifyWithValueCopyBehavior;
import static io.dropwizard.jackson.Jackson.newObjectMapper;
import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.jersey.validation.Validators;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class EmployeeTest {
  final Employee employee =
      codingchica.demo.test.dropwizard.core.model.external.Employee.builder()
          .guid("abc-123")
          .firstName("John")
          .lastName("Doe")
          .nickName("Johnny")
          .build();

  private static final Validator validator = Validators.newValidator();

  @Nested
  class ConstructorTests {
    @Test
    public void noArgConstructor() {
      // Setup

      // Execution
      Employee employee = new Employee();

      // Validation
      assertAll(
          () -> assertNull(employee.getGuid(), "guid"),
          () -> assertNull(employee.getFirstName(), "firstName"),
          () -> assertNull(employee.getLastName(), "lastName"),
          () -> assertNull(employee.getNickName(), "nickName"));
    }

    @Test
    public void allArgConstructor() {
      // Setup

      // Execution
      Employee employee = new Employee("abc-123", "first name", "last name", "nick name");

      // Validation
      assertAll(
          () -> assertEquals("abc-123", employee.getGuid(), "guid"),
          () -> assertEquals("first name", employee.getFirstName(), "firstName"),
          () -> assertEquals("last name", employee.getLastName(), "lastName"),
          () -> assertEquals("nick name", employee.getNickName(), "nickName"));
    }
  }

  @Nested
  class ToStringTest {
    /** Ensure toString output would be helpful for debugging. */
    @Test
    void employeeToString() {
      // Setup
      // Keep this one at default constructor values to minimize brittleness
      final Employee employee =
          codingchica.demo.test.dropwizard.core.model.external.Employee.builder().build();

      // Execute
      String result = employee.toString();

      // Validation
      assertEquals("Employee(guid=null, firstName=null, lastName=null, nickName=null)", result);
    }

    /** Ensure toString output would be helpful for debugging. */
    @Test
    void builderToString() {
      // Setup
      // Keep this one at default constructor values to minimize brittleness
      final Employee.EmployeeBuilder employeeBuilder =
          codingchica.demo.test.dropwizard.core.model.external.Employee.builder();

      // Execute
      String result = employeeBuilder.toString();

      // Validation
      assertEquals(
          "Employee.EmployeeBuilder(guid=null, firstName=null, lastName=null, nickName=null)",
          result);
    }
  }

  @Test
  public void validationHappyPath() {
    // Setup

    // Execution
    Set<ConstraintViolation<Employee>> violations = validator.validate(employee);

    // Validation
    assertEmpty(violations);
  }

  /**
   * Ensure that Lombok annotations are set up as expected. Confirm that annotations for validation
   * are not part of generated setters.
   */
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

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    public void validationBlank(String value) {
      // Setup
      employee.setGuid(value);

      // Execution
      Set<ConstraintViolation<Employee>> violations = validator.validate(employee);

      // Validation
      assertOneViolation("guid must not be blank", violations);
    }

    @ParameterizedTest
    @ValueSource(ints = {51})
    public void validationLength(int length) {
      // Setup
      String value = StringUtils.leftPad("abc", length);
      employee.setGuid(value);

      // Execution
      Set<ConstraintViolation<Employee>> violations = validator.validate(employee);

      // Validation
      assertOneViolation("guid length must be less than or equal to 50", violations);
    }

    @Test
    void testEqualsAndHashCodeMatch() {
      // Force duplication
      Employee matchingEmployee = employee.withGuid("new value");
      // Reset to original value
      matchingEmployee.setGuid(employee.getGuid());

      verifyEqualsCanEqualAndHashCodeForMatch(employee, employee);
      verifyEqualsCanEqualAndHashCodeForMatch(employee, matchingEmployee);
    }

    @Test
    void testEqualsAndHashCodeDoNotMatch() {
      verifyEqualsCanEqualAndHashCodeForNotMatch(employee.withGuid(null), employee);
      verifyEqualsCanEqualAndHashCodeForNotMatch(employee, employee.withGuid(null));
      verifyEqualsCanEqualAndHashCodeForNotMatch(employee, employee.withGuid("Some other value"));
    }

    @Test
    void testWithGuid()
        throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
      final Method withMethod = Employee.class.getDeclaredMethod("withGuid", String.class);
      verifyWithValueCopyBehavior(employee, employee.getGuid(), "some other value", withMethod);
    }
  }

  /**
   * Ensure that Lombok annotations are set up as expected. Confirm that annotations for validation
   * are not part of generated setters.
   */
  @Nested
  class FirstNameTest {
    private final Method getter = Employee.class.getDeclaredMethod("getFirstName");
    private final Method setter = Employee.class.getDeclaredMethod("setFirstName", String.class);

    FirstNameTest() throws NoSuchMethodException {}

    @ParameterizedTest
    @ValueSource(strings = {"some value goes here"})
    @NullAndEmptySource
    void testGetterSetter(String value) throws InvocationTargetException, IllegalAccessException {
      valueRetrievedCorrectly(employee, value, getter, setter);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    public void validationBlank(String value) {
      // Setup
      employee.setFirstName(value);

      // Execution
      Set<ConstraintViolation<Employee>> violations = validator.validate(employee);

      // Validation
      assertOneViolation("firstName must not be blank", violations);
    }

    @ParameterizedTest
    @ValueSource(ints = {51})
    public void validationLength(int length) {
      // Setup
      String value = StringUtils.leftPad("abc", length);
      employee.setFirstName(value);

      // Execution
      Set<ConstraintViolation<Employee>> violations = validator.validate(employee);

      // Validation
      assertOneViolation("firstName length must be less than or equal to 50", violations);
    }

    @Test
    void testEqualsAndHashCodeMatch() {
      // Force duplication
      Employee matchingEmployee = employee.withFirstName("new value");
      // Reset to original value
      matchingEmployee.setFirstName(employee.getFirstName());

      verifyEqualsCanEqualAndHashCodeForMatch(employee, employee);
      verifyEqualsCanEqualAndHashCodeForMatch(employee, matchingEmployee);
    }

    @Test
    void testEqualsAndHashCodeDoNotMatch() {
      verifyEqualsCanEqualAndHashCodeForNotMatch(employee.withFirstName(null), employee);
      verifyEqualsCanEqualAndHashCodeForNotMatch(employee, employee.withFirstName(null));
      verifyEqualsCanEqualAndHashCodeForNotMatch(
          employee, employee.withFirstName("Some other value"));
    }

    @Test
    void testWithFirstName()
        throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
      final Method withMethod = Employee.class.getDeclaredMethod("withFirstName", String.class);
      verifyWithValueCopyBehavior(
          employee, employee.getFirstName(), "some other value", withMethod);
    }
  }

  /**
   * Ensure that Lombok annotations are set up as expected. Confirm that annotations for validation
   * are not part of generated setters.
   */
  @Nested
  class LastNameTest {
    private final Method getter = Employee.class.getDeclaredMethod("getLastName");
    private final Method setter = Employee.class.getDeclaredMethod("setLastName", String.class);

    LastNameTest() throws NoSuchMethodException {}

    @ParameterizedTest
    @ValueSource(strings = {"some value goes here"})
    @NullAndEmptySource
    void testGetterSetter(String value) throws InvocationTargetException, IllegalAccessException {
      valueRetrievedCorrectly(employee, value, getter, setter);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    public void validationBlank(String value) {
      // Setup
      employee.setLastName(value);

      // Execution
      Set<ConstraintViolation<Employee>> violations = validator.validate(employee);

      // Validation
      assertOneViolation("lastName must not be blank", violations);
    }

    @ParameterizedTest
    @ValueSource(ints = {51})
    public void validationLength(int length) {
      // Setup
      String value = StringUtils.leftPad("abc", length);
      employee.setLastName(value);

      // Execution
      Set<ConstraintViolation<Employee>> violations = validator.validate(employee);

      // Validation
      assertOneViolation("lastName length must be less than or equal to 50", violations);
    }

    @Test
    void testEqualsAndHashCodeMatch() {
      // Force duplication
      Employee matchingEmployee = employee.withLastName("new value");
      // Reset to original value
      matchingEmployee.setLastName(employee.getLastName());

      verifyEqualsCanEqualAndHashCodeForMatch(employee, employee);
      verifyEqualsCanEqualAndHashCodeForMatch(employee, matchingEmployee);
    }

    @Test
    void testEqualsAndHashCodeDoNotMatch() {
      verifyEqualsCanEqualAndHashCodeForNotMatch(employee.withLastName(null), employee);
      verifyEqualsCanEqualAndHashCodeForNotMatch(employee, employee.withLastName(null));
      verifyEqualsCanEqualAndHashCodeForNotMatch(
          employee, employee.withLastName("Some other value"));
    }

    @Test
    void testWithLastName()
        throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
      final Method withMethod = Employee.class.getDeclaredMethod("withLastName", String.class);
      verifyWithValueCopyBehavior(employee, employee.getLastName(), "some other value", withMethod);
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

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    public void validationBlank(String value) {
      // Setup
      employee.setNickName(value);

      // Execution
      Set<ConstraintViolation<Employee>> violations = validator.validate(employee);

      // Validation
      assertEmpty(violations);
    }

    @ParameterizedTest
    @ValueSource(ints = {51})
    public void validationLength(int length) {
      // Setup
      String value = StringUtils.leftPad("abc", length);
      employee.setNickName(value);

      // Execution
      Set<ConstraintViolation<Employee>> violations = validator.validate(employee);

      // Validation
      assertOneViolation("nickName length must be less than or equal to 50", violations);
    }

    @Test
    void testEqualsAndHashCodeMatch() {
      // Force duplication
      Employee matchingEmployee = employee.withNickName("new value");
      // Reset to original value
      matchingEmployee.setNickName(employee.getNickName());

      verifyEqualsCanEqualAndHashCodeForMatch(employee, employee);
      verifyEqualsCanEqualAndHashCodeForMatch(employee, matchingEmployee);
    }

    @Test
    void testEqualsAndHashCodeDoNotMatch() {
      verifyEqualsCanEqualAndHashCodeForNotMatch(employee.withNickName(null), employee);
      verifyEqualsCanEqualAndHashCodeForNotMatch(employee, employee.withNickName(null));
      verifyEqualsCanEqualAndHashCodeForNotMatch(
          employee, employee.withNickName("Some other last name"));
    }

    @Test
    void testWithNickName()
        throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
      final Method withMethod = Employee.class.getDeclaredMethod("withNickName", String.class);
      verifyWithValueCopyBehavior(employee, employee.getNickName(), "some other value", withMethod);
    }
  }

  @Test
  void testEqualsAndHashCodeDoNotMatch() {
    verifyEqualsForOtherScenarios(employee);
  }

  @Test
  void testEqualsAndHashCodeMatch() {
    assertTrue((new Employee()).equals(new Employee()));
  }

  @Nested
  class SerializationTest {
    private static final ObjectMapper MAPPER = newObjectMapper();
    private final Employee fixtureEmployee =
        MAPPER.readValue(
            getClass().getResource("/fixtures/external/employee.json"), Employee.class);

    SerializationTest() throws IOException {}

    @Test
    void serializeToJSON() throws Exception {
      // Setup
      final String expected = MAPPER.writeValueAsString(fixtureEmployee);
      String[] expectedSnippets =
          new String[] {
            "\"guid\":\"abc-123\"",
            "\"firstName\":\"John\"",
            "\"lastName\":\"Doe\"",
            "\"nickName\":\"Johnny\""
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
      assertEquals("abc-123", fixtureEmployee.getGuid());
      assertEquals("John", fixtureEmployee.getFirstName());
      assertEquals("Doe", fixtureEmployee.getLastName());
      assertEquals("Johnny", fixtureEmployee.getNickName());
    }
  }
}
