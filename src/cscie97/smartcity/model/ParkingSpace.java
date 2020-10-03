package cscie97.smartcity.model;

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

	public void generateBill() {

	}

	public int getRate() {
		return this.fee;
	}

	public String toString() { 
        String result = (
			super.toString() + " is a Parking Space" +
			"\nisEnabled: " + super.getEnabled() +
			"\nCharging: " + this.getRate()
        ); 
        return result;
    }

}
