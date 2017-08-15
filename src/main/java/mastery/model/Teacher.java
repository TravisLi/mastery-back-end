package mastery.model;

import mastery.schooltracs.model.StaffMap;

public class Teacher {
	
	private String id;
	private String name;

	public Teacher() {
		super();
	}
	
	public Teacher(StaffMap map){
		this.id = map.getStaffId();
		this.name = map.getName();
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "Teacher [id=" + id + ", name=" + name + "]";
	}
	
}
