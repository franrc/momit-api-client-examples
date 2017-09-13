package examples;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;

import org.apache.http.client.ClientProtocolException;
import org.apache.log4j.Logger;

import com.greenmomit.api.client.APIClient;
import com.greenmomit.dto.UserDTO;
import com.greenmomit.exception.APIException;

public class APIUsersAccountExamples {
	/**
	 * Common logger
	 */
	private static Logger log = Logger.getLogger(APIGeneralExamples.class);
	public static void main(String[] args) throws URISyntaxException, ClientProtocolException, UnsupportedEncodingException, APIException, IOException{
		
		/*******************
		 * FORGOTTEN PASSWORD
		 */
		APIClient api = new APIClient("http://localhost:8080/momitbevel/v2/", "345678");
		//1. First create a user
		//1.1 Prepare
		UserDTO user = new UserDTO();
		user.setEmail("danipenaperez@gmail.com");//random
		
		//2.Forgot password
		api.getSecurityClient().resetPassword(user); //send email, esperar a que te llegue el mail y coger el token
		
		//setear nueva password en el usuario
		user.setPassword("765123"); 
		
		//3. me llega el token de reseteo en el correo, y lo uso asi :
		api.getSecurityClient().changePassword(user, "9wUDXsf63NCcPghCvIqEC9MvO4I7l0GwcUeRI7WCtGn7mltP1JTHbrdpXKfibpJ1");
		
		
		
		/*******************
		 * SIMPLE CHANGE PASSWORD, un usuario logado puede cambiarse su password con el token de session
		 */

		//1. First create a user
		//1.1 Prepare
		user.setEmail("danipenaperez@gmail.com");//random
		user.setPassword("765123"); 
		user = api.connect(user);
		
		//setear nueva password en el usuario
		user.setPassword("abced"); 
		
		//3. me llega el token de reseteo en el correo, y lo uso asi :
		api.getSecurityClient().changePassword(user, null);
		
	}
}
