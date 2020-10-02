package cscie97.smartcity.model;

import java.util.Map;

public class Robot extends IOTDevice {

	private Person issuer;
	private String action;
	private Map<Person, String[]> actionMap;

	public Robot(String id, Location location, boolean enabled, String activity) {
		super(id, enabled, location);
		this.action = activity;
	}

	public String getAction() {
		return this.action;
	}

	public String toString() { 
        String result = (
			super.toString() + " is a Robot currently enabled: " + super.getEnabled() +
			" Performing: " + this.getAction()
        ); 
        return result;
    }

}