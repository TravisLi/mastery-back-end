package mastery.schooltracs.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Date;

/*{"6 ":
 * ["Activity.searchActivities",
 * "2",
 * {"customer":true,"facility":true,"staff":true,"product":true},
 * "s",
 * ["K2�Ӯa"],
 * "2017-05-25 00:00:00",
 * "2017-05-25 00:00:00",
 * "",
 * 4,
 * 50,
 * "2",
 * {}]}
 * */

@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchActivityRequest {

	private Integer reqSeq;
	private String task;
	private String unknownStr1;
	private ContentOpt contentOpt;
	private String displayMode;
	//private String unknownStr2;
	private String searchStr;
	private Date fromDate;
	private Date toDate;
	private String timeslot;
	private Integer unknownInt1;
	private Integer unknownInt2;
	private String unknownStr3;
	private Object unknownObj; 
	
	public SearchActivityRequest(){
		
		unknownStr1 = "2";
		contentOpt = new ContentOpt();
		//unknownStr2 = "";
		unknownInt1 = 4;
		unknownInt2 = 10000;	// possible is maximum number of record to scan
		unknownStr3 = "2";
		unknownObj = new Object();
		
	}
	
	public SearchActivityRequest(Integer reqSeq, String task, String displayMode, String searchStr, 
			Date fromDate, Date toDate, String timeslot, ContentOpt contentOpt){
		
		this();
		
		this.reqSeq = reqSeq;
		this.task = task;
		this.displayMode = displayMode;
		this.searchStr = searchStr;
		this.fromDate = fromDate;
		this.toDate = toDate;
		this.timeslot = timeslot;
		this.contentOpt = contentOpt;
		
	}
	
	public static class ContentOpt{
		
		private Boolean customer;
		private Boolean facility;
		private Boolean product;
		private Boolean staff;
		
		public ContentOpt(){
			customer=true;
			facility=true;
			product=true;
			staff=true;
		}
		
		public ContentOpt(Boolean customer, Boolean facility, Boolean product, Boolean staff) {
			super();
			this.customer = customer;
			this.facility = facility;
			this.product = product;
			this.staff = staff;
		}
		
		public Boolean getCustomer() {
			return customer;
		}
		public void setCustomer(Boolean customer) {
			this.customer = customer;
		}
		public Boolean getFacility() {
			return facility;
		}
		public void setFacility(Boolean facility) {
			this.facility = facility;
		}
		public Boolean getProduct() {
			return product;
		}
		public void setProduct(Boolean product) {
			this.product = product;
		}
		public Boolean getStaff() {
			return staff;
		}
		public void setStaff(Boolean staff) {
			this.staff = staff;
		}
		
	}
	
	public Integer getReqSeq() {
		return reqSeq;
	}

	public void setReqSeq(Integer reqSeq) {
		this.reqSeq = reqSeq;
	}

	public String getTask() {
		return task;
	}

	public void setTask(String task) {
		this.task = task;
	}

	public String getUnknownStr1() {
		return unknownStr1;
	}

	public void setUnknownStr1(String unknownStr1) {
		this.unknownStr1 = unknownStr1;
	}

	public ContentOpt getContentOpt() {
		return contentOpt;
	}

	public void setContentOpt(ContentOpt contentOpt) {
		this.contentOpt = contentOpt;
	}

	public String getDisplayMode() {
		return displayMode;
	}

	public void setDisplayMode(String displayMode) {
		this.displayMode = displayMode;
	}

	/*public String getUnknownStr2() {
		return unknownStr2;
	}

	public void setUnknownStr2(String unknownStr2) {
		this.unknownStr2 = unknownStr2;
	}
*/
	public String getSearchStr() {
		return searchStr;
	}

	public void setSearchStr(String searchStr) {
		this.searchStr = searchStr;
	}

	public Date getFromDate() {
		return fromDate;
	}
	
	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public String getTimeslot() {
		return timeslot;
	}

	public void setTimeslot(String timeslot) {
		this.timeslot = timeslot;
	}

	public Integer getUnknownInt1() {
		return unknownInt1;
	}

	public void setUnknownInt1(Integer unknownInt1) {
		this.unknownInt1 = unknownInt1;
	}

	public Integer getUnknownInt2() {
		return unknownInt2;
	}

	public void setUnknownInt2(Integer unknownInt2) {
		this.unknownInt2 = unknownInt2;
	}

	public String getUnknownStr3() {
		return unknownStr3;
	}

	public void setUnknownStr3(String unknownStr3) {
		this.unknownStr3 = unknownStr3;
	}

	public Object getUnknownObj() {
		return unknownObj;
	}

	public void setUnknownObj(Object unknownObj) {
		this.unknownObj = unknownObj;
	}
}
