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
	public String getId() {
		return this.id;
	}
}
