package ch.ffhs.spring_boosters;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
class SpringBoostersApplicationTests {

	@Test
	void contextLoads() {
		// Test that the application context loads successfully with Testcontainers
	}

}
