package cscie97.smartcity.model;

/**
* The device class provides a variety of services to both city residents and visitors, 
* including: Street Lights, Parking Meters, Street Signs, Information Kiosk, Robots, and Vehicles. 
*
* @author  Zachary Sullivan
* @since   2020-10-05 
*/

public abstract class IOTDevice {

	// Current status of the device, indicating current usage
	private enum Status {
		ONLINE,
		OFFLINE
	}

	private String id;
	private Status status;
	private boolean enabled;
	private Event currentEvent;
	private Location location;

	public IOTDevice (String id, boolean enabled, Location location) {
		this.id = id;
		this.status = Status.ONLINE;
		this.enabled = enabled;
		this.location = location;
	}
	// The following methods are overloaded to allow for up-classed vehicle objects to be readilly accessable
	//Overload
	public int getFee(){return 0;}
	//Overload
	public String getActivity() {return null;}
	//Overload
	public int getCapacity(){return 0;}

	public String getId() {
		return this.id;
	}

	public boolean getEnabled() {
		return this.enabled;
	}

	public Status getStatus() {
		return this.status;
	}

	public Location getLocation() {
		return this.location;
	}

	public Event getCurrentEvent() {
		return this.currentEvent;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public void setCurrentEvent(Event event) {
		this.currentEvent = event;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled; 
	}

	public String toString() { 
        String result = (
			"[IoT device: " + this.getId() + "]" +
			"\nAction: " + (this.getCurrentEvent() != null ? this.getCurrentEvent() : this.getStatus())
			
        ); 
        return result;
    }

}
