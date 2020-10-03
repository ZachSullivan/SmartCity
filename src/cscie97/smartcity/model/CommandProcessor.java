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
                        case "device":
                            // Command should specify the following arguments
                            args = new String[]{"device"};
                            // Populate a new mapping of arguments to properties
                            cmdMap = parseArgs (cmds, args);

                            // Check if the command denotes a specific device via a ":"
                            if (cmdMap.get("device").contains(":")) {
                                System.out.println("Retrieving a Device");
                                // Need to split the city id from the device id
                                String cityId = null; 
                                String deviceId = null;
                                try {
                                    String ids[] = cmdMap.get("device").split(":"); 
                                    cityId = ids[0]; 
                                    deviceId = ids[1];
                                } catch (ArrayIndexOutOfBoundsException e) {
                                    e.printStackTrace();
                                }
                                System.out.println(modelService.getDevice(cityId, deviceId).getPhysicalDevice());
                            } else {
                                System.out.println("Retrieving all Devices within city: " + cmdMap.get("device"));

                                // Retrieve all devices in a given city, in the form of a map
                                Map<String, VirtualIOT> devices = modelService.getDevices(cmdMap.get("device"));

                                // Iterate through the device map, displaying all physical device entries
                                for (Map.Entry<String, VirtualIOT> deviceEntry:devices.entrySet()){
                                    System.out.println(deviceEntry.getValue().getPhysicalDevice());
                                    System.out.println();
                                }
                            }
                            break;
                        
                        case "city":
                            /**
                             * NOTE THE OUTPUT OF THIS COMMAND SHOULD ALSO SHOW PEOPLE AND IOT DEVICES WITHIN THE REQUESTED CITY
                             * Therefore Ill need to obtain all persons and devices within the city radius using the Haversine method
                             */
                            System.out.println("Retrieving City");

                            // Command should specify the following arguments
                            args = new String[]{"city"};
                            // Populate a new mapping of arguments to properties
                            cmdMap = parseArgs (cmds, args);

                            System.out.println(modelService.getCity(cmdMap.get("city")));
                            break;
                    }
                    break;

                case "create":
                    switch(cmds[1]) { 
                        case "sensor-event":

                            System.out.println("Simulating new Event...");

                            // Command should specify the following arguments
                            args = new String[]{"sensor-event", "type", "value", "subject"};
                            // Populate a new mapping of arguments to properties
                            cmdMap = parseArgs (cmds, args);

                            String cityId = null; 
                            String deviceId = null;
                            try {
                                String ids[] = cmdMap.get("sensor-event").split(":"); 
                                cityId = ids[0]; 
                                deviceId = ids[1];
                            } catch (ArrayIndexOutOfBoundsException e) {
                                e.printStackTrace();
                            }

                            modelService.simulateEvent(
                                cityId, 
                                deviceId,
                                cmdMap.get("type"), 
                                cmdMap.get("value"), 
                                cmdMap.get("subject")
                            );

                            // Display to the user the simulated event
                            System.out.println("Event was simulated for following device:");
                            System.out.println(modelService.getDevice(cityId, deviceId).getPhysicalDevice());
                            System.out.println();

                            break;
                    }
                    break;  
                    
                case "update":
                    switch(cmds[1]) {

                        case "visitor":
                            System.out.println("Updating existing Visitor");

                            // When updating, the command may specify the following arguments
                            args = new String[]{"visitor", "bio-metric", "lat", "long"};

                            // Populate a new mapping of arguments to properties
                            cmdMap = parseArgs (cmds, args);

                            // Obtain the old resident
                            Person oldVisitor = modelService.getPerson(cmdMap.get("visitor"));

                            String bioId = null;
                            if (cmdMap.get("bio-metric") == null) {
                                bioId = oldVisitor.getBioId();
                            } else {
                                bioId = cmdMap.get("bio-metric");
                            }

                            // Update the location object
                            Location location = oldVisitor.getLocation();
                            if (cmdMap.get("lat") != null && cmdMap.get("long") != null) {
                                // Location constructor arguments accept floats, must convert
                                location = this.parseLocation(cmdMap.get("lat"), cmdMap.get("long"));
                            }

                            // Create a new city with the specified args
                            Person visitor = new Visitor(
                                cmdMap.get("visitor"), 
                                bioId,
                                location
                            );
  
                            // In order to safely update the object in runtime, 
                            // .. we will destroy the old object and instanciate a new one with the same ID
                            modelService.destroyPerson(cmdMap.get("visitor"));
                            modelService.definePerson(visitor);
                            System.out.println("Updated Visitor:\n" + visitor);
                            break;

                        case "resident":
                            System.out.println("Updating existing Resident");

                            // When updating a streetsign, the command may specify the following arguments
                            args = new String[]{"resident", "name", "bio-metric", "phone", "role", "lat", "long", "account"};

                            // Populate a new mapping of arguments to properties
                            cmdMap = parseArgs (cmds, args);

                            // Obtain the old resident
                            Person oldResident = modelService.getPerson(cmdMap.get("resident"));

                            String name = null;
                            if (cmdMap.get("name") == null) {
                                name = oldResident.getName();
                            } else {
                                name = cmdMap.get("name");
                            }

                            bioId = null;
                            if (cmdMap.get("bio-metric") == null) {
                                bioId = oldResident.getBioId();
                            } else {
                                bioId = cmdMap.get("bio-metric");
                            }
                            
                            String phone = null;
                            if (cmdMap.get("phone") == null) {
                                phone = oldResident.getPhoneNumber();
                            } else {
                                phone = cmdMap.get("phone");
                            }

                            String role = null;
                            if (cmdMap.get("role") == null) {
                                role = oldResident.getRole();
                            } else {
                                role = cmdMap.get("role");
                            }

                            String account = null;
                            if (cmdMap.get("account") == null) {
                                account = oldResident.getAccount();
                            } else {
                                account = cmdMap.get("account");
                            }
                            
                            // Update the location object
                            float resLat = 0;
                            float resLong = 0;
                            location = oldResident.getLocation();
                            if (cmdMap.get("lat") != null && cmdMap.get("long") != null) {
                                // Location constructor arguments accept floats, must convert
                                try {
                                    resLat = Float.parseFloat(cmdMap.get("lat"));
                                    resLong = Float.parseFloat(cmdMap.get("long"));
                                } catch (NumberFormatException e) {
                                    e.printStackTrace();
                                }
                                location = new Location(resLat, resLong);
                            }

                            // Create a new city with the specified args
                            Person resident = new Resident (
                                cmdMap.get("resident"),
                                name, 
                                bioId,
                                phone,
                                role,
                                location,
                                account
                            );
  
                            // In order to safely update the object in runtime, 
                            // .. we will destroy the old object and instanciate a new one with the same ID
                            modelService.destroyPerson(cmdMap.get("resident"));
                            modelService.definePerson(resident);
                            System.out.println("Updated Resident:\n" + resident);
                            break;

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

                            // First retrieve the old IOTdevice
                            VirtualIOT oldVDevice = modelService.getDevice(cityId, deviceId);
                            IOTDevice oldDevice = oldVDevice.getPhysicalDevice();
                            // User can only modify the display text OR if the device is enabled
                            boolean deviceEnabled = false;
                            // If the user hasn't specified a new enabled status, then use the existing status
                            if (cmdMap.get("enabled") == null) {
                                deviceEnabled = oldDevice.getEnabled();
                            } else {
                                if (cmdMap.get("enabled").toLowerCase().equals("true")) {
                                    deviceEnabled = true;
                                }
                            }

                            String display = null;
                            if (cmdMap.get("text") != null) {
                                display = cmdMap.get("text");
                                StreetSign sign = new StreetSign(oldDevice.getId(), oldDevice.getLocation(), deviceEnabled, display);
                                
                                modelService.destroyDevice(oldDevice, cityId);
                                modelService.defineDevice(sign, cityId);
                            } else {
                                modelService.updateDevice(oldDevice, oldDevice.getLocation(), deviceEnabled);
                            }

                            System.out.println("Updated device: " + modelService.getDevice(cityId, deviceId).getPhysicalDevice());
                            break;
                        
                        case "info-kiosk":
                            System.out.println("Updating existing Info Kiosk");
                            
                            // When updating a streetsign, the command may specify the following arguments
                            args = new String[]{"info-kiosk", "enabled", "image"};
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

                            // First retrieve the old IOTdevice
                            oldVDevice = modelService.getDevice(cityId, deviceId);
                            oldDevice = oldVDevice.getPhysicalDevice();
                            
                            // User can only modify the display text OR if the device is enabled
                            deviceEnabled = false;
                            // If the user hasn't specified a new enabled status, then use the existing status
                            if (cmdMap.get("enabled") == null) {
                                deviceEnabled = oldDevice.getEnabled();
                            } else {
                                if (cmdMap.get("enabled").toLowerCase().equals("true")) {
                                    deviceEnabled = true;
                                }
                            }

                            String image = null;
                            if (cmdMap.get("image") != null) {
                                image = cmdMap.get("image");
                                InfoKiosk kiosk = new InfoKiosk(oldDevice.getId(), oldDevice.getLocation(), deviceEnabled, image);
                                
                                modelService.destroyDevice(oldDevice, cityId);
                                modelService.defineDevice(kiosk, cityId);

                            } else {
                                modelService.updateDevice(oldDevice, oldDevice.getLocation(), deviceEnabled);
                            }

                            System.out.println("Updated device: " + modelService.getDevice(cityId, deviceId).getPhysicalDevice());
                            break;

                        case "street-light":
                            System.out.println("Updating existing Street Light");

                            // When updating a streetsign, the command may specify the following arguments
                            args = new String[]{"street-light", "enabled", "brightness"};
                            // Populate a new mapping of arguments to properties
                            cmdMap = parseArgs (cmds, args);

                            // Need to split the city id from the device id
                            cityId = null; 
                            deviceId = null;
                            try {
                                String ids[] = cmdMap.get("street-light").split(":"); 
                                cityId = ids[0]; 
                                deviceId = ids[1];
                            } catch (ArrayIndexOutOfBoundsException e) {
                                e.printStackTrace();
                            }

                            // First retrieve the old IOTdevice
                            oldVDevice = modelService.getDevice(cityId, deviceId);
                            oldDevice = oldVDevice.getPhysicalDevice();

                            // User can only modify the display text OR if the device is enabled
                            deviceEnabled = false;
                            // If the user hasn't specified a new enabled status, then use the existing status
                            if (cmdMap.get("enabled") == null) {
                                deviceEnabled = oldDevice.getEnabled();
                            } else {
                                if (cmdMap.get("enabled").toLowerCase().equals("true")) {
                                    deviceEnabled = true;
                                }
                            }

                            int brightness = 0;
                            if (cmdMap.get("brightness") != null) {
                                
                                // Convert brightness input string to int
                                try {
                                    brightness = Integer.parseInt(cmdMap.get("brightness"));
                                } catch (NumberFormatException e) {
                                    e.printStackTrace();
                                }

                                StreetLight light = new StreetLight(oldDevice.getId(), oldDevice.getLocation(), deviceEnabled, brightness);
                                
                                modelService.destroyDevice(oldDevice, cityId);
                                modelService.defineDevice(light, cityId);
                            } else {
                                modelService.updateDevice(oldDevice, oldDevice.getLocation(), deviceEnabled);
                            }

                            System.out.println("Updated device: " + modelService.getDevice(cityId, deviceId).getPhysicalDevice());

                            break;

                        case "parking-space":
                            System.out.println("Updating existing Parking Space");

                            // When updating a streetsign, the command may specify the following arguments
                            args = new String[]{"parking-space", "enabled", "rate"};
                            // Populate a new mapping of arguments to properties
                            cmdMap = parseArgs (cmds, args);

                            // Need to split the city id from the device id
                            cityId = null; 
                            deviceId = null;
                            try {
                                String ids[] = cmdMap.get("parking-space").split(":"); 
                                cityId = ids[0]; 
                                deviceId = ids[1];
                            } catch (ArrayIndexOutOfBoundsException e) {
                                e.printStackTrace();
                            }

                            // First retrieve the old IOTdevice
                            oldVDevice = modelService.getDevice(cityId, deviceId);
                            oldDevice = oldVDevice.getPhysicalDevice();

                            // User can only modify the display text OR if the device is enabled
                            deviceEnabled = false;
                            // If the user hasn't specified a new enabled status, then use the existing status
                            if (cmdMap.get("enabled") == null) {
                                deviceEnabled = oldDevice.getEnabled();
                            } else {
                                if (cmdMap.get("enabled").toLowerCase().equals("true")) {
                                    deviceEnabled = true;
                                }
                            }

                            int rate = 0;
                            if (cmdMap.get("rate") != null) {
                                
                                // Convert rate input string to int
                                try {
                                    rate = Integer.parseInt(cmdMap.get("rate"));
                                } catch (NumberFormatException e) {
                                    e.printStackTrace();
                                }

                                ParkingSpace parkingSpace = new ParkingSpace(oldDevice.getId(), oldDevice.getLocation(), deviceEnabled, rate);
                                
                                modelService.destroyDevice(oldDevice, cityId);
                                modelService.defineDevice(parkingSpace, cityId);

                            } else {
                                modelService.updateDevice(oldDevice, oldDevice.getLocation(), deviceEnabled);
                            }

                            System.out.println("Updated device: " + modelService.getDevice(cityId, deviceId).getPhysicalDevice());

                            break;

                        case "robot":
                            System.out.println("Updating existing Robot");

                            // When updating a streetsign, the command may specify the following arguments
                            args = new String[]{"robot", "lat", "long", "enabled", "activity"};
                            // Populate a new mapping of arguments to properties
                            cmdMap = parseArgs (cmds, args);

                            // Need to split the city id from the device id
                            cityId = null; 
                            deviceId = null;
                            try {
                                String ids[] = cmdMap.get("robot").split(":"); 
                                cityId = ids[0]; 
                                deviceId = ids[1];
                            } catch (ArrayIndexOutOfBoundsException e) {
                                e.printStackTrace();
                            }

                            // First retrieve the old IOTdevice
                            oldVDevice = modelService.getDevice(cityId, deviceId);
                            oldDevice = oldVDevice.getPhysicalDevice();

                            // User can only modify the display text OR if the device is enabled
                            deviceEnabled = false;
                            // If the user hasn't specified a new enabled status, then use the existing status
                            if (cmdMap.get("enabled") == null) {
                                deviceEnabled = oldDevice.getEnabled();
                            } else {
                                if (cmdMap.get("enabled").toLowerCase().equals("true")) {
                                    deviceEnabled = true;
                                }
                            }

                            // Update the location object
                            location = oldDevice.getLocation();
                            if (cmdMap.get("lat") != null && cmdMap.get("long") != null) {
                                // Location constructor arguments accept floats, must convert
                                location = this.parseLocation(cmdMap.get("lat"), cmdMap.get("long"));
                            }

                            String activity = null;
                            if (cmdMap.get("activity") != null) {
                                
                                activity = cmdMap.get("activity");

                                Robot robot = new Robot(oldDevice.getId(), location, deviceEnabled, activity);
                                
                                modelService.destroyDevice(oldDevice, cityId);
                                modelService.defineDevice(robot, cityId);

                            } else {
                                modelService.updateDevice(oldDevice, oldDevice.getLocation(), deviceEnabled);
                            }

                            System.out.println("Updated device: " + modelService.getDevice(cityId, deviceId).getPhysicalDevice());

                            break;

                        case "vehicle":
                            System.out.println("Updating existing Vehicle");

                            // When updating a streetsign, the command may specify the following arguments
                            args = new String[]{"vehicle", "lat", "long", "enabled", "activity", "fee"};
                            // Populate a new mapping of arguments to properties
                            cmdMap = parseArgs (cmds, args);

                            // Need to split the city id from the device id
                            cityId = null; 
                            deviceId = null;
                            try {
                                String ids[] = cmdMap.get("vehicle").split(":"); 
                                cityId = ids[0]; 
                                deviceId = ids[1];
                            } catch (ArrayIndexOutOfBoundsException e) {
                                e.printStackTrace();
                            }

                            // First retrieve the old IOTdevice
                            oldVDevice = modelService.getDevice(cityId, deviceId);
                            oldDevice = oldVDevice.getPhysicalDevice();

                            // User can only modify the display text OR if the device is enabled
                            deviceEnabled = false;
                            // If the user hasn't specified a new enabled status, then use the existing status
                            if (cmdMap.get("enabled") == null) {
                                deviceEnabled = oldDevice.getEnabled();
                            } else {
                                if (cmdMap.get("enabled").toLowerCase().equals("true")) {
                                    deviceEnabled = true;
                                }
                            }

                            // Update the location object
                            location = oldDevice.getLocation();
                            if (cmdMap.get("lat") != null && cmdMap.get("long") != null) {
                                // Location constructor arguments accept floats, must convert
                                location = this.parseLocation(cmdMap.get("lat"), cmdMap.get("long"));
                            }

                            activity = null;
                            if (cmdMap.get("activity") != null) {
                                activity = cmdMap.get("activity");
                            } else {
                                activity = oldDevice.getActivity();
                            }

                            rate = 0;
                            if (cmdMap.get("rate") != null) {
                                // Convert rate input string to int
                                try {
                                    rate = Integer.parseInt(cmdMap.get("rate"));
                                } catch (NumberFormatException e) {
                                    e.printStackTrace();
                                }                        
                            } else {
                                rate = oldDevice.getFee();
                            }

                            IOTDevice vehicle = null;
                            if (deviceId.toLowerCase().contains("car")) {
                                vehicle = new Car(oldDevice.getId(), location, deviceEnabled, activity, oldDevice.getCapacity(), rate);
                            } else {
                                vehicle = new Bus(oldDevice.getId(), location, deviceEnabled, activity, oldDevice.getCapacity(), rate);
                            }
                            
                            modelService.destroyDevice(oldDevice, cityId);
                            modelService.defineDevice(vehicle, cityId);
                            System.out.println("Updated device: " + modelService.getDevice(cityId, deviceId).getPhysicalDevice());

                            break;

                        default:
                            System.out.println("Unable to find entity to update.");
                            break;
                    }
                    break;
                case "define":
                    
                    // Process user command based on what entity to define (person, city or device)
                    switch(cmds[1]) { 
                        
                        case "resident":
                            System.out.println("Defining a new Resident");

                            // Command should specify the following arguments
                            args = new String[]{"resident", "name", "bio-metric", "phone", "role", "lat", "long", "account"};
                            // Populate a new mapping of arguments to properties
                            cmdMap = parseArgs (cmds, args);

                            Location resLocation = this.parseLocation(cmdMap.get("lat"), cmdMap.get("long"));

                            // Create a new city with the specified args
                            Person resident = new Resident (
                                cmdMap.get("resident"),
                                cmdMap.get("name"), 
                                cmdMap.get("bio-metric"),
                                cmdMap.get("phone"),
                                cmdMap.get("role"),
                                resLocation,
                                cmdMap.get("account")
                            );

                            
                            modelService.definePerson(resident);
                            System.out.println("Created new Resident: \n" + resident);
                            break;

                        case "visitor":
                            System.out.println("Defining a new Visitor");

                            // Command should specify the following arguments
                            args = new String[]{"visitor", "bio-metric", "lat", "long"};
                            // Populate a new mapping of arguments to properties
                            cmdMap = parseArgs (cmds, args);

                            Location visLocation = this.parseLocation(cmdMap.get("lat"), cmdMap.get("long"));

                            // Create a new city with the specified args
                            Person visitor = new Visitor (
                                cmdMap.get("visitor"),
                                cmdMap.get("bio-metric"),
                                visLocation
                            );
   
                            modelService.definePerson(visitor);
                            System.out.println("Created new Visitor: \n" + visitor);
                            break;

                        case "street-sign":
                            System.out.println("Defining a new Street Sign");

                            // Command should specify the following arguments
                            args = new String[]{"street-sign", "lat", "long", "enabled", "text"};
                            // Populate a new mapping of arguments to properties
                            cmdMap = parseArgs (cmds, args);

                            // Need to split the city id from the device id
                            String[] ids = parseIds(cmdMap.get("street-sign"));
                            String cityId = ids[0]; 
                            String deviceId = ids[1];
                            
                            Location deviceLocation = this.parseLocation(cmdMap.get("lat"), cmdMap.get("long"));
                            
                            boolean deviceEnabled = false;
                            if (cmdMap.get("enabled").toLowerCase().equals("true")) {
                                deviceEnabled = true;
                            }

                            // Create a new street sign object
                            StreetSign sign = new StreetSign(deviceId, deviceLocation, deviceEnabled, cmdMap.get("text"));
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
                            ids = parseIds(cmdMap.get("info-kiosk"));
                            cityId = ids[0]; 
                            deviceId = ids[1];

                            // Location constructor arguments accept floats, must convert
                            deviceLocation = this.parseLocation(cmdMap.get("lat"), cmdMap.get("long"));

                            deviceEnabled = false;
                            if (cmdMap.get("enabled").toLowerCase().equals("true")) {
                                deviceEnabled = true;
                            }

                            // Create a new street sign object
                            InfoKiosk kiosk = new InfoKiosk(deviceId, deviceLocation, deviceEnabled, cmdMap.get("image"));
                            // Add the iotdevice to the user specified city  
                            System.out.println("Created new IoTDevice: " + kiosk + " for city: " + modelService.defineDevice(kiosk, cityId));
                            
                            break;
                        
                        case "parking-space":
                            System.out.println("Defining a new Parking Space");

                            // Command should specify the following arguments
                            args = new String[]{"parking-space", "lat", "long", "enabled", "rate"};
                            // Populate a new mapping of arguments to properties
                            cmdMap = parseArgs (cmds, args);

                            // Need to split the city id from the device id
                            ids = parseIds(cmdMap.get("parking-space"));
                            cityId = ids[0]; 
                            deviceId = ids[1];

                            int rate = parseInt(cmdMap.get("rate"));

                            deviceLocation = this.parseLocation(cmdMap.get("lat"), cmdMap.get("long"));

                            deviceEnabled = false;
                            if (cmdMap.get("enabled").toLowerCase().equals("true")) {
                                deviceEnabled = true;
                            }

                            // Create a new street sign object
                            ParkingSpace parkingSpace = new ParkingSpace(deviceId, deviceLocation, deviceEnabled, rate);
                            // Add the iotdevice to the user specified city  
                            System.out.println("Created new IoTDevice: " + parkingSpace + " for city: " + modelService.defineDevice(parkingSpace, cityId));
                            
                            break;
                        
                        case "street-light":
                            System.out.println("Defining a new Street Light");

                            // Command should specify the following arguments
                            args = new String[]{"street-light", "lat", "long", "enabled", "brightness"};
                            // Populate a new mapping of arguments to properties
                            cmdMap = parseArgs (cmds, args);

                            // Need to split the city id from the device id
                            cityId = null; 
                            deviceId = null;

                            ids = parseIds(cmdMap.get("street-light"));
                            cityId = ids[0]; 
                            deviceId = ids[1];

                            int brightness = parseInt(cmdMap.get("brightness"));

                            deviceLocation = this.parseLocation(cmdMap.get("lat"), cmdMap.get("long"));
                            
                            deviceEnabled = false;
                            if (cmdMap.get("enabled").toLowerCase().equals("true")) {
                                deviceEnabled = true;
                            }

                            // Create a new street sign object
                            StreetLight light = new StreetLight(deviceId, deviceLocation, deviceEnabled, brightness);
                            // Add the iotdevice to the user specified city  
                            System.out.println("Created new IoTDevice: " + light + " for city: " + modelService.defineDevice(light, cityId));
                            
                            break;
                        
                        case "robot":
                            System.out.println("Defining a new Robot");

                            // Command should specify the following arguments
                            args = new String[]{"robot", "lat", "long", "enabled", "activity"};
                            // Populate a new mapping of arguments to properties
                            cmdMap = parseArgs (cmds, args);

                            // Need to split the city id from the device id
                            ids = parseIds(cmdMap.get("robot"));
                            cityId = ids[0]; 
                            deviceId = ids[1];
               
                            // Location constructor arguments accept floats, must convert
                            deviceLocation = this.parseLocation(cmdMap.get("lat"), cmdMap.get("long"));

                            deviceEnabled = false;
                            if (cmdMap.get("enabled").toLowerCase().equals("true")) {
                                deviceEnabled = true;
                            }

                            // Create a new street sign object
                            Robot robot = new Robot(deviceId, deviceLocation, deviceEnabled, cmdMap.get("activity"));
                            // Add the iotdevice to the user specified city  
                            System.out.println("Created new IoTDevice: " + robot + " for city: " + modelService.defineDevice(robot, cityId));
                            
                            break;

                        case "vehicle":
                            System.out.println("Defining a new Vehicle");

                            // Command should specify the following arguments
                            args = new String[]{"vehicle", "lat", "long", "enabled", "type", "activity", "capacity", "fee"};
                            // Populate a new mapping of arguments to properties
                            cmdMap = parseArgs (cmds, args);

                            // Need to split the city id from the device id
                            ids = parseIds(cmdMap.get("vehicle"));
                            cityId = ids[0]; 
                            deviceId = ids[1];

                            /*try {
                                String ids[] = cmdMap.get("vehicle").split(":"); 
                                cityId = ids[0]; 
                                deviceId = ids[1];
                            } catch (ArrayIndexOutOfBoundsException e) {
                                e.printStackTrace();
                            }*/

                            int capacity = parseInt(cmdMap.get("capacity"));
                            int fee = parseInt(cmdMap.get("fee"));

                            deviceLocation = this.parseLocation(cmdMap.get("lat"), cmdMap.get("long"));

                            deviceEnabled = false;
                            if (cmdMap.get("enabled").toLowerCase().equals("true")) {
                                deviceEnabled = true;
                            }

                            if (cmdMap.get("type").toLowerCase().equals("car")) {
                                Car car = new Car(deviceId, deviceLocation, deviceEnabled, cmdMap.get("activity"), capacity, fee);
                                System.out.println("Created new IoTDevice: " + car + " for city: " + modelService.defineDevice(car, cityId));
                            } else {
                                Bus bus = new Bus(deviceId, deviceLocation, deviceEnabled, cmdMap.get("activity"), capacity, fee);
                                System.out.println("Created new IoTDevice: " + bus + " for city: " + modelService.defineDevice(bus, cityId));

                            }

                            break;

                        case "city":
                            System.out.println("Defining a new City");

                            // Define-city command should specify the following arguments
                            args = new String[]{"city", "name", "account", "lat", "long", "radius"};
                            // Populate a new mapping of arguments to properties
                            cmdMap = parseArgs (cmds, args);

                            int radius = parseInt(cmdMap.get("radius"));
                            Location cityLocation = this.parseLocation(cmdMap.get("lat"), cmdMap.get("long"));

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

    private Location parseLocation (String latitiude, String longitude) {
		float locLat = 0;
		float locLong = 0;
		// Location constructor arguments accept floats, must convert
		try {
			locLat = Float.parseFloat(latitiude);
			locLong = Float.parseFloat(longitude);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}

		return new Location(locLat, locLong);
    }
    
    private int parseInt (String input) {
        int result = 0;
		try {
			result = Integer.parseInt(input);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}

		return result;
    }
    
    private String[] parseIds (String input) {
        String[] result = null;
        try {
            result = input.split(":");
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        return result;
    }
}

