package mastery.schooltracs.model;

import java.util.List;

public class ActivityFacilityResponse {
	
	private List<FacilityMap> data;
	private Integer total;
	private Boolean success;
	
	public List<FacilityMap> getData() {
		return data;
	}
	public void setData(List<FacilityMap> data) {
		this.data = data;
	}
	public Integer getTotal() {
		return total;
	}
	public void setTotal(Integer total) {
		this.total = total;
	}
	public Boolean getSuccess() {
		return success;
	}
	public void setSuccess(Boolean success) {
		this.success = success;
	}
	
		
}
