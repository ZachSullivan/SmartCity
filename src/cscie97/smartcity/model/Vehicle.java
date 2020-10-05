package cscie97.smartcity.model;

/**
* The Vehicle class defines the mobile IoT device that can transport one or more persons around the city. 
* The Vehicle class is subdivided into two subclasses that offer small extensions upon the parent vehicle class
*
* @author  Zachary Sullivan
* @since   2020-10-05 
*/
import java.util.ArrayList;

public abstract class Vehicle extends IOTDevice {

	private String activity; 	// Had to add acivity variable
	private String owner;		// Currently a placeholder for future functitonality
	private int capacity;
	private int fee; 			// Had to add a fee variable
	private ArrayList<Person> passengers;	// Currently a placeholder for future functitonality

	public Vehicle(String id, boolean enabled, Location location, String activity, int capacity, int fee) {
		super(id, enabled, location);
		this.activity = activity;
		this.capacity = capacity;
		this.fee = fee;
	}

	public int getFee() {
		return this.fee;
	}

	public String getActivity() {
		return this.activity;
	}

	public int getCapacity() {
		return this.capacity;
	}

}
