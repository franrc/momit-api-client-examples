package examples;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.log4j.Logger;

import com.greenmomit.api.client.APIClient;
import com.greenmomit.dto.DeviceDTO;
import com.greenmomit.dto.ErrorDTO;
import com.greenmomit.dto.InstallationDTO;
import com.greenmomit.dto.SystemTypeDTO;
import com.greenmomit.dto.UserDTO;
import com.greenmomit.exception.APIException;
import com.greenmomit.utils.device.constants.DeviceTypeEnum;

public class APInstallationDeviceSetUpExamples {
	private static Logger log = Logger.getLogger(APIUserExamples.class);

	public static void main (String[] args) {

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
	
			//get one of the installations
			InstallationDTO installation = allInstallations.get(0);
			List<DeviceDTO> registeredDevices = api.getInstallationClient().getInstallationDevices(installation);
			log.info("There are " + registeredDevices.size());
			registeredDevices.stream().forEach(dev -> log.info("Device " + dev.getSerialNumber() + ", name " + dev.getName() + ", type " + dev.getType()));
			
			//DONT COPY EXACTLY THIS PIECE OF CODE BECAUSE PROBABLY IS NOT THE BEST WAY TO PREPARE DATA BEFORE CALLING SETUP
			//get display serialNum
			long serialNumber = registeredDevices.stream().filter(
				dev -> DeviceTypeEnum.BEVEL_US_DISPLAY == DeviceTypeEnum.getByCode(dev.getType().getId().intValue()))
				.findFirst().orElse(new DeviceDTO(-1L)).getSerialNumber();
			log.info("Display serial number: " + serialNumber);
			
			//get systemType --> client should get hvac catalog and get systemType from wires configuration
			//suppose the following systemType for the user home
			String systemType = "133F";
			//the device should have been updated with its Subtype
			DeviceDTO updatedDevice = api.getInstallationClient().setUpInstallation(installation.getId(), serialNumber, new SystemTypeDTO(systemType));
			log.info("The device " + updatedDevice.getSerialNumber() + " has been updated with Subtype " + updatedDevice.getSubType().getId());
		
			api.disconnect();
		} catch (APIException e) {
			log.error("API Controlled Exception " + e.getError());
			
			//Further info of the error is on 
			ErrorDTO error = e.getError();
			error.getCode(); // Use it for translations, maybe?
			error.getInfo(); //Use it for po files translations
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
