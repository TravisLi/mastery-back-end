package mastery.model;

import mastery.schooltracs.model.StaffMap;

public class Teacher {
	
	private String id;
	private String name;
	private Integer cap;
	private Room room;
	
	public Teacher() {
		super();
	}
	
	public Teacher(String id, String name, Integer cap, Room room) {
		super();
		this.id = id;
		this.name = name;
		this.cap = cap;
		this.room = room;
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
	public Integer getCap() {
		return cap;
	}
	public void setCap(Integer cap) {
		this.cap = cap;
	}
	public Room getRoom() {
		return room;
	}
	public void setRoom(Room room) {
		this.room = room;
	}

	@Override
	public String toString() {
		return "Teacher [id=" + id + ", name=" + name + ", cap=" + cap + ", room=" + room + "]";
	}
	
}
