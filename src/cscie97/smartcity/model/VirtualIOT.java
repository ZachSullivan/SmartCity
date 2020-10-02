package cscie97.smartcity.model;

public class VirtualIOT {

	// Modifications made, updated id to be nonstatic, added an object reference to the physical IoT Counterpart
	// .. for easier reference. 
	private String id;
	private IOTDevice phyiscalIoT;

	public VirtualIOT (IOTDevice device) {
		this.id = device.getId();
		this.phyiscalIoT = device;
	}

	public String getId() {
		return this.id;
	}

	public IOTDevice getPhysicalDevice () {
		return this.phyiscalIoT;
	}

	public String getCommand(String command) {
		return null;
	}

	public Event getEvent(Event event) {
		return null;
	}

	public void sendCommand(String command) {

	}

	public void sendEvent(Event event) {

	}

}
