package codingchica.demo.test.dropwizard;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

/**
 * The main DropWizard application / controller.
 */
public class DropwizardTestDemoApplication extends
        Application<DropwizardTestDemoConfiguration> {

    /**
     * Entry point from the command line when starting up the DropWizard
     * application.
     * @param args Command line arguments.
     * @throws Exception If the application is unable to start up.
     */
    public static void main(final String[] args) throws Exception {
        new DropwizardTestDemoApplication().run(args);
    }

    /**
     * Retrieve the name of the application.
     * @return The application name.
     */
    @Override
    public String getName() {
        return "DropwizardTestDemo";
    }

    /**
     * Initialize the application with the provided bootstrap configuration.
     * @param bootstrap The configuration to use to bootstrap the application
     *                  during startup.
     */
    @Override
    public void initialize(
            final Bootstrap<DropwizardTestDemoConfiguration> bootstrap) {
        // Nothing to do yet.
    }

    /**
     * Execute the DropWizard application with the specified configuration and
     * environment settings.
     * @param configuration POJO representing configuration file provided
     *                      during application launch.
     * @param environment Environment setup to work within.
     */
    @Override
    public void run(final DropwizardTestDemoConfiguration configuration,
                    final Environment environment) {
        // Nothing to do yet.
    }

}
