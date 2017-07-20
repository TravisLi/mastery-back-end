package mastery.model;

import org.apache.commons.lang3.StringUtils;

import mastery.schooltracs.model.Facility;
import mastery.schooltracs.model.FacilityMap;

public class Room {

	private String id;
	private String name;
	private Integer cap;
	
	public Room() {
		super();
	}
	
	public Room(String id, String name, Integer cap) {
		super();
		this.id = id;
		this.name = name;
		this.cap = cap;
	}

	public Room(FacilityMap map){
		this.id = map.getFacilityId();
		this.name = map.getName();
	}
	
	public Room(Facility fac){
		this.id = fac.getId();
		this.name = fac.getName();
		this.cap = Integer.parseInt(StringUtils.isEmpty(fac.getCapacity())?"0":fac.getCapacity());
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
	
	@Override
	public String toString() {
		return "Room [id=" + id + ", name=" + name + ", cap=" + cap + "]";
	}
	
}
