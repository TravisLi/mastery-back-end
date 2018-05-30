package mastery.schooltracs.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchActivityResponse {

	/*{"7 ":{
	"activities":[],
	"staffColData":[{"staffId":"90","name":"Adrian Ko"}],
	"customerColData":[],
	"roomColData":[],
	"productColData":[],
	"holidays":[],
	"customerMap":{},
	"staffMap":{},
	"facilityMap":{},
	"productMap":{}}*/
	
	private List<Activity> activities;
	private List<String> staffColData;
	private List<String> customerColData;
	private List<String> roomColData;
	private List<String> productionColData;
	private List<String> hoildays;
	private List<StaffMap> staffMaps;
	private List<FacilityMap> facilityMaps;
	private List<ProductMap> productMaps;
	private List<CustomerMap> customerMaps;
	
	public List<Activity> getActivities() {
		return activities;
	}
	public void setActivities(List<Activity> activities) {
		this.activities = activities;
	}
	public List<String> getStaffColData() {
		return staffColData;
	}
	public void setStaffColData(List<String> staffColData) {
		this.staffColData = staffColData;
	}
	public List<String> getCustomerColData() {
		return customerColData;
	}
	public void setCustomerColData(List<String> customerColData) {
		this.customerColData = customerColData;
	}
	public List<String> getRoomColData() {
		return roomColData;
	}
	public void setRoomColData(List<String> roomColData) {
		this.roomColData = roomColData;
	}
	public List<String> getProductionColData() {
		return productionColData;
	}
	public void setProductionColData(List<String> productionColData) {
		this.productionColData = productionColData;
	}
	public List<String> getHoildays() {
		return hoildays;
	}
	public void setHoildays(List<String> hoildays) {
		this.hoildays = hoildays;
	}
	public List<StaffMap> getStaffMaps() {
		return staffMaps;
	}
	public void setStaffMaps(List<StaffMap> staffMaps) {
		this.staffMaps = staffMaps;
	}
	public List<FacilityMap> getFacilityMaps() {
		return facilityMaps;
	}
	public void setFacilityMaps(List<FacilityMap> facilityMaps) {
		this.facilityMaps = facilityMaps;
	}
	public List<ProductMap> getProductMaps() {
		return productMaps;
	}
	public void setProductMaps(List<ProductMap> productMaps) {
		this.productMaps = productMaps;
	}
	public List<CustomerMap> getCustomerMaps() {
		return customerMaps;
	}
	public void setCustomerMaps(List<CustomerMap> customerMaps) {
		this.customerMaps = customerMaps;
	}
	
}
