package cscie97.smartcity.model;

/**
* The Car class, extends the Vehicle class
* Each car provides the same core functionality as a bus, but does NOT record stops.
* @author  Zachary Sullivan
* @since   2020-10-05 
*/

public class Car extends Vehicle {

	public Car(String id, Location location, boolean enabled, String activity, int capacity, int fee) {
        super(id, enabled, location, activity, capacity, fee);
    }

    public String toString() { 
        String result = (
            super.toString() + " is a Car" +
            "\nisEnabled: "+ super.getEnabled() +
            "\nCapacity: " + super.getCapacity() + 
            "\nFee: " + super.getFee() + 
            "\nPerforming: " + super.getActivity()
        ); 
        return result;
    }

}
