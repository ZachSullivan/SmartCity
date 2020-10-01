package cscie97.smartcity.model;

public class StreetLight extends IOTDevice {

	private int brightness;

	public StreetLight(String id, Location location, boolean enabled, int brightness) {
		super(id, enabled, location);
		this.brightness = brightness;
	}

	public int getBrightness () {
		return this.brightness;
	}
}
