package codingchica.demo.test.dropwizard;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class DropwizardTestDemoApplication extends Application<DropwizardTestDemoConfiguration> {

    public static void main(final String[] args) throws Exception {
        new DropwizardTestDemoApplication().run(args);
    }

    @Override
    public String getName() {
        return "DropwizardTestDemo";
    }

    @Override
    public void initialize(final Bootstrap<DropwizardTestDemoConfiguration> bootstrap) {
        // TODO: application initialization
    }

    @Override
    public void run(final DropwizardTestDemoConfiguration configuration,
                    final Environment environment) {
        // TODO: implement application
    }

}
