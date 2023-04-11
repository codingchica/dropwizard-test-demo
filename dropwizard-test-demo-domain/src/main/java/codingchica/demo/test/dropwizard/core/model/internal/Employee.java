package codingchica.demo.test.dropwizard.core.model.internal;

import javax.validation.constraints.NotNull;
import lombok.*;

/** An internal representation of an employee. */
@Builder
@Data
@With
@NoArgsConstructor
@AllArgsConstructor
public class Employee {
  /** The numeric ID for the employee. */
  @NotNull private int id;

  /** The global unique identifier for the employee. */
  @NotNull private String guid;

  /** The employee's first name. */
  @NotNull private String first;

  /** The employee's last name. */
  @NotNull private String last;

  /** The employee's nickname. */
  private String nickName;
}
