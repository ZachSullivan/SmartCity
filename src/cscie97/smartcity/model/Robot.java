package cscie97.smartcity.model;

/**
* The Robot class offers action-based assistance to a given user. 
* The robot class extends the device class by recording a requested action. 
*
* @author  Zachary Sullivan
* @since   2020-10-05 
*/
public class Robot extends IOTDevice {

	private String action;

	public Robot(String id, Location location, boolean enabled, String activity) {
		super(id, enabled, location);
		this.action = activity;
	}

	public String getAction() {
		return this.action;
	}

	public String toString() { 
        String result = (
			super.toString() + " is a Robot" +
			"\nisEnabled: " + super.getEnabled() +
			"\nPerforming: " + this.getAction()
        ); 
        return result;
    }

}