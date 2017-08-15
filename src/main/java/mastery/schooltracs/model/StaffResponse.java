package mastery.schooltracs.model;

import java.util.List;

public class StaffResponse {
	
	private Integer total;
	private List<StStaff> data;
	private Boolean success;
	
	public Integer getTotal() {
		return total;
	}
	public void setTotal(Integer total) {
		this.total = total;
	}
	public List<StStaff> getData() {
		return data;
	}
	public void setData(List<StStaff> data) {
		this.data = data;
	}
	public Boolean getSuccess() {
		return success;
	}
	public void setSuccess(Boolean success) {
		this.success = success;
	}
		
}
