package cscie97.smartcity.model;

import java.util.ArrayList;

public abstract class Vehicle extends IOTDevice {

	public Vehicle(String id, boolean enabled, Location location) {
		super(id, enabled, location);
		// TODO Auto-generated constructor stub
	}

	private String owner;

	private int capacity;

	private ArrayList<Person> passengers;

}
