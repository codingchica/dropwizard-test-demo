package codingchica.demo.test.dropwizard.config;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import javax.validation.ConstraintViolation;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * A utility class to aid in the validation of javax validation constraint violation related unit
 * tests, such as those performed by Dropwizard on the appConfig file consumed during startup.
 *
 * @param <T> - The type of object being validated.
 */
public class AnnotationValidationUtils<T> {

  /**
   * Enforce that no violations were found during the validation.
   *
   * @param violations Violations from the test that should be validated.
   */
  public void assertEmpty(Set<ConstraintViolation<T>> violations) {
    assertTrue(
        CollectionUtils.isEmpty(violations),
        "Expected " + getInterpretedMessages(violations) + " to be empty.");
  }

  /**
   * Enforce that one and only one violation is in the set provided.
   *
   * @param expectedViolation The expected violation verbiage. This is an interpreted version, and
   *     may differ from what Dropwizard actually returns at runtime.
   * @param violations The violations returned during the test.
   */
  public void assertOneViolation(String expectedViolation, Set<ConstraintViolation<T>> violations) {
    assertFalse(
        CollectionUtils.isEmpty(violations),
        "Expected " + getInterpretedMessages(violations) + " to not be empty.");
    List<String> expectedViolations = new ArrayList<>();
    expectedViolations.add(expectedViolation);
    assertEquivalentViolations(expectedViolations, violations);
  }

  /**
   * Enforce that the violations provided match those returned during the test. The comparisons are
   * done in a sorted order, so that race conditions, etc. do not influence the results of the
   * validation.
   *
   * @param expectedViolations The violation(s) expected.
   * @param violations The violation(s) received during the test.
   */
  public void assertEquivalentViolations(
      List<String> expectedViolations, Set<ConstraintViolation<T>> violations) {
    Comparator<String> stringComparator =
        new Comparator<String>() {
          @Override
          public int compare(String s1, String s2) {
            return StringUtils.compare(s1, s2);
          }
        };
    List<String> actualMessages = getInterpretedMessages(violations);
    actualMessages.sort(stringComparator);
    expectedViolations.sort(stringComparator);
    assertFalse(
        CollectionUtils.isEmpty(violations),
        "Expected " + getInterpretedMessages(violations) + " to not be empty.");
    assertEquals(
        expectedViolations.size(),
        violations.size(),
        "Expected " + getInterpretedMessages(violations) + " to only contain one violation.");
    assertEquals(expectedViolations, actualMessages, "Violation message mismatch");
  }

  /**
   * Interpret the message of the violation contstraint received during testing in order to provide
   * a meaningful message to the user showing both which field failed validation, as well as what
   * failure occurred.
   *
   * @param violations The violations received during testing.
   * @return An interpreted value containing information about both which field failed validation
   *     and the failure that occurred.
   */
  private List<String> getInterpretedMessages(Set<ConstraintViolation<T>> violations) {
    List<String> interpretedValues = new ArrayList<>();
    if (violations != null) {
      violations.stream()
          .forEach(
              (item) -> interpretedValues.add(item.getPropertyPath() + " " + item.getMessage()));
    }
    return interpretedValues;
  }
}
