package examples;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.UUID;

import org.apache.http.client.ClientProtocolException;
import org.apache.log4j.Logger;

import com.greenmomit.api.client.APIClient;
import com.greenmomit.dto.CountryDTO;
import com.greenmomit.dto.ErrorDTO;
import com.greenmomit.dto.InstallationDTO;
import com.greenmomit.dto.LanguageDTO;
import com.greenmomit.dto.TimeZoneDTO;
import com.greenmomit.dto.UserDTO;
import com.greenmomit.exception.APIException;

public class APIGeneralExamples {
	/**
	 * Common logger
	 */
	private static Logger log = Logger.getLogger(APIGeneralExamples.class);
	
	/**
	 * Show how to access all main resources from api. 
	 * Available endpoint facades 
	 * OPTIONS -> http://localhost:8080/momitbevel/v2/
	 * 
	 * @param args
	 * @throws URISyntaxException 
	 * @throws IOException 
	 * @throws APIException 
	 * @throws UnsupportedEncodingException 
	 * @throws ClientProtocolException 
	 */
	public static void main(String[]args) {
		//This is the authorized application key
		String apiKey = "dekalabsAndroidAPP-key-123";
		
		//Generate simple API remote Client 
		APIClient api;
		try {
			api = new APIClient("http://54.194.234.22:8080/momitbevel/v2/", apiKey);

			
			/////////////////////////////////////////////////////////////////////////////////
			/////////////////////NON SECURE ENDPOINT RESOURCES///////////////////////////////
			/////////////////////////////////////////////////////////////////////////////////
			
			
			//1.Errors catalog
			List<ErrorDTO> errorCatalog = api.getErrorClient().getAll();
			log.info("Available errors "+ errorCatalog.size());
			
			//1.1Find error by code
			if(errorCatalog!= null && errorCatalog.size()>10){
				ErrorDTO errorDetail = api.getErrorClient().getByCode(errorCatalog.get(10).getCode());//select one
				log.info(errorDetail.getCode()+ " "+ errorDetail.getInfo());
			}
			
			//2.Language Catalog
			List<LanguageDTO> languagesCatalog = api.getLanguageClient().getLanguages();
			log.info("Available languages "+ languagesCatalog.size());
			//2.1Find language by code
			if(languagesCatalog!= null && languagesCatalog.size()>2){
				LanguageDTO languageDetail = api.getLanguageClient().getLanguage(languagesCatalog.get(1));//or you can pass new LanguageDTO("es")
				log.info(languageDetail.getCode()+ " "+ languageDetail.getName());
			}
			
			//3.Country Catalog
			List<CountryDTO> countriesCatalog = api.getCountryClient().getCountries();
			log.info("Available countries "+ countriesCatalog.size());
			//2.1Find country by code
			if(countriesCatalog!= null && countriesCatalog.size()>2){
				CountryDTO countryDetail = api.getCountryClient().getCountryByCode(countriesCatalog.get(1).getIsoCode());//
				log.info(countryDetail.getIsoCode()+ " "+ countryDetail.getName());
			}
			
			//4.Timezone Catalogs
			List<TimeZoneDTO> timeZonesCatalog = api.getTimeZoneClient().getTimeZones();
			if(timeZonesCatalog!=null && timeZonesCatalog.size()>0){
				TimeZoneDTO timeZoneDetail = api.getTimeZoneClient().getTimeZone(new TimeZoneDTO(timeZonesCatalog.get(1).getId()));//Search by model
				log.info(timeZoneDetail.getId()+ " "+ timeZoneDetail.getName());
			}
					
			/////////////////////////////////////////////////////////////////////////////////
			/////////////////////SECURE ENDPOINT RESOURCES///////////////////////////////
			/////////////////////////////////////////////////////////////////////////////////
			
			//1. First create a user
			//1.1 Prepare
			UserDTO user = new UserDTO();
			user.setEmail("testUser-"+UUID.randomUUID().toString()+"@gmail.com");//random
			user.setPassword("123456"); //valid pass
			user.setLanguage(languagesCatalog.get(1));//select one from catalog
			user.setName("apiGeneralExamples");
			user.setNewsletter(true);
			user.setSurname("apiGeneralExamplesSURNAME");
			user.setCountry(countriesCatalog.get(2)); //select one from catalog
			//1.2 Execute
			UserDTO created = api.getUserClient().createUser(user);
			log.info("User created "+created);//receive the created user
			
			//2.Loggin into remote api
			//For existing already users you can loggin in this way:
			//UserDTO existingUser = new UserDTO();
			//existingUser.setEmail("dekalabs@momit.com");
			//existingUser.setPassword("123456");
			//api.connect(existingUser);
			api.connect(user);//internal login and session token management
			
			//Congrats you are logged in ;-D
			
			//3.Authenticated operations, create installation associated current user logged on apiClient object.
			//3.1 Prepare data
			InstallationDTO installation = new InstallationDTO();
			installation.setAddress("Playa la malvarosa 25");
			installation.setCity("Valencia");
			installation.setCountry(user.getCountry());//Inherit from user but can be distinct
			installation.setLatitude("-3.43242342");
			installation.setLongitude("-45.432666642");
			installation.setName("Mi casa de la playa");
			installation.setPostalCode("48045");
			installation.setTimeZone(timeZonesCatalog.get(1)); //one from the catalog
			
			//3.2 Execute
			InstallationDTO createdInstallation = api.getInstallationClient().createInstallation(installation);
			log.info("Installation created "+createdInstallation);	
			
			
			
			
			
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
