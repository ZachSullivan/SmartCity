package cscie97.smartcity.model;

public abstract class IOTDevice {

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
		//if (this.enabled == false) {
			//this.status
		//}
	}

	public String toString() { 
        String result = (
			"[IoT device: " + this.getId() + "]" +
			"\nAction: " + (this.getCurrentEvent() != null ? this.getCurrentEvent() : this.getStatus())
			
        ); 
        return result;
    }

}
