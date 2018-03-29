package mastery.whatsapp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import mastery.model.Lesson;
import mastery.model.Staff;
import mastery.schooltracs.model.Customer;

@Service
public class WhatsappRestAgent {

	private static final Logger logger = LoggerFactory.getLogger(WhatsappWebAgent.class);
	private static final String MOBILE_REGEX = "^[965][0-9]{7}";

	@Value("${whatsapp.server}")
	private String whatsappServer = "";
	private static final String SEND_REST_API = "http://%s/send/%s/to/%s";

	public WhatsappRestAgent(){

	}

	public WhatsappRestAgent(String server){
		this.whatsappServer = server;
	}

	public void sendMkupTchMsg(Staff s, String stdName, Lesson frLson, Lesson toLson){
		logger.info("Sending makeup msg to teacher");

		String msg = WhatsappMsg.buildMkupTchMsg(s, stdName, frLson, toLson);

		if(this.sendMsg(s.getMobile(), msg)){
			logger.info("Send msg to teacher mobile success!");
		}else{
			logger.warn("Send msg to teacher mobile fail!");
		}
	}

	public void sendMkupAdmMsg(Staff s, String stdName, Lesson frLson, Lesson toLson){
		logger.info("Sending makeup msg to admin");

		String msg = WhatsappMsg.buildMkupAdmMsg(s, stdName, frLson, toLson);

		if(this.sendMsg(s.getMobile(), msg)){
			logger.info("Send msg to admin mobile success!");
		}else{
			logger.warn("Send msg to admin mobile fail!");
		}

	}

	public void sendMkupStdMsg(Customer c, Lesson frLson, Lesson toLson){
		logger.info("Sending makeup msg to student");

		String msg = WhatsappMsg.buildMkupStdMsg(c, frLson, toLson);

		if(this.sendMsg(c.getMobile(), msg)){
			logger.info("Send msg to student mobile success!");
		}else{
			logger.warn("Send msg to student mobile fail!");
		}

	}

	public void sendActivateMsg(String stdName, String mobile, String pw){
		logger.info("Sending activate msg to student");

		String msg = WhatsappMsg.buildOpenSrvMsg(stdName, pw);

		if(this.sendMsg(mobile, msg)){
			logger.info("Send activate msg success!");
		}else{
			logger.info("Send activate msg fail!");
		}

	}

	public void sendChgPwdMsg(String stdName, String mobile){
		logger.info("Sending change password msg to student");

		String msg = WhatsappMsg.buildChgPwdMsg(stdName);

		if(this.sendMsg(mobile, msg)){
			logger.info("Send change pwd msg to student mobile success!");
		}else{
			logger.info("Send change pwd service msg to student mobile fail!");
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

		String url = String.format(SEND_REST_API , whatsappServer, msg, phoneNo);

		RestTemplate rt = new RestTemplate();
		ResponseEntity<String> re = rt.getForEntity(url, String.class);

		logger.info(re.getStatusCode().toString());

		if(re.getStatusCode().equals(HttpStatus.OK)){
			return true;
		}

		return false;
	}

	/*public static void main(String[] args){

		WhatsappRestAgent agent = new WhatsappRestAgent("be.masteryoim.com:8080");

		logger.info(agent.sendMsg("96841163", "testing").toString());

	}*/

}
