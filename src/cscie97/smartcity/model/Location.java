package cscie97.smartcity.model;

/**
* The location class specifies the latitude and longitude of the given entity
*
* @author  Zachary Sullivan
* @since   2020-10-05 
*/
public class Location {

	// Note opted to change params to floats from strings per review feedback
	private float latitude;
	private float longitude;

	public Location (float laititude, float longitude) {
		this.latitude = laititude;
		this.longitude = longitude;
	}

	public float getLatitude () {
		return this.latitude;
	}

	public float getLongitiude () {
		return this.longitude;
	}
	public String toString() { 
        String result = (
            "Location [lat: " + this.getLatitude() + 
			"],[long: " + this.getLongitiude() + "]"
        ); 
        return result;
    }
}
