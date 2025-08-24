package ch.ffhs.spring_boosters;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class SpringBoostersApplicationTests {

	@Test
	void contextLoads() {
	}

}
