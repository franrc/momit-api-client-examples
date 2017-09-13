package examples;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.apache.log4j.Logger;

import com.greenmomit.api.client.APIClient;
import com.greenmomit.dto.DeviceAvailabityDTO;
import com.greenmomit.dto.DeviceDTO;
import com.greenmomit.dto.ErrorDTO;
import com.greenmomit.dto.InstallationDTO;
import com.greenmomit.dto.UserDTO;
import com.greenmomit.exception.APIException;
import com.greenmomit.utils.device.constants.DeviceTypeEnum;

public class APIDeviceCreationExamples {


	private static Logger log = Logger.getLogger(APIUserExamples.class);
	
	public static void main (String[] args) throws Exception {

		String apiKey = "dekalabsAndroidAPP-key-123";
		
		//Generate simple API remote Client 
		APIClient api;
		try {
			api = new APIClient("http://localhost:8080/momitbevel/v2/", apiKey);
			
			UserDTO user = new UserDTO();
			String email = "juan@gmail.com";
			String password = "123456";
			user.setEmail(email);//random
			user.setPassword(password); //valid pass
			user = api.connect(user);
			log.info("User logged " + user);//receive the logged user
			
			//get installation
			List<InstallationDTO> allInstallations = api.getInstallationClient().getInstallations();
			log.info("Retrieved all installations --> " + allInstallations.size());
			allInstallations.stream().forEach(ins -> log.info("\nInstallation " + ins.getId() + " " + ins.toString()));

			//add devices to the following installation ---> for each device is required: installationId, serialNum. and type. Name is optional
			InstallationDTO installation = allInstallations.get(0);
			
			deviceRegistrationSteps(api, 1111111111L, DeviceTypeEnum.GATEWAY, "My Gateway", installation);
			deviceRegistrationSteps(api, 2222222222L, DeviceTypeEnum.BEVEL_US_BASE, "My Base", installation);
			deviceRegistrationSteps(api, 3333333333L, DeviceTypeEnum.BEVEL_US_DISPLAY, "My Display", installation);
			
			List<DeviceDTO> registeredDevices = api.getInstallationClient().getInstallationDevices(installation);
			log.info("After registering the devices, we get the devices for this installation. There are " + registeredDevices.size());
			registeredDevices.stream().forEach(dev -> log.info("Device " + dev.getSerialNumber() + ", name " + dev.getName() + ", type " + dev.getType()));
			
			//disconnect
			api.disconnect();
			
		} catch (APIException e) {
			log.error("API Controlled Exception " + e.getError());
			
			//Further info of the error is on 
			ErrorDTO error = e.getError();
			error.getCode(); // Use it for translations, maybe?
			error.getInfo(); //Use it for po files translations
			
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
	
	private static void deviceRegistrationSteps(APIClient api, long serialNumber, DeviceTypeEnum deviceType, String name, InstallationDTO installation) throws Exception {
		//add GW
		DeviceDTO newDevice = new DeviceDTO();
		newDevice.setSerialNumber(serialNumber);
		newDevice.setType(Long.valueOf(deviceType.getId()));
		newDevice.setInstallation(installation);
		newDevice.setName(name);
		//isRegistered???
		DeviceAvailabityDTO deviceAvailability = api.getDeviceClient().getDeviceAvailability(installation.getId(), serialNumber);
		log.info("Dev with SerialNum " + serialNumber + "=> registered: " + deviceAvailability.isRegistered() + ", connected: " + deviceAvailability.isConnected()
			+ ", state: " + deviceAvailability.getDeviceState());
		if(deviceAvailability.isRegistered()) {
			log.error("Unexpected error in tests. We expect this device would be not registered");
			throw new Exception("Device " + serialNumber + " was already registered");
		} else {
			newDevice = api.getDeviceClient().createDeviceLoggedUser(newDevice);
		}
		
		log.info("After device " + serialNumber + " registration, the device is => registered: " + deviceAvailability.isRegistered()
			+ ", connected: " + deviceAvailability.isConnected() + ", state: " + deviceAvailability.getDeviceState());
	}

}
