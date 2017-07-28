package mastery.util;

import java.util.Calendar;
import java.util.Date;

import mastery.schooltracs.util.SchoolTracsConst;

public class MasteryUtil {

	public static Calendar getPlainCal(Date date){
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY,0);
		cal.set(Calendar.MINUTE,0);
		cal.set(Calendar.SECOND,0);
		cal.set(Calendar.MILLISECOND,0);
		return cal;
	}
	
	public static String nullGuard(String src){
		return src!=null?src:"";
	}
	
	public static SchoolTracsConst.Level nullGuard(SchoolTracsConst.Level src){
		return src!=null?src:SchoolTracsConst.Level.NONE;
	}
}
