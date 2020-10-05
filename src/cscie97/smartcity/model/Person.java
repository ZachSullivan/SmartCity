package cscie97.smartcity.model;

/**
* The Person class models each person as either a Visitor or a Resident. 
* - A Persons has a unique identification value that is not shared by any other person
* - as well as a biometric identification value. 
* - A Person has a record of their current location in the form of latitude and longitude coordinates.
*
* @author  Zachary Sullivan
* @since   2020-10-05 
*/
public abstract class Person {

	private String id;
	private String bioId;
	private Location location;

	public Person(String id, String bioMetric, Location location) {
		this.id = id;
		this.bioId = bioMetric;
		this.location = location;

	}

	//Overload
	public String getName() {return null;}
	//Overload
	public String getAccount() {return null;}
	//Overload
	public String getPhoneNumber() {return null;}
	//Overload
	public String getRole() {return null;}


	public Location getLocation() {
		return this.location;
	}

	public String getId() {
		return this.id;
	}

	public String getBioId() {
		return this.bioId;
	}
}


