package mastery.schooltracs.model;

import java.util.List;

public class ListReadResponse<T> {

	private Integer total;
	private List<T> data;
	
	private Boolean success;
	public Integer getTotal() {
		return total;
	}
	public void setTotal(Integer total) {
		this.total = total;
	}
	public List<T> getData() {
		return data;
	}
	public void setData(List<T> data) {
		this.data = data;
	}
	public Boolean getSuccess() {
		return success;
	}
	public void setSuccess(Boolean success) {
		this.success = success;
	}
	
}
