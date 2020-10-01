package cscie97.smartcity.model;

public class ModelServiceException extends Exception {

	private static final long serialVersionUID = 1L;

	private String action;
	private String cause;

	public ModelServiceException (String action, String cause) {
        super((cause + " " + action));
        this.action = action;
        this.cause = cause;
    }

	public String getAction () {
        return this.action;
    }

}
