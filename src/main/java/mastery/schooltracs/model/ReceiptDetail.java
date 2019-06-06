package mastery.schooltracs.model;

public class ReceiptDetail {
	
	private String lessonName;
	private String dateTimeStr;
	private String teacher;
	private String venue;
	
	public ReceiptDetail(String lessonName, String dateTimeStr, String teacher, String venue) {
		super();
		this.lessonName = lessonName;
		this.dateTimeStr = dateTimeStr;
		this.teacher = teacher;
		this.venue = venue;
	}
	
	public String getLessonName() {
		return lessonName;
	}
	public void setLessonName(String lessonName) {
		this.lessonName = lessonName;
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
