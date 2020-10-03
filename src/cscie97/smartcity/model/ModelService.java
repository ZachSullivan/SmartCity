package cscie97.smartcity.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ModelService {

	private List<Person> personList;

	private List<City> cityList;

	public ModelService () {
		this.personList = new ArrayList<Person>();
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

	public VirtualIOT getDevice(String cityId, String deviceId) throws ModelServiceException {
	//public IOTDevice getDevice(String cityId, String deviceId) throws ModelServiceException {
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

		VirtualIOT result = null;
		//IOTDevice result = null;
		// Search all iot devices stored in this city 
		for (Map.Entry<String, VirtualIOT> deviceEntry:cityResult.getIOTDeviceMap().entrySet()) {
			// Check if current device matches requested ID
			if (deviceEntry.getKey().equals(deviceId)) {
				//result = deviceEntry.getValue().getPhysicalDevice();
				result = deviceEntry.getValue();
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

	// Changed the name from generate  to simulate to better describe what this method does
	// modified Sigature to have this method to actually generate event AND append event, instead of simply appending an event
	public void simulateEvent(String cityId, String deviceId, String type, String action, String personName)
			throws ModelServiceException {

		//Create a new sensor object, with type: TYPE
		Sensor sensor = new Sensor (type);

		//Call create event inside sensor object, passing the value and subject
		Person person = this.getPerson(personName);
		Event event = sensor.createEvent(action, person);
		
		// Pass the newly created event to the virtual IoT device
		VirtualIOT vDevice = this.getDevice(cityId, deviceId);
		vDevice.sendEvent(event);
	}

	public void deviceOutput(String cityId, String deviceId, Event event) {

	}

	// Adds a new person to the model service, this is modified from the design document
	// .. modified from the design document to simplify code.
	// Rather than updating old persons in run time, 
	// .. I've opted to simply create new object instances to replace the old one
	public String definePerson(Person person) throws ModelServiceException {
		// Check if a person already exists with the new person's id   
		if (this.personList.isEmpty() == false) {
			for (Person p:this.personList) {

				// If the provided person id is already used, throw exception.
				if (p.getId().equals(person.getId())) {
					throw new ModelServiceException(
						"ModelService", "Failed while creating new person, ID already used"
					);
				}
			}
		}

        this.personList.add(person);
		return person.getId();
	}




	public Person getPerson(String personId) {
		Person result = null;
		for (Person p:this.personList){
			if (p.getId().equals(personId)){
				result = p;
			}
		}
		return result;
	}

	// Retrieves a person by name, if multiple people exist with that name, returns the first occurance!
	// Note this should only be used in the case where we DON'T know the person ID
	public Person getPersonByName (String name) {
		Person result = null;
		for (Person p:this.personList){
			if (p.getName().equals(name)){
				result = p;
			}
		}
		return result;
	}

	// Removes a specific person from existance, returns boolean if the person that was removed correctly. 
	// pretty dark...
	public boolean destroyPerson(String personId) throws ModelServiceException {
		Person person = this.getPerson(personId);
		if (person == null) {
			throw new ModelServiceException(
                "ModelService", "Failed while getting person, recieved null value."
            );
		}

		if (this.personList.remove(person) == false) {
			throw new ModelServiceException(
                "ModelService", "Failed while removing person, recieved null value."
            );
		} else {
			return true;
		}
	}

}
