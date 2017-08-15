package mastery.model;

import mastery.schooltracs.model.StStaff;

public class Staff {

	private String id;
	private String uid;
	private String name;
	private String mobile;
	private String role;
	
	public Staff(StStaff s){
		this.id = s.getId();
		this.uid = s.getUid();
		this.name = s.getName();
		this.mobile = s.getMobile();
		this.role  = s.getRoleName();
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}

	@Override
	public String toString() {
		return "Staff [id=" + id + ", name=" + name + ", mobile=" + mobile + ", role=" + role + "]";
	}
	
}
