package mastery.schooltracs.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class InvoiceItemDetail{

	private String dateTimeStr;
	private String teacher;
	private String venue;

	public InvoiceItemDetail(String dateTimeStr, String teacher, String venue) {
		super();
		this.dateTimeStr = dateTimeStr;
		this.teacher = teacher;
		this.venue = venue;
	}
	
	public String getDateTimeStr() {
		return dateTimeStr;
	}
	public void setDateTimeStr(String dateTimeStr) {
		this.dateTimeStr = dateTimeStr;
	}
	public String getTeacher() {
		return teacher;
	}
	public void setTeacher(String teacher) {
		this.teacher = teacher;
	}
	public String getVenue() {
		return venue;
	}
	public void setVenue(String venue) {
		this.venue = venue;
	}
	
	
	
}
