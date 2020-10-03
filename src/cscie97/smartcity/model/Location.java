package cscie97.smartcity.model;

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
