package cscie97.smartcity.model;

public class ParkingSpace extends IOTDevice {

	public ParkingSpace(String id, boolean enabled, Location location) {
		super(id, enabled, location);
		// TODO Auto-generated constructor stub
	}

	private int fee;

	private Vehicle vehicle;

	private Person owner;

	private static int entryTime;

	private int exitTime;

	public void generateBill() {

	}

}
