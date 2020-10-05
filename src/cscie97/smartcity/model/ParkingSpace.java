package cscie97.smartcity.model;

/**
* Parking spaces record the vehicle currently occupying the space and bills the owner against a given hourly rate. 
* Therefore, the parking space class extends the functionality of the device class 
* by recording the vehicle currently using the space, the hourly rate to charge the occupant. 
* As well as a method for generating a new billing record once the occupant leaves the space.
* NOTE: This level of functionality is currently OUTSIDE the scope of assignment 02 
* .. and currently serves as a placeholder for future content
*
* @author  Zachary Sullivan
* @since   2020-10-05 
*/
public class ParkingSpace extends IOTDevice {

	private int fee;
	private Vehicle vehicle;
	private Person owner;
	private int entryTime; 	// Note changed from static to non static
	private int exitTime;

	public ParkingSpace(String id, Location location, boolean enabled, int rate) {
		super(id, enabled, location);
		this.fee = rate;
	}

	// Implementation out of scope for this assignment
	public void generateBill() {
	}

	public int getRate() {
		return this.fee;
	}

	public String toString() { 
        String result = (
			super.toString() + " is a Parking Space" +
			"\nisEnabled: " + super.getEnabled() +
			"\nLocation: "+ super.getLocation() +
			"\nCharging: $" + this.getRate()
        ); 
        return result;
    }

}
