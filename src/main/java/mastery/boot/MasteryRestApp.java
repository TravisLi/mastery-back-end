package mastery.boot;

import mastery.service.LessonNotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Calendar;


@SpringBootApplication(scanBasePackages={"mastery"})// same as @Configuration @EnableAutoConfiguration @ComponentScan combined
@EnableScheduling
public class MasteryRestApp {
	private static final Logger log = LoggerFactory.getLogger(LessonNotificationService.class);

	@Autowired
	LessonNotificationService lessonNotificationService;

	@Scheduled(cron = "${lessonNotificationService.job.cron[default:0 0 20 * * *]}")
	public void sendLessonNotificationOnTomrrorowJob() {
		log.info("Start sendLessonNotificationOnTomrrorowJob");
		Calendar date = Calendar.getInstance();
		date.add(Calendar.DATE, 1);
		lessonNotificationService.sendLessonNotificationOnDate(date.getTime());
		log.info("End sendLessonNotificationOnTomrrorowJob");
	}


	public static void main(String[] args) {
		SpringApplication.run(MasteryRestApp.class, args);
	}
}
