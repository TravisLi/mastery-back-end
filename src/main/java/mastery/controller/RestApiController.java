package mastery.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.ClientProtocolException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;

import mastery.model.Auth;
import mastery.model.Journal;
import mastery.model.Lesson;
import mastery.model.Student;
import mastery.model.User;
import mastery.schooltracs.core.SchoolTracsAgent;
import mastery.schooltracs.model.Customer;
import mastery.util.MasteryUtil;
import mastery.whatsapp.WhatsappRestAgent;

@CrossOrigin(maxAge = 4800, allowCredentials = "false") 
@RestController
@RequestMapping("/api")
public class RestApiController {

	private static final Logger logger = LoggerFactory.getLogger(RestApiController.class);
	private static final String DEFAULT_PWD = "masteryoim";
	private static final String PHONE_REGEX = "^[0-9]{8}";
	
	@Autowired 
	private WhatsappRestAgent wAgent;
	
	@Autowired
	private SchoolTracsAgent sAgent;
	
	@RequestMapping(value = "/send/whatsapp/msg/{pwd}/{phoneNo}", method = RequestMethod.GET)
	public ResponseEntity<Boolean> sendWhatsappMsg(@PathVariable("pwd")String pwd,@PathVariable("phoneNo")String phoneNo, @RequestParam("msg")String msg){
		
		if(pwd.equals(DEFAULT_PWD)){
			return new ResponseEntity<Boolean>(wAgent.sendMsg(phoneNo, msg), HttpStatus.OK);
		}
		
		return new ResponseEntity<Boolean>(false, HttpStatus.OK);
		
	}
	
	@RequestMapping(value = "/send/msg/tmp/{pwd}/{phoneNo}", method = RequestMethod.GET)
	public ResponseEntity<Boolean> sendMsgTmp(@PathVariable("pwd")String pwd,@PathVariable("phoneNo")String phoneNo){
		
		if(pwd.equals(DEFAULT_PWD)){
			
			wAgent.sendMsgTmp(phoneNo);
			
			return new ResponseEntity<Boolean>(true, HttpStatus.OK);
		}
		
		return new ResponseEntity<Boolean>(false, HttpStatus.OK);
		
	}
	
	@RequestMapping(value = "/check", method = RequestMethod.GET)
	public ResponseEntity<Boolean> check() {
		return new ResponseEntity<Boolean>(true, HttpStatus.OK);
	}
	
	@Deprecated
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public ResponseEntity<User> login(@RequestBody Auth auth){
		return this.studentLogin(auth);
	}
	
	@RequestMapping(value = "/student/login", method = RequestMethod.POST)
	public ResponseEntity<User> studentLogin(@RequestBody Auth auth){
		
		logger.info("Student login start");
		
		if(StringUtils.isEmpty(auth.getUsername())||StringUtils.isEmpty(auth.getPwd())){
			return null;
		}
		
		if(!auth.getUsername().matches(PHONE_REGEX)){
			logger.error("Phone No is not in correct format");
			return null;
		}
		
		
		List<Customer> custs = sAgent.schCustsByPhoneAndBarcode(auth.getUsername(), auth.getPwd());
		
		if(custs.isEmpty()){
			return null;
		}
		
		if(custs.size()>1){
			logger.info("More than one customer is found");
			return null;
		}
		
		for(Customer c: custs){
			if(auth.getPwd().equals(c.getBarCode())){
				return new ResponseEntity<User>(new User(c), HttpStatus.OK);
			}
		}
		
		return null;
		
	}
	
	@RequestMapping(value = "/parent/login", method = RequestMethod.POST)
	public ResponseEntity<User> parentLogin(@RequestBody Auth auth){
		
		logger.info("Parent login start");
		
		if(StringUtils.isEmpty(auth.getUsername())||StringUtils.isEmpty(auth.getPwd())){
			return null;
		}
		
		if(!auth.getUsername().matches(PHONE_REGEX)){
			logger.error("Phone No is not in correct format");
			return null;
		}
		
		
		List<Customer> custs = sAgent.schCustsByContact1Phone(auth.getUsername());
		
		if(custs.isEmpty()){
			return null;
		}
		
		boolean matchPwd = false;
		String name = "";
		String custId = ""; 
		
		List<Student> stdList = new ArrayList<Student>();
		
		for(Customer c:custs){

			if(auth.getPwd().equals(c.getBarCode())){
				logger.info("Password match");
				matchPwd = true;
				name = c.getName() + "家長";
				custId = c.getId();
			}
			
			stdList.add(new Student(c));
			
		}
		
		if(matchPwd){
			return new ResponseEntity<User>(new User(custId, name, auth.getUsername(), stdList), HttpStatus.OK);
		}
		
		return null;
		
	}
	
	@RequestMapping(value = "/user/activate/{name}/{phone}/{mobile}", method = RequestMethod.GET)
	public ResponseEntity<Boolean> activate(@PathVariable("name")String name, @PathVariable("phone")String phone, @PathVariable("mobile")String mobile){
		logger.info("Activiate Service");
		logger.info("name="+name);
		logger.info("phone="+phone);
		logger.info("mobile="+mobile);
				
		return new ResponseEntity<Boolean>(sAgent.activate(name, phone, mobile), HttpStatus.OK);
		
	}
	
	
	@RequestMapping(value = "/user/updatepwd/{custId}/{oldPwd}/{newPwd}", method = RequestMethod.GET)
	public ResponseEntity<Boolean> updateUserPwd(@PathVariable("custId")String custId, @PathVariable("oldPwd")String oldPwd, @PathVariable("newPwd")String newPwd){
		logger.info("Update User Password");
				
		return new ResponseEntity<Boolean>(sAgent.updateUserPwd(custId, oldPwd, newPwd), HttpStatus.OK);
		
	}
	
