package mastery.whatsapp;

import mastery.model.Lesson;
import mastery.model.Staff;
import mastery.schooltracs.model.Customer;

import org.apache.catalina.util.URLEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WhatsappRestAgent {

	private static final Logger logger = LoggerFactory.getLogger(WhatsappWebAgent.class);
	private static final String MOBILE_REGEX = "^[965][0-9]{7}";

	@Value("${whatsapp_server}")
	private String whatsappServer = "";
	private static final String SEND_REST_API = "http://%s/sendto/%s/?message=%s";
	private static final String SENDING_MSG = "Sending %s...";
	private static final String SEND_SUCCESS_MSG = "Send %s success!";
	private static final String SEND_FAIL_MSG = "Send %s fail!";
	private static final String ACTIVIATE_MSG = "activiation msg";
	private static final String CHG_PWD_MSG = "change pwd msg";
	private static final String LESSON_NOTIFICATION_MSG = "lesson notification msg";
	private static final String MAKEUP_MSG_ADM = "makeup msg to admin";
	private static final String MAKEUP_MSG_STD = "makeup msg to student";
	private static final String MAKEUP_MSG_TCH = "makeup msg to teacher";
		
	public WhatsappRestAgent(){

	}

	public WhatsappRestAgent(String server){
		this.whatsappServer = server;
	}
	
	public void sendMsgTmp(String phoneNo){
		logger.info("Sending msg template check");
		
		String msg = "";
		
		msg = WhatsappMsg.buildMsg(WhatsappMsg.ACTIVIATE_MSG_TMP);
		
		this.sendMsg(phoneNo, msg, ACTIVIATE_MSG);
		
		msg = WhatsappMsg.buildMsg(WhatsappMsg.CHG_PWD_MSG_TMP);
		
		this.sendMsg(phoneNo, msg, CHG_PWD_MSG);
		
		msg = WhatsappMsg.buildMsg(WhatsappMsg.MKUP_MSG_TMP_ADM);
		
		this.sendMsg(phoneNo, msg, MAKEUP_MSG_ADM);
		
		msg = WhatsappMsg.buildMsg(WhatsappMsg.MKUP_MSG_TMP_STD);
		
		this.sendMsg(phoneNo, msg, MAKEUP_MSG_STD);
		
		msg = WhatsappMsg.buildMsg(WhatsappMsg.MKUP_MSG_TMP_TCH);
		
		this.sendMsg(phoneNo, msg, MAKEUP_MSG_TCH);
		
	}

	public void sendMkupTchMsg(Staff s, String stdName, Lesson frLson, Lesson toLson){

		this.sendMsg(s.getMobile(), WhatsappMsg.buildMkupTchMsg(s, stdName, frLson, toLson), MAKEUP_MSG_TCH);
	}

	public void sendMkupAdmMsg(Staff s, String stdName, Lesson frLson, Lesson toLson){

		this.sendMsg(s.getMobile(), WhatsappMsg.buildMkupAdmMsg(s, stdName, frLson, toLson), MAKEUP_MSG_ADM);

	}

	public void sendMkupStdMsg(Customer c, Lesson frLson, Lesson toLson){

		this.sendMsg(c.getMobile(), WhatsappMsg.buildMkupStdMsg(c, frLson, toLson), MAKEUP_MSG_STD);

	}

	public void sendActivateMsg(String stdName, String mobile, String pw){
		
		this.sendMsg(mobile, WhatsappMsg.buildOpenSrvMsg(stdName, pw), ACTIVIATE_MSG);

	}

	public void sendChgPwdMsg(String stdName, String mobile){
		
		this.sendMsg(mobile, WhatsappMsg.buildChgPwdMsg(stdName), CHG_PWD_MSG);
	}

	public void sendLessonNotificationMsg(Customer customer, Lesson lesson) {
		this.sendMsg(customer.getMobile(), WhatsappMsg.buildLessonNotificationMsg(customer, lesson), LESSON_NOTIFICATION_MSG);
	}
	
	public void sendMsg(String phoneNo, String msg, String logMsg){
		
		String sendingMsg = String.format(SENDING_MSG, logMsg);
		String successMsg = String.format(SEND_SUCCESS_MSG, logMsg);
		String failMsg = String.format(SEND_FAIL_MSG, logMsg);
		
		logger.info(sendingMsg);
		
		if(this.sendMsg(phoneNo, msg)){
			logger.info(successMsg);
		}else{
			logger.info(failMsg);
		}
	}
	
	public Boolean sendMsg(String phoneNo, String msg){

		logger.info("phoneNo=" + phoneNo);
		logger.info("Message=" + msg);

		if(phoneNo==null){
			logger.error("Phone No is null");
			return false;
		}

		if(!phoneNo.matches(MOBILE_REGEX)){
			logger.error("Contact No is not in correct format");
			return false;
		}

		phoneNo = "852" + phoneNo;

		String encodedMsg = "";
		
		encodedMsg = new URLEncoder().encode(msg, "UTF-8");

		String url = String.format(SEND_REST_API , whatsappServer, phoneNo, encodedMsg);
		logger.debug("url=" + url);
		
		RestTemplate rt = new RestTemplate();
		ResponseEntity<String> re = rt.getForEntity(url, String.class);

		logger.info(re.getStatusCode().toString());

		if(re.getStatusCode().equals(HttpStatus.OK)){
			return true;
		}

		return false;
	}

}
