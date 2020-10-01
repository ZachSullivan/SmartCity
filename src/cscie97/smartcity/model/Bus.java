package cscie97.smartcity.model;

import java.util.ArrayList;

public class Bus extends Vehicle {

	public Bus(String id, Location location, boolean enabled, String activity, int capacity, int fee) {
        super(id, enabled, location, activity, capacity, fee);
    }

	private ArrayList<Location> stops;

}
