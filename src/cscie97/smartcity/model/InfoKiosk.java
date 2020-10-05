package cscie97.smartcity.model;

/**
* The Infokiosk class offer specific information that resolves a user query via a dynamic text display. 
* The infokiosk class extend the baseline iot device functionality, 
* by offering a readable string display variable.
*
* @author  Zachary Sullivan
* @since   2020-10-05 
*/
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
