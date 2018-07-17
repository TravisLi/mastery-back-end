package mastery.service;

import mastery.model.Lesson;
import mastery.schooltracs.core.SchoolTracsAgent;
import mastery.schooltracs.model.Customer;
import mastery.util.MasteryUtil;
import mastery.whatsapp.WhatsappRestAgent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LessonNotificationService {
    private static final Logger log = LoggerFactory.getLogger(LessonNotificationService.class);

    private final SchoolTracsAgent schoolTracsAgent;
    private final WhatsappRestAgent whatsappAgent;
    private final String enabledString;

    @Autowired
    public LessonNotificationService(SchoolTracsAgent schoolTracsAgent,
                                     WhatsappRestAgent whatsappAgent,
                                     @Value("${lessonNotificationService.enabledString}") final String enabledString) {
        this.schoolTracsAgent = schoolTracsAgent;
        this.whatsappAgent = whatsappAgent;
        this.enabledString = enabledString;
    }

    public void sendLessonNotificationOnDate(Date date) throws IOException {
        log.info("Start sending lesson notification on {}", date);

        List<Lesson> lessonWithStudents = allLessonOnDateHasStudents(date);
        Collections.sort(lessonWithStudents, Lesson::compareTo);
        log.info("total lessons with student(s): {}", lessonWithStudents.size());

        lessonWithStudents.forEach(this::sendNotificationMessageIfCustomerEnabled);

    }

    private void sendNotificationMessageIfCustomerEnabled(Lesson lesson) {
        try {
            Customer student = schoolTracsAgent.schCustsById(lesson.getStudents().get(0).getId());

        } catch (Exception e) {
            log.warn("Cannot send notification for lesson: {}", lesson);
        }
    }

    private List<Lesson> allLessonOnDateHasStudents(Date date) {
        try {
            Date plainDate = MasteryUtil.getPlainCal(date).getTime();
            List<Lesson> searchResults = schoolTracsAgent.schLsonByStd("", plainDate, plainDate);
            log.info("{} lessons result when searching on {}", searchResults.size(), date);
            return searchResults.stream().filter(Lesson::hasStudents).collect(Collectors.toList());
        } catch (IOException e) {
            log.warn("Cannot search lesson", e);
        }
        return Collections.EMPTY_LIST;
    }
}