	/*@RequestMapping(value = "/journal/parent/{stdIds}/{index}", method = RequestMethod.GET)
	public ResponseEntity<Journal> getJournalForParent(@PathVariable("phone")String phone, @PathVariable("index")Integer index){
		logger.info("Get Journal for Parent");
		
		return new ResponseEntity<Journal>(new Journal(), HttpStatus.OK);
		
	}*/
	
	@RequestMapping(value = "/journal/{stdId}/{index}", method = RequestMethod.GET)
	public ResponseEntity<List<Journal>> getJournalForStd(@PathVariable("stdId")String stdId, @PathVariable("index")Integer index){
		logger.info("Get Journal for Students");
		
		List<Journal> list = new ArrayList<Journal>();
		
		try {
			list = sAgent.getJournalByStdId(stdId, index);
		} catch (IOException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		
		return new ResponseEntity<List<Journal>>(list, HttpStatus.OK);
		
	}
	
	@RequestMapping(value = "/lesson/parent/{phone}/{weekNo}", method = RequestMethod.GET)
	public ResponseEntity<List<Lesson>> getLsonsForParent(@PathVariable("phone")String phone, @PathVariable("weekNo")Integer weekNo){
		logger.info("Get Lesson for Parent Start");
		logger.info("phone="+phone+" weekNo="+weekNo);
		
		List<Customer> custs = sAgent.schCustsByContact1Phone(phone);
		
		List<Lesson> list = new ArrayList<Lesson>();
		
		for(Customer c: custs){
			for(Lesson l: this.schStdLsons(c.getName(), weekNo)){
				//intent to set the student for identification in front end
				l.setStudent(new Student(c));
				list.add(l);	
			}	
		}
		
		Collections.sort(list);
		
		return new ResponseEntity<List<Lesson>>(list, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/lesson/student/{name}/{weekNo}", method = RequestMethod.GET)
	public ResponseEntity<List<Lesson>> getStdLsons(@PathVariable("name")String name, @PathVariable("weekNo")Integer weekNo){
		logger.info("Get Student Lesson Start");
		logger.info("name="+name+" weekNo="+weekNo);
				
		return new ResponseEntity<List<Lesson>>(schStdLsons(name,weekNo), HttpStatus.OK);
	}
	
	private List<Lesson> schStdLsons(String name, Integer weekNo){
				
		Calendar stCal = Calendar.getInstance();
		//if current week, the lesson before now should not be shown
		if(weekNo>0){
			stCal = MasteryUtil.getPlainCal(new Date());
		}
				
		Calendar edCal = MasteryUtil.getPlainCal(stCal.getTime());
		
		//get a week of class
		stCal.add(Calendar.DAY_OF_MONTH, (weekNo-1)*7);
		edCal.add(Calendar.DAY_OF_MONTH, weekNo*7);
		
		List<Lesson> list = new ArrayList<Lesson>();
		try {
			list = sAgent.schLsonByStd(name, stCal.getTime(), edCal.getTime());
			logger.info("Result from Search Lesson By Student=" + list.toString());
		} catch (ClientProtocolException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return list;
	}
	
	@RequestMapping(value = "/mkup/find/{stdName}", method = RequestMethod.POST)
	public ResponseEntity<List<Lesson>> findMkupLsons(@RequestBody Lesson lson, @PathVariable("stdName")String stdName){		
		List<Lesson> list = new ArrayList<Lesson>();
		
		list = sAgent.schMkup(lson,stdName);
		
		Collections.sort(list);
		
		logger.info(list.toString());
		
		return new ResponseEntity<List<Lesson>>(list, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/mkup/apply/new/{stdId}/{frLsonId}", method = RequestMethod.POST)
	public ResponseEntity<Boolean> applyMkupLsons(@PathVariable("stdId")String stdId, @PathVariable("frLsonId")String frLsonId, @RequestBody Lesson toLson) throws ClientProtocolException, UnsupportedEncodingException, JsonProcessingException, IOException{		
		
		logger.info("stdId=" + stdId);
		logger.info("frLsonId=" + frLsonId);
		logger.info("Lesson=" + toLson);
		
		Boolean result = sAgent.aplyNewMkup(stdId, frLsonId, toLson);
		
		logger.info(result.toString());
		
		return new ResponseEntity<Boolean>(result, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/mkup/apply/exist/{stdId}/{stdLsonId}/{frLsonId}", method = RequestMethod.POST)
	public ResponseEntity<Boolean> applyExtMkupLsons(@PathVariable("stdId")String stdId, @PathVariable("stdLsonId")String stdLsonId, @PathVariable("frLsonId")String frLsonId, @RequestBody Lesson toLson) throws ClientProtocolException, UnsupportedEncodingException, JsonProcessingException, IOException{		
		
		logger.info("stdId=" + stdId);
		logger.info("frLsonId=" + frLsonId);
		logger.info("Lesson=" + toLson);
		
		Boolean result = sAgent.aplyExtMkup(stdId, stdLsonId, frLsonId, toLson);
		
		logger.info(result.toString());
		
		return new ResponseEntity<Boolean>(result, HttpStatus.OK);
	}
	
}