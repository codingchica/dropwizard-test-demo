package codingchica.demo.test.dropwizard.core.config;

import io.dropwizard.Configuration;
import lombok.*;

/**
 * The POJO representing the application configuration that will be used when running the server.
 * See appConfig/README.md.
 */
@ToString
@NoArgsConstructor
public class DropwizardTestDemoConfiguration extends Configuration {
  @Getter @Setter private String testValue = null;
}
