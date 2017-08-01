package mastery.model;

import java.text.ParseException;
import java.util.Date;

import mastery.schooltracs.model.StaffWorkHour;
import mastery.schooltracs.util.SchoolTracsConst;

public class WorkHour {
	
	private Integer weekDay;
	private Date stTime;
	private Date edTime;
	
	public WorkHour(StaffWorkHour s){
		this.weekDay = Integer.parseInt(s.getWeekDay());
		try {
			this.stTime = SchoolTracsConst.SDF_TIME.parse(s.getStartTime());
			this.edTime = SchoolTracsConst.SDF_TIME.parse(s.getEndTime());
		} catch (ParseException e) {
			e.printStackTrace();
		}	
	}
	
	public Integer getWeekDay() {
		return weekDay;
	}
	public void setWeekDay(Integer weekDay) {
		this.weekDay = weekDay;
	}
	public Date getStTime() {
		return stTime;
	}
	public void setStTime(Date stTime) {
		this.stTime = stTime;
	}
	public Date getEdTime() {
		return edTime;
	}
	public void setEdTime(Date edTime) {
		this.edTime = edTime;
	}

	@Override
	public String toString() {
		return "WorkHour [weekDay=" + weekDay + ", stTime=" + stTime + ", edTime=" + edTime + "]";
	}
	
}
