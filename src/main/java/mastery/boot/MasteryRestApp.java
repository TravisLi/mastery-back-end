package mastery.boot;

import mastery.service.LessonNotificationService;

import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;


@SpringBootApplication(scanBasePackages={"mastery"})// same as @Configuration @EnableAutoConfiguration @ComponentScan combined
@EnableScheduling
public class MasteryRestApp {
	private static final Logger log = LoggerFactory.getLogger(LessonNotificationService.class);

	@Autowired
	LessonNotificationService lessonNotificationService;

	@Scheduled(cron = "${lessonNotificationService.job.cron[default:'0 0 0 1 1 ? 1970']}")
	public void sendLessNotificationOnTomrrorowJob() {
		log.info("Start sendLessNotificationOnTomrrorowJob");
		Calendar date = Calendar.getInstance();
		date.add(Calendar.DATE, 1);
		lessonNotificationService.sendLessonNotificationOnDate(date.getTime());
		log.info("End sendLessNotificationOnTomrrorowJob");
	}


	public static void main(String[] args) {
		SpringApplication.run(MasteryRestApp.class, args);
	}
}
