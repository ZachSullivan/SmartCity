package cscie97.smartcity.model;

/**
* Sensors provide information to IoT devices about the world around them, 
* there are 5 varieties of sensors (Microphone, Camera, Thermometer or CO2 meter, STATUS)
*
* @author  Zachary Sullivan
* @since   2020-10-05 
*/
public class Sensor {
	
	private enum SensorType {
		CAMERA,
		MICROPHONE,
		CO2METER,
		THERMOMETER, 
		STATUS;

		public String toString(){
			switch(this){
				case CAMERA:
					return "CAMERA";
				case MICROPHONE:
					return "MICROPHONE";
				case CO2METER:
					return "CO2METER";
				case THERMOMETER:
					return "THERMOMETER";
				case STATUS:
					return "STATUS";
				default:
					return null;
			}
		}
	}
	private SensorType sensorType;

	public Sensor (String type) {
		this.sensorType = this.setType(type);
	}

	private SensorType setType (String type) {
		// Iterate through all roles, return the one that matches our string query
		for (SensorType stype : SensorType.values()) {
			if (stype.toString().equalsIgnoreCase(type)) {
                return stype;
            }
		}
		throw new IllegalArgumentException("Requested Sensor type: " + type + " does not exist");
	}

	public String getSensorType () {
		return this.sensorType.toString();
	}

	/**
	 * Creates a new event with optional person param
	 * @param input		The input for the event
	 * @param person	The optional person issuing the event
	 * @return			The event created, null on failure
	 */
	public Event createEvent(String input, Person person) {

		// Create a new event based on the current sensor type
		switch(this.sensorType) {
			case CAMERA:
			case MICROPHONE:
				return new Event(this.sensorType.toString(), input, person);
			case THERMOMETER:
			case CO2METER:
			case STATUS:
				// These sensors take a numerical value, must convert
				float value = 0;
				try {
					value = Float.parseFloat(input);
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}
				return new Event(this.sensorType.toString(), value, person);
			default:
				return null;
		}
	}

	public String toString() { 
        String result = (
            "Sensor: " + this.getSensorType()
        ); 
        return result;
    }
}
