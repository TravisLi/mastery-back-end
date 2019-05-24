package mastery.schooltracs.util;

import java.text.SimpleDateFormat;

public class SchoolTracsConst {
	
	public static final String HTTPS = "https://";
	public static final String HOST = "my.schooltracs.com";
	public static final String HOST_URL = HTTPS + HOST;
	
	public static final String USER_AGENT = "Apache-HttpClient/4.1.1 (java 1.5)";
	public static final String ACCEPT_ENCODING = "gzip,deflate";
	public static final String CONNECTION = "Keep-Alive";
	public static final String CONTENT_TYPE_JSON = "application/json";
	public static final String CONTENT_TYPE_FORM = "application/x-www-form-urlencoded; charset=UTF-8";
	
	public static final String LOGIN_URL = HOST_URL + "/app/login";
	public static final String REST_URL = HOST_URL + "/masteryoim/app";
	public static final String TASK_REQ_URL = REST_URL + "/?v=&c=1";
	public static final String FAC_REQ_URL = REST_URL + "/facility/read";
	public static final String CUST_URL = REST_URL + "/customer";
	public static final String CUST_REQ_URL = CUST_URL + "/read";
	public static final String CUST_UPD_URL = CUST_URL + "/update";
	public static final String ACT_URL = REST_URL + "/activity";
	public static final String ACT_REQ_URL = ACT_URL + "/read";
	public static final String ACT_STAFF_URL = REST_URL + "/activityStaff";
	public static final String ACT_STAFF_REQ_URL = ACT_STAFF_URL + "/read";
	public static final String ACT_FAC_URL = REST_URL + "/activityFacility";
	public static final String ACT_FAC_REQ_URL = ACT_FAC_URL + "/read";
	public static final String STAFF_URL = REST_URL + "/staff";
	public static final String STAFF_REQ_URL = STAFF_URL + "/read";
	public static final String JOURNAL_URL = REST_URL + "/journal";
	public static final String JOURNAL_REQ_URL = JOURNAL_URL + "/read";
	
	public static final String OIM_CENTRE_ID = "2";
	public static final String HMTP_CENTRE_ID = "3";
			
	public static final Integer JOURNAL_PER_INDEX = 10;
	
	public static final SimpleDateFormat SDF_FULL = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static final SimpleDateFormat SDF_DATE = new SimpleDateFormat("yyyy-MM-dd");
	public static final SimpleDateFormat SDF_TIME = new SimpleDateFormat("HH:mm");
	
	public enum Task{
		
		EXIST_MAKE_UP("ActivityCustomer.makeup"),
		INCOME_RPT("Income_Report.getData"),
		NEW_MAKE_UP("ActivityCustomer.newMakeup"),
		SEARCH_ACTIVITY("Activity.searchActivities"),
		STAFF_WORK_HR("Staff.getWorkingHour");
		
		private String code;
		
		Task(String code){
			this.code = code;
		}
		
		public String code(){
			return code;
		}
	}
	
	public enum DisplayMode{
		COURSE("c"),
		STAFF("t"),
		CUSTOMER("s"),
		FACILITY("r"),
		PRODUCT("p");
		
		private String code;
		
		DisplayMode(String code){
			this.code = code;
		}
		
		public String code(){
			return code;
		}
	}
	
	public enum Timeslot{
		ALL("all"),
		OCCUPIED("occupied");
		
		private String code;
		
		Timeslot(String code){
			this.code = code;
		}
		
		public String code(){
			return code;
		}
	}
	
	public enum Level{
		NONE(-1),
		P1(0),
		P2(1),
		P3(2),
		P4(3),
		P5(4),
		P6(5),
		S1(6),
		S2(7),
		S3(8),
		S4(9),
		S5(10),
		S6(11);
		
		private int code;
		
		Level(int code){
			this.code = code;
		}
		
		public int code(){
			return code;
		}
		
	}
}
