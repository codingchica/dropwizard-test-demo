package codingchica.demo.test.dropwizard.core.model.external;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.validator.constraints.Length;

/**
 * A employee object. The constructors and setters in this class do not enforce validity, so this
 * class should be validated prior to consumption on the API, when persisting, etc.
 */
@Builder
@Data
@With
@NoArgsConstructor
@AllArgsConstructor
public class Employee {
  /** Global unique employee identifier for the employee. */
  @NotBlank
  @Length(max = 50, message = "length must be less than or equal to 50") private String guid;

  @NotBlank
  @Length(max = 50, message = "length must be less than or equal to 50") private String firstName;

  @NotBlank
  @Length(max = 50, message = "length must be less than or equal to 50") private String lastName;

  @Length(max = 50, message = "length must be less than or equal to 50") private String nickName;
}
