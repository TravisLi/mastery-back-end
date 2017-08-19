package mastery.model;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import mastery.schooltracs.model.StStaff;
import mastery.schooltracs.model.StaffConfig;

public class Staff {

	private String id;
	private String uid;
	private String name;
	private String mobile;
	private String role;
	private StaffConfig config;
	
	public Staff(StStaff s){
		this.id = s.getId();
		this.uid = s.getUid();
		this.name = s.getName();
		this.mobile = s.getMobile();
		this.role  = s.getRoleName();
		this.config = new StaffConfig();
		if(StringUtils.isNotEmpty(s.getRemark())){
			ObjectMapper mapper = new ObjectMapper();
			try {
				StaffConfig sf = mapper.readValue(s.getRemark(), StaffConfig.class);
				this.config = sf;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public Staff() {
		// TODO Auto-generated constructor stub
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
	public StaffConfig getConfig() {
		return config;
	}
	public void setConfig(StaffConfig config) {
		this.config = config;
	}

	@Override
	public String toString() {
		return "Staff [id=" + id + ", uid=" + uid + ", name=" + name + ", mobile=" + mobile + ", role=" + role + "]";
	}

}
