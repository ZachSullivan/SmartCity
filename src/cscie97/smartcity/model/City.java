package cscie97.smartcity.model;

import java.util.HashMap;
import java.util.Map;

public class City {

	private static String id;
	private String name;
	private String cityAccount;
	private int radius;
	private Location location;
	private Map<String, VirtualIOT> IOTDeviceMap;
	//private VirtualIOT virtualIOT;

	public City (String id, String name, String account, int radius, Location location) {
		this.id = id;
		this.name = name;
		this.cityAccount = account;
		this.radius = radius;
		this.location = location;
		this.IOTDeviceMap = new HashMap<String, VirtualIOT>();
	}
	
	public String getId () {
		return City.id;
	}

	public String getName () {
		return this.name;
	}
	
	public Location getLocation () {
		return this.location;
	}

	public Map<String, VirtualIOT> getIOTDeviceMap () {
		return this.IOTDeviceMap;
	}
	
	// Return null on failure
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

	public String toString() { 
        String result = (
            this.getId() + 
            " : " + this.getName() + 
            " Located at: " + this.getLocation()
        ); 
        return result;
    } 

}
