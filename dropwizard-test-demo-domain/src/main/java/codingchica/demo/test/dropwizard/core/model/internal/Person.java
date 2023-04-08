package codingchica.demo.test.dropwizard.core.model.internal;

import javax.validation.constraints.NotNull;
import lombok.*;

/** An internal representation of a person. */
@Builder
@Data
@With
@NoArgsConstructor
@AllArgsConstructor
public class Person {
  /** The numeric ID for the person. */
  @NotNull private int id;

  /** The person's first name. */
  @NotNull private String first;

  /** The person's last name. */
  @NotNull private String last;

  /** The person's nickname. */
  private String nickName;
}
