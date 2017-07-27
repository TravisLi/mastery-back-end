package mastery.model;

import mastery.schooltracs.model.Customer;

public class User {

	private String id;
	private String name;
	private String role;
	
	public User(Customer c, String role){
		this.id = c.getId();
		this.name = c.getName();
		this.role = role;
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
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	
}
