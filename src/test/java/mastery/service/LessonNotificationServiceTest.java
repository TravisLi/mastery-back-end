package mastery.service;

import mastery.model.Lesson;
import mastery.schooltracs.core.SchoolTracsAgent;
import mastery.schooltracs.model.Customer;
import mastery.util.MasteryUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;

@SpringBootApplication(scanBasePackages={"mastery"})
@RunWith(SpringRunner.class)
@SpringBootTest
public class LessonNotificationServiceTest {
    private static final Logger log = LoggerFactory.getLogger(LessonNotificationServiceTest.class);

    @Autowired
    private SchoolTracsAgent schoolTracsAgent;

    @Test
    public void searchAllLessonWithStudentsOnSameDate() throws Exception {
        Date today = MasteryUtil.getPlainCal(new Date()).getTime();
        List<Lesson> searchResults = schoolTracsAgent.schLsonByStd("", today, today);
        log.info("search result return lessons: {}", searchResults.size());
        assertThat(searchResults.size(), greaterThan(0));

        List<Lesson> lessonWithStudents = searchResults.stream().filter(Lesson::hasStudents).collect(Collectors.toList());
        log.info("total lessons with student(s): {}", lessonWithStudents.size());
        assertThat(lessonWithStudents.size(), greaterThan(0));

        Customer student = schoolTracsAgent.schCustsById(lessonWithStudents.get(0).getStudents().get(0).getId());
        assertThat(student.getId(), is(lessonWithStudents.get(0).getStudents().get(0).getId()));
    }
}
