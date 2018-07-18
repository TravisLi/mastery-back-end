package mastery.service;

import mastery.model.Lesson;
import mastery.model.Student;
import mastery.schooltracs.core.SchoolTracsAgent;
import mastery.schooltracs.model.Customer;
import mastery.whatsapp.WhatsappRestAgent;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class LessonNotificationServiceTest {
    private static final Logger log = LoggerFactory.getLogger(LessonNotificationServiceTest.class);

    private final String enabledString = "lessonnotification";
    private SchoolTracsAgent mockSchoolTracsAgent;
    private WhatsappRestAgent mockWhatsappAgnet;
    private LessonNotificationService service;

    @Before
    public void setup() {
        mockSchoolTracsAgent = Mockito.mock(SchoolTracsAgent.class);
        mockWhatsappAgnet = Mockito.mock(WhatsappRestAgent.class);
        service = new LessonNotificationService(mockSchoolTracsAgent, mockWhatsappAgnet, enabledString);
    }


    @Test
    public void sendNotificationOnToday() throws Exception {
        // given
        when(mockSchoolTracsAgent.schLsonByStd(eq(""), any(), any())).thenReturn(Lists.newArrayList(lessonWithStudents()));
        when(mockSchoolTracsAgent.schCustsById("1")).thenReturn(customerEnabledNoti());
        // when
        service.sendLessonNotificationOnDate(new Date());
        // verify
        verify(mockWhatsappAgnet, times(1)).sendLessonNotificationMsg(any(), any());
    }

    @Test
    public void dontSendNotificationCases() throws Exception {
        // given
        when(mockSchoolTracsAgent.schLsonByStd(eq(""), any(), any()))
                .thenReturn(Lists.newArrayList(
                        lessonWithoutStudents(),
                        lessonWithStudents()
                        ));
        when(mockSchoolTracsAgent.schCustsById("1")).thenReturn(customerNotEabledNoti());

        // when
        service.sendLessonNotificationOnDate(new Date());

        // verify
        verify(mockWhatsappAgnet, never()).sendLessonNotificationMsg(any(), any());
    }

    @Test
    public void notificationSendInOrderOfLessonStartTime() throws Exception {
        // given
        Lesson firstLesson = lessonWithStudents();
        Lesson secondLesson = lessonWithStudents();
        secondLesson.setId("2");
        secondLesson.getStartDateTime().setTime(firstLesson.getStartDateTime().getTime() + 10000);
        when(mockSchoolTracsAgent.schLsonByStd(eq(""), any(), any()))
                .thenReturn(Lists.newArrayList(
                        secondLesson,
                        firstLesson
                ));
        when(mockSchoolTracsAgent.schCustsById("1")).thenReturn(customerEnabledNoti());

        // when
        service.sendLessonNotificationOnDate(new Date());

        // verify
        InOrder orderVerifier = Mockito.inOrder(mockWhatsappAgnet);
        orderVerifier.verify(mockWhatsappAgnet).sendLessonNotificationMsg(any(), eq(firstLesson));
        orderVerifier.verify(mockWhatsappAgnet).sendLessonNotificationMsg(any(), eq(secondLesson));
    }

    private Customer customerNotEabledNoti() {
        Customer c = new Customer();
        return c;
    }

    private Lesson lessonWithoutStudents() {
        Lesson l = new Lesson();
        return l;
    }

    private Lesson lessonWithStudents() {
        Lesson l = new Lesson();
        l.setId("1");
        l.setStartDateTime(new Date());
        l.setStudents(Lists.newArrayList(student1()));
        return l;
    }

    private Student student1() {
        Student s = new Student();
        s.setId("1");
        return s;
    }

    private Customer customerEnabledNoti() {
        Customer c = new Customer();
        c.setRemark("xxxyyy" + enabledString);
        return c;
    }


}
