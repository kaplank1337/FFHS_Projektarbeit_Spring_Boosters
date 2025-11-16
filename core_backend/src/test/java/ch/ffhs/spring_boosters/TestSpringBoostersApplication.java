package ch.ffhs.spring_boosters;

import org.springframework.boot.SpringApplication;

public class TestSpringBoostersApplication {

	public static void main(String[] args) {
		SpringApplication.from(SpringBoostersApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
