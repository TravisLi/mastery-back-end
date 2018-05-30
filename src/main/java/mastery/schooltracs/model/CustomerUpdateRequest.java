package mastery.schooltracs.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import mastery.schooltracs.util.SchoolTracsConst;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomerUpdateRequest {

	private String centreId;
	private Integer deleted;
	private Customer data;
	
	public CustomerUpdateRequest(Customer cust){
		
		this.centreId = SchoolTracsConst.OIM_CENTRE_ID;
		this.deleted = 0;
		this.data = cust;
	}
	
	public String getCentreId() {
		return centreId;
	}
	public void setCentreId(String centreId) {
		this.centreId = centreId;
	}
	public Integer getDeleted() {
		return deleted;
	}
	public void setDeleted(Integer deleted) {
		this.deleted = deleted;
	}

	public Customer getData() {
		return data;
	}

	public void setData(Customer data) {
		this.data = data;
	}

	
	
}
