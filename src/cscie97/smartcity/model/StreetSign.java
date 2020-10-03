package cscie97.smartcity.model;

public class StreetSign extends IOTDevice {
	private String display;

	public StreetSign(String deviceId, Location deviceLocation, boolean enabled, String text) {
		super(deviceId, enabled, deviceLocation);
		this.display = text;
	}

	public String getDisplay() {
		return this.display;
	}

	public String toString() { 
        String result = (
			super.toString() + " is a Street Sign" +
			"\nLocated: " + super.getLocation() +
			"\nisEnabled: " + super.getEnabled() +
			"\nDisplaying: " + this.getDisplay()
        ); 
        return result;
    }
}
