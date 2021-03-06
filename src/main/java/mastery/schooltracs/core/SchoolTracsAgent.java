package mastery.schooltracs.core;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.type.CollectionType;

import mastery.model.FreeTimeslot;
import mastery.model.Journal;
import mastery.model.Lesson;
import mastery.model.Room;
import mastery.model.Staff;
import mastery.model.Teacher;
import mastery.model.WorkHour;
import mastery.schooltracs.json.deserializer.SearchResponseDeserializer;
import mastery.schooltracs.json.deserializer.StaffWorkHoursDeserializer;
import mastery.schooltracs.model.Activity;
import mastery.schooltracs.model.Customer;
import mastery.schooltracs.model.Facility;
import mastery.schooltracs.model.FacilityMap;
import mastery.schooltracs.model.IncomeReportData;
import mastery.schooltracs.model.IncomeReportResponse;
import mastery.schooltracs.model.Invoice;
import mastery.schooltracs.model.InvoiceItem;
import mastery.schooltracs.model.ListArrayReadResponse;
import mastery.schooltracs.model.ListReadResponse;
import mastery.schooltracs.model.ReadResponse;
import mastery.schooltracs.model.SearchActivityRequest;
import mastery.schooltracs.model.SearchActivityResponse;
import mastery.schooltracs.model.StStaff;
import mastery.schooltracs.model.StaffMap;
import mastery.schooltracs.model.StaffWorkHour;
import mastery.schooltracs.util.SchoolTracsConst;
import mastery.schooltracs.util.SchoolTracsUtil;
import mastery.util.MasteryUtil;
import mastery.whatsapp.WhatsappRestAgent;

@Service
public class SchoolTracsAgent {

	private static final Logger logger = LoggerFactory.getLogger(SchoolTracsAgent.class);

	private HashMap<String, Room> rmHash = new HashMap<String, Room>();

	private HashMap<String, Staff> stfHash = new HashMap<String, Staff>();

	private HashMap<String, Staff> adminHash = new HashMap<String, Staff>();

	private HashMap<String, Staff> skipHash = new HashMap<String, Staff>();
	
	public SchoolTracsAgent(SchoolTracsConn conn) throws IOException{
		this.conn = conn;
		this.conn.login();
	}
	
	@Autowired
	private SchoolTracsConn conn;

	@Autowired
	private WhatsappRestAgent wAgent;

	@Value("${schooltracs.mkup.sch.limit}")
	private Integer mkupSchLimit;

	//reload the staff and rm map for every 30 mins
	@Scheduled(fixedDelay = 180000)
	public void init(){
		this.loadRmHash();
		this.loadStfHash();
	}
	
	@Scheduled(cron = "0 0 9,12,15,18,21 * * *")
	public void healthCheck(){
		this.adminHash.forEach((k,v)->{

			wAgent.sendMsg(v.getMobile(), "daily test from backend");

		});
	}

	private void loadRmHash(){
		logger.info("Get All SchoolTracs Rooms");

		try {
			List<Room> rms = this.getRms();

			for(Room r:rms){
				if(rmHash.containsKey(r.getId())){
					rmHash.replace(r.getId(), r);
				}else{
					rmHash.put(r.getId(), r);
				}
			}

		} catch (IOException e) {
			logger.error("Load romm hash error");
			logger.error(e.getMessage());
			e.printStackTrace();
		}

	}

	public Collection<Room> getRooms() {
		return rmHash.values();
	}

