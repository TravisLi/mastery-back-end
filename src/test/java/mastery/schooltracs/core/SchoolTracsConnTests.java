package mastery.schooltracs.core;

import mastery.MasteryRestApp;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

import static org.hamcrest.Matchers.containsString;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes=MasteryRestApp.class)
public class SchoolTracsConnTests {
    @Autowired SchoolTracsConn schoolTracsConn;

    @Test
    public void getFacility_shouldSuccess() throws IOException {
        String result = schoolTracsConn.sendFacReq();
        MatcherAssert.assertThat(result, containsString("G21A"));
    }

}
