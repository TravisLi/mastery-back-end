package mastery.service;

import mastery.schooltracs.core.SchoolTracsAgent;
import mastery.whatsapp.WhatsappRestAgent;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

public class LessonNotificationServiceTest {
    private static final Logger log = LoggerFactory.getLogger(LessonNotificationServiceTest.class);

    private final String enabledString = "lessonnotification";
    private SchoolTracsAgent mockSchollTracsAgent;
    private WhatsappRestAgent mockWhatsappAgnet;
    private LessonNotificationService service;

    @Before
    public void setup() {
        mockSchollTracsAgent = Mockito.mock(SchoolTracsAgent.class);
        mockWhatsappAgnet = Mockito.mock(WhatsappRestAgent.class);
        service = new LessonNotificationService(mockSchollTracsAgent, mockWhatsappAgnet, enabledString);
    }


    @Test
    public void searchAllLessonWithStudentsOnSameDate() throws Exception {
        assertThat(service, notNullValue());
    }
}
