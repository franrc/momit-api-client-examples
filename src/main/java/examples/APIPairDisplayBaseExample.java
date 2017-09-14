package examples;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.apache.log4j.Logger;

import com.greenmomit.api.client.APIClient;
import com.greenmomit.dto.DeviceDTO;
import com.greenmomit.dto.ErrorDTO;
import com.greenmomit.dto.InstallationDTO;
import com.greenmomit.dto.UserDTO;
import com.greenmomit.exception.APIException;
import com.greenmomit.utils.device.constants.DeviceTypeEnum;

public class APIPairDisplayBaseExample {
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
			
			//DONT COPY EXACTLY THIS PIECE OF CODE BECAUSE PROBABLY IS NOT THE BEST WAY TO PREPARE DATA BEFORE CALLING STARTPAIRING
			//get display serialNum
			long serialNumberMaster = registeredDevices.stream().filter(dev -> DeviceTypeEnum.BEVEL_US_DISPLAY == DeviceTypeEnum.getByCode(dev.getType().intValue()))
				.findFirst().orElse(new DeviceDTO(-1L)).getSerialNumber();
			//get base serialNum
			long serialNumberSlave = registeredDevices.stream().filter(dev -> DeviceTypeEnum.BEVEL_US_BASE == DeviceTypeEnum.getByCode(dev.getType().intValue()))
					.findFirst().orElse(new DeviceDTO(-1L)).getSerialNumber();
			
			//start pairing base and display
			//TODO create web socket to receive listening event firstly and pairing event, which confirms base and display are paired
			api.getInstallationClient().startPairing(installation.getId(), serialNumberMaster, serialNumberSlave);			
		
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
}
