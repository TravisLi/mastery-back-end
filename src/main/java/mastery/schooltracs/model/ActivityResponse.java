package mastery.schooltracs.model;

public class ActivityResponse {
	
	private Activity data;
	private Boolean success;
	
	public Activity getData() {
		return data;
	}
	public void setData(Activity data) {
		this.data = data;
	}
	public Boolean getSuccess() {
		return success;
	}
	public void setSuccess(Boolean success) {
		this.success = success;
	}
		
}
