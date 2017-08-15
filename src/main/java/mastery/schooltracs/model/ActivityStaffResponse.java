package mastery.schooltracs.model;

import java.util.List;

public class ActivityStaffResponse {
	
	private List<StaffMap> data;
	private Boolean success;
	
	public List<StaffMap> getData() {
		return data;
	}
	public void setData(List<StaffMap> data) {
		this.data = data;
	}
	public Boolean getSuccess() {
		return success;
	}
	public void setSuccess(Boolean success) {
		this.success = success;
	}
		
}
