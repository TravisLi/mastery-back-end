package mastery.model;

import java.util.Date;

import mastery.schooltracs.util.SchoolTracsUtil;

public class FreeTimeslot {

	private Date startDateTime;
	private Date endDateTime;
	
	public FreeTimeslot(){
		
	}
	
	public FreeTimeslot(Date startDateTime, Date endDateTime){
		this.startDateTime = startDateTime;
		this.endDateTime = endDateTime;
	}
	
	public Date getStartDateTime() {
		return startDateTime;
	}
	public void setStartDateTime(Date startDateTime) {
		this.startDateTime = startDateTime;
	}
	public Date getEndDateTime() {
		return endDateTime;
	}
	public void setEndDateTime(Date endDateTime) {
		this.endDateTime = endDateTime;
	}
	
	public Integer duration(){
		return SchoolTracsUtil.minDiffBtwDates(startDateTime, endDateTime);
	}

	@Override
	public String toString() {
		return "FreeTimeslot [startDateTime=" + startDateTime + ", endDateTime=" + endDateTime + "]";
	}
}
