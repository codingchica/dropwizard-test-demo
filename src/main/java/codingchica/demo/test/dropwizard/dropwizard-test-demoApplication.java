package codingchica.demo.test.dropwizard;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class dropwizard-test-demoApplication extends Application<dropwizard-test-demoConfiguration> {

    public static void main(final String[] args) throws Exception {
        new dropwizard-test-demoApplication().run(args);
    }

    @Override
    public String getName() {
        return "dropwizard-test-demo";
    }

    @Override
    public void initialize(final Bootstrap<dropwizard-test-demoConfiguration> bootstrap) {
        // TODO: application initialization
    }

    @Override
    public void run(final dropwizard-test-demoConfiguration configuration,
                    final Environment environment) {
        // TODO: implement application
    }

}
