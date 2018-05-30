package mastery.schooltracs.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomerMap {

	private String id;
	private String activityId;
	private String customerId;
	private String status;
	private String userStatus;
	private String paid;
	private String orderItemId;
	private String moveToId;
	private String moveFromId;
	private String attendance;
	private String remark;
	private String name;
	private String level;
	private String birthday;
	
	private String moveFromPaid;
	private String moveFromOrderItemId;
	private String receiptId;
	private String studentRemark;
	private String endDate;
	private String pickupTime;
	private String feeRemark;
	private String number;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getActivityId() {
		return activityId;
	}
	public void setActivityId(String activityId) {
		this.activityId = activityId;
	}
	public String getCustomerId() {
		return customerId;
	}
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getUserStatus() {
		return userStatus;
	}
	public void setUserStatus(String userStatus) {
		this.userStatus = userStatus;
	}
	public String getPaid() {
		return paid;
	}
	public void setPaid(String paid) {
		this.paid = paid;
	}
	public String getOrderItemId() {
		return orderItemId;
	}
	public void setOrderItemId(String orderItemId) {
		this.orderItemId = orderItemId;
	}
	public String getMoveToId() {
		return moveToId;
	}
	public void setMoveToId(String moveToId) {
		this.moveToId = moveToId;
	}
	public String getMoveFromId() {
		return moveFromId;
	}
	public void setMoveFromId(String moveFromId) {
		this.moveFromId = moveFromId;
	}
	public String getAttendance() {
		return attendance;
	}
	public void setAttendance(String attendance) {
		this.attendance = attendance;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
	}
	public String getMoveFromPaid() {
		return moveFromPaid;
	}
	public void setMoveFromPaid(String moveFromPaid) {
		this.moveFromPaid = moveFromPaid;
	}
	public String getMoveFromOrderItemId() {
		return moveFromOrderItemId;
	}
	public void setMoveFromOrderItemId(String moveFromOrderItemId) {
		this.moveFromOrderItemId = moveFromOrderItemId;
	}
	public String getReceiptId() {
		return receiptId;
	}
	public void setReceiptId(String receiptId) {
		this.receiptId = receiptId;
	}
	public String getStudentRemark() {
		return studentRemark;
	}
	public void setStudentRemark(String studentRemark) {
		this.studentRemark = studentRemark;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public String getPickupTime() {
		return pickupTime;
	}
	public void setPickupTime(String pickupTime) {
		this.pickupTime = pickupTime;
	}
	public String getFeeRemark() {
		return feeRemark;
	}
	public void setFeeRemark(String feeRemark) {
		this.feeRemark = feeRemark;
	}
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public String getBirthday() {
		return birthday;
	}
	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}
}
