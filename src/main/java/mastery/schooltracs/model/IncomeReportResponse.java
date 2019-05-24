package mastery.schooltracs.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class IncomeReportResponse {

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

}
