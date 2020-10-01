package cscie97.smartcity.test;

import cscie97.smartcity.model.CommandProcessor;
import cscie97.smartcity.model.CommandProcessorException;
import cscie97.smartcity.model.ModelServiceException;

public class TestDriver {
    public static void main(String args[]) {
      
        CommandProcessor cliProcessor = new CommandProcessor();

        try {
            cliProcessor.processCommandFile(args[0]);
        } catch (CommandProcessorException | ModelServiceException e) {
            System.out.println(e.toString());
        }

    }
}
