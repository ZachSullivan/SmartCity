package cscie97.smartcity.model;

/**
* The Bus class, extends the Vehicle class
* Each bus provides the same core functionality as a car, but allows recording of stops.
* NOTE: The array of stops is out of scope for the requirements of assignment 02
* @author  Zachary Sullivan
* @since   2020-10-05 
*/

import java.util.ArrayList;

public class Bus extends Vehicle {

	public Bus(String id, Location location, boolean enabled, String activity, int capacity, int fee) {
        super(id, enabled, location, activity, capacity, fee);
    }

	// Records each stop made by the bus.
	// Added for future support of bus stops, outside scope of assignment2
	private ArrayList<Location> stops;

	public String toString() { 
        String result = (
			super.toString() + " is a Bus" + 
			"\nisEnabled: "+ super.getEnabled() +
            "\nCapacity: " + super.getCapacity() + 
            "\nFee: " + super.getFee() + 
            "\nPerforming: " + super.getActivity()
        ); 
        return result;
    }
}
