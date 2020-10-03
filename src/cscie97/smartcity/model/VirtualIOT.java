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
	
	public Event getEvent(Event event) {
		return this.phyiscalIoT.getCurrentEvent();
	}

	// Receieves a command from a controller, passes along to a physical IoT device in the form of an Event
	// Note removed getCommand as it is dedundant.
	// Controller that wants to issue a command will just call this method
	public void sendCommand(String command, Person subject) {
		
		Event event = new Event("Command", command, subject);
		// Pass the newly created event to the physical IoT device
		this.sendEvent(event);
	}

	public void sendEvent(Event event) {
		phyiscalIoT.setCurrentEvent(event);
	}

}
