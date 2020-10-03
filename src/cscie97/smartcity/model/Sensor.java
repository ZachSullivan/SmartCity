package cscie97.smartcity.model;

// REMOVING THIS CLASS, IT IS REDUNDANT: I COULD JUST SET THE EVENT TYPE TO BE THE SENSOR TYPE ENUM, NO NEED TO CREATE A SENSOR CLASS
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
