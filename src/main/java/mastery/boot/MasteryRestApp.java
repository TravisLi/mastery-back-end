package mastery.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication(scanBasePackages={"mastery"})// same as @Configuration @EnableAutoConfiguration @ComponentScan combined
@EnableScheduling
public class MasteryRestApp {

	public static void main(String[] args) {
		SpringApplication.run(MasteryRestApp.class, args);
	}
}
