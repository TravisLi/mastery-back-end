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

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.type.CollectionType;

import mastery.model.Lesson;
import mastery.model.Room;
import mastery.model.Teacher;
import mastery.schooltracs.model.Customer;
import mastery.schooltracs.model.Facility;
import mastery.schooltracs.model.SearchRequest;
import mastery.schooltracs.model.SearchResponse;
import mastery.schooltracs.util.FacilitiesDeserializer;
import mastery.schooltracs.util.SchoolTracsConst;
import mastery.schooltracs.util.SchoolTracsUtil;
import mastery.schooltracs.util.SearchResponseDeserializer;
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
	public Customer schCustByName(String name){
		logger.info("Search Customer by Name Start");
		List <NameValuePair> nvps = new ArrayList <NameValuePair>();
		nvps.add(new BasicNameValuePair("id", "784"));
		try {
			String result = conn.sendCustReq(nvps);
			Customer c = jsonToCust(result);
			return c;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	//make up
	public Boolean aplyMkup(Lesson l, String stdId){
		logger.info("Apply Makeup class start");

		String result;
		try {
			result = conn.sendNewMkupReq(l, stdId);
			logger.debug(result);
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
	
	private static Customer jsonToCust(String json) throws JsonParseException, JsonMappingException, IOException{

		logger.info("Json="+json);
	
		ObjectMapper mapper = new ObjectMapper();

		Customer c = mapper.readValue(mapper.readTree(json).get("data").toString(), Customer.class);

		return c;

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
		SchoolTracsAgent agent = new SchoolTracsAgent();
		agent.uname = "travisli@masteryoim";
		agent.pwd = "24643466";
		agent.init();
		Calendar stCal = MasteryUtil.getPlainCal(new Date());
		Calendar edCal = MasteryUtil.getPlainCal(new Date());
		edCal.add(Calendar.DAY_OF_MONTH, 7);
		//List<Lesson> list = agent.schLsonByStd("K2李頌圻", stCal.getTime(), edCal.getTime());
		Customer c = agent.schCustByName("K2李頌圻");
		
		
		logger.info(c.toString());

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
