package mastery.tool;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import mastery.MasteryRestApp;
import mastery.schooltracs.core.SchoolTracsAgent;

public class ReceiptEasyBuilder {

	public static void main(String[] args) {

		ApplicationContext ctx = new AnnotationConfigApplicationContext(MasteryRestApp.class);
		
		SchoolTracsAgent agent = ctx.getBean(SchoolTracsAgent.class);
		
		agent.getRooms();
		
	}

}
