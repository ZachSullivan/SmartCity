package cscie97.smartcity.model;

/**
* The Visitor class represents an anonymous person
* extending person functionality, by without excess identifying info
*
* @author  Zachary Sullivan
* @since   2020-10-05 
*/
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
