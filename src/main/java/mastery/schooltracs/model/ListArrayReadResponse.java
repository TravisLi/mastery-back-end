package mastery.schooltracs.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ListArrayReadResponse<T> {

	private List<String> total;
	private List<T> data;	
	private Boolean success;
	
	public List<String> getTotal() {
		return total;
	}
	public void setTotal(List<String> total) {
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
