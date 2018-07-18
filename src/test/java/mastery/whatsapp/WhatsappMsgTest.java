package mastery.whatsapp;

import mastery.model.Lesson;
import mastery.schooltracs.model.Customer;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class WhatsappMsgTest {

    @Test
    public void testBuildLessonNotificationMsg() throws ParseException {
        // given
        Customer c = new Customer();
        c.setName("中文名");
        c.setMobile("43420024");

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Lesson l = new Lesson();
        l.setName("碩士英文班");
        l.setStartDateTime(dateFormat.parse("2018-07-23 10:00:00"));
        l.setEndDateTime(dateFormat.parse("2018-07-23 11:30:00"));

        // when
        String message = WhatsappMsg.buildLessonNotificationMsg(c, l);

        // verify
        assertThat(message, equalTo("碩士教室(愛民)上課提示\r\n同學:中文名\r\n課堂:碩士英文班\r\n時間:2018-07-23 10:00-11:30\r\n敬請留意"));
    }
}
