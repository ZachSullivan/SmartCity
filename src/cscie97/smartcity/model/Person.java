package cscie97.smartcity.model;

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


