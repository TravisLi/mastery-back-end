package mastery;

import mastery.service.LessonNotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@SpringBootApplication
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

	@Bean
	public ExecutorService executionPool () {
		return Executors.newFixedThreadPool(20);
	}

	@Bean
	public TaskExecutor taskExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(5);
		executor.setMaxPoolSize(10);
		executor.setQueueCapacity(25);
		return executor;
	}

	public static void main(String[] args) {
		SpringApplication.run(MasteryRestApp.class, args);
	}
}
