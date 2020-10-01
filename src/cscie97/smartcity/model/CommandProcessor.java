package cscie97.smartcity.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
* The command processor class, reads a script file and parses each command line.
* Commands are executed by the ledger service.
*
* @author  Zachary Sullivan
* @since   2020-09-13 
*/
public class CommandProcessor {

    // NOTE: This will only support 1 legder at a time, doc doesnt specify more than one simulatenously so keeping as is.
    ModelService modelService = null;

    public CommandProcessor() {
        modelService = new ModelService();
    }

    // Search a list of commands for a suite of given arguments
    private Map<String, String> parseArgs (String cmds[], String args[]) {
        // Create a mapping of command keywords and their vars
        Map<String, String> cmdMap = new HashMap <String, String> ();

        for(int i = 0; i < cmds.length - 1; i++) {
            for (String arg : args) {
                // Check if the current command matches the expected argument
                if (cmds[i].equalsIgnoreCase(arg)){
                    // Before we copy the neighboring command, ensure the neighboring value is not outofbounds
                    if ((i+1) > 0 && (i+1) <= cmds.length) {
                        
                        cmdMap.put(cmds[i], cmds[i+1]);
                    }
                }
            }
        }
        return cmdMap;
    }
    
    /**
     * Processes, formats and outputs a single command
     * @param command   The string command to be processed
     * @throws ModelServiceException
     * @throws CommandProcessorException
     */
    public void processCommand(String command) throws ModelServiceException, CommandProcessorException {
        // Extract substrings from the command string
        // .. start by splitting based on spaces
        // NOTE: I found the quotations in the sample script were not the same ascii "chars I was testing for
        command = command.replaceAll("[“”]", "\"");

        // Split our cmd string based on spaces (ignoring quotations)
        // Citation: this regex expression was obtained from Bohemian♦ on Stackoverflow
        // via: https://stackoverflow.com/questions/25477562/split-java-string-by-space-and-not-by-double-quotation-that-includes-space
        String cmds[] = command.split("\"?( |$)(?=(([^\"]*\"){2})*[^\"]*$)\"?");

        String args[];
        Map<String, String> cmdMap;

        // Skip any blank commands or commands denoted as a comment (start with '#')
        if (!cmds[0].equals("#")) {
            // Process user command based on first element in command string either define, update or show
            switch(cmds[0]) {   
                case "show":
                    switch(cmds[1]) { 
                        case "city":
                            /**
                             * NOTE THE OUTPUT OF THIS COMMAND SHOULD ALSO SHOW PEOPLE AND IOT DEVICES WITHIN THE REQUESTED CITY
                             * Therefore Ill need to obtain all persons and devices within the city radius using the Haversine method
                             */
                            System.out.println("Retrieving City");

                            // Define-city command should specify the following arguments
                            args = new String[]{"city"};
                            // Populate a new mapping of arguments to properties
                            cmdMap = parseArgs (cmds, args);

                            System.out.println(modelService.getCity(cmdMap.get("city")));
                            break;
                    }
                    break;
                case "update":
                    switch(cmds[1]) {
                        case "street-sign":
                            System.out.println("Updating existing Street Sign");

                            // When updating a streetsign, the command may specify the following arguments
                            args = new String[]{"street-sign", "enabled", "text"};
                            // Populate a new mapping of arguments to properties
                            cmdMap = parseArgs (cmds, args);

                            // Need to split the city id from the device id
                            String cityId = null; 
                            String deviceId = null;
                            try {
                                String ids[] = cmdMap.get("street-sign").split(":"); 
                                cityId = ids[0]; 
                                deviceId = ids[1];
                            } catch (ArrayIndexOutOfBoundsException e) {
                                e.printStackTrace();
                            }

                            // Retrieve and update the IOTdevice
                            //modelService.updateDevice(sign, cityId);
                            System.out.println("Created new IoTDevice for city: " + cityId);

                            break;

                        default:
                            System.out.println("Unable to find entity to update.");
                            break;
                    }
                    break;
                case "define":
                    
                    // Process user command based on what entity to define (person, city or device)
                    switch(cmds[1]) { 
                        
                        case "street-sign":
                            System.out.println("Defining a new Street Sign");

                            // Command should specify the following arguments
                            args = new String[]{"street-sign", "lat", "long", "enabled", "text"};
                            // Populate a new mapping of arguments to properties
                            cmdMap = parseArgs (cmds, args);

                            // Need to split the city id from the device id
                            String cityId = null; 
                            String deviceId = null;
                            try {
                                String ids[] = cmdMap.get("street-sign").split(":"); 
                                cityId = ids[0]; 
                                deviceId = ids[1];
                            } catch (ArrayIndexOutOfBoundsException e) {
                                e.printStackTrace();
                            }
                            float signLat = 0;
                            float signLong = 0;
                            // Location constructor arguments accept floats, must convert
                            // City constructor accepts ints, must convert
                            try {
                                signLat = Float.parseFloat(cmdMap.get("lat"));
                                signLong = Float.parseFloat(cmdMap.get("long"));
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }

                            Location deviceLocation = new Location(signLat, signLong);
                            
                            boolean deviceEnabled = false;
                            if (cmdMap.get("enabled").toLowerCase().equals("true")) {
                                deviceEnabled = true;
                            }

                            // Create a new street sign object
                            IOTDevice sign = new StreetSign(deviceId, deviceLocation, deviceEnabled, cmdMap.get("text"));
                            // Add the iotdevice to the user specified city  
                            System.out.println("Created new IoTDevice: " + sign + " for city: " + modelService.defineDevice(sign, cityId));

                            break;
                        
                        case "info-kiosk":
                            System.out.println("Defining a new Info Kiosk");

                            // Command should specify the following arguments
                            args = new String[]{"info-kiosk", "lat", "long", "enabled", "image"};
                            // Populate a new mapping of arguments to properties
                            cmdMap = parseArgs (cmds, args);

                            // Need to split the city id from the device id
                            cityId = null; 
                            deviceId = null;

                            try {
                                String ids[] = cmdMap.get("info-kiosk").split(":"); 
                                cityId = ids[0]; 
                                deviceId = ids[1];
                            } catch (ArrayIndexOutOfBoundsException e) {
                                e.printStackTrace();
                            }
                            float kioskLat = 0;
                            float kioskLong = 0;

                            // Location constructor arguments accept floats, must convert
                            // Constructor accepts ints, must convert
                            try {
                                kioskLat = Float.parseFloat(cmdMap.get("lat"));
                                kioskLong = Float.parseFloat(cmdMap.get("long"));
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }

                            deviceLocation = new Location(kioskLat, kioskLong);
                            
                            deviceEnabled = false;
                            if (cmdMap.get("enabled").toLowerCase().equals("true")) {
                                deviceEnabled = true;
                            }

                            // Create a new street sign object
                            IOTDevice kiosk = new InfoKiosk(deviceId, deviceLocation, deviceEnabled, cmdMap.get("image"));
                            // Add the iotdevice to the user specified city  
                            System.out.println("Created new IoTDevice: " + kiosk + " for city: " + modelService.defineDevice(kiosk, cityId));
                            
                            break;
                        
                        case "city":
                            System.out.println("Defining a new City");

                            // Define-city command should specify the following arguments
                            args = new String[]{"city", "name", "account", "lat", "long", "radius"};
                            // Populate a new mapping of arguments to properties
                            cmdMap = parseArgs (cmds, args);

                            int radius = 0;
                            float cityLat = 0;
                            float cityLong = 0;
                            // Location constructor arguments accept floats, must convert
                            // City constructor accepts ints, must convert
                            try {
                                cityLat = Float.parseFloat(cmdMap.get("lat"));
                                cityLong = Float.parseFloat(cmdMap.get("long"));

                                radius = Integer.parseInt(cmdMap.get("radius"));
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }

                            Location cityLocation = new Location(cityLat, cityLong);

                            // Create a new city with the specified args
                            City city = new City (
                                cmdMap.get("city"),
                                cmdMap.get("name"), 
                                cmdMap.get("account"),
                                radius,
                                cityLocation
                            );
                            modelService.processCity(city);
                            System.out.println("Created new City: " + city);
                            break;
                        
                        default:
                            //System.out.println();
                            break;
                    }
                    break;
                default:
                    System.out.println();
                    break;
            }
        }
    }
   // }

    /**
     * Processes a file containing multiple commands, passes extracted command to
     * processCommand method
     * 
     * @param commandFile The file of commands to be processed
     * @throws CommandProcessorException
     * @throws ModelServiceException
     */
    public void processCommandFile(String commandFile) throws CommandProcessorException, ModelServiceException {

        // Open the file, thow exception if file doesnt exist or is a dir
        File testScript = new File (commandFile);
        if (testScript.exists() == false || testScript.isDirectory() == true) {
            throw new CommandProcessorException (
                "processCommandFile", "Failed while attempting to open file", 25
            );
        } else {
            try {
                Scanner fileScanner = new Scanner(testScript);
                while (fileScanner.hasNextLine()) {
                    try {
                        processCommand(fileScanner.nextLine()); 
                    } catch (CommandProcessorException e) {
                        System.out.println(e); 
                    }
                }  
    
                fileScanner.close();
            } catch (FileNotFoundException e) { 
                System.out.println(e);       
            }
			
        }
    }
}

