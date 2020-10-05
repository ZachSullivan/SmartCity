package cscie97.smartcity.model;

/**
* Model Service exception is called when a failure occurs within the Model Service Class. 
* The Model Service Exception records both the point of failure in the form of a method name,
* and the cause of the failure as a description.
*
* @author  Zachary Sullivan
* @since   2020-10-05 
*/
public class ModelServiceException extends Exception {

	private static final long serialVersionUID = 1L;

	private String action;
	private String reason;

	public ModelServiceException (String action, String cause) {
        super((cause + " " + action));
        this.action = action;
        this.reason = cause;
    }

	public String getAction () {
        return this.action;
    }

    public String getReason () {
        return this.reason;
    }
}
