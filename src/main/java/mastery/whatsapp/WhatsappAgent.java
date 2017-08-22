package mastery.whatsapp;

import java.io.IOException;

import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import mastery.model.Lesson;
import mastery.model.Staff;
import mastery.schooltracs.model.Customer;
import mastery.util.MasteryUtil;

@Service
public class WhatsappAgent {

	private static final Logger logger = LoggerFactory.getLogger(WhatsappAgent.class);
	private static final String AREA_CODE = "852";
	
	@Value("${whatsapp.url}")
	private String whatsappRestUri;
	
	private static final String MKUP_MSG_TMP_TCH = "致%s老師\n你的學生%s將課堂:\n%s\n%s\n%s\n轉至\n%s\n%s\n%s\n敬請留意!";
	private static final String MKUP_MSG_TMP_ADM = "致校務管理員\n學生%s將課堂:\n%s\n%s\n%s\n%s\n轉至\n%s\n%s\n%s\n%s\n敬請留意!";
	private static final String MKUP_MSG_TMP_STD = "致%s同學\n你己成功將課堂:\n%s\n%s\n%s\n%s\n轉至\n%s\n%s\n%s\n%s";
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
			logger.info("Send msg to studet mobile success!");
		}else{
			logger.warn("Send msg to student mobile fail!");
		}
		
		if(this.sendMsg(c.getContact1Phone(), msg)){
			logger.info("Send msg to student contact 1 phone success!");
		}else{
			logger.warn("Send msg to student contact1 phone fail!");
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

		WhatsappMsg w = new WhatsappMsg(AREA_CODE + ctatNo, msg);
		
		
		try {
			CloseableHttpClient client = HttpClients.createDefault();
		    HttpPost httpPost = new HttpPost(this.whatsappRestUri);
		 
		    ObjectMapper om = new ObjectMapper();
			om.setSerializationInclusion(Include.NON_NULL);
			om.enable(SerializationFeature.INDENT_OUTPUT);
			
			String json = om.writeValueAsString(w);
			logger.debug(json);
			StringEntity entity = new StringEntity(json);
		    httpPost.setEntity(entity);
		    httpPost.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
		 
		    CloseableHttpResponse response = client.execute(httpPost);
		    logger.info("Response status code" + response.getStatusLine().getStatusCode() + "");
		    client.close();
		    
		    if(response.getStatusLine().getStatusCode()==200){
		    	result = true;
		    }
		    
		} catch (IOException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	    
		return result;
	}

}
