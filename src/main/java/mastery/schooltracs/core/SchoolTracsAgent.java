package mastery.schooltracs.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.type.CollectionType;

import mastery.model.Lesson;
import mastery.model.Room;
import mastery.model.Student;
import mastery.model.Teacher;
import mastery.schooltracs.json.deserializer.CustomersDeserializer;
import mastery.schooltracs.json.deserializer.FacilitiesDeserializer;
import mastery.schooltracs.json.deserializer.SearchResponseDeserializer;
import mastery.schooltracs.model.Customer;
import mastery.schooltracs.model.Facility;
import mastery.schooltracs.model.SearchRequest;
import mastery.schooltracs.model.SearchResponse;
import mastery.schooltracs.util.SchoolTracsConst;
import mastery.schooltracs.util.SchoolTracsUtil;
import mastery.util.MasteryUtil;

@Service
public class SchoolTracsAgent {

	private static final Logger logger = LoggerFactory.getLogger(SchoolTracsAgent.class);

	private SchoolTracsConn conn = new SchoolTracsConn();

	private HashMap<String, Room> rmHash = new HashMap<String, Room>();

	@Value("${schooltracs.uname}")
	private String uname;

	@Value("${schooltracs.pwd}")
	private String pwd;

	@PostConstruct
	public void init() throws IOException{
		conn.login(uname, pwd);
		List<Room> rms = this.getRms();
		for(Room r:rms){
			if(rmHash.containsKey(r.getId())){
				rmHash.replace(r.getId(), r);
			}else{
				rmHash.put(r.getId(), r);
			}
		}
	}
	
