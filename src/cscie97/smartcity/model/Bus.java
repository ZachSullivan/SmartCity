package cscie97.smartcity.model;

import java.util.ArrayList;

public class Bus extends Vehicle {

	public Bus(String id, boolean enabled, Location location) {
		super(id, enabled, location);
		// TODO Auto-generated constructor stub
	}

	private ArrayList<Location> stops;

}
