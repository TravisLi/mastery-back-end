package mastery.schooltracs.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class IncomeReportData {

	private String studentNumber;
	private String level;
	private String phone;
	private String studentName;
	private String receiptDetail;
	private String reonciled;
	private String reconcileDate;
	private String startDate;
	private String endDate;
	private String facility;
	private String paymentStaff;
	private String receiptNum;
	private String voided;
	private String center;
	private String paymentDate;
	private String remarks;
	private String courseFee;
	private String productFee;
	private String otherFee;
	private String deposit;
	private String depositRefund;
	private String membershipFee;
	private String price;
	private String paymentMethod;
	private String bankName;
	private String chequeNo;
	private String invoice;
	
	public String getStudentNumber() {
		return studentNumber;
	}
	public void setStudentNumber(String studentNumber) {
		this.studentNumber = studentNumber;
	}
	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getStudentName() {
		return studentName;
	}
	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}
	public String getReceiptDetail() {
		return receiptDetail;
	}
	public void setReceiptDetail(String receiptDetail) {
		this.receiptDetail = receiptDetail;
	}
	public String getReonciled() {
		return reonciled;
	}
	public void setReonciled(String reonciled) {
		this.reonciled = reonciled;
	}
	public String getReconcileDate() {
		return reconcileDate;
	}
	public void setReconcileDate(String reconcileDate) {
		this.reconcileDate = reconcileDate;
	}
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public String getFacility() {
		return facility;
	}
	public void setFacility(String facility) {
		this.facility = facility;
	}
	public String getPaymentStaff() {
		return paymentStaff;
	}
	public void setPaymentStaff(String paymentStaff) {
		this.paymentStaff = paymentStaff;
	}
	public String getReceiptNum() {
		return receiptNum;
	}
	public void setReceiptNum(String receiptNum) {
		this.receiptNum = receiptNum;
	}
	public String getVoided() {
		return voided;
	}
	public void setVoided(String voided) {
		this.voided = voided;
	}
	public String getCenter() {
		return center;
	}
	public void setCenter(String center) {
		this.center = center;
	}
	public String getPaymentDate() {
		return paymentDate;
	}
	public void setPaymentDate(String paymentDate) {
		this.paymentDate = paymentDate;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public String getCourseFee() {
		return courseFee;
	}
	public void setCourseFee(String courseFee) {
		this.courseFee = courseFee;
	}
	public String getProductFee() {
		return productFee;
	}
	public void setProductFee(String productFee) {
		this.productFee = productFee;
	}
	public String getOtherFee() {
		return otherFee;
	}
	public void setOtherFee(String otherFee) {
		this.otherFee = otherFee;
	}
	public String getDeposit() {
		return deposit;
	}
	public void setDeposit(String deposit) {
		this.deposit = deposit;
	}
	public String getDepositRefund() {
		return depositRefund;
	}
	public void setDepositRefund(String depositRefund) {
		this.depositRefund = depositRefund;
	}
	public String getMembershipFee() {
		return membershipFee;
	}
	public void setMembershipFee(String membershipFee) {
		this.membershipFee = membershipFee;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public String getPaymentMethod() {
		return paymentMethod;
	}
	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}
	public String getBankName() {
		return bankName;
	}
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	public String getChequeNo() {
		return chequeNo;
	}
	public void setChequeNo(String chequeNo) {
		this.chequeNo = chequeNo;
	}
	public String getInvoice() {
		return invoice;
	}
	public void setInvoice(String invoice) {
		this.invoice = invoice;
	}

}
