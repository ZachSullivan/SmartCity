package cscie97.smartcity.model;

public class Visitor extends Person {

	public Visitor(String id, String bioMetric, Location location) {
        super(id, bioMetric, location);
	}

    public String toString() { 
        String result = (
            "Visitor: " + super.getId() +
			"\nBioId: " + super.getBioId() +
			"\nLocation: " + super.getLocation()
        ); 
        return result;
    }
}
