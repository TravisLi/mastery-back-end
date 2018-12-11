package mastery.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

public class Journal {

	private String id;
	private String content;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date created;
	private String staffName;
	private String staffId;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Date getCreated() {
		return created;
	}
	public void setCreated(Date created) {
		this.created = created;
	}
	public String getStaffName() {
		return staffName;
	}
	public void setStaffName(String staffName) {
		this.staffName = staffName;
	}
	public String getStaffId() {
		return staffId;
	}
	public void setStaffId(String staffId) {
		this.staffId = staffId;
	}
	@Override
	public String toString() {
		return "Journal [id=" + id + ", content=" + content + ", created=" + created + ", staffName=" + staffName
				+ ", staffId=" + staffId + "]";
	}
	
}
