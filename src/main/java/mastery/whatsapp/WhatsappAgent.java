package mastery.whatsapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import mastery.schooltracs.util.SchoolTracsConst;

@Service
public class WhatsappAgent {

	private static final Logger logger = LoggerFactory.getLogger(WhatsappAgent.class);

	@Value("${python.path}")
	private String pythonPath;

	@Value("${whatsapp.token}")
	private String whatsappToken;

	private static final String MKUP_MSG_TMP_TCH = "致%s老師\n你的學生 %s將課堂:%s-%s\n由\n%s\n轉至\n%s\n敬請留意!";
	private static final String MKUP_MSG_TMP_ADM = "致校務管理員\n學生 %s將課堂:%s-%s (%s)\n由\n%s\n轉至\n%s\n敬請留意!";
	private static final String MKUP_MSG_TMP_STD = "致%s同學\n你己成功將課堂將課堂:%s-%s (%s)\n由\n%s\n轉至\n%s\n敬請留意!";

	public Boolean sendMkupTchMsg(String ctatNo, String tchName, String stdName, String lsonName, String rm, Date frDt, Date toDt){

		String msg = this.buildMkupTchMsg(tchName, stdName, lsonName, rm, frDt, toDt);

		return this.sendMsg(ctatNo, msg);
	}

	public Boolean sendMkupAdmMsg(String ctatNo, String tchName, String stdName, String lsonName, String rm, Date frDt, Date toDt){

		String msg = this.buildMkupAdmMsg(tchName, stdName, lsonName, rm, frDt, toDt);

		return this.sendMsg(ctatNo, msg);

	}

	public Boolean sendMkupStdMsg(String ctatNo, String tchName, String stdName, String lsonName, String rm, Date frDt, Date toDt){

		String msg = this.buildMkupStdMsg(tchName, stdName, lsonName, rm, frDt, toDt);

		return this.sendMsg(ctatNo, msg);
	}

	private String buildMkupTchMsg(String tchName, String stdName, String lsonName, String rm, Date frDt, Date toDt){
		return String.format(MKUP_MSG_TMP_TCH, tchName, stdName, lsonName, rm, SchoolTracsConst.SDF_FULL.format(frDt), SchoolTracsConst.SDF_FULL.format(toDt));
	}

	private String buildMkupAdmMsg(String tchName, String stdName, String lsonName, String rm, Date frDt, Date toDt){
		return String.format(MKUP_MSG_TMP_ADM, stdName, lsonName, rm, tchName, SchoolTracsConst.SDF_FULL.format(frDt), SchoolTracsConst.SDF_FULL.format(toDt));
	}
	
	private String buildMkupStdMsg(String tchName, String stdName, String lsonName, String rm, Date frDt, Date toDt){
		return String.format(MKUP_MSG_TMP_STD, stdName, lsonName, rm, tchName, SchoolTracsConst.SDF_FULL.format(frDt), SchoolTracsConst.SDF_FULL.format(toDt));
	}

	private String[] buildSendMsgCmd(String ctatNo, String msg){
		List<String> list = new ArrayList<String>();
		list.add(pythonPath + "\\python");
		list.add(pythonPath + "\\Scripts\\yowsup-cli");
		list.add("demos");
		list.add("-l");
		list.add(whatsappToken);
		list.add("-s");
		list.add(ctatNo);
		list.add(msg);

		String[] list2 = new String[list.size()];

		return list.toArray(list2);
	}

	private Boolean sendMsg(String ctatNo, String msg){

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

	public static void main(String[] args) throws IOException{
		WhatsappAgent agent = new WhatsappAgent();
		agent.whatsappToken = "85292648633:jE3cBZxWRgm3nwG8ZfFOj1n2d3U=";
		agent.pythonPath = "C:\\Users\\Travis Li\\AppData\\Local\\Programs\\Python\\Python36-32";
		agent.sendMkupTchMsg("85296841163", "tchName", "stdName", "Lesson", "Rm", new Date(), new Date());
		agent.sendMkupAdmMsg("85296841163", "tchName", "stdName", "Lesson", "Rm", new Date(), new Date());
		agent.sendMkupStdMsg("85296841163", "tchName", "stdName", "Lesson", "Rm", new Date(), new Date());
		//logger.info(agent.sendMsg("85296841163", "Hello1\nHello2").toString());
	}

}
