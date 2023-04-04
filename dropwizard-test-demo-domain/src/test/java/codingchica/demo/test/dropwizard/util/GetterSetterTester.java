package codingchica.demo.test.dropwizard.util;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class GetterSetterTester {

  public static <C, T> void nullValueRetrievedCorrectly(
      C objectUnderTest, T fieldValue, Method getter, Method setter)
      throws InvocationTargetException, IllegalAccessException {
    // Setup
    setter.invoke(objectUnderTest, (T) null);

    // Execution
    Object result = getter.invoke(objectUnderTest);

    // Validation
    assertNull(
        result,
        getter.getName() + " returned non-null result when null used in " + setter.getName());
  }

  public static <C, T> void populatedValueRetrievedCorrectly(
      C objectUnderTest, T fieldValue, Method getter, Method setter)
      throws InvocationTargetException, IllegalAccessException {
    // Setup
    setter.invoke(objectUnderTest, fieldValue);

    // Execution
    Object result = getter.invoke(objectUnderTest);

    // Validation
    assertNotNull(
        result,
        getter.getName() + " returned null result when non-null value used in " + setter.getName());
    assertSame(
        fieldValue,
        result,
        getter.getName() + " didn't return the same object as used in " + setter.getName());
  }
}
