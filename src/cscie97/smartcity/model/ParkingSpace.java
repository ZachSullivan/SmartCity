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

}
