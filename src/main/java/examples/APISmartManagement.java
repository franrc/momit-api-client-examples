package examples;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.List;

import com.greenmomit.api.client.APIClient;
import com.greenmomit.dto.DeviceDTO;
import com.greenmomit.dto.DevicePropertiesDTO;
import com.greenmomit.dto.InstallationDTO;
import com.greenmomit.dto.UserDTO;
import com.greenmomit.exception.APIException;
import com.greenmomit.exception.APIValidationException;
import com.greenmomit.utils.device.constants.DevicePropertiesEnum;
import com.greenmomit.utils.device.constants.WorkingModeEnum;

public class APISmartManagement {
	
	public static APIClient api;
	/**
	 * Prueba basada en un usuario que tiene una instalacion que ya contiene un device.
	 * 
	 * Ahoar el usuario va a pasar por primera vez a modo Smart (pero no tiene nada definido), asi que originara un error
	 * 
	 * Este error hara que se capture en la capa de presentacion y se pida cual es la configuracion de smart para ese device
	 * 
	 * Cuando lo tenga vera que no tiene definido ni configuracion para calendario ni para geolocalizacion
	 *
	 *	asi que empieza a genera un calendario, lo guarda y deja definido el modo smart con submodo calendario.
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		APIClient api;
		try {
			api = new APIClient("http://localhost:8080/momitbevel/v2/", "345678");
			UserDTO currentUser = new UserDTO();
			currentUser.setEmail("testUser-0315eadb-e649-4769-a25c-3bbc7200d544@gmail.com");
			currentUser.setPassword("123456");
			currentUser = api.connect(currentUser); //fetch preferences for current user, as country, measures, timezone, etc..
			
			InstallationDTO installation = null;
			DeviceDTO device = null;
			
			//fetch data for the execution
			List<InstallationDTO> userInstallations = api.getInstallationClient().getInstallations();
			if(userInstallations!=null && userInstallations.get(0)!=null){
				List<DeviceDTO> installationDevices = api.getInstallationClient().getInstallationDevices(userInstallations.get(0));
				if(installationDevices!=null && installationDevices.get(0)!=null){
					installation = userInstallations.get(0);
					device = installationDevices.get(0);
				}
			}
			
			//get all properties now for this device
			List<DevicePropertiesDTO> deviceStatusProperties = api.getDeviceClient().getDeviceCurrentProperties(installation, device);
			
			//Look for current "SmartMode" property
			for(DevicePropertiesDTO prop: deviceStatusProperties){
				if(prop.getFkDevicePropertyTypeId() == DevicePropertiesEnum.WORKING_MODE.getCode()){
					//Toggle for test
					if(prop.getValue().equals(WorkingModeEnum.MANUAL.getLiteral())){
						prop.setValue(WorkingModeEnum.SMART.getLiteral());
					}else{
						prop.setValue(WorkingModeEnum.MANUAL.getLiteral());
					}
					//send operation
					//if the mode is SMART, but is not defined already a smart configuration will be thrown throw (-5005) new APIValidationException(ErrorsEnum.INVALID_ACTION_SMART_MODE_DEVICE_CONFIGURATION_DOES_NOT_EXISTS);
					try{
						api.getDeviceClient().updateDeviceProperties(installation.getId().toString(), device.getSerialNumber().toString(), prop);
					}catch(APIValidationException e){
						//not defined already a smart mode, so, lets to generate the first SMART configuration (on wizard screen will be shown create a calendar)
						generateSmartCalendarConfiguration(); //terminate it
					}
				}
			}
			
			//fecth only DevicePropertiesEnum.WORKING_MODE property to verify changed value
			DevicePropertiesDTO currentWorkingMode = api.getDeviceClient().getPropertiesByTypeId(installation.getId().toString(), device.getSerialNumber().toString(), DevicePropertiesEnum.WORKING_MODE.getCode().toString());
			System.out.println(currentWorkingMode.getValue());
			
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
	
	public static void generateSmartCalendarConfiguration(){
		
	}

}
