package mastery.schooltracs.util;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import mastery.model.FreeTimeslot;
import mastery.model.Lesson;
import mastery.model.Room;
import mastery.model.Student;
import mastery.model.Teacher;
import mastery.model.WorkHour;
import mastery.schooltracs.model.Activity;
import mastery.schooltracs.model.CustomerMap;
import mastery.schooltracs.model.FacilityMap;
import mastery.schooltracs.model.SearchActivityResponse;
import mastery.schooltracs.model.StaffMap;
import mastery.schooltracs.model.StaffWorkHour;
import mastery.util.MasteryUtil;

public class SchoolTracsUtil {

	private static final Logger logger = LoggerFactory.getLogger(SchoolTracsUtil.class);

	public static List<Lesson> findSimLvlLsonOfTch(final Lesson src, final String stdName, final int lvlGap, final List<Lesson> listByTch){

		logger.info("Find Similar Level Lessons of Teacher with lvlGap=" + lvlGap);

		List<Lesson> list = new ArrayList<Lesson>();

		logger.debug("Src Lesson="+src);

		Student master = null;

		for(Student s: src.getStudents()){
			if(s.getName().equals(stdName)){
				master = s;
			}
		}

		if(master == null){
			return list;
		}

		logger.info("master student id=" + master.getId());
		logger.info("master student name=" + master.getName());

		for(Lesson l: listByTch){

			logger.debug("lesson by Teacher="+l.toString());

			boolean sameName = MasteryUtil.nullGuard(l.getName()).equals(MasteryUtil.nullGuard(src.getName()));
			boolean sameFrLvl = MasteryUtil.nullGuard(l.getFrLvl()).equals(MasteryUtil.nullGuard(src.getFrLvl()));
			boolean sameToLvl = MasteryUtil.nullGuard(l.getToLvl()).equals(MasteryUtil.nullGuard(src.getToLvl()));

			//the gap between two student meet the requirement
			boolean meetLvl = true;

			boolean sameStd = false;

			for(Student s: l.getStudents()){

				if(master.getId().equals(s.getId())){
					sameStd = true;
				}

				int calGap = MasteryUtil.nullGuard(master.getLvl()).code()-MasteryUtil.nullGuard(s.getLvl()).code();

				logger.debug("calGap="+Math.abs(calGap));
				logger.debug("lvlGap="+lvlGap);

				//either one student not match forfeit
				if(Math.abs(calGap) != lvlGap){
					meetLvl = false;
				}

			}

			logger.debug("sameName="+sameName);
			logger.debug("sameFrLvl="+sameFrLvl);
			logger.debug("sameToLvl="+sameToLvl);
			logger.debug("meetlvl="+meetLvl);
			logger.debug("sameStudent="+sameStd);

			if(sameName&&sameFrLvl&&sameToLvl&&meetLvl&&!sameStd){
				logger.info("Lesson is added to list for return =" + l.toString());
				list.add(l);
			}

		}

		return list;

	}

	public static boolean isRmFullInPrd(Room r, Date startTime, Date endTime, List<Lesson> listByRoom){

		logger.info("Is Room Full In Period Start");

		logger.debug("Start Time="+startTime);
		logger.debug("End Time="+endTime);
		logger.debug("Room Cap="+r.getCap());

		final int minStep = 15;
		Calendar cal = Calendar.getInstance();
		cal.setTime(startTime);

		Date cTime = cal.getTime();

		while(cTime.before(endTime)||cTime.equals(endTime)){

			logger.debug("Check Time="+cTime);

			int noOfStd = getNoOfStdInRmAtTime(cTime, listByRoom);

			logger.debug("No.of Studnet="+noOfStd);

			if(noOfStd>r.getCap()){
				return true;
			}
			cal.add(Calendar.MINUTE, minStep);
			cTime = cal.getTime();
		}

		return false;

	}

	public static int getNoOfStdInRmAtTime(Date time, List<Lesson> listByRoom){

		logger.info("Get Student No At Time Start");

		int stdCnt = 0;

		for(Lesson l: listByRoom){
			if(timeFallIntoTheMiddleOfLson(l,time)){
				logger.info("lesson fall into middle" + l.toString());
				stdCnt += l.getStudents().size();
			}
		}

		return stdCnt;
	}

	private static boolean timeFallIntoTheMiddleOfLson(Lesson l, Date src){

		boolean gtEqStartTime = src.after(l.getStartDateTime())||src.equals(l.getStartDateTime());
		boolean stEqEndTime = src.before(l.getEndDateTime())||src.equals(l.getEndDateTime());

		if(gtEqStartTime&&stEqEndTime){
			return true;
		}

		return false;
	}

