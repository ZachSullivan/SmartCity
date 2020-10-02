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
                                System.out.println(modelService.getDevice(cityId, deviceId));
                            } else {
                                System.out.println("Retrieving all Devices within city: " + cmdMap.get("device"));

                                // Retrieve all devices in a given city, in the form of a map
                                Map<String, VirtualIOT> devices = modelService.getDevices(cmdMap.get("device"));

                                // Iterate through the device map, displaying all physical device entries
                                for (Map.Entry<String, VirtualIOT> deviceEntry:devices.entrySet()){
                                    System.out.println(deviceEntry.getValue().getPhysicalDevice());
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



                            // First retrieve the old IOTdevice
                            IOTDevice oldDevice = modelService.getDevice(cityId, deviceId);

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
                                System.out.println("Updated device: " + modelService.getDevice(cityId, deviceId));

                            } else {
                                modelService.updateDevice(oldDevice, oldDevice.getLocation(), deviceEnabled);
                                System.out.println("Updated device: " + modelService.getDevice(cityId, deviceId));
                            }

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
                            oldDevice = modelService.getDevice(cityId, deviceId);
                            System.out.println("got old device: " + oldDevice);
                            System.out.println(oldDevice.getClass());
                            
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

                            System.out.println("IMAGE: "+cmdMap.get("image"));

                            String image = null;
                            if (cmdMap.get("image") != null) {
                                image = cmdMap.get("image");
                                InfoKiosk kiosk = new InfoKiosk(oldDevice.getId(), oldDevice.getLocation(), deviceEnabled, image);
                                
                                modelService.destroyDevice(oldDevice, cityId);
                                modelService.defineDevice(kiosk, cityId);
                                System.out.println("New device: " + modelService.getDevice(cityId, deviceId));

                            } else {
                                modelService.updateDevice(oldDevice, oldDevice.getLocation(), deviceEnabled);
                                System.out.println("New device: " + modelService.getDevice(cityId, deviceId));
                            }

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
                            oldDevice = modelService.getDevice(cityId, deviceId);

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
                                System.out.println("Updated device: " + modelService.getDevice(cityId, deviceId));

                            } else {
                                modelService.updateDevice(oldDevice, oldDevice.getLocation(), deviceEnabled);
                                System.out.println("Updated device: " + modelService.getDevice(cityId, deviceId));
                            }

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
                            oldDevice = modelService.getDevice(cityId, deviceId);

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
                                System.out.println("Updated device: " + modelService.getDevice(cityId, deviceId));

                            } else {
                                modelService.updateDevice(oldDevice, oldDevice.getLocation(), deviceEnabled);
                                System.out.println("Updated device: " + modelService.getDevice(cityId, deviceId));
                            }

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
                            oldDevice = modelService.getDevice(cityId, deviceId);

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
                            float robotLat = 0;
                            float robotLong = 0;
                            Location location = oldDevice.getLocation();
                            if (cmdMap.get("lat") != null && cmdMap.get("long") != null) {
                                // Location constructor arguments accept floats, must convert
                                try {
                                    robotLat = Float.parseFloat(cmdMap.get("lat"));
                                    robotLong = Float.parseFloat(cmdMap.get("long"));
                                } catch (NumberFormatException e) {
                                    e.printStackTrace();
                                }
                                location = new Location(robotLat, robotLong);
                            }

                            String activity = null;
                            if (cmdMap.get("activity") != null) {
                                
                                activity = cmdMap.get("activity");

                                Robot robot = new Robot(oldDevice.getId(), location, deviceEnabled, activity);
                                
                                modelService.destroyDevice(oldDevice, cityId);
                                modelService.defineDevice(robot, cityId);
                                System.out.println("Updated device: " + modelService.getDevice(cityId, deviceId));

                            } else {
                                modelService.updateDevice(oldDevice, oldDevice.getLocation(), deviceEnabled);
                                System.out.println("Updated device: " + modelService.getDevice(cityId, deviceId));
                            }

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
                            oldDevice = modelService.getDevice(cityId, deviceId);

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
                            float vehicleLat = 0;
                            float vehicleLong = 0;
                            location = oldDevice.getLocation();
                            if (cmdMap.get("lat") != null && cmdMap.get("long") != null) {
                                // Location constructor arguments accept floats, must convert
                                try {
                                    vehicleLat = Float.parseFloat(cmdMap.get("lat"));
                                    vehicleLong = Float.parseFloat(cmdMap.get("long"));
                                } catch (NumberFormatException e) {
                                    e.printStackTrace();
                                }
                                location = new Location(vehicleLat, vehicleLong);
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
                            System.out.println("Updated device: " + modelService.getDevice(cityId, deviceId));

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
                        
                        case "parking-space":
                            System.out.println("Defining a new Parking Space");

                            // Command should specify the following arguments
                            args = new String[]{"parking-space", "lat", "long", "enabled", "rate"};
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
                            float parkingLat = 0;
                            float parkingLong = 0;
                            int rate = 0;
                            // Location constructor arguments accept floats, must convert
                            // Constructor accepts ints, must convert
                            try {
                                parkingLat = Float.parseFloat(cmdMap.get("lat"));
                                parkingLong = Float.parseFloat(cmdMap.get("long"));
                                rate = Integer.parseInt(cmdMap.get("rate"));
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }

                            deviceLocation = new Location(parkingLat, parkingLong);
                            
                            deviceEnabled = false;
                            if (cmdMap.get("enabled").toLowerCase().equals("true")) {
                                deviceEnabled = true;
                            }

                            // Create a new street sign object
                            IOTDevice parkingSpace = new ParkingSpace(deviceId, deviceLocation, deviceEnabled, rate);
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

                            try {
                                String ids[] = cmdMap.get("street-light").split(":"); 
                                cityId = ids[0]; 
                                deviceId = ids[1];
                            } catch (ArrayIndexOutOfBoundsException e) {
                                e.printStackTrace();
                            }
                            float lightLat = 0;
                            float lightLong = 0;
                            int brightness = 0;
                            // Location constructor arguments accept floats, must convert
                            // Constructor accepts ints, must convert
                            try {
                                lightLat = Float.parseFloat(cmdMap.get("lat"));
                                lightLong = Float.parseFloat(cmdMap.get("long"));
                                brightness = Integer.parseInt(cmdMap.get("brightness"));
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }

                            deviceLocation = new Location(lightLat, lightLong);
                            
                            deviceEnabled = false;
                            if (cmdMap.get("enabled").toLowerCase().equals("true")) {
                                deviceEnabled = true;
                            }

                            // Create a new street sign object
                            IOTDevice light = new StreetLight(deviceId, deviceLocation, deviceEnabled, brightness);
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
                            cityId = null; 
                            deviceId = null;

                            try {
                                String ids[] = cmdMap.get("robot").split(":"); 
                                cityId = ids[0]; 
                                deviceId = ids[1];
                            } catch (ArrayIndexOutOfBoundsException e) {
                                e.printStackTrace();
                            }
                            float robotLat = 0;
                            float robotLong = 0;

                            // Location constructor arguments accept floats, must convert
                            try {
                                robotLat = Float.parseFloat(cmdMap.get("lat"));
                                robotLong = Float.parseFloat(cmdMap.get("long"));
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }

                            deviceLocation = new Location(robotLat, robotLong);
                            
                            deviceEnabled = false;
                            if (cmdMap.get("enabled").toLowerCase().equals("true")) {
                                deviceEnabled = true;
                            }

                            // Create a new street sign object
                            IOTDevice robot = new Robot(deviceId, deviceLocation, deviceEnabled, cmdMap.get("activity"));
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
                            cityId = null; 
                            deviceId = null;

                            try {
                                String ids[] = cmdMap.get("vehicle").split(":"); 
                                cityId = ids[0]; 
                                deviceId = ids[1];
                            } catch (ArrayIndexOutOfBoundsException e) {
                                e.printStackTrace();
                            }
                            float vehicleLat = 0;
                            float vehicleLong = 0;

                            int capacity = 0;
                            int fee = 0;
                            // Location constructor arguments accept floats, must convert
                            try {
                                vehicleLat = Float.parseFloat(cmdMap.get("lat"));
                                vehicleLong = Float.parseFloat(cmdMap.get("long"));
                                capacity = Integer.parseInt(cmdMap.get("capacity"));
                                fee = Integer.parseInt(cmdMap.get("fee"));
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }

                            deviceLocation = new Location(vehicleLat, vehicleLong);
                            
                            deviceEnabled = false;
                            if (cmdMap.get("enabled").toLowerCase().equals("true")) {
                                deviceEnabled = true;
                            }

                            IOTDevice vehicle = null;
                            if (cmdMap.get("type").toLowerCase().equals("car")) {
                                vehicle = new Car(deviceId, deviceLocation, deviceEnabled, cmdMap.get("activity"), capacity, fee);
                            } else {
                                vehicle = new Bus(deviceId, deviceLocation, deviceEnabled, cmdMap.get("activity"), capacity, fee);
                            }

                            // Create a new street sign object
                            // Add the iotdevice to the user specified city  
                            System.out.println("Created new IoTDevice: " + vehicle + " for city: " + modelService.defineDevice(vehicle, cityId));
                            
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

