package cscie97.smartcity.model;

/**
* The Event class details the IoT device’s sensory input, 
* recording the type of sensor, the reading and optionally the subject
*
* @author  Zachary Sullivan
* @since   2020-10-05 
*/
public class Event {

	private String type;
	private String action;
	private float value;	// Added this value to take into account non-string sensor event values
	private Person subject;

	public Event (String type, String action, Person subject) {
		this.type = type;
		this.action = action;
		this.subject = subject;
	}

	public Event (String type, float value, Person subject) {
		this.type = type;
		this.value = value;
		this.subject = subject;
	}

	private String getType() {
		return this.type;
	}

	private String getAction() {
		return this.action;
	}

	private float getValue() {
		return this.value;
	}

	private Person getSubject() {
		return this.subject;
	}

	public String toString() { 
        String result = (
			this.getType() + 
			" Event: " +
			(this.getAction() != null ? this.getAction() : this.getValue())
        ); 
        return result;
    }


}
