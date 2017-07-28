package mastery.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
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
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;

import mastery.model.Auth;
import mastery.model.Lesson;
import mastery.model.User;
import mastery.schooltracs.core.SchoolTracsAgent;
import mastery.schooltracs.model.Customer;
import mastery.util.MasteryUtil;

@CrossOrigin(origins = {"http://localhost:4200"}, maxAge = 4800, allowCredentials = "false") 
@RestController
@RequestMapping("/api")
public class RestApiController {

	public static final Logger logger = LoggerFactory.getLogger(RestApiController.class);
	
	@Autowired
	SchoolTracsAgent agent;
		
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public ResponseEntity<User> login(@RequestBody Auth auth){
		
		if(StringUtils.isEmpty(auth.getUsername())||StringUtils.isEmpty(auth.getPwd())){
			return null;
		}
		
		List<Customer> custs = agent.schCustsByPhone(auth.getUsername());
		
		if(custs.isEmpty()){
			return null;
		}
		
		if(custs.size()>1){
			logger.info("More than one customer is found");
			return null;
		}
		
		Customer c = custs.get(0);
		
		if(auth.getPwd().equals(c.getBarCode())){
			return new ResponseEntity<User>(new User(c, "student"), HttpStatus.OK); 
		}
		
		return null;
		
	}
	
	
	@RequestMapping(value = "/lesson/student/{name}/{weekNo}", method = RequestMethod.GET)
	public ResponseEntity<List<Lesson>> getStdLsons(@PathVariable("name")String name, @PathVariable("weekNo")Integer weekNo){
		logger.info("Get Student Lesson Start");
		logger.info("name="+name+" weekNo="+weekNo);
		//for demo purpose we date back to march
		Calendar stCal = MasteryUtil.getPlainCal(new Date());
		
		//stCal.set(Calendar.MONTH, 6);
		//stCal.set(Calendar.DAY_OF_MONTH,1);
		
		Calendar edCal = MasteryUtil.getPlainCal(stCal.getTime());
		stCal.add(Calendar.DAY_OF_MONTH, (weekNo-1)*7);
		edCal.add(Calendar.DAY_OF_MONTH, weekNo*2*7);
		
		List<Lesson> list = new ArrayList<Lesson>();
		try {
			list = agent.schLsonByStd(name, stCal.getTime(), edCal.getTime());
			logger.info(list.toString());
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return new ResponseEntity<List<Lesson>>(list, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/mkup/find/{stdName}", method = RequestMethod.POST)
	public ResponseEntity<List<Lesson>> findMkupLsons(@RequestBody Lesson lson, @PathVariable("stdName")String stdName){		
		List<Lesson> list = new ArrayList<Lesson>();
		
		list = agent.schMkup(lson,stdName);
		
		logger.info(list.toString());
		
		return new ResponseEntity<List<Lesson>>(list, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/mkup/apply/{stdId}", method = RequestMethod.POST)
	public ResponseEntity<Boolean> applyMkupLsons(@RequestBody Lesson lson, @PathVariable("stdId")String stdId) throws ClientProtocolException, UnsupportedEncodingException, JsonProcessingException, IOException{		
		
		Boolean result = agent.aplyMkup(lson,stdId);
		
		logger.info(result.toString());
		
		return new ResponseEntity<Boolean>(result, HttpStatus.OK);
	}

}