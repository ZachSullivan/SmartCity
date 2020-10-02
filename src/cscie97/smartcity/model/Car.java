package cscie97.smartcity.model;

public class Car extends Vehicle {

	public Car(String id, Location location, boolean enabled, String activity, int capacity, int fee) {
        super(id, enabled, location, activity, capacity, fee);

    }

    public String toString() { 
        String result = (
			super.toString() + " is a Car currently enabled: " + super.getEnabled() +
            " Capacity: " + super.getCapacity() + 
            " Fee: " + super.getFee() + 
            " Performing: " + super.getActivity()
        ); 
        return result;
    }

}
