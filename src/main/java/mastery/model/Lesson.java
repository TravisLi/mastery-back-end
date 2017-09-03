package mastery.model;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mastery.schooltracs.model.Activity;
import mastery.schooltracs.util.SchoolTracsConst;
import mastery.schooltracs.util.SchoolTracsUtil;

public class Lesson implements Comparable<Lesson>  {
	
	private static final Logger logger = LoggerFactory.getLogger(Lesson.class);
	
	private String id;
	private String name;
	private SchoolTracsConst.Level frLvl;
	private SchoolTracsConst.Level toLvl;
	private Date startDateTime;
	private Date endDateTime;
	private String centerId;
	private String category;
	private Room room;
	private Teacher teacher;
	private List<Student> students = new ArrayList<Student>();
	
	public Lesson() {
		super();
	}
	
	public Lesson(Lesson l){
		this.name = l.getName();
		this.frLvl = l.getFrLvl();
		this.toLvl = l.getToLvl();
		this.startDateTime = l.getStartDateTime();
		this.endDateTime = l.getEndDateTime();
		this.centerId = l.getCenterId();
		this.room = l.getRoom();
		this.teacher = l.getTeacher();
		//this.students = l.getStudents();
	}

	public Lesson(Activity act) throws ParseException{
		this.id = act.getId();
		this.name = act.getName();
		this.centerId = act.getCenterId();
		this.category = act.getCategory();
		String[] lvls= act.getLevel().split("-");
		if(lvls.length==2){
			frLvl = SchoolTracsUtil.classifyLevel(lvls[0]);
			toLvl = SchoolTracsUtil.classifyLevel(lvls[1]);
		}else{
			frLvl = SchoolTracsConst.Level.NONE;
			toLvl = SchoolTracsConst.Level.NONE;
		}
		
		String startDateStr = act.getDate() + " " + act.getStartTime();
		String endDateStr = act.getDate() + " " + act.getEndTime();
		
		logger.debug("startDate=" + startDateStr);
		logger.debug("endDate=" + endDateStr);
		
		this.startDateTime = SchoolTracsConst.SDF_FULL.parse(startDateStr);
		this.endDateTime = SchoolTracsConst.SDF_FULL.parse(endDateStr);
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

	public SchoolTracsConst.Level getFrLvl() {
		return frLvl;
	}

	public void setFrLvl(SchoolTracsConst.Level frLvl) {
		this.frLvl = frLvl;
	}

	public SchoolTracsConst.Level getToLvl() {
		return toLvl;
	}

	public void setToLvl(SchoolTracsConst.Level toLvl) {
		this.toLvl = toLvl;
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

	public String getCenterId() {
		return centerId;
	}

	public void setCenterId(String centerId) {
		this.centerId = centerId;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public Room getRoom() {
		return room;
	}

	public void setRoom(Room room) {
		this.room = room;
	}

	public Teacher getTeacher() {
		return teacher;
	}

	public void setTeacher(Teacher teacher) {
		this.teacher = teacher;
	}

	public List<Student> getStudents() {
		return students;
	}

	public void setStudents(List<Student> students) {
		this.students = students;
	}
	
	public Integer duration(){
		return SchoolTracsUtil.minDiffBtwDates(startDateTime, endDateTime);
	}

	@Override
	public String toString() {
		return "Lesson [id=" + id + ", name=" + name + ", frLvl=" + frLvl + ", toLvl=" + toLvl + ", startDateTime="
				+ startDateTime + ", endDateTime=" + endDateTime + ", teacher=" + teacher + ", students=" + students
				+ ", room=" + room + "]";
	}

	@Override
	public int compareTo(Lesson l) {
		if(this.startDateTime.before(l.getStartDateTime())){
			return -1;
		}
		
		if(this.startDateTime.after(l.getStartDateTime())){
			return 1;
		}
		
		return 0;
	}

}