	//customer
	public List<Customer> schCustsByPhone(String phone){
		logger.info("Search Customer by Name Start");
		
		List <NameValuePair> nvps = new ArrayList <NameValuePair>();
		nvps.add(new BasicNameValuePair("filter[0][field]", "phone"));
		nvps.add(new BasicNameValuePair("filter[0][data][type]", "string"));
		nvps.add(new BasicNameValuePair("filter[0][data][value]", phone));
		nvps.add(new BasicNameValuePair("centerId", "2"));
		nvps.add(new BasicNameValuePair("start", "0"));
		
		try {
			return jsonToCusts(conn.sendCustReq(nvps));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new ArrayList<Customer>();
	}

	//make up
	public Boolean aplyMkup(Lesson l, String stdId){
		logger.info("Apply Makeup class start");

		
		String result;
		try {
			if(l.getId()!=null){
				result = conn.sendNewMkupReq(l, stdId);
				logger.debug(result);
			}else{
				
				String stdLsonId = null;
				for(Student s: l.getStudents()){
					if(s.getId().equals(stdId)){
						stdLsonId = s.getStdLsonId();
					}
				}
				
				result = conn.sendExtMkupReq(l.getId(), stdLsonId);
				logger.debug(result);
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
		if(result.contains("Exception")){
			return false;
		}
		return true;
	}
	
	public List<Lesson> schMkup(Lesson src, String stdName){

		logger.info("Search For Makeup Class Start");
		//room-date Hash
		HashMap<String, List<Lesson>> rdLsonHash = new HashMap<String, List<Lesson>>();
		List<Lesson> rstLsons= new ArrayList<Lesson>();

		Calendar stCal = MasteryUtil.getPlainCal(src.getStartDateTime());
		stCal.add(Calendar.DAY_OF_MONTH, 1);

		Calendar edCal = MasteryUtil.getPlainCal(src.getStartDateTime());
		edCal.add(Calendar.DAY_OF_MONTH, 6);

		Date stDate = stCal.getTime();
		Date edDate = edCal.getTime();

		Teacher t = src.getTeacher();

		logger.info("Teacher="+t.getName());
		logger.info("startDate="+stDate);
		logger.info("endDate="+edDate);

		//find the lesson of the same teach and same class of upcoming six days
		try {
			List<Lesson> tLsons = this.schLsonByTch(t.getName(),stDate,edDate);

			//find the lesson without gap
			List<Lesson> g0Lsons = SchoolTracsUtil.findSimLvlLsonOfTch(src, stdName, 0, tLsons);

			logger.info("g0Lsons count="+g0Lsons.size());

			for(Lesson l: fltLsonByRmAva(g0Lsons, rdLsonHash)){
				rstLsons.add(l);
				if(rstLsons.size()==3){
					return rstLsons;
				}
			}

			//find the lesson with gap 1
			List<Lesson> g1Lsons = SchoolTracsUtil.findSimLvlLsonOfTch(src, stdName, 1, tLsons);

			logger.info("g1Lsons count="+g1Lsons.size());

			for(Lesson l: fltLsonByRmAva(g1Lsons, rdLsonHash)){
				rstLsons.add(l);
				if(rstLsons.size()==3){
					return rstLsons;
				}
			}

			//TODO find free time of teacher later

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return rstLsons;
	}

	//lesson
	public List<Lesson> schLsonByName(String name, Date fromDate, Date toDate) throws ClientProtocolException, IOException{

		logger.info("Search Lesson by Name Start");

		//without product
		SearchRequest.ContentOpt opt =new SearchRequest.ContentOpt(true,true,false,true);

		return schLson(name, fromDate, toDate, SchoolTracsConst.DisplayMode.COURSE.code(), opt);

	}

	//search lesson by facility
	public List<Lesson> schLsonByRm(String name, Date fromDate, Date toDate) throws ClientProtocolException, IOException{

		logger.info("Search Lesson by Room Start");

		//without facility, product
		SearchRequest.ContentOpt opt =new SearchRequest.ContentOpt(true,true,false,true);

		return this.schLson(name, fromDate, toDate, SchoolTracsConst.DisplayMode.FACILITY.code(), opt);

	}

	public List<Lesson> schLsonByTch(String name, Date fromDate, Date toDate) throws ClientProtocolException, IOException{

		logger.info("Search Lesson by Teacher Start");

		//without product staff
		SearchRequest.ContentOpt opt =new SearchRequest.ContentOpt(true,true,false,true);

		return schLson(name, fromDate, toDate, SchoolTracsConst.DisplayMode.STAFF.code(), opt);

	}

	public List<Lesson> schLsonByStd(String name, Date fromDate, Date toDate) throws ClientProtocolException, IOException{

		logger.info("Search Lesson by Student Start");

		//without product
		SearchRequest.ContentOpt opt =new SearchRequest.ContentOpt(true,true,false,true);

		return schLson(name, fromDate, toDate, SchoolTracsConst.DisplayMode.CUSTOMER.code(), opt);

	}

	private List<Lesson> schLson(String schStr, Date fromDate, Date toDate, String displayMode, SearchRequest.ContentOpt opt) throws JsonParseException, JsonMappingException, ClientProtocolException, IOException{

		SearchResponse schRsp = jsonToSchRsp(conn.sendSchReq(schStr, fromDate, toDate, displayMode, opt)); 

		return SchoolTracsUtil.schRspToLson(schRsp);

	}

	private List<Lesson> fltLsonByRmAva(List<Lesson> lsons, HashMap<String, List<Lesson>> rdLsonHash){

		logger.info("Filter Lesson by Room Availability");

		List<Lesson> rstLsons = new ArrayList<Lesson>();

		lsons.forEach((l)->{
			if(rmHash.containsKey(l.getRoom().getId())){
				Room r = rmHash.get(l.getRoom().getId());
				Calendar cal = MasteryUtil.getPlainCal(l.getStartDateTime());
				String key = r.getId() + "_" + SchoolTracsConst.SDF_FULL.format(cal.getTime());
				logger.debug("Hash Key=" + key);
				List<Lesson> lsonsByRm = null;

				if(rdLsonHash.containsKey(key)){
					lsonsByRm = rdLsonHash.get(key);
				}else{
					try {
						lsonsByRm = this.schLsonByRm(r.getName(), cal.getTime(), cal.getTime());
						rdLsonHash.put(key, lsonsByRm);
					} catch (IOException e) {
						logger.error("Cannot get Lesson by Room");
						logger.error(e.getMessage());
						e.printStackTrace();
					}
				}

				boolean isRmFull = SchoolTracsUtil.isRmFullInPrd(r, l.getStartDateTime(), l.getEndDateTime(), lsonsByRm);
				if(!isRmFull){
					logger.info("Room is not Full");
					logger.info("Lesson is added to list" + l.toString());
					rstLsons.add(l);
				}

			}else{
				logger.error("Cannot find room with ID="+l.getRoom().getId());
			}
		});

		return rstLsons;
	}

	//room
	public Room getRmByName(String name){
		if(rmHash.containsKey(name)){
			return rmHash.get(name);
		}
		return null;
	}

	public List<Room> getRms() throws JsonParseException, JsonMappingException, ClientProtocolException, IOException{
		return jsonToRms(conn.sendFacReq());
	}

	private static SearchResponse jsonToSchRsp(String json) throws JsonParseException, JsonMappingException, IOException{

		logger.info("Json="+json);

		ObjectMapper mapper = new ObjectMapper();
		SimpleModule module = new SimpleModule();
		module.addDeserializer(SearchResponse.class, new SearchResponseDeserializer());
		mapper.registerModule(module);

		return mapper.readValue(json, SearchResponse.class);

	}
	
	private static List<Customer> jsonToCusts(String json) throws JsonParseException, JsonMappingException, IOException{

		logger.info("Json="+json);
	
		ObjectMapper mapper = new ObjectMapper();
		CollectionType type = mapper.getTypeFactory().constructCollectionType(List.class,Facility.class);
		SimpleModule module = new SimpleModule();
		module.addDeserializer(List.class, new CustomersDeserializer());
		mapper.registerModule(module);

		return mapper.readValue(json, type);

	}
	
	private static List<Room> jsonToRms(String json) throws JsonParseException, JsonMappingException, IOException{

		ObjectMapper mapper = new ObjectMapper();
		CollectionType type = mapper.getTypeFactory().constructCollectionType(List.class,Facility.class);
		SimpleModule module = new SimpleModule();
		module.addDeserializer(List.class, new FacilitiesDeserializer());
		mapper.registerModule(module);

		List<Facility> facList = mapper.readValue(json, type);

		List<Room> roomList = new ArrayList<Room>();

		for(Facility fac: facList){
			roomList.add(new Room(fac));
		}

		return roomList;

	}

	public static void main(String[] args) throws IOException{
		/*SchoolTracsAgent agent = new SchoolTracsAgent();
		agent.uname = "travisli@masteryoim";
		agent.pwd = "24643466";
		agent.init();
		Calendar stCal = MasteryUtil.getPlainCal(new Date());
		Calendar edCal = MasteryUtil.getPlainCal(new Date());
		edCal.add(Calendar.DAY_OF_MONTH, 7);
		//List<Lesson> list = agent.schLsonByStd("K2李頌圻", stCal.getTime(), edCal.getTime());
		//logger.info(list.toString());
		List<Customer> list = agent.schCustsByPhone("96841163");
		
		
		logger.info(list.toString());*/
		
		String str = "{\"9 \":{\"type\":\"Exception\",\"message\":\"Resource conflicts occurs\",\"code\":101,\"data\":[{\"date\":\"2017-07-28\",\"time\":\"11:00:00\",\"activity2\":\"\u78a9\u58eb\u82f1\u6587\u73ed (\u6bcf\u9031\u4e00\u5802)\",\"level2\":\"S1-S3\",\"activityId2\":\"416135\",\"activity1\":\"1:1 \u78a9\u58eb\u4e2d\u6587\u73ed (\u6bcf\u9031\u4e00\u5802)\",\"level1\":\"P1-P6\",\"activityId1\":\"416486\",\"name\":\"G20B(9)\",\"type\":\"F\"},{\"date\":\"2017-07-28\",\"time\":\"11:00:00\",\"activity2\":\"\u78a9\u58eb\u82f1\u6587\u73ed (\u6bcf\u9031\u4e00\u5802)\",\"level2\":\"S1-S3\",\"activityId2\":\"408008\",\"activity1\":\"1:1 \u78a9\u58eb\u4e2d\u6587\u73ed (\u6bcf\u9031\u4e00\u5802)\",\"level1\":\"P1-P6\",\"activityId1\":\"416486\",\"name\":\"G20B(9)\",\"type\":\"F\"}]}}";
		
		JsonFactory factory = new JsonFactory();
		JsonParser  parser  = factory.createParser(str);

		while(!parser.isClosed()){
		    JsonToken jsonToken = parser.nextToken();
		    parser.getCurrentName().equals("type");
		    System.out.println("p = " + parser.getText());
		    //System.out.println("jsonToken = " + jsonToken.toString());
		}
		
		/*if(list.size()>0){
			Lesson l = list.get(0);
			Calendar start = Calendar.getInstance();
			Calendar end = Calendar.getInstance();
			start.setTime(l.getStartDateTime());
			end.setTime(l.getEndDateTime());

			start.add(Calendar.HOUR, 2);
			end.add(Calendar.HOUR, 2);
			l.setStartDateTime(start.getTime());
			l.setEndDateTime(end.getTime());
			Boolean result = agent.aplyMkup(l, "813");
			logger.info(result.toString());
		}*/

	}

}
