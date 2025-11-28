package ch.ffhs.spring_boosters.test;

import org.flywaydb.core.Flyway;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

public class TestFlywayInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        Environment env = applicationContext.getEnvironment();
        String url = env.getProperty("spring.datasource.url");
        String user = env.getProperty("spring.datasource.username");
        String pass = env.getProperty("spring.datasource.password");
        String locations = env.getProperty("spring.flyway.locations", "classpath:db/test-migration");

        Flyway flyway = Flyway.configure()
                .dataSource(url, user, pass)
                .locations(locations)
                .cleanDisabled(false)
                .load();

        // Clean and migrate to ensure test database matches expectations
        flyway.clean();
        flyway.migrate();
    }
}
