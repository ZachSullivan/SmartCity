package cscie97.smartcity.model;

/**
* Streetlights illuminate specific locations in a city. 
* The streetlight class extend the baseline device functionality, 
* by offering an adjustable brightness value.
*
* @author  Zachary Sullivan
* @since   2020-10-05 
*/
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
			super.toString() + " is a Street Light" +
			"\nisEnabled: " + super.getEnabled() +
			"\nBrightness: " + this.getBrightness()
        ); 
        return result;
    }
}
