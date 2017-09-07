package examples;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.apache.log4j.Logger;

import com.greenmomit.api.client.APIClient;
import com.greenmomit.dto.CountryDTO;
import com.greenmomit.dto.ErrorDTO;
import com.greenmomit.dto.InstallationDTO;
import com.greenmomit.dto.TimeZoneDTO;
import com.greenmomit.dto.UserDTO;
import com.greenmomit.exception.APIException;

public class APInstallationExamples {

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
			
			//create installation
			InstallationDTO installation = new InstallationDTO();
			installation.setName("Ins-MyHome01");
			installation.setCity("Melbourne");
			installation.setPostalCode("28043");
			installation.setAddress("Pez, 3");
			installation.setLongitude("-3.58556");
			installation.setLatitude("40.23555");
			installation.setCountry(new CountryDTO(193L));
			installation.setTimeZone(new TimeZoneDTO(311L));
			installation = api.getInstallationClient().createInstallation(installation);
			log.info("Created installation " + installation);
			
			//get installation
			List<InstallationDTO> allInstallations = api.getInstallationClient().getInstallations();
			log.info("Retrieved all installations --> " + allInstallations.size());
			allInstallations.stream().forEach(ins -> log.info("\nInstallation " + ins.getId() + " " + ins.toString()));

			
			//modify installation
			installation = allInstallations.get(0);
			installation.setCity("Oliva");
			installation = api.getInstallationClient().updateInstallation(installation);
			log.info("Installation was modified " + installation);
			
			//create another installation
			InstallationDTO installation2 = new InstallationDTO();
			installation2.setName("Ins-MyHome02");
			installation2.setCity("Madrid");
			installation2.setPostalCode("28003");
			installation2.setAddress("Limon, 3333");
			installation2.setLongitude("-3.522256");
			installation2.setLatitude("40.2311155");
			installation2.setCountry(new CountryDTO(193L));
			installation2.setTimeZone(new TimeZoneDTO(311L));
			installation2 = api.getInstallationClient().createInstallation(installation2);
			log.info("Created installation2 " + installation2);
			
			//get all installations
			allInstallations = api.getInstallationClient().getInstallations();
			log.info("Retrieved all installations --> " + allInstallations.size());
			allInstallations.stream().forEach(ins -> log.info("\nInstallation " + ins.getId() + " " + ins.toString()));
			
			//get info of last installation
			installation2 = api.getInstallationClient().getInstallationById(allInstallations.get(1).getId());
			log.info("Retrieved installation2 " + installation2.getId() + " " + installation2);
			
			//delete one installation
			api.getInstallationClient().deleteInstallation(installation2.getId());
			log.info("Installation " + installation2.getId() + " was deleted");

			
			//get all installations
			allInstallations = api.getInstallationClient().getInstallations();
			log.info("Retrieved all installations --> " + allInstallations.size());
			allInstallations.stream().forEach(ins -> log.info("\nInstallation " + ins.getId() + " " + ins.toString()));
			
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
}
