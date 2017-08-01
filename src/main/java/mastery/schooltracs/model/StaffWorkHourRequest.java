package mastery.schooltracs.model;

import mastery.schooltracs.util.SchoolTracsConst;

public class StaffWorkHourRequest {

	/*{
	 * "43 ":
	 * [
	 * "Staff.getWorkingHour",
	 * 102
	 * ]
	 * }*/
	
	private Integer reqSeq;
	private String task;
	private String staffId;
	
	public StaffWorkHourRequest(Integer reqSeq, String staffId) {
		super();
		this.reqSeq = reqSeq;
		this.task = SchoolTracsConst.Task.STAFF_WORK_HR.code();
		this.staffId = staffId;
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
	public String getStaffId() {
		return staffId;
	}
	public void setStaffId(String staffId) {
		this.staffId = staffId;
	}
}
