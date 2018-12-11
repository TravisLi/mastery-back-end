package mastery.controller;

import mastery.service.LessonNotificationService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class LessonNotificationControllerTest {
    LessonNotificationController controller;
    LessonNotificationService mockService;

    @Before
    public void setup() {
        mockService = Mockito.mock(LessonNotificationService.class);
        controller = new LessonNotificationController(mockService);
    }

    @Test
    public void send_givenWrongDateFormat_shouldReturnFailResponse() {
        ResponseEntity<String> response = controller.send("1 Aug 2018");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Fail to parse date", response.getBody());
        verify(mockService, times(0)).sendLessonNotificationOnDate(any());
    }

    @Test
    public void send_givenYesterday_shouldReturnFailResponse() {
        Calendar yesterday = Calendar.getInstance();
        yesterday.add(Calendar.DATE, -1);

        ResponseEntity<String> response = controller.send(new SimpleDateFormat("yyyyMMdd").format(yesterday.getTime()));
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Input date is not future date", response.getBody());
        verify(mockService, times(0)).sendLessonNotificationOnDate(any());
    }

    @Test
    public void send_givenTomorrow_shouldReturnSucess() {
        Calendar tomorrow = Calendar.getInstance();
        tomorrow.add(Calendar.DATE, 1);

        ResponseEntity<String> response = controller.send(new SimpleDateFormat("yyyyMMdd").format(tomorrow.getTime()));
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Success", response.getBody());
        verify(mockService, times(1)).sendLessonNotificationOnDate(any());
    }
}