	public static SchoolTracsConst.Level classifyLevel(String lvl){
		if(lvl!=null){
			switch(lvl){
			case "P1":
				return SchoolTracsConst.Level.P1;
			case "P2":
				return SchoolTracsConst.Level.P2;
			case "P3":
				return SchoolTracsConst.Level.P3;
			case "P4":
				return SchoolTracsConst.Level.P4;
			case "P5":
				return SchoolTracsConst.Level.P5;
			case "P6":
				return SchoolTracsConst.Level.P6;
			case "S1":
				return SchoolTracsConst.Level.S1;
			case "S2":
				return SchoolTracsConst.Level.S2;
			case "S3":
				return SchoolTracsConst.Level.S3;
			case "S4":
				return SchoolTracsConst.Level.S4;
			case "S5":
				return SchoolTracsConst.Level.S5;
			case "S6":
				return SchoolTracsConst.Level.S6;
			default:
				return SchoolTracsConst.Level.NONE;
			}
		}
		return SchoolTracsConst.Level.NONE;
	}
	
	public static HashMap<Integer, WorkHour> stfWkHrToMap(List<StaffWorkHour> list){
		
		logger.info("Staff Working Hour to Map start");
		
		HashMap<Integer, WorkHour> map = new HashMap<Integer, WorkHour>();
		for(StaffWorkHour s: list){
			if(s.getOnDuty()){
				WorkHour w = new WorkHour(s);
				map.put(w.getWeekDay(), w);
				logger.info("Work Hour=" + w.toString());
			}
		}
		return map;
	}

