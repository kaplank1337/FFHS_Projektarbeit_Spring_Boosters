package ch.ffhs.spring_boosters;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SpringBoostersApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBoostersApplication.class, args);
	}

}
