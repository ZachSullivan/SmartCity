package cscie97.smartcity.model;

import java.util.ArrayList;

public abstract class Vehicle extends IOTDevice {

	private String activity; 	// Had to add acivity variable
	private String owner;
	private int capacity;
	private int fee; 			// Had to add a fee variable
	private ArrayList<Person> passengers;

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
