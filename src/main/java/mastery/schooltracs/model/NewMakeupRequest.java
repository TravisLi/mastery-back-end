package mastery.schooltracs.model;

import java.util.Date;

import mastery.model.Lesson;
import mastery.schooltracs.util.SchoolTracsConst;

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

public class NewMakeupRequest {

	private Integer reqSeq;
	private String task;
	private Date startTime;
	private Date endTime;
	private String staffId;
	private String facilityId;
	private String productId;
	private String id;
	private String centerId;
	private String customerId;
	
	public NewMakeupRequest(Lesson l){
		this.task = SchoolTracsConst.Task.NEW_MAKE_UP.code();
		this.startTime = l.getStartDateTime();
		this.endTime = l.getEndDateTime();
		this.staffId = l.getTeacher().getId();
		this.facilityId = l.getRoom().getId();
		this.productId = "";
		this.id = l.getId();
		this.centerId = l.getCenterId();
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
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	public String getStaffId() {
		return staffId;
	}
	public void setStaffId(String staffId) {
		this.staffId = staffId;
	}
	public String getFacilityId() {
		return facilityId;
	}
	public void setFacilityId(String facilityId) {
		this.facilityId = facilityId;
	}
	public String getProductId() {
		return productId;
	}
	public void setProductId(String productId) {
		this.productId = productId;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCenterId() {
		return centerId;
	}
	public void setCenterId(String centerId) {
		this.centerId = centerId;
	}
	public String getCustomerId() {
		return customerId;
	}
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	
	
	
}
