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

				// NEED TO CHECK IF THE CITY LOCATIONS OVERLAP

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
	// Note rather than updating old devices in run time, 
	// .. I've opted to simply create new object instances to replace the old one
	public String defineDevice(IOTDevice device, String cityId) throws ModelServiceException {
		// Retrieve the city object, handling edgecases accordingly
		City city = this.getCity(cityId);
		if (city == null) {
			throw new ModelServiceException(
                "ModelService", "Failed while getting city, recieved null value."
            );
		}

		city.addIoT(device);
		//System.out.println("City now has the following devices: " + this.getDevices(cityId));

		return device.getId();
	}

	// Removes a specific IOT device from a given city, returns the device that was removed.
	public VirtualIOT destroyDevice (IOTDevice device, String cityId) throws ModelServiceException {
		City city = this.getCity(cityId);
		if (city == null) {
			throw new ModelServiceException(
                "ModelService", "Failed while getting city, recieved null value."
            );
		}
		return city.removeIoT(device);

	}

	// Update's an IOT device's universal parameter
	// .. NOTE updating device child specific components requires the instanciation of a new object
	public void updateDevice (IOTDevice device, Location location, boolean enabled) throws ModelServiceException {
		if (location == null) {
			throw new ModelServiceException(
                "ModelService", "Failed while updating device, recieved null parameter."
            );
		}
		device.setLocation(location);
		device.setEnabled(enabled);
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
		IOTDevice result = null;
		// Search all iot devices stored in this city 
		for (Map.Entry<String, VirtualIOT> deviceEntry:cityResult.getIOTDeviceMap().entrySet()) {
			// Check if current device matches requested ID
			if (deviceEntry.getKey().equals(deviceId)) {
				result = deviceEntry.getValue().getPhysicalDevice();
				break;
			}
		}

		if (result == null) {
			throw new ModelServiceException(
                "ModelService", "Failed while getting IoT device, no device found."
            );
		} else {
			return result;
		}

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
