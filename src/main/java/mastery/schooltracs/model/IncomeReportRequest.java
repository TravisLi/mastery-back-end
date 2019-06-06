package mastery.schooltracs.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import mastery.schooltracs.util.SchoolTracsConst;

@JsonIgnoreProperties(ignoreUnknown = true)
public class IncomeReportRequest {

	/*"1":["Income_Report.getData",{"centerId":3,"start":"2019-05-23 00:00:00","end":"2019-05-24 23:59:00"}]*/
	
	private Integer reqSeq;
	private String task;
	private IncomeRptOpt incomeRptOpt;
	
	
	public IncomeReportRequest(Integer reqSeq, Integer centreId, String start, String end){
		
		this.reqSeq = reqSeq;
		this.task = SchoolTracsConst.Task.INCOME_RPT.code();
		this.incomeRptOpt = new IncomeRptOpt(centreId, start, end);
		
	}
	
	public static class IncomeRptOpt{
		
		private Integer centerId;
		private String start;
		private String end;
		
		public IncomeRptOpt(Integer centerId, String start, String end){
			this.centerId = centerId;
			this.start = start;
			this.end = end;
		}
		
		public Integer getCenterId() {
			return centerId;
		}
		public void setCenterId(Integer centerId) {
			this.centerId = centerId;
		}
		public String getStart() {
			return start;
		}
		public void setStart(String start) {
			this.start = start;
		}
		public String getEnd() {
			return end;
		}
		public void setEnd(String end) {
			this.end = end;
		}
		
	}


	public Integer getReqSeq() {
		return reqSeq;
	}


	public void setReqSeq(Integer reqSeq) {
		this.reqSeq = reqSeq;
	}


	public String getTask() {
		return task;
	}


	public void setTask(String task) {
		this.task = task;
	}


	public IncomeRptOpt getIncomeRptOpt() {
		return incomeRptOpt;
	}


	public void setIncomeRptOpt(IncomeRptOpt incomeRptOpt) {
		this.incomeRptOpt = incomeRptOpt;
	}

}
