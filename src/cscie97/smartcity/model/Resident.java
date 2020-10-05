package cscie97.smartcity.model;

/**
* The Resident class extends the functionality offered by the Person class,
* by providing additional information regarding the identity of the person
*
* @author  Zachary Sullivan
* @since   2020-10-05 
*/
public class Resident extends Person {

	// Role for the resident
	private enum ResidentRoles {
		ADULT,
		CHILD,
		ADMINISTRATOR;

		public String toString(){
			switch(this){
				case ADULT:
					return "ADULT";
				case CHILD:
					return "CHILD";
				case ADMINISTRATOR:
					return "ADMINISTRATOR";
				default:
					return null;
			}
		}
	}

	private ResidentRoles role;
	private String phoneNumber;
	private String account;
	private String name;

	public Resident(String id, String name, String bioMetric, String phone, String type, Location location, String account) {
		super(id, bioMetric, location);
		this.name = name;
		this.phoneNumber = phone;
		this.account = account;
		this.role = this.setRole(type);
	}

	private ResidentRoles setRole (String type) {
		// Iterate through all roles, return the one that matches our string query
		for (ResidentRoles role : ResidentRoles.values()) {
			if (role.toString().equalsIgnoreCase(type)) {
                return role;
            }
		}
		throw new IllegalArgumentException("Requested Resident role type: " + type + " does not exist");
	}

	public String getAccount() {
		return this.account;
	}

	public String getPhoneNumber() {
		return this.phoneNumber;
	}

	public String getRole() {
		return this.role.toString();
	}

	public String getName() {
		return this.name;
	}

	public String toString() { 
        String result = (
            "Resident: " + super.getId() + 
			" " + this.getName() +
			"\nBioId: " + super.getBioId() +
			"\nLocation: " + super.getLocation() + 
			"\nPhone: " + this.getPhoneNumber() +
			"\nAccount: " + this.getAccount()
        ); 
        return result;
    }
}
