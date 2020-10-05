package cscie97.smartcity.model;

/**
* The City class are a centrialized collection of IoT devices and people.
* - People are global entities and are members of all cities simultaneousl
* - Cities are designated with a location comprised of a latitude and longitude value.
* - Each city specifies its geographical boundaries in the form of an integer radius.
* - Each city has a name in the form of a string value.
* - A city is identifiable via a globally unique value, where no two cities share the same
* global id.
* - Each city has its own blockchain account for administering transactions throughout
* the city between official city property and its people.
* @author  Zachary Sullivan
* @since   2020-10-05 
*/

import java.util.HashMap;
import java.util.Map;

public class City {

	private String id;
	private String name;
	private String account;
	private int radius;
	private Location location;

	// A mapping of all virtual IoT devices within the city, to their unique ids
	private Map<String, VirtualIOT> IOTDeviceMap;

	public City (String id, String name, String account, int radius, Location location) {
		this.id = id;
		this.name = name;
		this.account = account;
		this.radius = radius;
		this.location = location;
		this.IOTDeviceMap = new HashMap<String, VirtualIOT>();
	}
	
	public String getId () {
		return this.id;
	}

	public String getName () {
		return this.name;
	}

	public int getRadius () {
		return this.radius;
	}

	public String getAccount () {
		return this.account;
	}
	
	public Location getLocation () {
		return this.location;
	}

	public Map<String, VirtualIOT> getIOTDeviceMap () {
		return this.IOTDeviceMap;
	}
	
	/**
	 * Adds a new IoT device to the city
	 * @param device	the iotdevice to add to the city
	 * @return	the id value of the device added, null if fail
	 */
	public String addIoT (IOTDevice device) {
		// first valdidate there are no existing IoT devices that match the unqiue device ID
		if (this.IOTDeviceMap.isEmpty() == false){
			for (Map.Entry<String, VirtualIOT> deviceEntry:this.IOTDeviceMap.entrySet()) {
				if (deviceEntry.getKey().equals(device.getId())) {
					return null;
				}
			}
		}
		// create a new virtual IoT device to facilate communication between modelservice and physical device. 
		VirtualIOT virtualIOT = new VirtualIOT(device);
		// Add the newly created virtual IoT device to the city's map of IoT devices.
		this.IOTDeviceMap.put(virtualIOT.getId(), virtualIOT);
		return virtualIOT.getId();
	}

	/**
	 * Removes a specified IOT device from the city's map of devices. 
	 * @param device device to remove from the city's device map
	 * @return the device removed
	 */ 
	public VirtualIOT removeIoT(IOTDevice device) {

		return this.IOTDeviceMap.remove(device.getId());
	} 

	public String toString() { 
        String result = (
            this.getId() + 
            " : " + this.getName() + 
			"\nLocated: " + this.getLocation() + 
			"\nAccount: " + this.getAccount()
        ); 
        return result;
    }
}
