package mastery.schooltracs.util;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mastery.model.Lesson;
import mastery.model.Room;
import mastery.model.Student;
import mastery.model.Teacher;
import mastery.schooltracs.model.Activity;
import mastery.schooltracs.model.CustomerMap;
import mastery.schooltracs.model.FacilityMap;
import mastery.schooltracs.model.SearchResponse;
import mastery.schooltracs.model.StaffMap;

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
			
			boolean sameName = l.getName().equals(src.getName());
			boolean sameFrLvl = l.getFrLvl().equals(src.getFrLvl());
			boolean sameToLvl = l.getToLvl().equals(src.getToLvl());
			
			//the gap between two student meet the requirement
			boolean meetLvl = true;
			
			boolean sameStd = false;
			
			for(Student s: l.getStudents()){
				
				if(master.getId().equals(s.getId())){
					sameStd = true;
				}
				
				int calGap = master.getLvl().code()-s.getLvl().code();
				
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
	
	public static List<Lesson> schRspToLson(SearchResponse sr){
		
		logger.info("Search Response to Lessons start");
		
		List<Lesson> list = new ArrayList<Lesson>();
		
		HashMap<String, Teacher> tHash = stfMapToActHash(sr.getStaffMaps());
		HashMap<String, List<Student>> sHash = custMapToActHash(sr.getCustomerMaps());
		HashMap<String, Room> rHash = facMapToActHash(sr.getFacilityMaps());
		
		for(Activity a: sr.getActivites()){
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
							if(!s.getPaid()){
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
		
		list.sort(new Comparator<Lesson>(){

			@Override
			public int compare(Lesson o1, Lesson o2) {
				
				if(o1.getStartDateTime().before(o2.getStartDateTime())){
					return -1;
				}
				
				if(o1.getStartDateTime().equals(o2.getStartDateTime())){
					return 0;
				}
				
				if(o1.getStartDateTime().after(o2.getStartDateTime())){
					return 1;
				}
				
				return 0;
			}
			
		});
		
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
}
