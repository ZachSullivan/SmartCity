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

	public String toString() { 
        String result = (
			super.toString() + " is a Street Light currently enabled: " + super.getEnabled() +
			" Brightness: " + this.getBrightness()
        ); 
        return result;
    }
}
