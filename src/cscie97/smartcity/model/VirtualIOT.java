package cscie97.smartcity.model;

/**
* The virtual device class provides a cloud-based representation of 
* .. a physical IoT counterpart device (in effect a digital twin). 
* Virtual devices are responsible for sending new commands and events to the corresponding physical IoT device. 
* Additionally, the virtual IoT device will retrieve commands 
* and events issued by the physical device for relay back to the Model Service.
*
* @author  Zachary Sullivan
* @since   2020-10-05 
*/
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
	
	public Event getEvent() {
		return this.phyiscalIoT.getCurrentEvent();
	}

	/**
	 * Receieves a command from a controller, passes along to a physical IoT device in the form of an Event
	 * Note removed getCommand as it is rededundant, Controller that wants to issue a command will just call this method
	 * 
	 * @param type		The tpe of command to be issued ie. speaker
	 * @param command	The contents of the command
	 */
	public void sendCommand(String type, String command) {
		// If no command type was specfied, set default
		if (type == null) {
			type = "command";
		}

		Event event = new Event(type, command, null);
		// Pass the newly created event to the physical IoT device
		this.sendEvent(event);
	}

	/**
	 * Sends an event object to the given physical Iot Device
	 * @param event		The event to be sent to the physical IoTDevice
	 */
	public void sendEvent(Event event) {
		phyiscalIoT.setCurrentEvent(event);
	}

}
