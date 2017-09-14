package examples;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.UUID;

import org.apache.log4j.Logger;

import com.greenmomit.api.client.APIClient;
import com.greenmomit.dto.CountryDTO;
import com.greenmomit.dto.ErrorDTO;
import com.greenmomit.dto.LanguageDTO;
import com.greenmomit.dto.UserDTO;
import com.greenmomit.exception.APIException;

public class APIUserExamples {
	
	private static Logger log = Logger.getLogger(APIUserExamples.class);

	public static void main (String[] args) {
		String apiKey = "dekalabsAndroidAPP-key-123";
		
		//Generate simple API remote Client 
		APIClient api;
		try {
			api = new APIClient("http://localhost:8080/momitbevel/v2/", apiKey);
			
			//1. First create a user
			//1.1 Prepare
			UserDTO user = new UserDTO();
			String email = "testUser-"+UUID.randomUUID().toString()+"@gmail.com";
			String password = "123456";
			user.setEmail(email);//random
			user.setPassword(password); //valid pass
			LanguageDTO languageDTO = new LanguageDTO();
			languageDTO.setCode("es");
			user.setLanguage(languageDTO);//select one from catalog
			user.setName("apiGeneralExamples");
			user.setNewsletter(true);
			user.setSurname("apiGeneralExamplesSURNAME");
			user.setCountry(new CountryDTO(192L)); //select one from catalog
			//1.2 Execute
			UserDTO created = api.getUserClient().createUser(user);
			log.info("User created " + created);//receive the created user
			
			created = api.connect(created);
			log.info("User logged " + created);//receive the logged user
			
			api.disconnect();//user logout
			
			created = new UserDTO();
			created.setEmail(email);
			created.setPassword(password);
			created = api.connect(created);//user login
			
			//modification
			created.setName("Raúl");
			created.setSurname("González Blanco");
			created.getUserPreferences().setTemperature("ºF");
			created = api.getUserClient().updateLoggedUser(created);
			log.info("User modified is " + created);
			
			//delete
			api.getUserClient().deleteLoggedUser();

			
		} catch (APIException e) {
			log.error("API Controlled Exception " + e.getError());
			
			//Further info of the error is on 
			ErrorDTO error = e.getError();
			error.getCode(); // Use it for translations, maybe?
			error.getInfo(); //Use it for po files translations
			
		} catch (URISyntaxException e) {
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
