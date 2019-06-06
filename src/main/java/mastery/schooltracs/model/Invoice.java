package mastery.schooltracs.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Invoice {

	private String id;
	private String customerId;
	private String billDate;
	private String customers;
	private String staffName;
	private String invoicestaffid;
	private String invoicefId;
	private String type;
	private String total;
	private String paid;
	private String invoicedate;
	private String dueDate;
	private String actualStaffId;
	private String actionTime;
	private String actionDetail;
	private String actionResult;
	private String finalized;
	private String lastSent;
	private String sentCount;
	private String reminded;
	private String novapay;
	private String novapayId;
	private String meta1;
	private String meta2;
	private String centreId;
	private String remark;
	private String number;
	private String oldNumber;
	private String created;
	private String updated;
	private String deleted;
	private String deletedAt;
	private String oid;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCustomerId() {
		return customerId;
	}
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	public String getBillDate() {
		return billDate;
	}
	public void setBillDate(String billDate) {
		this.billDate = billDate;
	}
	public String getCustomers() {
		return customers;
	}
	public void setCustomers(String customers) {
		this.customers = customers;
	}
	public String getStaffName() {
		return staffName;
	}
	public void setStaffName(String staffName) {
		this.staffName = staffName;
	}
	public String getInvoicestaffid() {
		return invoicestaffid;
	}
	public void setInvoicestaffid(String invoicestaffid) {
		this.invoicestaffid = invoicestaffid;
	}
	public String getInvoicefId() {
		return invoicefId;
	}
	public void setInvoicefId(String invoicefId) {
		this.invoicefId = invoicefId;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getTotal() {
		return total;
	}
	public void setTotal(String total) {
		this.total = total;
	}
	public String getPaid() {
		return paid;
	}
	public void setPaid(String paid) {
		this.paid = paid;
	}
	public String getInvoicedate() {
		return invoicedate;
	}
	public void setInvoicedate(String invoicedate) {
		this.invoicedate = invoicedate;
	}
	public String getDueDate() {
		return dueDate;
	}
	public void setDueDate(String dueDate) {
		this.dueDate = dueDate;
	}
	public String getActualStaffId() {
		return actualStaffId;
	}
	public void setActualStaffId(String actualStaffId) {
		this.actualStaffId = actualStaffId;
	}
	public String getActionTime() {
		return actionTime;
	}
	public void setActionTime(String actionTime) {
		this.actionTime = actionTime;
	}
	public String getActionDetail() {
		return actionDetail;
	}
	public void setActionDetail(String actionDetail) {
		this.actionDetail = actionDetail;
	}
	public String getActionResult() {
		return actionResult;
	}
	public void setActionResult(String actionResult) {
		this.actionResult = actionResult;
	}
	public String getFinalized() {
		return finalized;
	}
	public void setFinalized(String finalized) {
		this.finalized = finalized;
	}
	public String getLastSent() {
		return lastSent;
	}
	public void setLastSent(String lastSent) {
		this.lastSent = lastSent;
	}
	public String getSentCount() {
		return sentCount;
	}
	public void setSentCount(String sentCount) {
		this.sentCount = sentCount;
	}
	public String getReminded() {
		return reminded;
	}
	public void setReminded(String reminded) {
		this.reminded = reminded;
	}
	public String getNovapay() {
		return novapay;
	}
	public void setNovapay(String novapay) {
		this.novapay = novapay;
	}
	public String getNovapayId() {
		return novapayId;
	}
	public void setNovapayId(String novapayId) {
		this.novapayId = novapayId;
	}
	public String getMeta1() {
		return meta1;
	}
	public void setMeta1(String meta1) {
		this.meta1 = meta1;
	}
	public String getMeta2() {
		return meta2;
	}
	public void setMeta2(String meta2) {
		this.meta2 = meta2;
	}
	public String getCentreId() {
		return centreId;
	}
	public void setCentreId(String centreId) {
		this.centreId = centreId;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public String getOldNumber() {
		return oldNumber;
	}
	public void setOldNumber(String oldNumber) {
		this.oldNumber = oldNumber;
	}
	public String getCreated() {
		return created;
	}
	public void setCreated(String created) {
		this.created = created;
	}
	public String getUpdated() {
		return updated;
	}
	public void setUpdated(String updated) {
		this.updated = updated;
	}
	public String getDeleted() {
		return deleted;
	}
	public void setDeleted(String deleted) {
		this.deleted = deleted;
	}
	public String getDeletedAt() {
		return deletedAt;
	}
	public void setDeletedAt(String deletedAt) {
		this.deletedAt = deletedAt;
	}
	public String getOid() {
		return oid;
	}
	public void setOid(String oid) {
		this.oid = oid;
	}
	
	
}
