package examples;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.List;

import com.greenmomit.api.client.APIClient;
import com.greenmomit.dto.DeviceDTO;
import com.greenmomit.dto.InstallationDTO;
import com.greenmomit.dto.UserDTO;
import com.greenmomit.exception.APIException;

public class APIUserAndInstallations {

	public static void main(String[] args){

		APIClient api;
		try {
			api = new APIClient("http://54.194.234.22:8080/momitbevel/v2/", "345678");
			UserDTO currentUser = new UserDTO();
			currentUser.setEmail("testUser-0315eadb-e649-4769-a25c-3bbc7200d544@gmail.com");
			currentUser.setPassword("123456");
			api.connect(currentUser);
			
			List<InstallationDTO> userInstallations = api.getInstallationClient().getInstallations();
			System.out.println(userInstallations);
			
			List<DeviceDTO> installationDevices = api.getInstallationClient().getInstallationDevices(userInstallations.get(0));
			System.out.println(installationDevices);
			
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (APIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}
}
