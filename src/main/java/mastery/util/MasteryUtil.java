package mastery.util;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;

import mastery.model.Lesson;
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
	
	public static Date copyDate(Date stDate, Date src){
		Calendar srcCal = Calendar.getInstance();
		srcCal.setTime(src);
		Calendar cal = Calendar.getInstance();
		cal.setTime(stDate);
		
		cal.set(Calendar.YEAR, srcCal.get(Calendar.YEAR));
		cal.set(Calendar.MONTH, srcCal.get(Calendar.MONTH));
		cal.set(Calendar.DAY_OF_MONTH, srcCal.get(Calendar.DAY_OF_MONTH));
		
		return cal.getTime();
	}
	
	public static String getFormattedLsonTime(Lesson l){
		String s = SchoolTracsConst.SDF_DATE.format(l.getStartDateTime()) + "\n";
		s+= SchoolTracsConst.SDF_TIME.format(l.getStartDateTime()) + "-";
		s+= SchoolTracsConst.SDF_TIME.format(l.getEndDateTime());
		return s;
	}
	
	public static String nullGuard(String src){
		return src!=null?src:"";
	}
	
	public static SchoolTracsConst.Level nullGuard(SchoolTracsConst.Level src){
		return src!=null?src:SchoolTracsConst.Level.NONE;
	}
	
	public static String pwGen(){
		Random r = new Random();
		String s = StringUtils.EMPTY;
		for(int i=0;i<7;i++){
			Integer d = r.nextInt(10);
			s += d.toString();
		}
		return s;
	}
}
