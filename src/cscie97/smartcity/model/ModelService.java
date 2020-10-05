package cscie97.smartcity.model;

/**
* The Model Service manages the state of all domain objects (cities, people, devices, etc.)
* and is responsible for the API that facilitates the interaction with these objects.
*
* @author  Zachary Sullivan
* @since   2020-10-05 
*/

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModelService {

	private List<Person> personList;

	private List<City> cityList;

	public ModelService () {
		this.personList = new ArrayList<Person>();
		this.cityList = new ArrayList<City>();
	}

	/**
	 * Create a new city and appends to the mapping of cities in the model service. 
	 * @param city	City to be added
	 * @return	Return the unique identifier for the city.
	 * @throws ModelServiceException
	 */
	public String processCity(City city) throws ModelServiceException {

		if (city == null) {
            throw new ModelServiceException(
                "ModelService", "Failed while defining city, provided null"
            );
		}

		// Check if a city already exists with the new city's id   
		if (this.cityList.isEmpty() == false) {
			for (City c:this.cityList) {

				// Check if city overlaps existing city
				// Obtain the distance between the new city and the current city
				double cDistance = this.distance(c.getLocation(), city.getLocation());

				// Add the person to our results if they're within the city
				if (cDistance <= city.getRadius()){
					throw new ModelServiceException(
						"ModelService", "Failed while creating new city, city overlaps existing city"
					);
				}
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

	/**
	 * Retrieves a specified city by unique id value, 
	 * returns the requested city object. 
	 * Throws a Model Service Exception if the city doesn’t exist.
	 * 
	 * @param id 	id of the city to retrieve
	 * @return 		the city requested
	 * @throws ModelServiceException
	 */
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

	/**
	 * Adds a new device to the given city, this is modified from the design document
	 * .. modified from the design document to simplify code.
	 * Note rather than updating old devices in run time, 
	 * .. I've opted to simply create new object instances to replace the old one
	 * @param device 	the device to add to our city
	 * @param cityid	the id of the city to add our device to
	 * @return 			the id of the device added, null on failure
	 * @throws ModelServiceException
	 */
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

	/**
	 * Removes a specific IOT device from a given city, returns the device that was removed.
	 * @param device 	the device to remove from our city
	 * @param cityid	the id of the city to remove our device from
	 * @return 			the id of the device removed, null on failure
	 * @throws ModelServiceException
	 */
	public VirtualIOT destroyDevice (IOTDevice device, String cityId) throws ModelServiceException {
		City city = this.getCity(cityId);
		if (city == null) {
			throw new ModelServiceException(
                "ModelService", "Failed while getting city, recieved null value."
            );
		}
		return city.removeIoT(device);

	}

	/**
	 * Update's an IOT device's universal parameter
	 * NOTE updating device child specific components requires the instanciation of a new object
	 * I opted for this approach as wanted to limit my class footprint, 
	 * .. and having a method for each entity to update seemed excessive
	 * @param device		The device to update
	 * @param location		The location to update
	 * @param enabled		The device status to update
	 * @throws ModelServiceException
	 */
	public void updateDevice (IOTDevice device, Location location, boolean enabled) throws ModelServiceException {
		if (location == null) {
			throw new ModelServiceException(
                "ModelService", "Failed while updating device, recieved null parameter."
            );
		}
		device.setLocation(location);
		device.setEnabled(enabled);
	}

	/**
	 * Retrieve a specified IoT device for a specific city, return the queried device object. 
	 * Throw a Model Service Exception if the device does not exist in the specified city
	 * @param cityId	The id of the city to retrieve the device from
	 * @param deviceId	The id of the device to retrieve
	 * @return			The device retrieved
	 * @throws ModelServiceException
	 */
	public VirtualIOT getDevice(String cityId, String deviceId) throws ModelServiceException {
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

	/**
	 * Retrieve all IoT device located in a specific city, 
	 * return the mapping of all devices. 
	 * Throw a Model Service Exception if the city does not exist.
	 * @param cityId	The id of the city to retrieve all devices from
	 * @return			All devices found in the given city (as a map of id to devices)
	 * @throws ModelServiceException
	 */
	public Map<String, VirtualIOT> getDevices(String cityId) throws ModelServiceException {
		if (cityId == null) {
            throw new ModelServiceException(
                "ModelService", "Failed while getting city, provided ID null"
            );
		}

		// Obtain the city requested
		City city = this.getCity(cityId);
		
		if (city != null) {

			// Create a new map of all device results
			Map<String, VirtualIOT> results = new HashMap<String, VirtualIOT>();

			// Obtain the distance between the device and the given city
			for (Map.Entry<String, VirtualIOT> deviceEntry:city.getIOTDeviceMap().entrySet()) {
				IOTDevice device = deviceEntry.getValue().getPhysicalDevice();
				double deviceDistance = this.distance(device.getLocation(), city.getLocation());

				// Add the device to our results if they're within the city
				if (deviceDistance <= city.getRadius()){
					results.put(deviceEntry.getKey(), deviceEntry.getValue());
				}
			}

			return results;

		} else {
			return null;
		}
	}

	/**
	 * Generates a new sensory event for a specified IoT device located in the specified city. 
	 * Throws a Model Service Exception if the device or city does not exist.
	 * Changed the name from generate to simulate to better describe what this method does
	 * Modified Sigature to have this method to actually generate event AND append event, instead of simply appending an event
	 * @param cityId		Id of the requested city
	 * @param deviceId		Id of the requested device
	 * @param type			The specifed event sensor type
	 * @param action		The action performed by the event
	 * @param personName	The name of the person read by the sensor (OPTIONAL)
	 * @throws ModelServiceException
	 */
	public void simulateEvent(String cityId, String deviceId, String type, String action, String personName)
			throws ModelServiceException {
		
		if (cityId == null) {
			throw new ModelServiceException(
				"ModelService", "Failed while getting city, provided ID null"
			);
		}

		//Create a new sensor object, with type: TYPE
		Sensor sensor = new Sensor (type);

		//Call create event inside sensor object, passing the value and subject
		Person person = null;
		if (personName != null) {
			person = this.getPerson(personName);
		} 
		Event event = sensor.createEvent(action, person);

		// Pass the newly created event to the virtual IoT device
		VirtualIOT vDevice = this.getDevice(cityId, deviceId);
		vDevice.sendEvent(event);
	}

	/**
	 * Simulates a new device output via a speaker sensor, then returns the event.
	 * If no device specifed then sends event to ALL devices in the city
	 * Modified Sigature to have this method to actually generate event AND append event, 
	 * instead of simply appending an event
	 * @param cityId		Id of the requested city
	 * @param deviceId		Id of the requested device
	 * @param type			The type of sensor performing the output
	 * @param action		The action performed by the sensor
	 * @return 				A mapping of all devices and the event generated
	 * @throws ModelServiceException
	 */
	public Map<VirtualIOT, Event> deviceOutput(String cityId, String deviceId, String type, String action) throws ModelServiceException {
		Map<VirtualIOT, Event> results = new HashMap<VirtualIOT, Event>();

		if (cityId == null) {
            throw new ModelServiceException(
                "ModelService", "Failed while getting city, provided ID null"
            );
		}

		// If the deviceId is null, then output the command to all devices in the city.
		if (deviceId == null) {
			Map <String, VirtualIOT> vDevices = this.getDevices(cityId);
			// Issue the command to all devices found in the city
			for (Map.Entry<String, VirtualIOT> entry:vDevices.entrySet()) {
				VirtualIOT device = entry.getValue();
				device.sendCommand(type, action);
				results.put(device, device.getEvent());
			}
		} else {
			// User has specified a specific device to issue the output command to
			VirtualIOT vDevice = this.getDevice(cityId, deviceId);
			// Issue a new command to the specified device 
			vDevice.sendCommand(type, action);
			results.put(vDevice, vDevice.getEvent());
		}
		return results;
	}

	/**
	 * Adds a new person to the model service, this is modified from the design document
	 * .. modified from the design document to simplify code.
	 * Rather than updating old persons in run time, 
	 * .. I've opted to simply create new object instances to replace the old one
	 * @param person	The person to be created
	 * @return 			The id of the person created, null on failure
	 * @throws ModelServiceException
	 */
	public String definePerson(Person person) throws ModelServiceException {

		if (person == null) {
            throw new ModelServiceException(
                "ModelService", "Failed while defining person, provided null"
            );
		}

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

	/**
	 * Retrieve a specified person object. 
	 * Throw a Model Service Exception if the person does not exist.
	 * @param personId		Id of the person to retrieve
	 * @return				The person retrieved
	 * @throws ModelServiceException
	 */
	public Person getPerson(String personId) throws ModelServiceException {

		if (personId == null) {
            throw new ModelServiceException(
                "ModelService", "Failed while getting person, provided ID null"
            );
		}

		// Obtain the person in the global list of people
		for (Person p:this.personList){
			if (p.getId().equals(personId)){
				return p;
			}
		}

		// If we've reached this point, then no person was found
		throw new ModelServiceException(
			"ModelService", "Failed while getting person, person doesn't exist"
		);
	}

	/**
	 * Retrieves a person by name, if multiple people exist with that name, returns the first occurance!
	 * Note this should only be used in the case where we DON'T know the person ID
	 * @param name		The NON-UNIQUE name of the person to find
	 * @return			The FIRST occurance of the person with given name, null on failure
	 * @throws ModelServiceException
	 */
	public Person getPersonByName (String name) throws ModelServiceException {

		if (name == null) {
            throw new ModelServiceException(
                "ModelService", "Failed while getting person, provided name null"
            );
		}

		Person result = null;
		for (Person p:this.personList){
			if (p.getName().equals(name)){
				result = p;
			}
		}
		return result;
	}

	/**
	 * Retrieves all persons in a given city
	 * @param cityId	The id of the city to get all people within
	 * @return 			A list of all people found in the city, empty on failure
	 * @throws ModelServiceException
	 */
	public List<Person> getPersons(String cityId) throws ModelServiceException {

		if (cityId == null) {
            throw new ModelServiceException(
                "ModelService", "Failed while getting persons, provided cityID null"
            );
		}

		List<Person> results = new ArrayList<Person>();
		City city = this.getCity(cityId);
		for (Person p:this.personList){
			// Obtain the distance between the person and the given city
			double pDistance = this.distance(p.getLocation(), city.getLocation());

			// Add the person to our results if they're within the city
			if (pDistance <= city.getRadius()){
				results.add(p);
			}
		}
		return results;
	}

	/**
	 * Removes a specific person from existance, returns boolean if the person that was removed correctly. 
	 * pretty dark...
	 * @param personId		The id of the person to remove
	 * @return				True if removed, false if failed
	 * @throws ModelServiceException
	 */
	public boolean destroyPerson(String personId) throws ModelServiceException {

		if (personId == null) {
            throw new ModelServiceException(
                "ModelService", "Failed while getting person, provided ID null"
            );
		}

		// Get the person in question
		Person person = this.getPerson(personId);
		if (person == null) {
			throw new ModelServiceException(
                "ModelService", "Failed while getting person, recieved null value."
            );
		}

		// Remove the person from the global list of people
		if (this.personList.remove(person) == false) {
			throw new ModelServiceException(
                "ModelService", "Failed while removing person, recieved null value."
            );
		} else {
			return true;
		}
	}

	/**
	 * Calculates the distance between two points in latitude and longitude
	 * 
	 * CITATION: I did NOT write the following function!
	 * Original code was obtained from the following GeeksforGeeks post:
	 * https://www.geeksforgeeks.org/program-distance-two-points-earth/	 
	 * 
	 * @param locationA		The starting location
	 * @param locationB		The ending location
	 * @return 				The distance between points
	 * @throws ModelServiceException
	 */
	private double distance(Location locationA, Location locationB) throws ModelServiceException {

		if (locationA == null || locationB == null) {
            throw new ModelServiceException(
                "ModelService", "Failed while getting distance, provided location null"
            );
		}

		double lon1 = Math.toRadians(locationA.getLongitiude()); 
        double lon2 = Math.toRadians(locationB.getLongitiude()); 
		double lat1 = Math.toRadians(locationA.getLatitude()); 
        double lat2 = Math.toRadians(locationB.getLatitude()); 

		// Haversine formula  
		double dlon = lon2 - lon1;  
		double dlat = lat2 - lat1; 
		double a = Math.pow(Math.sin(dlat / 2), 2) 
				+ Math.cos(lat1) * Math.cos(lat2) 
				* Math.pow(Math.sin(dlon / 2),2); 
			
		double c = 2 * Math.asin(Math.sqrt(a));

		// Earth's radius
		final int R = 6371; 
	
        return(c * R); 	
	}

}
