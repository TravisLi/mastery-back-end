package mastery.schooltracs.model;

public class StaffRole {

	private String id;
	private String _srid;
	private Role role;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String get_srid() {
		return _srid;
	}
	public void set_srid(String _srid) {
		this._srid = _srid;
	}
	public Role getRole() {
		return role;
	}
	public void setRole(Role role) {
		this.role = role;
	}
}
