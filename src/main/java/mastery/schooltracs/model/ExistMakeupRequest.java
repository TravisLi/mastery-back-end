package mastery.schooltracs.model;

import mastery.schooltracs.util.SchoolTracsConst.Task;

public class ExistMakeupRequest {

	/* {
	 * "15 ":
	 * ["ActivityCustomer.makeUp",
	 * "401005",
	 * [482327],
	 * 0,
	 * null]}*/
	
	private Integer reqSeq;
	private String task;
	private String toLsonId;
	private String stdLsonId;
	private String unknownNo;
	private String unknownStr;
	
	public ExistMakeupRequest(Integer reqSeq, String toLsonId, String stdLsonId){
		this.reqSeq = reqSeq;
		this.task = Task.EXIST_MAKE_UP.code();
		this.toLsonId = toLsonId;
		this.stdLsonId = stdLsonId;
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

	public String getToLsonId() {
		return toLsonId;
	}

	public void setToLsonId(String toLsonId) {
		this.toLsonId = toLsonId;
	}

	public String getStdLsonId() {
		return stdLsonId;
	}

	public void setStdLsonId(String stdLsonId) {
		this.stdLsonId = stdLsonId;
	}

	public String getUnknownNo() {
		return unknownNo;
	}

	public void setUnknownNo(String unknownNo) {
		this.unknownNo = unknownNo;
	}

	public String getUnknownStr() {
		return unknownStr;
	}

	public void setUnknownStr(String unknownStr) {
		this.unknownStr = unknownStr;
	}
	
}
