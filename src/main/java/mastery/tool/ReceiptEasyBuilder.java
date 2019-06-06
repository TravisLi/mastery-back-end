package mastery.tool;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.ClientProtocolException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.common.collect.Lists;

import mastery.schooltracs.core.SchoolTracsAgent;
import mastery.schooltracs.core.SchoolTracsConn;
import mastery.schooltracs.model.IncomeReportData;
import mastery.schooltracs.model.Invoice;
import mastery.schooltracs.model.InvoiceItem;
import mastery.schooltracs.model.InvoiceItemDetail;
import mastery.schooltracs.util.SchoolTracsConst;
import mastery.schooltracs.util.SchoolTracsConst.Centre;
import mastery.schooltracs.util.SchoolTracsUtil;
import mastery.tool.model.ReceiptEasyRecord;

public class ReceiptEasyBuilder {

	private static final Logger logger = LoggerFactory.getLogger(ReceiptEasyBuilder.class);
	private static SimpleDateFormat SDF_DATE = new SimpleDateFormat("dd/MM/yyyy");
	private static SimpleDateFormat SDF_INPUT_DATE = new SimpleDateFormat("yyyy-MM-dd");
	private static SimpleDateFormat SDF_OUTPUT_DATE = new SimpleDateFormat("dd-MM-yyyy");
	private static String NA = "N/A";
	private static String BASE_PATH = "C:\\ReceiptEasyBuilder";
	private static String TEMPLATE_FILE = BASE_PATH + "\\ReceiptEasyTemplate.xlsx";
	private static String START_TIME = " 00:00:00";
	private static String END_TIME = " 23:59:59";
	private static String ENROLL_MONTH_TEMPLATE = "%s年%s月份";
	private static String MONTH_RANGE_TEMPLATE = "(%s月1日至%s月%s日)";
	private static String OUTPUT_FILE_TEMPLATE = BASE_PATH + "//%s_ReceiptEasyRecord_%s.xlsx";
	private SchoolTracsAgent agent;

	public ReceiptEasyBuilder() throws IOException{
		SchoolTracsConn conn = new SchoolTracsConn("travisli@masteryoim","24643466");
		this.agent = new SchoolTracsAgent(conn);

	}

	public void run(String fromDate, String toDate) throws JsonParseException, JsonMappingException, ClientProtocolException, IOException{

		for(Centre centre: Centre.values()){
			generateExcelFile(centre,fromDate,toDate);
		}
	}

	private void generateExcelFile(Centre centre, String fromDateStr, String toDateStr) throws JsonParseException, JsonMappingException, ClientProtocolException, IOException{

		String fromDateTimeStr = fromDateStr + START_TIME;
		String toDateTimeStr = toDateStr + END_TIME;
		
		/*fromDateTimeStr = fromDateStr + " 17:20:00";
		toDateTimeStr = toDateStr + " 17:30:00";*/
		
		List<IncomeReportData> dataList = agent.getIncomeReport(centre.id(), fromDateTimeStr, toDateTimeStr);
		List<ReceiptEasyRecord> recordList = new ArrayList<ReceiptEasyRecord>();
		for(IncomeReportData data:dataList){
			logger.debug("studentName=" + data.getStudentName());
			logger.debug("paymentDate=" + data.getPaymentDate());
			logger.debug("courseFeeStr=" + data.getCourseFee());
			logger.debug("receiptDetail=" + data.getReceiptDetail());
			String courseFeeStr = data.getCourseFee();
			if(StringUtils.isNotBlank(courseFeeStr)){
				BigDecimal courseFee = new BigDecimal(courseFeeStr);
				if(courseFee.compareTo(BigDecimal.ZERO)>0){

					List<InvoiceItem> invItemList = this.getInvItems(data.getInvoice());

					for(InvoiceItem item: invItemList){
						recordList.addAll(convertInvoiceItemList(data, item, centre));
					}

				}
			}		
		}

		recordList = Lists.reverse(recordList);
		writeExcelFile(centre, recordList,fromDateStr, toDateStr);

	}

