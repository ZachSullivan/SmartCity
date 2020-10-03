package cscie97.smartcity.model;

public class InfoKiosk extends IOTDevice {
	private String display;

	public InfoKiosk(String id, Location location, boolean enabled, String image) {
		super(id, enabled, location);
		this.display = image;
	}

	public String getDisplay () {
		return this.display;
	}

	public String toString() { 
        String result = (
			super.toString() + " is an Info Kiosk" +
			"\nisEnabled: " + super.getEnabled() +
			"\nDisplaying: " + this.getDisplay()
        ); 
        return result;
    }
}
