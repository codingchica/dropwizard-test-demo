package codingchica.demo.test.dropwizard.core.model.external;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import lombok.*;
import org.hibernate.validator.constraints.Length;

/**
 * A person object. The constructors and setters in this class do not enforce validity, so this
 * class should be validated prior to consumption on the API, when persisting, etc.
 */
@Builder
@Data
@With
@NoArgsConstructor
@AllArgsConstructor
public class Person {
  @NotNull @Positive private int id;

  @NotNull @Length(min = 1, max = 50) private String firstName;

  @NotNull @Length(min = 1, max = 50) private String lastName;

  @Length(min = 0, max = 50) private String nickName;
}
