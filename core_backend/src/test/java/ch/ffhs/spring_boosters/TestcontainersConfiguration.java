package ch.ffhs.spring_boosters;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfiguration {

	@Bean
	@ServiceConnection
	PostgreSQLContainer<?> postgresContainer() {
		return new PostgreSQLContainer<>(DockerImageName.parse("postgres:15-alpine"))
				.withDatabaseName("spring_boosters_test")
				.withUsername("test")
				.withPassword("test")
				.withReuse(true);
	}

	public static void main(String[] args) {
		SpringApplication.from(SpringBoostersApplication::main)
				.with(TestcontainersConfiguration.class)
				.run(args);
	}

}
