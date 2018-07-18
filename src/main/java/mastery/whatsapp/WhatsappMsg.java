package mastery.whatsapp;

import mastery.model.Lesson;
import mastery.model.Staff;
import mastery.schooltracs.model.Customer;
import mastery.util.MasteryUtil;

public class WhatsappMsg {

	public static final String MKUP_MSG_TMP_ADM = "致校務管理員\r\n學生%s將課堂:\r\n%s\r\n%s\r\n%s\r\n%s\n轉至\r\n%s\r\n%s\r\n%s\r\n%s\r\n敬請留意!";
	public static final String MKUP_MSG_TMP_STD = "致%s同學\r\n你己成功將課堂:\r\n%s\n%s\r\n%s\r\n%s\n轉至\r\n%s\r\n%s\r\n%s\r\n%s";
	public static final String MKUP_MSG_TMP_TCH = "致%s老師\r\n你的學生%s將課堂:\r\n%s\r\n%s\r\n%s\r\n轉至\n%s\r\n%s\r\n%s\r\n敬請留意!";
	public static final String ACTIVIATE_MSG_TMP = "致%s同學\r\n歡迎使用碩士教室(愛民)課堂系統!\r\n你的賬戶現已啟用，登入密碼為%s。";
	public static final String CHG_PWD_MSG_TMP = "致%s同學\r\n你的登入密碼已成功更新。";
	public static final String LESSON_NOTIFICATION_MSG_TMP = "碩士教室(愛民)上課提示\r\n同學:%s\r\n課堂:%s\r\n時間:%s\r\n敬請留意";

	public static String buildMkupTchMsg(Staff s, String stdName, Lesson frLson, Lesson toLson){

		String frLsonDt = MasteryUtil.getFormattedLsonTime(frLson);
		String toLsonDt = MasteryUtil.getFormattedLsonTime(toLson);

		return buildMkupTchMsg(stdName, s.getName(), frLson.getName(), toLson.getName(), frLson.getRoom().getName(), toLson.getRoom().getName(), frLsonDt, toLsonDt);

	}

	public static String buildMkupAdmMsg(Staff s, String stdName, Lesson frLson, Lesson toLson){

		String frLsonDt = MasteryUtil.getFormattedLsonTime(frLson);
		String toLsonDt = MasteryUtil.getFormattedLsonTime(toLson);

		return buildMkupAdmMsg(stdName, frLson.getName(), toLson.getName(), frLson.getRoom().getName(), toLson.getRoom().getName(), frLson.getTeacher().getName(), toLson.getTeacher().getName(), frLsonDt, toLsonDt);

	}

	public static String buildMkupStdMsg(Customer c, Lesson frLson, Lesson toLson){

		String frLsonDt = MasteryUtil.getFormattedLsonTime(frLson);
		String toLsonDt = MasteryUtil.getFormattedLsonTime(toLson);

		return buildMkupStdMsg(c.getName(), frLson.getName(), toLson.getName(), frLson.getRoom().getName(), toLson.getRoom().getName(), frLson.getTeacher().getName(), toLson.getTeacher().getName(), frLsonDt, toLsonDt);

	}

	public static String buildLessonNotificationMsg(Customer customer, Lesson lesson) {
		String time = MasteryUtil.getFormattedLsonTime(lesson);
		return buildLessonNotificationMsg(customer.getName(), lesson.getName(), time);
	}

	public static String buildOpenSrvMsg(String stdName, String pw){
		return String.format(ACTIVIATE_MSG_TMP, stdName, pw);
	}

	public static String buildChgPwdMsg(String stdName){
		return String.format(CHG_PWD_MSG_TMP, stdName);
	}

	public static String buildMsg(String template){

		String frLsonName = "Lesson A";
		String frLsonRm = "Room A";
		String frLsonDt = "Date A";
		String frLsonTch = "Teacher A";
		String toLsonName = "Lesson B";
		String toLsonTch = "Teacher B";
		String toLsonDt = "Date B";
		String toLsonRm = "Room B";
		String stdName =  "Student";
		String tchName =  "Teacher";
		String pw = "Password";

		String msg = "";

		switch(template){
			case ACTIVIATE_MSG_TMP:
				msg = String.format(ACTIVIATE_MSG_TMP, stdName, pw);
				break;
			case CHG_PWD_MSG_TMP:
				msg = String.format(CHG_PWD_MSG_TMP, stdName);
				break;
			case MKUP_MSG_TMP_ADM:
				msg = String.format(MKUP_MSG_TMP_ADM, stdName, frLsonName, frLsonTch, frLsonRm, frLsonDt, toLsonName, toLsonTch, toLsonRm, toLsonDt);
				break;
			case MKUP_MSG_TMP_STD:
				msg = String.format(MKUP_MSG_TMP_STD, stdName, frLsonName, frLsonTch, frLsonRm, frLsonDt, toLsonName, toLsonTch, toLsonRm, toLsonDt);
				break;
			case MKUP_MSG_TMP_TCH:
				msg = String.format(MKUP_MSG_TMP_TCH, tchName, stdName, frLsonName, frLsonRm, frLsonDt, toLsonName, toLsonRm, toLsonDt);
				break;
		}

		return msg;

	}

	private static String buildMkupAdmMsg(String stdName, String frLsonName, String toLsonName, String frLsonRm, String toLsonRm, String frLsonTch, String toLsonTch, String frLsonDt, String toLsonDt){
		return String.format(MKUP_MSG_TMP_ADM, stdName, frLsonName, frLsonTch, frLsonRm, frLsonDt, toLsonName, toLsonTch, toLsonRm, toLsonDt);
	}
	
	private static String buildMkupStdMsg(String stdName, String frLsonName, String toLsonName, String frLsonRm, String toLsonRm, String frLsonTch, String toLsonTch, String frLsonDt, String toLsonDt){
		return String.format(MKUP_MSG_TMP_STD, stdName, frLsonName, frLsonTch, frLsonRm, frLsonDt, toLsonName, toLsonTch, toLsonRm, toLsonDt);
	}
	
	private static String buildMkupTchMsg(String stdName, String tchName, String frLsonName, String toLsonName, String frLsonRm, String toLsonRm, String frLsonDt, String toLsonDt){
		return String.format(MKUP_MSG_TMP_TCH, tchName, stdName, frLsonName, frLsonRm, frLsonDt, toLsonName, toLsonRm, toLsonDt);
	}

	private static String buildLessonNotificationMsg(String studentName, String lessonName, String time) {
		return String.format(LESSON_NOTIFICATION_MSG_TMP, studentName, lessonName, time);
	}
}
