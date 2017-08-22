package mastery.whatsapp;

public class WhatsappMsg {

	private String address;
	private String body;
	private String type;
	
	public WhatsappMsg(String address, String body) {
		super();
		this.address = address;
		this.body = body;
		this.type="simple";
	}
	
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	
}
