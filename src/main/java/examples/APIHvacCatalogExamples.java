package examples;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.http.client.ClientProtocolException;
import org.apache.log4j.Logger;

import com.greenmomit.api.client.APIClient;
import com.greenmomit.dto.HvacDTO;
import com.greenmomit.dto.HvacWireDTO;
import com.greenmomit.dto.UserDTO;

public class APIHvacCatalogExamples {
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
			
			//get possible wire values
			log.info("Getting possible values for wireList...");
			List<HvacWireDTO> validWires = api.getHvacClient().getWireList();
			log.info("Allowed values for wires are " + validWires.stream().map(hw -> hw.getWireName()).collect(Collectors.toList()).toString());
			
			//query for a systemType, given a list of wires
			List<String> wires = Arrays.asList("Inertial", "O_B", "Y_Y1", "W_W1", "R_RH", "C");
			HvacDTO hvac = api.getHvacClient().getHvacByWireList(wires);
			log.info("For wires " + wires.toString() + " systemType " + hvac.getSystemType() + " was received");// expected 210A
			
			wires = Arrays.asList("Inertial", "O_B_Cool", "GH", "O_B", "B", "W2", "R_RH", "C");//not found
			hvac = api.getHvacClient().getHvacByWireList(wires);
			log.info("For wires " + wires.toString() + " we expect no systemType. Received Hvac is " + hvac);
			
			//get hvac by systemType
			String systemType = "213E";
			hvac = api.getHvacClient().getHvacBySystemType(systemType);
			log.info("For system type " + hvac + " was received");
			
			api.disconnect();
				
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
