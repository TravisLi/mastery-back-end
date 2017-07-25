package mastery.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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

import mastery.model.Lesson;
import mastery.schooltracs.core.SchoolTracsAgent;
import mastery.util.MasteryUtil;

@CrossOrigin(origins = {"http://localhost:4200"}, maxAge = 4800, allowCredentials = "false") 
@RestController
@RequestMapping("/api")
public class RestApiController {

	public static final Logger logger = LoggerFactory.getLogger(RestApiController.class);
	
	@Autowired
	SchoolTracsAgent agent;
		
	@RequestMapping(value = "/lesson/student/{name}/{weekNo}", method = RequestMethod.GET)
	public ResponseEntity<List<Lesson>> getStdLsons(@PathVariable("name")String name, @PathVariable("weekNo")Integer weekNo){
		logger.info("Get Student Lesson Start");
		logger.info("name="+name+" weekNo="+weekNo);
		//for demo purpose we date back to march
		Calendar stCal = MasteryUtil.getPlainCal(new Date());
		
		stCal.set(Calendar.MONTH, 6);
		stCal.set(Calendar.DAY_OF_MONTH,1);
		
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
	
	@RequestMapping(value = "/mkuplson/find/{stdName}", method = RequestMethod.POST)
	public ResponseEntity<List<Lesson>> findMkupLsons(@RequestBody Lesson lson, @PathVariable("stdName")String stdName){		
		List<Lesson> list = new ArrayList<Lesson>();
		
		list = agent.schMkupCls(lson,stdName);
		
		logger.info(list.toString());
		
		return new ResponseEntity<List<Lesson>>(list, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/mkuplson/apply/{stdId}", method = RequestMethod.POST)
	public ResponseEntity<Boolean> applyMkupLsons(@RequestBody Lesson lson, @PathVariable("stdId")String stdId){		
		
		Boolean result = agent.appMkupCls(lson,stdId);
		
		logger.info(result.toString());
		
		return new ResponseEntity<Boolean>(result, HttpStatus.OK);
	}

}