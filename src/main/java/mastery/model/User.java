package mastery.model;

import java.util.ArrayList;
import java.util.List;

import mastery.schooltracs.model.Customer;

public class User {

	private String id;
	private String name;
	private String contact;
	private String role;
	private List<Student> students = new ArrayList<Student>();
	
	public User(Customer c){
		this.id = c.getId();
		this.name = c.getName();
		this.contact = c.getPhone();
		this.role = "student";
	}
	
	public User(String name, String contact, List<Student> list){
		this.id = "0";
		this.name = name;
		this.contact = contact;
		this.role = "parent";
		this.students = list;
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
	public String getContact() {
		return contact;
	}
	public void setContact(String contact) {
		this.contact = contact;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}

	public List<Student> getStudents() {
		return students;
	}

	public void setStudents(List<Student> students) {
		this.students = students;
	}
	
}
