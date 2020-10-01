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

}