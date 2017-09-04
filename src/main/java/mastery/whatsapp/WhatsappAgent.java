package mastery.whatsapp;

import mastery.model.Lesson;
import mastery.model.Staff;
import mastery.schooltracs.model.Customer;
import mastery.util.MasteryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Service
public class WhatsappAgent {

	private static final Logger logger = LoggerFactory.getLogger(WhatsappAgent.class);
	private static final String AREA_CODE = "852";

	@Value("${whatsapp.token}")
	private String whatsappToken;

	private static final String MKUP_MSG_TMP_TCH = "致%s老師\n你的學生%s將課堂:\n%s\n%s\n%s\n轉至\n%s\n%s\n%s\n敬請留意!";
	private static final String MKUP_MSG_TMP_ADM = "致校務管理員\n學生%s將課堂:\n%s\n%s\n%s\n%s\n轉至\n%s\n%s\n%s\n%s\n敬請留意!";
	private static final String MKUP_MSG_TMP_STD = "致%s同學\n你己成功將課堂:\n%s\n%s\n%s\n%s\n轉至\n%s\n%s\n%s\n%s";
	private static final String OPEN_SRV_MSG_TMP = "致%s同學\n歡迎使用碩士教室(愛民)課堂系統!\n你的賬戶現已啟用，登入密碼為%s。";
	private static final String CHG_PWD_MSG_TMP = "致%s同學\n你的登入密碼已成功更新。";
	private static final String MOBILE_REGEX = "^[965][0-9]{7}";

	public void sendMkupTchMsg(Staff s, String stdName, Lesson frLson, Lesson toLson){
		logger.info("Sending makeup msg to teacher");

		String frLsonDt = MasteryUtil.getFormattedLsonTime(frLson);
		String toLsonDt = MasteryUtil.getFormattedLsonTime(toLson);

		String msg = this.buildMkupTchMsg(stdName, s.getName(), frLson.getName(), toLson.getName(), frLson.getRoom().getName(), toLson.getRoom().getName(), frLsonDt, toLsonDt);

		if(this.sendMsg(s.getMobile(), msg)){
			logger.info("Send msg to teacher mobile success!");
		}else{
			logger.warn("Send msg to teacher mobile fail!");
		}
	}

	public void sendMkupAdmMsg(Staff s, String stdName, Lesson frLson, Lesson toLson){
		logger.info("Sending makeup msg to admin");

		String frLsonDt = MasteryUtil.getFormattedLsonTime(frLson);
		String toLsonDt = MasteryUtil.getFormattedLsonTime(toLson);

		String msg = this.buildMkupAdmMsg(stdName, frLson.getName(), toLson.getName(), frLson.getRoom().getName(), toLson.getRoom().getName(), frLson.getTeacher().getName(), toLson.getTeacher().getName(), frLsonDt, toLsonDt);

		if(this.sendMsg(s.getMobile(), msg)){
			logger.info("Send msg to admin mobile success!");
		}else{
			logger.warn("Send msg to admin mobile fail!");
		}

	}

	public void sendMkupStdMsg(Customer c, Lesson frLson, Lesson toLson){
		logger.info("Sending makeup msg to student");

		String frLsonDt = MasteryUtil.getFormattedLsonTime(frLson);
		String toLsonDt = MasteryUtil.getFormattedLsonTime(toLson);

		String msg = this.buildMkupStdMsg(c.getName(), frLson.getName(), toLson.getName(), frLson.getRoom().getName(), toLson.getRoom().getName(), frLson.getTeacher().getName(), toLson.getTeacher().getName(), frLsonDt, toLsonDt);

		if(this.sendMsg(c.getMobile(), msg)){
			logger.info("Send msg to student mobile success!");
		}else{
			logger.warn("Send msg to student mobile fail!");
		}

		/*if(this.sendMsg(c.getContact1Phone(), msg)){
			logger.info("Send msg to student contact1 phone success!");
		}else{
			logger.warn("Send msg to student contact1 phone fail!");
		}*/

	}
	
	public void sendOpenOnlineSrvMsg(String stdName, String mobile, String pw){
		String msg = this.buildOpenSrvMsg(stdName, pw);
		
		if(this.sendMsg(mobile, msg)){
			logger.info("Send open online service msg to student mobile success!");
		}else{
			logger.info("Send open online service msg to student mobile fail!");
		}
		
	}
	
	public void sendChgPwdMsg(String stdName, String mobile){
		String msg = this.buildChgPwdMsg(stdName);
		
		if(this.sendMsg(mobile, msg)){
			logger.info("Send change pwd msg to student mobile success!");
		}else{
			logger.info("Send change pwd service msg to student mobile fail!");
		}
	}

	private String buildMkupTchMsg(String stdName, String tchName, String frLsonName, String toLsonName, String frLsonRm, String toLsonRm, String frLsonDt, String toLsonDt){
		return String.format(MKUP_MSG_TMP_TCH, tchName, stdName, frLsonName, frLsonRm, frLsonDt, toLsonName, toLsonRm, toLsonDt);
	}

	private String buildMkupAdmMsg(String stdName, String frLsonName, String toLsonName, String frLsonRm, String toLsonRm, String frLsonTch, String toLsonTch, String frLsonDt, String toLsonDt){
		return String.format(MKUP_MSG_TMP_ADM, stdName, frLsonName, frLsonTch, frLsonRm, frLsonDt, toLsonName, toLsonTch, toLsonRm, toLsonDt);
	}

	private String buildMkupStdMsg(String stdName, String frLsonName, String toLsonName, String frLsonRm, String toLsonRm, String frLsonTch, String toLsonTch, String frLsonDt, String toLsonDt){
		return String.format(MKUP_MSG_TMP_STD, stdName, frLsonName, frLsonTch, frLsonRm, frLsonDt, toLsonName, toLsonTch, toLsonRm, toLsonDt);
	}
	
	private String buildOpenSrvMsg(String stdName, String pw){
		return String.format(OPEN_SRV_MSG_TMP, stdName, pw);
	}
	
	private String buildChgPwdMsg(String stdName){
		return String.format(CHG_PWD_MSG_TMP, stdName);
	}

	private String[] buildSendMsgCmd(String ctatNo, String msg){
		List<String> list = new ArrayList<String>();
		list.add("yowsup-cli");
		list.add("demos");
		list.add("-l");
		list.add(whatsappToken);
		list.add("-s");
		list.add(AREA_CODE + ctatNo);
		list.add(msg);

		String[] list2 = new String[list.size()];

		return list.toArray(list2);
	}

	private Boolean sendMsg(String ctatNo, String msg){

		logger.info("contactNo=" + ctatNo);
		logger.info("Message=" + msg);

		if(ctatNo==null){
			logger.error("Contact No is null");
			return false;
		}

		if(!ctatNo.matches(MOBILE_REGEX)){
			logger.error("Contact No is not in correct format");
			return false;
		}

		Boolean result = false;

		try {
			ProcessBuilder pb = new ProcessBuilder(buildSendMsgCmd(ctatNo, msg));
			Process process=pb.start();

			BufferedReader input = new BufferedReader(new InputStreamReader(process.getErrorStream()));

			String line;

			while ((line = input.readLine()) != null) {
				logger.debug(line);
				if(line.contains("Message sent")){
					result = true;
				}
			}

			return result;

		} catch (IOException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}

		return result;
	}

}
