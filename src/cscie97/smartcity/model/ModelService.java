package cscie97.smartcity.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ModelService {

	private List<Person> personList;

	private List<City> cityList;

	public ModelService () {
		this.cityList = new ArrayList<City>();
	}

	public String processCity(City city) throws ModelServiceException {

		// Check if a city already exists with the new city's id   
		if (this.cityList.isEmpty() == false) {
			for (City c:this.cityList) {

				// If the provided city id is already used, throw exception.
				if (c.getId().equals(city.getId())) {
					throw new ModelServiceException(
						"ModelService", "Failed while creating new city, ID already used"
					);
				}
			}
		}

        this.cityList.add(city);
		return city.getId();
	}

	public City getCity(String id) throws ModelServiceException {
		if (id == null) {
            throw new ModelServiceException(
                "ModelService", "Failed while getting city, provided ID null"
            );
        }
        for (City c:this.cityList) {
            // Return city that matches provided id
            if (c.getId().equals(id)) {
                return c;
            }
        }

        return null;
	}

	// Adds a new device to the given city, this is modified from the design document
	// .. modified from the design document to simplify code.
	public String defineDevice(IOTDevice device, String cityId) throws ModelServiceException {
		// Retrieve the city object, handling edgecases accordingly
		City city = this.getCity(cityId);
		if (city == null) {
			throw new ModelServiceException(
                "ModelService", "Failed while getting city, recieved null value."
            );
		}

		city.addIoT(device);
		System.out.println("City now has the following devices: " + this.getDevices(cityId));

		return device.getId();
	}

	public IOTDevice getDevice(String cityId, String deviceId) throws ModelServiceException {
		if (cityId == null) {
            throw new ModelServiceException(
                "ModelService", "Failed while getting city, provided ID null"
            );
		}
		
		if (deviceId == null) {
            throw new ModelServiceException(
                "ModelService", "Failed while getting IoT device, provided ID null"
            );
		}
		
		City cityResult = null;
        for (City c:this.cityList) {
            // Return city that matches provided id
            if (c.getId().equals(cityId)) {
                cityResult = c;
            }
		}
		return null;
		// todo need to search all iot devices stored in this city 
		/*for (IOTDevice) {

		}*/
	}

	public Map<String, VirtualIOT> getDevices(String cityId) throws ModelServiceException {
		if (cityId == null) {
            throw new ModelServiceException(
                "ModelService", "Failed while getting city, provided ID null"
            );
		}
		City city = null;
		for (City c:this.cityList) {
			if (c.getId().equals(cityId)){
				city = c;
			}
		}
		
		if (city != null) {
			return city.getIOTDeviceMap();
		} else {
			return null;
		}
	}

	public void generateEvent(String cityId, String deviceId, Event event) {

	}

	public void deviceOutput(String cityId, String deviceId, Event event) {

	}

	public String processPerson(Person person) {
		return null;
	}

	public Person getPerson(String personId) {
		return null;
	}

}