	private List<InvoiceItem> getInvItems(String invStr){

		List<InvoiceItem> invItemList = new ArrayList<InvoiceItem>();
		List<String> invNoList = new ArrayList<String>();

		if(invStr.contains(",")){
			String[] array = invStr.split(",");
			for(String invNo:array){
				invNoList.add(invNo);
			}
		}else{
			invNoList.add(invStr);
		}

		for(String invNo: invNoList){
			try {
				Invoice inv = agent.schInvByInvNo(invNo);
				if(inv!=null){
					invItemList.addAll(agent.schInvItmByInvId(inv.getId()));
				}
			} catch (Exception e) {
				logger.error(e.getMessage(),e);
				e.printStackTrace();
			}
		}

		return invItemList;

	}


	private static void writeExcelFile(Centre centre, List<ReceiptEasyRecord> recordList, String fromDateStr, String toDateStr) throws IOException{

		FileInputStream excelFile = new FileInputStream(new File(TEMPLATE_FILE));
		Workbook workbook = new XSSFWorkbook(excelFile);
		CellStyle style = workbook.createCellStyle();
		DataFormat df = workbook.createDataFormat();
		style.setDataFormat(df.getFormat("d/m/yyyy hh:mm"));

		int rowIdx = 1;

		Sheet sheet = (Sheet) workbook.getSheetAt(0);

		for(ReceiptEasyRecord record:recordList){
			Row row = sheet.createRow(rowIdx++);

			int cellIdx = 0;

			for(Field f:ReceiptEasyRecord.class.getDeclaredFields()){
				f.setAccessible(true);

				Cell cell = row.createCell(cellIdx++);

				try {
					if(f.getType()==String.class){
						cell.setCellValue((String)f.get(record));
					}else if(f.getType()==BigDecimal.class){
						BigDecimal value = (BigDecimal)f.get(record);
						cell.setCellValue(value.setScale(2, RoundingMode.HALF_UP).doubleValue());
					}else if(f.getType()==Date.class){

						cell.setCellStyle(style);
						cell.setCellValue((Date)f.get(record));

					}

				} catch (IllegalArgumentException e) {
					logger.error(e.getMessage(),e);
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					logger.error(e.getMessage(),e);
					e.printStackTrace();
				}

			}
		}

		String fileDate = fromDateStr;
		if(!fromDateStr.equals(toDateStr)){
			fileDate = fromDateStr + "_" + toDateStr;
		}
		
		try {
			String fileName = String.format(OUTPUT_FILE_TEMPLATE,centre.name(),fileDate);
			FileOutputStream outputStream = new FileOutputStream(fileName);
			workbook.write(outputStream);
			workbook.close();
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage(),e);
			e.printStackTrace();
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
			e.printStackTrace();
		}


	}

	private static List<ReceiptEasyRecord> convertInvoiceItemList(IncomeReportData data, InvoiceItem item, Centre centre){

		List<ReceiptEasyRecord> resultList = new ArrayList<ReceiptEasyRecord>();

		InvoiceItemDetail detail = digestInvoiceItemDetail(item.getDetail());

		if(detail!=null){
			
			List<Date> lessonDateList = getLessonDateList(detail.getDateTimeStr(), item.getCreated());

			BigDecimal paidFee = new BigDecimal(item.getPaid());

			BigDecimal lessonFee = paidFee;
			
			if(lessonDateList.size()>0){
				lessonFee = paidFee.divide(new BigDecimal(lessonDateList.size()),4,RoundingMode.HALF_UP);
			}
			
			HashMap<String,Integer> map = getLessonsPerMonth(lessonDateList);
			
			//mean no lesson detail
			if(map.isEmpty()){
				
				ReceiptEasyRecord record = new ReceiptEasyRecord();
				try {
					record.setEnrollDate(SchoolTracsConst.SDF_FULL.parse(data.getPaymentDate()));
				} catch (ParseException e) {
					logger.error(e.getMessage(),e);
					e.printStackTrace();
				}
				record.setBranchCode(centre.name());
				record.setReceiptNo(data.getReceiptNum());
				record.setTeacherName(detail.getTeacher());
				record.setStudentName(data.getStudentName().substring(2));
				record.setStudentlevel(SchoolTracsUtil.translateLevel(data.getLevel()));
				record.setLessonName(item.getName().trim());
				
				Calendar cal = Calendar.getInstance();
				
				if(detail.getDateTimeStr().toLowerCase().contains("september")){
					cal.set(Calendar.MONTH, Calendar.SEPTEMBER);
					String yearStr = Integer.toString(cal.get(Calendar.YEAR));
					String monthStr = "9";
					record.setEnrollYear(yearStr);
					String trimMonthStr = Integer.toString((Integer.parseInt(monthStr)));
					String enrollMonth = String.format(ENROLL_MONTH_TEMPLATE, yearStr, trimMonthStr);
					int endDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
					String monthRange = String.format(MONTH_RANGE_TEMPLATE,trimMonthStr,trimMonthStr,endDay);
					enrollMonth = enrollMonth + monthRange;
					record.setEnrollMonth(enrollMonth);		
				}else{
					record.setEnrollYear("");
					record.setEnrollMonth("");
				}
					
				record.setSatSunTime(NA);
				record.setClassTime(NA);
				record.setMonTime(NA);
				record.setTueTime(NA);
				record.setWedTime(NA);
				record.setThuTime(NA);
				record.setFriTime(NA);
				
				record.setMonthlyFee(new BigDecimal(item.getPrice()).multiply(new BigDecimal(item.getQuantity())));
				record.setDiscountFee(new BigDecimal(item.getTotal()));
				
				record.setLessonStartDay("0");
				record.setNoOfLessons("0");
				record.setCollectedFee(lessonFee);

				resultList.add(record);
			}else{
				for(String key: map.keySet()){

					String[] array = key.split("-");
					String monthStr = array[0];
					String yearStr = array[1];

					ReceiptEasyRecord record = new ReceiptEasyRecord();
					try {
						record.setEnrollDate(SchoolTracsConst.SDF_FULL.parse(data.getPaymentDate()));
					} catch (ParseException e) {
						logger.error(e.getMessage(),e);
						e.printStackTrace();
					}
					record.setBranchCode(centre.name());
					record.setReceiptNo(data.getReceiptNum());
					record.setTeacherName(detail.getTeacher());
					record.setStudentName(data.getStudentName().substring(2));
					record.setStudentlevel(SchoolTracsUtil.translateLevel(data.getLevel()));
					record.setLessonName(item.getName().replace(detail.getTeacher()+":", "").trim().replaceAll("\\(([a-zA-Z]{1}[0-9]{1}-[a-zA-Z]{1}[0-9]{1})\\)", "").trim());
					record.setEnrollYear(yearStr);

					String trimMonthStr = Integer.toString((Integer.parseInt(monthStr)));
					String enrollMonth = String.format(ENROLL_MONTH_TEMPLATE, yearStr, trimMonthStr);
					String dateStr = "1-" + key;
					try {
						Date date = SDF_OUTPUT_DATE.parse(dateStr);
						Calendar cal = Calendar.getInstance();
						cal.setTime(date);
						int endDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
						String monthRange = String.format(MONTH_RANGE_TEMPLATE,trimMonthStr,trimMonthStr,endDay);
						enrollMonth = enrollMonth + monthRange;
					} catch (ParseException e) {
						logger.error(e.getMessage(),e);
						e.printStackTrace();
					}

					record.setEnrollMonth(enrollMonth);
					record.setSatSunTime(NA);
					record.setClassTime(NA);
					record.setMonTime(NA);
					record.setTueTime(NA);
					record.setWedTime(NA);
					record.setThuTime(NA);
					record.setFriTime(NA);
					
					record.setMonthlyFee(new BigDecimal(item.getPrice()).multiply(new BigDecimal(item.getQuantity())));
					record.setDiscountFee(new BigDecimal(item.getTotal()));

					Calendar cal = Calendar.getInstance();
					cal.setTime(lessonDateList.get(0));

					record.setLessonStartDay(Integer.toString(cal.get(Calendar.DAY_OF_MONTH)));
					record.setNoOfLessons(map.get(key).toString());
					record.setCollectedFee(lessonFee.multiply(new BigDecimal(map.get(key))).setScale(1, RoundingMode.HALF_UP));

					resultList.add(record);

				}
			}
			
			

		}

		return resultList;
	}

	private static HashMap<String, Integer> getLessonsPerMonth(List<Date> lessonDateList){

		HashMap<String, Integer> map = new HashMap<String, Integer>();

		for(Date date:lessonDateList){

			SimpleDateFormat sdf = new SimpleDateFormat("MM-yyyy");
			String monthYearStr = sdf.format(date);

			if(map.containsKey(monthYearStr)){
				Integer value = map.get(monthYearStr);
				value = value + 1;
				map.put(monthYearStr, value);
			}else{
				map.put(monthYearStr, 1);
			}

		}

		return map;

	}

	private static InvoiceItemDetail digestInvoiceItemDetail(String detail){

		logger.debug(detail);

		if(StringUtils.isNotEmpty(detail)){
			
			String dateTimeStr = detail;
			String teacherStr = "";
			String venueStr = "";		
			
			int teacherIdx = detail.indexOf("Teacher");
			
			if(teacherIdx != -1){
				dateTimeStr = detail.substring(0,teacherIdx).trim();
			}
						
			int venueIdx = detail.indexOf("Venue");

			if(venueIdx != -1){
				teacherStr = detail.substring(teacherIdx,venueIdx).trim();
				venueStr = detail.substring(venueIdx).trim();
			}
			
			logger.debug("datetimeStr=" + dateTimeStr);
			logger.debug("teacherStr=" + teacherStr);
			logger.debug("venueStr=" + venueStr);

			if(teacherStr.contains(":")&&teacherStr.contains(";")){
				teacherStr = teacherStr.substring(teacherStr.indexOf(":")+1, teacherStr.indexOf(";")).trim();
			}
			
			if(venueStr.contains(":")){
				venueStr = venueStr.substring(venueStr.indexOf(":")+1).trim();
			}
			
			logger.debug("teacherStr=" + teacherStr);
			logger.debug("venueStr=" + venueStr);

			return new InvoiceItemDetail(dateTimeStr,teacherStr,venueStr);
		}

		return null;

	}

	private static List<Date> getLessonDateList(String datetimeStr, String invCreated){

		String pattern = "[0-9]{2}/[0-9]{2}" ;

		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(datetimeStr);

		Calendar cal = Calendar.getInstance();
		
		try {
			Date createDate = SchoolTracsConst.SDF_FULL.parse(invCreated);
			cal.setTime(createDate);
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		Integer currentYear = cal.get(Calendar.YEAR);
		Integer currentMonth = cal.get(Calendar.MONTH)+1;

		List<Date> resultList = new ArrayList<Date>();

		while(m.find()){

			String ddmmStr = datetimeStr.substring(m.start(), m.end());

			try{

				String[] array = ddmmStr.split("/");
				String mmStr = array[1];

				Integer receiptMonth = Integer.parseInt(mmStr);
				Integer year = new Integer(currentYear);

				//date forward for 3 months
				if((receiptMonth<currentMonth)&&((currentMonth-receiptMonth)>8)){
					year = year + 1;
				}

				//date back for 3 months
				if((receiptMonth<currentMonth)&&((currentMonth-receiptMonth)<4)){
					year = year - 1;
				}


				String ddmmyyyyStr = ddmmStr + "/" + year.toString();

				Date date = SDF_DATE.parse(ddmmyyyyStr);

				resultList.add(date);


			}catch(Exception e){
				e.printStackTrace();
				logger.error(e.getMessage(),e);
				logger.error(ddmmStr + " will not be added to list");
			}


		}

		return resultList;

	}

	public static void main(String[] args) throws IOException {

		String startDateStr = SDF_INPUT_DATE.format(new Date());
		String endDateStr = SDF_INPUT_DATE.format(new Date());

		/*String startDateStr = "2019-06-02";
		String endDateStr = "2019-06-05";*/

		if(args.length==0){
			logger.info("No start date and end date is found, today date will be used");
		}else if(args.length!=2){
			logger.error("Please enter start date and end date");
			System.exit(0);
		}else{
			startDateStr = args[0];
			endDateStr = args[1];
		}

		try{
			SDF_INPUT_DATE.parse(startDateStr);
		}catch(ParseException e){
			logger.error("Start date is not in yyyy-MM-dd format");
			System.exit(0);
		}

		try{
			SDF_INPUT_DATE.parse(endDateStr);
		}catch(ParseException e){
			logger.error("End date is not in yyyy-MM-dd format");
			System.exit(0);
		}


		ReceiptEasyBuilder builder = new ReceiptEasyBuilder();
		builder.run(startDateStr, endDateStr);


	}

}
