package mastery.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication(scanBasePackages={"mastery"})// same as @Configuration @EnableAutoConfiguration @ComponentScan combined
public class MasteryRestApp {

	public static void main(String[] args) {
		SpringApplication.run(MasteryRestApp.class, args);
	}
}
