package mastery.model;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mastery.schooltracs.model.Activity;
import mastery.schooltracs.util.SchoolTracsConst;
import mastery.schooltracs.util.SchoolTracsConst.Level;
import mastery.schooltracs.util.SchoolTracsUtil;

public class Lesson {
	
	private static final Logger logger = LoggerFactory.getLogger(Lesson.class);
	
	private String id;
	private String name;
	private SchoolTracsConst.Level frLvl;
	private SchoolTracsConst.Level toLvl;
	private Date startDateTime;
	private Date endDateTime;
	private String centerId;
	private Room room;
	private Teacher teacher;
	private List<Student> students = new ArrayList<Student>();
	
	
	public Lesson() {
		super();
	}

	public Lesson(String id, String name, Level frLvl, Level toLvl, Date startDateTime, Date endDateTime,
			Teacher teacher, List<Student> students, Room room) {
		super();
		this.id = id;
		this.name = name;
		this.frLvl = frLvl;
		this.toLvl = toLvl;
		this.startDateTime = startDateTime;
		this.endDateTime = endDateTime;
		this.teacher = teacher;
		this.students = students;
		this.room = room;
	}

	public Lesson(Activity act) throws ParseException{
		this.id = act.getId();
		this.name = act.getName();
		this.centerId = act.getCenterId();
		String[] lvls= act.getLevel().split("-");
		if(lvls.length==2){
			frLvl = SchoolTracsUtil.classifyLevel(lvls[0]);
			toLvl = SchoolTracsUtil.classifyLevel(lvls[1]);
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

	@Override
	public String toString() {
		return "Lesson [id=" + id + ", name=" + name + ", frLvl=" + frLvl + ", toLvl=" + toLvl + ", startDateTime="
				+ startDateTime + ", endDateTime=" + endDateTime + ", teacher=" + teacher + ", students=" + students
				+ ", room=" + room + "]";
	}

}
