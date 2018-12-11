package mastery.controller;

import mastery.service.LessonNotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.InvalidParameterException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
@RequestMapping(value = "/api/lesson-notify")
public class LessonNotificationController {

	private static final Logger log = LoggerFactory.getLogger(LessonNotificationController.class);
	private static final String dateInputFormat = "yyyyMMdd";

	private final LessonNotificationService lessonNotificationService;

	@Autowired
	public LessonNotificationController(LessonNotificationService lessonNotificationService) {
		this.lessonNotificationService = lessonNotificationService;
	}

	@RequestMapping(value = "/send/{date}", method = RequestMethod.GET)
	public ResponseEntity<String> send(@PathVariable("date") String date) {
		log.info("send lesson notification on {}", date);

		try {
			Date parsedDate = parseAndValidateDate(date);
			lessonNotificationService.sendLessonNotificationOnDate(parsedDate);
		} catch (InvalidParameterException e) {
			log.info("invalid date, {}: {}", e.getMessage(), date);
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}

		return new ResponseEntity<>("Success", HttpStatus.OK);
	}

	private Date parseAndValidateDate(String date) {
		Date parsedDate;
		try {
			parsedDate = new SimpleDateFormat(dateInputFormat).parse(date);
		} catch (ParseException e) {
			throw new InvalidParameterException("Fail to parse date");
		}

		if (new Date().after(parsedDate))
			throw new InvalidParameterException("Input date is not future date");

		return parsedDate;
	}

}