	public static List<Lesson> schRspToLson(SearchActivityResponse sr){

		logger.info("Search Response to Lessons start");

		List<Lesson> list = new ArrayList<Lesson>();

		HashMap<String, Teacher> tHash = stfMapToActHash(sr.getStaffMaps());
		HashMap<String, List<Student>> sHash = custMapToActHash(sr.getCustomerMaps());
		HashMap<String, Room> rHash = facMapToActHash(sr.getFacilityMaps());

		for(Activity a: sr.getActivities()){
			if(!a.getEnrolled().equals("0")){
				boolean error = false;
				try {  
					logger.debug("Processing id:" + a.getId());
					Lesson l = new Lesson(a);

					if(rHash.containsKey(l.getId())){
						l.setRoom(rHash.get(l.getId()));
					}else{
						logger.info("Cannot find room");
					}

					if(tHash.containsKey(l.getId())){
						l.setTeacher(tHash.get(l.getId()));
					}else{
						logger.info("Cannot find teacher");
					}

					if(sHash.containsKey(l.getId())){
						for(Student s: sHash.get(l.getId())){
							l.getStudents().add(s);
							if(!s.getPaid()&&!s.getIsMkup()){
								error = true;
							}
						}
					}else{
						logger.info("Cannot find student");
					}
					if(!error){
						list.add(l);
					}

				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		}

		Collections.sort(list);

		return list;

	}

	public static HashMap<String, Teacher> stfMapToActHash(List<StaffMap> list){
		HashMap<String, Teacher> map = new HashMap<String, Teacher>();

		for(StaffMap sMap: list ){
			Teacher t =  new Teacher(sMap);
			if(map.containsKey(sMap.getActivityId())){
				String aMsg = "Same activity ID: %s is found";
				String tMsg = "Teacher: %s is replaced with Teacher: %s";
				logger.warn(String.format(aMsg, sMap.getActivityId()));
				logger.warn(String.format(tMsg, map.get(sMap.getActivityId()).getName(),t.getName()));
				map.replace(sMap.getActivityId(),t);
			}else{
				map.put(sMap.getActivityId(), t);
			}

		}

		return map;
	}

	public static HashMap<String, List<Student>> custMapToActHash(List<CustomerMap> list){
		HashMap<String, List<Student>> map = new HashMap<String, List<Student>>();

		for(CustomerMap cMap: list){
			Student s = new Student(cMap);
			if(map.containsKey(cMap.getActivityId())){
				map.get(cMap.getActivityId()).add(s);
			}else{
				List<Student> tList = new ArrayList<Student>();
				tList.add(s);
				map.put(cMap.getActivityId(), tList);
			}

		}

		return map;
	}

	public static HashMap<String, Room> facMapToActHash(List<FacilityMap> list){
		HashMap<String, Room> map = new HashMap<String, Room>();

		for(FacilityMap fMap: list){
			Room r = new Room(fMap);
			if(map.containsKey(fMap.getActivityId())){
				String aMsg = "Same activity ID: %s is found";
				String rMsg = "Room: %s is replaced with Room: %s";
				logger.warn(String.format(aMsg, fMap.getActivityId()));
				logger.warn(String.format(rMsg, map.get(fMap.getActivityId()).getName(),r.getName()));
				map.replace(fMap.getActivityId(), r);
			}else{
				map.put(fMap.getActivityId(), r);
			}

		}

		return map;
	}
	
	public static Integer lsonDuration(Lesson l){
		return  minDiffBtwDates(l.getStartDateTime(),l.getEndDateTime());
	}
	
	public static Integer minDiffBtwDates(Date d1, Date d2){
		Long diff = Math.abs(d1.getTime() - d2.getTime())/1000/60;
		return diff.intValue() ;
	}
	
	public static Integer secDiffBtwDates(Date d1, Date d2){
		Long diff = Math.abs(d1.getTime() - d2.getTime())/1000;
		return diff.intValue() ;
	}
	
	public static HashMap<Date, List<Lesson>> lsonsToDateMap(List<Lesson> list){
		HashMap<Date, List<Lesson>> map = new HashMap<Date,List<Lesson>>();
		
		for(Lesson l:list){
			Date date = MasteryUtil.getPlainCal(l.getStartDateTime()).getTime();
			if(map.containsKey(date)){
				map.get(date).add(l);
			}else{
				List<Lesson> newList = new ArrayList<Lesson>();
				newList.add(l);
				map.put(date, newList);
			}
		}
		
		return map;
	}
	
	public static List<FreeTimeslot> findFreeTimeSlotPerDate(List<Lesson> lsons, WorkHour wkhr, Date date){
		
		logger.info("Find free timeslot per date");
		
		logger.debug("Date="+ SchoolTracsConst.SDF_FULL.format(date));
		
		List<FreeTimeslot> list = new ArrayList<FreeTimeslot>();
		
		Date wkhrStDate = MasteryUtil.copyDate(wkhr.getStTime(), date);
		Date wkhrEdDate = MasteryUtil.copyDate(wkhr.getEdTime(), date);
		
		logger.debug("Wkhr start Date="+ SchoolTracsConst.SDF_FULL.format(wkhrStDate));
		logger.debug("Wkhr end Date="+ SchoolTracsConst.SDF_FULL.format(wkhrEdDate));
		
		Date stComp = wkhrStDate;
		
		if(lsons.size()==0){
			list.add(new FreeTimeslot(wkhrStDate, wkhrEdDate));
			return list;
		}
		
		boolean isAllLsonBeforeWkhr = true;
		
		for(int i=0;i<lsons.size();i++){
			
			Lesson l = lsons.get(i);
			
			logger.debug("Lesson l = " + l.toString());
			
			//only cares about lesson within working hour
			if(l.getStartDateTime().after(wkhrStDate)||l.getStartDateTime().equals(wkhrStDate)){
				isAllLsonBeforeWkhr = false;
				FreeTimeslot t1 = new FreeTimeslot();
				Date startDateTime = new Date();
				startDateTime.setTime(stComp.getTime());
				t1.setStartDateTime(startDateTime);
				logger.debug("start Date time="+ SchoolTracsConst.SDF_FULL.format(startDateTime));
				if(l.getStartDateTime().before(stComp) || l.getStartDateTime().equals(stComp)){
					t1.setStartDateTime(l.getEndDateTime());
				}else{
					logger.debug("free timesolt add");
					t1.setEndDateTime(l.getStartDateTime());
					logger.debug("t1=" + t1);
					list.add(t1);
					stComp = l.getEndDateTime();
				}
				
				//if it is last lesson
				if(i == lsons.size()-1){
					//the lesson is before end working hour
					if(l.getEndDateTime().before(wkhrEdDate)){
						logger.debug("free timesolt add");
						FreeTimeslot t2 = new FreeTimeslot();
						t2.setStartDateTime(l.getEndDateTime());
						t2.setEndDateTime(wkhrEdDate);
						logger.debug("t2=" + t2);
						list.add(t2);
					}
				}
			}	
		}
		
		//if all lessons are before working hour, the whole working hour will be free timeslot
		if(isAllLsonBeforeWkhr){
			list.add(new FreeTimeslot(wkhrStDate, wkhrEdDate));
		}
		
		return list;
		
	}
	
	public static ObjectMapper getObjMapper(){
		ObjectMapper om = new ObjectMapper();
		om.setSerializationInclusion(Include.NON_NULL);
		om.enable(SerializationFeature.INDENT_OUTPUT);
		return om;
	}
		
}