	private void loadStfHash(){
		logger.info("Get All SchoolTracs Staffs");

		try {

			List<StStaff> list = digestListReadRspJson(conn.sendStfReq(), StStaff.class);

			for(StStaff s: list){
				if(!s.getDeleted()){

					Staff stf = new Staff(s);

					if(stf.getConfig().getAdminWapp()){
						if(adminHash.containsKey(stf.getId())){
							logger.info(stf.getName()+" is replaced in admin hash");
							adminHash.replace(stf.getId(), stf);
						}else{
							logger.info(stf.getName()+" is added to admin hash");
							adminHash.put(stf.getId(),stf);
						}
					}

					if(stf.getConfig().getMkupSchSkip()){
						if(skipHash.containsKey(stf.getId())){
							logger.info(stf.getName()+" is replaced in skip hash");
							skipHash.replace(stf.getId(), stf);
						}else{
							logger.info(stf.getName()+" is added to skip hash");
							skipHash.put(stf.getId(),stf);
						}
					}

					logger.debug(stf.toString());
					if(stfHash.containsKey(stf.getId())){
						logger.info(stf.getName()+" is replaced in staff hash");
						stfHash.replace(stf.getId(), stf);
					}else{
						logger.info(stf.getName()+" is added to staff hash");
						stfHash.put(stf.getId(), stf);
					}
				}	
			}		

		} catch (IOException e) {
			logger.error("Load staff hash error");
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}


	private Staff getStfFromHash(String stfId, String stfName){
		if(stfHash.containsKey(stfId)){
			return stfHash.get(stfId);
		}else{
			try {
				//as the search by id will throw SQL exception
				//replace by search name here 4 Apr 2018
				//StStaff st = this.schStfsById(stfId);
				StStaff st = this.schStfsByName(stfName);
				if(st!=null){
					Staff s = new Staff(st);
					stfHash.put(s.getId(), s);
					return s;
				}
			} catch (Exception e) {
				logger.error(e.getMessage());
				e.printStackTrace();
			}
		}
		return null;
	}

	public Invoice schInvByInvNo(String invNo) throws Exception{
		logger.info("Search Invoice by Invoice No Start");
		logger.info("Invoice no = " + invNo);
		
		List <NameValuePair> nvps = new ArrayList <NameValuePair>();
		nvps.add(new BasicNameValuePair("start", "0"));
		nvps.add(new BasicNameValuePair("limit", "50"));
		nvps.add(new BasicNameValuePair("centerId", "1"));
		nvps.add(new BasicNameValuePair("loadType", "all"));
		nvps.add(new BasicNameValuePair("sort", "created"));
		nvps.add(new BasicNameValuePair("dir", "DESC"));
		nvps.add(new BasicNameValuePair("filter[0][field]", "number"));
		nvps.add(new BasicNameValuePair("filter[0][data][type]", "string"));
		nvps.add(new BasicNameValuePair("filter[0][data][value]", invNo));
		
		try {

			List<Invoice> list = digestListArrayReadRspJson(conn.sendInvReq(nvps), Invoice.class);

			if(list.size()==1){
				return list.get(0);
			}else if(list.size()>1){
				throw new Exception("More than one invoice share the same name");
			}

		} catch (IOException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return null;
		
	}
	
	public List<InvoiceItem> schInvItmByInvId(String invId) throws Exception{
		logger.info("Search Invoice Item by Invoice Id Start");
		logger.info("Invoice id = " + invId);
		
		List<InvoiceItem> list = new ArrayList<InvoiceItem>();
		
		List <NameValuePair> nvps = new ArrayList <NameValuePair>();
		nvps.add(new BasicNameValuePair("invoiceId", invId));
		
		try {

			list = digestListReadRspJson(conn.sendInvItmReq(nvps), InvoiceItem.class);

		} catch (IOException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return list;
		
	}
	
	private StStaff schStfsByName(String name) throws Exception{
		logger.info("Search Staff by Name Start");
		logger.info("name = " + name);

		List <NameValuePair> nvps = new ArrayList <NameValuePair>();
		nvps.add(new BasicNameValuePair("filter[0][field]", "name"));
		nvps.add(new BasicNameValuePair("filter[0][data][type]", "string"));
		nvps.add(new BasicNameValuePair("filter[0][data][value]", name));
		nvps.add(new BasicNameValuePair("centerId", SchoolTracsConst.OIM_CENTRE_ID));
		nvps.add(new BasicNameValuePair("start", "0"));

		try {

			List<StStaff> list = digestListReadRspJson(conn.sendStfReq(nvps), StStaff.class);

			if(list.size()==1){
				return list.get(0);
			}else if(list.size()>1){
				throw new Exception("More than one staff share the same name");
			}

		} catch (IOException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	//Activity
	private List<FacilityMap> schActFacById(String actId){
		logger.info("Search Activities Facility by Id");
		logger.info("actId = " + actId);
		try {

			return digestListReadRspJson(conn.sendActFacReq(actId), FacilityMap.class);

		} catch (IOException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}

		return null;

	}

	private List<StaffMap> schActStfById(String actId){
		logger.info("Search Activities Staff by Id");
		logger.info("actId = " + actId);
		try {

			return digestListReadRspJson(conn.sendActStfReq(actId), StaffMap.class);

		} catch (IOException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}

		return null;

	}

	private Activity schActById(String actId){
		logger.info("Search Activities by Id");
		logger.info("actId = " + actId);

		try {
			return digestReadRspJson(conn.sendActReq(actId), Activity.class);			
		} catch (IOException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}

		return null;
	}

	//customer
	public List<Customer> schCustsByPhone(String phone){
		logger.info("Search Customer by Phone Start");
		logger.info("phone = " + phone);

		List <NameValuePair> nvps = new ArrayList <NameValuePair>();
		nvps.add(new BasicNameValuePair("filter[0][field]", "phone"));
		nvps.add(new BasicNameValuePair("filter[0][data][type]", "string"));
		nvps.add(new BasicNameValuePair("filter[0][data][comparison]", "eq"));
		nvps.add(new BasicNameValuePair("filter[0][data][value]", phone));
		nvps.add(new BasicNameValuePair("centerId", SchoolTracsConst.OIM_CENTRE_ID));
		nvps.add(new BasicNameValuePair("deleted", "0"));
		nvps.add(new BasicNameValuePair("start", "0"));

		try {
			return digestListReadRspJson(conn.sendCustReq(nvps), Customer.class);
		} catch (IOException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}

		return new ArrayList<Customer>();

	}

	public List<Customer> schCustsByPhoneAndName(String phone, String name){
		logger.info("Search Customer by Phone and Name Start");
		logger.info("phone = " + phone);
		logger.info("name = " + name);

		List <NameValuePair> nvps = new ArrayList <NameValuePair>();
		nvps.add(new BasicNameValuePair("filter[0][field]", "phone"));
		nvps.add(new BasicNameValuePair("filter[0][data][type]", "string"));
		nvps.add(new BasicNameValuePair("filter[0][data][comparison]", "eq"));
		nvps.add(new BasicNameValuePair("filter[0][data][value]", phone));
		nvps.add(new BasicNameValuePair("filter[1][field]", "name"));
		nvps.add(new BasicNameValuePair("filter[1][data][type]", "string"));
		nvps.add(new BasicNameValuePair("filter[1][data][value]", name));
		nvps.add(new BasicNameValuePair("centerId", SchoolTracsConst.OIM_CENTRE_ID));
		nvps.add(new BasicNameValuePair("deleted", "0"));
		nvps.add(new BasicNameValuePair("start", "0"));

		try {
			return digestListReadRspJson(conn.sendCustReq(nvps), Customer.class);
		} catch (IOException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}

		return new ArrayList<Customer>();

	}

	//customer
	public List<Customer> schCustsByPhoneAndBarcode(String phone, String barcode){
		logger.info("Search Customer by Phone and Barcode Start");
		logger.info("phone = " + phone);
		logger.info("barcode = " + barcode);

		List <NameValuePair> nvps = new ArrayList <NameValuePair>();
		nvps.add(new BasicNameValuePair("filter[0][field]", "phone"));
		nvps.add(new BasicNameValuePair("filter[0][data][type]", "string"));
		nvps.add(new BasicNameValuePair("filter[0][data][comparison]", "eq"));
		nvps.add(new BasicNameValuePair("filter[0][data][value]", phone));
		nvps.add(new BasicNameValuePair("filter[1][field]", "barcode"));
		nvps.add(new BasicNameValuePair("filter[1][data][type]", "string"));
		nvps.add(new BasicNameValuePair("filter[1][data][comparison]", "eq"));
		nvps.add(new BasicNameValuePair("filter[1][data][value]", barcode));
		nvps.add(new BasicNameValuePair("centerId", SchoolTracsConst.OIM_CENTRE_ID));
		nvps.add(new BasicNameValuePair("deleted", "0"));
		nvps.add(new BasicNameValuePair("start", "0"));

		try {
			return digestListReadRspJson(conn.sendCustReq(nvps), Customer.class);
		} catch (IOException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}

		return new ArrayList<Customer>();

	}
	
	public List<Customer> schCustsByContact1Phone(String phone){
		logger.info("Search Customer by Contact1Phone Start");
		logger.info("phone = " + phone);

		List <NameValuePair> nvps = new ArrayList <NameValuePair>();
		nvps.add(new BasicNameValuePair("filter[0][field]", "contact1Phone"));
		nvps.add(new BasicNameValuePair("filter[0][data][type]", "string"));
		nvps.add(new BasicNameValuePair("filter[0][data][comparison]", "eq"));
		nvps.add(new BasicNameValuePair("filter[0][data][value]", phone));
		nvps.add(new BasicNameValuePair("centerId", SchoolTracsConst.OIM_CENTRE_ID));
		nvps.add(new BasicNameValuePair("deleted", "0"));
		nvps.add(new BasicNameValuePair("start", "0"));

		try {
			return digestListReadRspJson(conn.sendCustReq(nvps), Customer.class);
		} catch (IOException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}

		return new ArrayList<Customer>();

	}

	public Customer schCustsById(String id) throws Exception{
		logger.info("Search Customer by Id Start");
		logger.info("id = " + id);

		List <NameValuePair> nvps = new ArrayList <NameValuePair>();
		nvps.add(new BasicNameValuePair("filter[0][field]", "id"));
		nvps.add(new BasicNameValuePair("filter[0][data][type]", "numeric"));
		nvps.add(new BasicNameValuePair("filter[0][data][comparison]", "eq"));
		nvps.add(new BasicNameValuePair("filter[0][data][value]", id));
		nvps.add(new BasicNameValuePair("centerId", SchoolTracsConst.OIM_CENTRE_ID));
		nvps.add(new BasicNameValuePair("start", "0"));

		try {

			List<Customer> list = digestListReadRspJson(conn.sendCustReq(nvps), Customer.class);

			if(list.size()==1){
				return list.get(0);
			}else if(list.size()>1){
				throw new Exception("More than one staff share the same id");
			}

		} catch (IOException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	public Boolean updateCustInfo(Customer cust){
		logger.info("Update Customer Info");
		try {
			return this.processCustUpdRsp(conn.sendCustUpdReq(cust));
		} catch (IOException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return false;
	}

	//teacher
	private HashMap<Integer, WorkHour> getTchWkhr(String tchId){
		logger.info("Get Teacher Working Hour");
		logger.info("tchId = " + tchId);
		HashMap<Integer, WorkHour> map = new HashMap<Integer, WorkHour>();

		try{

			map = SchoolTracsUtil.stfWkHrToMap(jsonToStfWkHrs(conn.sendStfWkhrReq(tchId)));

		}catch (IOException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}

		return map;
	}

	//service
	public Boolean updateUserPwd(String custId, String oldPwd, String newPwd){

		Customer cust;
		try {
			cust = this.schCustsById(custId);
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
			return false;
		}

		if(!cust.getBarCode().equals(oldPwd)){
			logger.info("Entered old pwd is not equal to DB");
			return false;
		}

		//search if any customer using the same phone with the same password which the user want to update
		List<Customer> custs = this.schCustsByPhoneAndBarcode(cust.getPhone(), newPwd);

		for(Customer c:custs){
			logger.info("Pwd collide with customer " + c.getName());
			return false;
		}

		Customer dummy = new Customer();
		dummy.setId(cust.getId());
		dummy.setBarCode(newPwd);

		Boolean result = this.updateCustInfo(dummy);

		if(result){
			Runnable msgTask = new Runnable(){

				@Override
				public void run() {

					wAgent.sendChgPwdMsg(cust.getName(), cust.getMobile());

				}
			};

			Thread t = new Thread(msgTask);
			t.start();
		}

		return result;
	}


	//update the customer check with phone and loose name
	public Boolean activate(String stdName, String phone, String mobile){

		List<Customer> custs = schCustsByPhoneAndName(phone,stdName);

		if(custs.isEmpty()){
			logger.info("cannot find customer by phone");
			return false;
		}

		if(custs.size()>1){
			logger.info("More than one customer is found");
			return false;
		}

		Customer cust = custs.get(0);

		if(StringUtils.isNotEmpty(cust.getBarCode())){
			logger.info("service already opened");
			return false;
		}

		String pw = MasteryUtil.pwGen();

		Customer c = new Customer();
		c.setId(cust.getId());
		c.setMobile(mobile);
		c.setBarCode(pw);

		Boolean result = this.updateCustInfo(c);

		if(result){
			Runnable msgTask = new Runnable(){

				@Override
				public void run() {

					wAgent.sendActivateMsg(stdName, mobile, pw);

				}
			};

			Thread t = new Thread(msgTask);
			t.start();
		}

		return result;
	}

	//make up
	public Boolean aplyNewMkup(String stdId, String frLsonId, Lesson toLson){
		logger.info("Apply New Makeup class start");

		try {

			String result = conn.sendNewMkupReq(toLson, stdId, false);
			logger.debug(result);
			if(result.contains("Exception")){
				String msg = this.processExptMsg(result);
				if(msg.equals("Resource conflicts occurs")){
					result = conn.sendNewMkupReq(toLson, stdId, true);
					logger.debug(result);
				}
			}

			if(result.contains("Exception")){
				return false;
			}

			Runnable msgTask = new Runnable(){

				@Override
				public void run() {
					sendWhatsappMsg(stdId, frLsonId, toLson);
				}
			};

			Thread t = new Thread(msgTask);
			t.start();

		} catch (IOException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public Boolean aplyExtMkup(String stdId, String stdLsonId, String frLsonId, Lesson toLson){
		logger.info("Apply Existing Makeup class start");

		try {

			String result = conn.sendExtMkupReq(toLson.getId(), stdLsonId);
			logger.debug(result);

			if(result.contains("Exception")){
				return false;
			}

			Runnable msgTask = new Runnable(){

				@Override
				public void run() {
					sendWhatsappMsg(stdId, frLsonId, toLson);
				}
			};

			Thread t = new Thread(msgTask);
			t.start();

		} catch (IOException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public List<Lesson> schMkup(Lesson src, String stdName){
		logger.info("Search For Makeup Class Start");

		List<Lesson> rstLsons= new ArrayList<Lesson>();

		if(skipHash.containsKey(src.getTeacher().getId())){
			return rstLsons;
		}

		//room-date Hash
		HashMap<String, List<Lesson>> rdLsonHash = new HashMap<String, List<Lesson>>();

		Date currentDateTime = new Date();

		Calendar todayCal = MasteryUtil.getPlainCal(currentDateTime);

		Calendar stCal = MasteryUtil.getPlainCal(src.getStartDateTime());

		Long dayBtwTodayLsonDay = TimeUnit.MILLISECONDS.toDays(Math.abs(stCal.getTimeInMillis()-todayCal.getTimeInMillis()));

		logger.debug("Day between today and lesson date = " + dayBtwTodayLsonDay);

		if(dayBtwTodayLsonDay==0){
			stCal.add(Calendar.DAY_OF_MONTH, 1);
		}else if(dayBtwTodayLsonDay>6){
			stCal.add(Calendar.DAY_OF_MONTH, -6);
		}else{
			stCal.add(Calendar.DAY_OF_MONTH, 0-dayBtwTodayLsonDay.intValue());
		}

		Calendar edCal = MasteryUtil.getPlainCal(src.getStartDateTime());
		edCal.add(Calendar.DAY_OF_MONTH, 6);

		Date stDate = stCal.getTime();

		//prevent the lesson with start time early then current date time shown
		if(stCal.equals(todayCal)){
			stDate = currentDateTime;
		}

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
				if(rstLsons.size()==this.mkupSchLimit){
					return rstLsons;
				}
			}

			//find the lesson with gap 1
			List<Lesson> g1Lsons = SchoolTracsUtil.findSimLvlLsonOfTch(src, stdName, 1, tLsons);

			logger.info("g1Lsons count="+g1Lsons.size());

			for(Lesson l: fltLsonByRmAva(g1Lsons, rdLsonHash)){
				rstLsons.add(l);
				if(rstLsons.size()==this.mkupSchLimit){
					return rstLsons;
				}
			}

			//find free time of teacher later
			HashMap<Integer, WorkHour> wkhrMap = this.getTchWkhr(t.getId());
			HashMap<Date,List<Lesson>> dateLsonMap = SchoolTracsUtil.lsonsToDateMap(tLsons);

			Calendar runCal = Calendar.getInstance();
			runCal.setTime(stCal.getTime());

			logger.debug("runCal=" + SchoolTracsConst.SDF_FULL.format(runCal.getTime()));
			logger.debug("edCal=" + SchoolTracsConst.SDF_FULL.format(edCal.getTime()));

			while(runCal.before(edCal)||runCal.equals(edCal)){

				List<FreeTimeslot> ftss = new ArrayList<FreeTimeslot>();

				Date runDate = runCal.getTime();
				Integer weekDay = runCal.get(Calendar.DAY_OF_WEEK) - 1;
				logger.debug("weekDay=" + weekDay);
				if(wkhrMap.containsKey(weekDay)){
					WorkHour wkhr = wkhrMap.get(weekDay);
					if(dateLsonMap.containsKey(runDate)){
						List<Lesson> lsons = dateLsonMap.get(runDate);
						ftss = SchoolTracsUtil.findFreeTimeSlotPerDate(lsons, wkhr, runDate);
						for(FreeTimeslot f: ftss){
							logger.debug("freetimeslot duration=" + f.duration());
							logger.debug("freetimeslot start=" + SchoolTracsConst.SDF_FULL.format(f.getStartDateTime()));
							logger.debug("freetimeslot end=" + SchoolTracsConst.SDF_FULL.format(f.getEndDateTime()));
							logger.debug("src duration=" + src.duration());
							if(f.duration()>=src.duration()){
								Lesson l = new Lesson(src);
								l.setStartDateTime(f.getStartDateTime());

								Calendar cal = Calendar.getInstance();
								cal.setTime(f.getStartDateTime());
								cal.add(Calendar.MINUTE, src.duration());

								l.setEndDateTime(cal.getTime());

								try {
									if(!isRmFull(l, rdLsonHash)){
										rstLsons.add(l);
										if(rstLsons.size()==this.mkupSchLimit){
											return rstLsons;
										}
									}
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}
					}
				}

				runCal.add(Calendar.DAY_OF_MONTH, 1);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return rstLsons;
	}

	//lesson
	public List<Lesson> schLsonByRm(String name, Date fromDate, Date toDate) throws ClientProtocolException, IOException{

		logger.info("Search Lesson by Room Start");

		//without product
		SearchActivityRequest.ContentOpt opt =new SearchActivityRequest.ContentOpt(true,true,false,true);

		return this.schLson(name, fromDate, toDate, SchoolTracsConst.DisplayMode.FACILITY.code(), opt);

	}

	public List<Lesson> schLsonByTch(String name, Date fromDate, Date toDate) throws ClientProtocolException, IOException{

		logger.info("Search Lesson by Teacher Start");

		//without product
		SearchActivityRequest.ContentOpt opt =new SearchActivityRequest.ContentOpt(true,true,false,true);

		return schLson(name, fromDate, toDate, SchoolTracsConst.DisplayMode.STAFF.code(), opt);

	}

	public List<Lesson> schLsonByStd(String name, Date fromDate, Date toDate) throws ClientProtocolException, IOException{

		logger.info("Search Lesson by Student Start");

		//without product
		SearchActivityRequest.ContentOpt opt =new SearchActivityRequest.ContentOpt(true,true,false,true);

		return schLson(name, fromDate, toDate, SchoolTracsConst.DisplayMode.CUSTOMER.code(), opt);

	}
	
	public List<Journal> getJournalByStdId(String stdId, Integer index) throws ClientProtocolException, IOException{
		logger.info("Get Journal by Student Id start");
		return digestListReadRspJson(conn.sendJournalReq(stdId, index) , Journal.class);
	}

	private Lesson getLsonById(String id){

		logger.info("Get Lesson by Id Start");
		logger.info("id = " + id);

		Activity act = this.schActById(id);

		if(act==null){
			logger.error("Cannot find from lesson with id=" +id);
			return null;
		}

		Lesson frLson;

		try {
			frLson = new Lesson(act);
		} catch (ParseException e) {
			logger.error(e.getMessage());
			return null;
		}

		//assume only one staff will be found
		List<StaffMap> stfs = this.schActStfById(act.getId());

		if(stfs.isEmpty() || stfs.size() > 1){
			logger.error("More than one or no staff is found");
			return null;
		}

		StaffMap sMap = stfs.get(0);

		//assume only one facility will be found
		List<FacilityMap> facs = this.schActFacById(act.getId());

		if(facs.isEmpty() || facs.size() > 1){
			logger.error("More than one or no facilty is found");
			return null;
		}

		FacilityMap f = facs.get(0);

		frLson.setTeacher(new Teacher(sMap));
		frLson.setRoom(new Room(f));

		return frLson;

	}

	private List<Lesson> schLson(String schStr, Date fromDate, Date toDate, String displayMode, SearchActivityRequest.ContentOpt opt) throws JsonParseException, JsonMappingException, ClientProtocolException, IOException{

		SearchActivityResponse schRsp = jsonToSchRsp(conn.sendSchReq(schStr, fromDate, toDate, displayMode, opt)); 

		return SchoolTracsUtil.schRspToLson(schRsp);

	}

	private List<Lesson> fltLsonByRmAva(List<Lesson> lsons, HashMap<String, List<Lesson>> rdLsonHash){

		logger.info("Filter Lesson by Room Availability");

		List<Lesson> rstLsons = new ArrayList<Lesson>();

		lsons.forEach((l)->{

			try {
				if(!isRmFull(l, rdLsonHash)){
					logger.info("Room is not Full");
					logger.info("Lesson is added to list" + l.toString());
					rstLsons.add(l);
				}
			} catch (Exception e) {
				logger.error(e.getMessage());
				e.printStackTrace();
			}

		});

		return rstLsons;
	}

	//room
	private List<Room> getRms() throws JsonParseException, JsonMappingException, ClientProtocolException, IOException{
		return jsonToRms(conn.sendFacReq());
	}

	private Boolean isRmFull(Lesson l, HashMap<String, List<Lesson>> rdLsonHash) throws Exception{
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

			return SchoolTracsUtil.isRmFullInPrd(r, l.getStartDateTime(), l.getEndDateTime(), lsonsByRm);

		}else{
			throw new Exception("Cannot find room with ID="+l.getRoom().getId());
		}
	}

	//Whatsapp
	private void sendWhatsappMsg(String stdId, String frLsonId, Lesson toLson){
		logger.info("Send Whatsapp message start");

		logger.debug("student id=" + stdId);
		logger.debug("frLson id=" + frLsonId);
		logger.debug(toLson.toString());

		Lesson frLson = this.getLsonById(frLsonId);

		if(frLson == null){
			logger.error("From Lesson cannot be found");
			return;
		}

		Customer c;
		try {
			c = this.schCustsById(stdId);
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
			return;
		}

		Staff s = this.getStfFromHash(frLson.getTeacher().getId(), frLson.getTeacher().getName());

		wAgent.sendMkupStdMsg(c, frLson, toLson);

		if(s!=null){
			wAgent.sendMkupTchMsg(s, c.getName(), frLson, toLson);
		}else{
			logger.error("Cannot find teacher = " + s + " teacher whatsapp will not be send");
		}

		this.adminHash.forEach((k,v)->{

			wAgent.sendMkupAdmMsg(v, c.getName(), frLson, toLson);

		});
	}

	public List<IncomeReportData> getIncomeReport(String centreId, String fromDate, String toDate) throws JsonParseException, JsonMappingException, ClientProtocolException, IOException{
		
		return jsonToIncomeRptRsp(conn.sendIncomeRptReq(centreId, fromDate, toDate));
		
	}
	
	
	//aux
	
	private static List<IncomeReportData> jsonToIncomeRptRsp(String json) throws JsonParseException, JsonMappingException, IOException{

		logger.info("Json="+json);
		
		return digestIncomeRptRspJson(processIncomeRptRsp(json), IncomeReportData.class);
		
	}
	
	private static SearchActivityResponse jsonToSchRsp(String json) throws JsonParseException, JsonMappingException, IOException{

		logger.info("Json="+json);

		ObjectMapper mapper = new ObjectMapper();
		SimpleModule module = new SimpleModule();
		module.addDeserializer(SearchActivityResponse.class, new SearchResponseDeserializer());
		mapper.registerModule(module);

		return mapper.readValue(json, SearchActivityResponse.class);

	}

	private static List<Room> jsonToRms(String json) throws JsonParseException, JsonMappingException, IOException{

		List<Facility> facList = digestListReadRspJson(json, Facility.class);

		List<Room> roomList = new ArrayList<Room>();

		for(Facility fac: facList){
			roomList.add(new Room(fac));
		}

		return roomList;

	}
	
	private static String processIncomeRptRsp(String json){

		String rspNo = "";
		String dataJson = "";
		
		try {
			JsonFactory factory = new JsonFactory();
			JsonParser parser  = factory.createParser(json);

			while(!parser.isClosed()){
				JsonToken t = parser.nextToken();
				if(JsonToken.FIELD_NAME.equals(t)){
					String f = parser.getCurrentName();
					if(f.matches("^[0-9]+ $")){
						rspNo = f;
						break;
					}
				}
			}
			
			ObjectMapper mapper = new ObjectMapper();
			JsonNode root = mapper.readTree(json);
			
			if(root.isObject()){
				dataJson = root.get(rspNo).toString();
				logger.info(dataJson);
			}
					
		} catch (IOException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}

		return dataJson;
	}
	
	private static <T> List<T> digestIncomeRptRspJson(String json, Class<T> c){
		ObjectMapper mapper = new ObjectMapper();
		JavaType type = mapper.getTypeFactory().constructParametricType(IncomeReportResponse.class, c);
		try {
			logger.debug("json="+json);
			IncomeReportResponse<T> r = mapper.readValue(json, type);
			if(r!=null){
				return r.getData();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return new ArrayList<T>();
	}
	
	private static <T> T digestReadRspJson(String json, Class<T> c){
		ObjectMapper mapper = new ObjectMapper();
		JavaType type = mapper.getTypeFactory().constructParametricType(ReadResponse.class, c);
		try {
			logger.debug("json="+json);
			ReadResponse<T> r = mapper.readValue(json, type);
			if(r.getSuccess()){
				return r.getData();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	private static <T> List<T> digestListReadRspJson(String json, Class<T> c){
		ObjectMapper mapper = new ObjectMapper();
		JavaType type = mapper.getTypeFactory().constructParametricType(ListReadResponse.class, c);
		try {
			logger.debug("json="+json);
			ListReadResponse<T> r = mapper.readValue(json, type);
			if(r.getSuccess()){
				return r.getData();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return new ArrayList<T>();
	}
	
	private static <T> List<T> digestListArrayReadRspJson(String json, Class<T> c){
		ObjectMapper mapper = new ObjectMapper();
		JavaType type = mapper.getTypeFactory().constructParametricType(ListArrayReadResponse.class, c);
		try {
			logger.debug("json="+json);
			ListArrayReadResponse<T> r = mapper.readValue(json, type);
			if(r.getSuccess()){
				return r.getData();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return new ArrayList<T>();
	}

	private static List<StaffWorkHour> jsonToStfWkHrs(String json) throws JsonParseException, JsonMappingException, IOException{

		logger.info("Json="+json);

		ObjectMapper mapper = new ObjectMapper();
		CollectionType type = mapper.getTypeFactory().constructCollectionType(List.class, StaffWorkHour.class);
		SimpleModule module = new SimpleModule();
		module.addDeserializer(List.class, new StaffWorkHoursDeserializer());
		mapper.registerModule(module);

		return mapper.readValue(json, type);

	}

	private Boolean processCustUpdRsp(String json){

		Boolean result = false;

		try {
			JsonFactory factory = new JsonFactory();
			JsonParser parser  = factory.createParser(json);

			while(!parser.isClosed()){
				JsonToken t = parser.nextToken();
				if(JsonToken.FIELD_NAME.equals(t)){
					String f = parser.getCurrentName();
					if(f.equals("success")){
						t = parser.nextToken();
						if(JsonToken.VALUE_TRUE.equals(t)||JsonToken.VALUE_FALSE.equals(t)){
							result = parser.getBooleanValue();
						}
					}
				}
			}
		} catch (IOException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}

		return result;
	}

	/*{
	 * "9 ":
	 * {"type":"Exception",
	 * "message":"Resource conflicts occurs",
	 * "code":101,
	 * "data":[
	 * {"date":"2017-07-28",
	 * "time":"11:00:00",
	 * "activity2":"\u78a9\u58eb\u82f1\u6587\u73ed (\u6bcf\u9031\u4e00\u5802)\",
	 * "level2":"S1-S3","activityId2":"416135","activity1":
	 * "1:1 \u78a9\u58eb\u4e2d\u6587\u73ed (\u6bcf\u9031\u4e00\u5802)\",
	 * \"level1\":\"P1-P6\",
	 * }]}}";*/

	private String processExptMsg(String json){

		try {
			JsonFactory factory = new JsonFactory();
			JsonParser parser  = factory.createParser(json);

			while(!parser.isClosed()){
				JsonToken t = parser.nextToken();
				if(JsonToken.FIELD_NAME.equals(t)){
					String f = parser.getCurrentName();
					if(f.equals("message")){
						t = parser.nextToken();
						if(JsonToken.VALUE_STRING.equals(t)){
							return parser.getValueAsString();
						}
					}
				}
			}
		} catch (IOException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}

		return "";
	}

}
