package cscie97.smartcity.model;

import java.util.Map;

public class Robot extends IOTDevice {

	public Robot(String id, boolean enabled, Location location) {
		super(id, enabled, location);
		// TODO Auto-generated constructor stub
	}

	private Person issuer;

	private String action;

	private Map<Person, String[]> actionMap;

}