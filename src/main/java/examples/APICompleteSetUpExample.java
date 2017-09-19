package examples;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.apache.log4j.Logger;

import com.greenmomit.api.client.APIClient;
import com.greenmomit.dto.CDeviceTypeDTO;
import com.greenmomit.dto.CountryDTO;
import com.greenmomit.dto.DeviceAvailabityDTO;
import com.greenmomit.dto.DeviceDTO;
import com.greenmomit.dto.HvacDTO;
import com.greenmomit.dto.InstallationDTO;
import com.greenmomit.dto.LanguageDTO;
import com.greenmomit.dto.SystemTypeDTO;
import com.greenmomit.dto.TimeZoneDTO;
import com.greenmomit.dto.UserDTO;
import com.greenmomit.utils.device.constants.DeviceTypeEnum;

public class APICompleteSetUpExample {
	
	private static Logger log = Logger.getLogger(APIGeneralExamples.class);
	
	private static Random random = new Random();
	
	public static void main(String[] args) throws Exception {
		
		//NOTA DEKALABS : No es necesario hacer login y logout en cada paso... solo lo hacemos en este test para verificar que todo funciona
		//de una sesion a otra y que todo persiste correctamente
		
		String apiKey = "dekalabsAndroidAPP-key-123";
		String apiUrl = "http://localhost:8080/momitbevel/v2/";

		APIClient api = new APIClient(apiUrl, apiKey);
		
		/////////////////
		// CREATE USER //
		/////////////////
		//prepare user
		UserDTO user = new UserDTO();
		String email = "testUser-" + UUID.randomUUID().toString() + "@gmail.com";
		String password = "3.1416";
		user.setEmail(email);
		user.setPassword(password);
		user.setName("Sergio");
		LanguageDTO languageDTO = new LanguageDTO();
		languageDTO.setCode("es");
		user.setLanguage(languageDTO);
		user.setCountry(new CountryDTO(192L));
		user.setNewsletter(true);
		user.setSurname("Ramos");
		//create user
		user = api.getUserClient().createUser(user);
		log.info("User created " + user);//receive the created user
		//user login
		user = api.connect(user);
		log.info("User logged " + user);//receive the logged user
		//user logout
		api.disconnect();
		log.info("User " + user + " logout");
		
		/////////////////////////
		// CREATE INSTALLATION //
		/////////////////////////
		//user login
		user = new UserDTO();
		user.setEmail(email);
		user.setPassword(password);
		user = api.connect(user);
		log.info("User logged " + user);//receive the logged user
		//prepare installation
		InstallationDTO installation = new InstallationDTO();
		installation.setName("myInstallation-" + UUID.randomUUID().toString());
		installation.setCountry(new CountryDTO(193L));
		installation.setCity("Melbourne");
		installation.setPostalCode("28043");
		installation.setAddress("Pez, 3");
		installation.setLongitude("-3.58556");
		installation.setLatitude("40.23555");
		installation.setTimeZone(new TimeZoneDTO(311L));
		//create installation
		installation = api.getInstallationClient().createInstallation(installation);
		log.info("Installation created " + installation);
		//user logout
		api.disconnect();
		log.info("User " + user + " logout");
		
		//////////////////////
		// REGISTER DEVICES //
		//////////////////////
		//user login
		user = new UserDTO();
		user.setEmail(email);
		user.setPassword(password);
		user = api.connect(user);
		log.info("User logged " + user);//receive the logged user
		//get installations
		List<InstallationDTO> allInstallations = api.getInstallationClient().getInstallations();//we expect only one
		log.info("Retrieved " + allInstallations.size() + " installations");
		installation = allInstallations.get(0);
		/////////////////
		// REGISTER GW //
		/////////////////
		//prepare Gateway
		DeviceDTO gateway = new DeviceDTO();
		gateway.setSerialNumber(Long.valueOf(random.nextInt(999999999)));
		gateway.setType(new CDeviceTypeDTO());
		gateway.getType().setId(Long.valueOf(DeviceTypeEnum.GATEWAY.getId()));
		gateway.setInstallation(installation);
		gateway.setName("MyGW");
		//check if it's registered
		DeviceAvailabityDTO deviceAvailability = api.getDeviceClient().getDeviceAvailability(installation.getId(), gateway.getSerialNumber());
		log.info("Dev with SerialNum " + gateway.getSerialNumber() + "=> registered: " + deviceAvailability.isRegistered() +
			", connected: " + deviceAvailability.isConnected() + ", state: " + deviceAvailability.getDeviceState());
		//NOTA PARA DEKALABS: AQUI DEBE DE COMPROBARSE TAMBIEN QUE ESTA CONECTADO... (EL CODIGO SE DEJA COMENTADO PARA QUE FUNCIONE CON DISPOSITIVOS FICTICIOS)
		if(deviceAvailability.isRegistered() /*|| !deviceAvailability.isConnected()*/) {
			log.error("Unexpected error in tests. We expect this device  with serial number " + gateway.getSerialNumber() + " would be not registered");
			throw new Exception("Device " + gateway.getSerialNumber() + " was already registered");
		} 
		//register device
		gateway = api.getDeviceClient().createDeviceLoggedUser(gateway);
		log.info("Gateway registered succesfully " + gateway);
		///////////////////
		// REGISTER BASE //
		///////////////////
		//prepare base
		DeviceDTO base = new DeviceDTO();
		base.setSerialNumber(Long.valueOf(random.nextInt(999999999)));
		base.setType(new CDeviceTypeDTO());
		base.getType().setId(Long.valueOf(DeviceTypeEnum.BEVEL_US_BASE.getId()));
		base.setInstallation(installation);
		base.setName("MyBase");
		//check if it's registered
		deviceAvailability = api.getDeviceClient().getDeviceAvailability(installation.getId(), base.getSerialNumber());
		log.info("Dev with SerialNum " + base.getSerialNumber() + "=> registered: " + deviceAvailability.isRegistered() +
			", connected: " + deviceAvailability.isConnected() + ", state: " + deviceAvailability.getDeviceState());
		//NOTA PARA DEKALABS: AQUI NO SE COMPRUEBA SI LA BASE ESTA CONECTADA O NO, PUES ES LA BASE
		if(deviceAvailability.isRegistered()) {
			log.error("Unexpected error in tests. We expect this device  with serial number " + base.getSerialNumber() + " would be not registered");
			throw new Exception("Device " + base.getSerialNumber() + " was already registered");
		} 
		//register device
		base = api.getDeviceClient().createDeviceLoggedUser(base);
		log.info("Base registered succesfully " + base);
		//////////////////////
		// REGISTER DISPLAY //
		//////////////////////
		//prepare Display
		DeviceDTO display = new DeviceDTO();
		display.setSerialNumber(Long.valueOf(random.nextInt(999999999)));
		display.setType(new CDeviceTypeDTO());
		display.getType().setId(Long.valueOf(DeviceTypeEnum.BEVEL_US_DISPLAY.getId()));
		display.setInstallation(installation);
		display.setName("MyDisplay");
		//check if it's registered
		deviceAvailability = api.getDeviceClient().getDeviceAvailability(installation.getId(), display.getSerialNumber());
		log.info("Dev with SerialNum " + display.getSerialNumber() + "=> registered: " + deviceAvailability.isRegistered() +
			", connected: " + deviceAvailability.isConnected() + ", state: " + deviceAvailability.getDeviceState());
		//NOTA PARA DEKALABS: AQUI DEBE DE COMPROBARSE TAMBIEN QUE ESTA CONECTADO... (EL CODIGO SE DEJA COMENTADO PARA QUE FUNCIONE CON DISPOSITIVOS FICTICIOS)
		if(deviceAvailability.isRegistered() /*|| !deviceAvailability.isConnected()*/) {
			log.error("Unexpected error in tests. We expect this device  with serial number " + display.getSerialNumber() + " would be not registered");
			throw new Exception("Device " + display.getSerialNumber() + " was already registered");
		} 
		//register device
		display = api.getDeviceClient().createDeviceLoggedUser(display);
		log.info("Display registered succesfully " + display);
		//user logout
		api.disconnect();
		log.info("User " + user + " logout");
		
		/////////////////////
		// GET SYSTEM TYPE //
		/////////////////////
		///user login
		user = new UserDTO();
		user.setEmail(email);
		user.setPassword(password);
		user = api.connect(user);
		log.info("User logged " + user);//receive the logged user
		//Suppose that user has the following configuration and wires at home
		List<String> wires = Arrays.asList("Inertial", "O_B", "Y_Y1", "W_W1", "R_RH", "C");
		//get hvac from catalog
		HvacDTO hvac = api.getHvacClient().getHvacByWireList(wires);
		log.info("For wires " + wires.toString() + " systemType " + hvac.getSystemType() + " was received");// expected 210A
		//user logout
		api.disconnect();
		log.info("User " + user + " logout");
		
		/////////////////////////////////////////
		// INSTALLATION SETUP FROM SYSTEM TYPE //
		/////////////////////////////////////////
		//user login
		user = new UserDTO();
		user.setEmail(email);
		user.setPassword(password);
		user = api.connect(user);
		log.info("User logged " + user);//receive the logged user
		//get installations
		allInstallations = api.getInstallationClient().getInstallations();//we expect only one
		log.info("Retrieved " + allInstallations.size() + " installations");
		installation = allInstallations.get(0);
		//get installation devices
		List<DeviceDTO> registeredDevices = api.getInstallationClient().getInstallationDevices(installation);
		log.info("There are " + registeredDevices.size());
		registeredDevices.stream().forEach(dev -> log.info("Device " + dev.getSerialNumber() + ", name " + dev.getName() + ", type " + dev.getType()));
		//DONT COPY EXACTLY THIS PIECE OF CODE BECAUSE PROBABLY IS NOT THE BEST WAY TO PREPARE DATA BEFORE CALLING SETUP
		//get display serialNum
		long serialNumber = registeredDevices.stream().filter(
			dev -> DeviceTypeEnum.BEVEL_US_DISPLAY == DeviceTypeEnum.getByCode(dev.getType().getId().intValue()))
			.findFirst().orElse(new DeviceDTO(-1L)).getSerialNumber();
		log.info("Display serial number: " + serialNumber);
		//setup installation for this system type
		//the device should have been updated with its Subtype
		DeviceDTO updatedDevice = api.getInstallationClient().setUpInstallation(installation.getId(), serialNumber, new SystemTypeDTO(hvac.getSystemType()));
		log.info("The device " + updatedDevice.getSerialNumber() + " has been updated with Subtype " + updatedDevice.getSubType().getId());
		//user logout
		api.disconnect();
		log.info("User " + user + " logout");
	
		//////////////////////////
		// DISPLAY-BASE PAIRING //
		//////////////////////////
		//user login
		user = new UserDTO();
		user.setEmail(email);
		user.setPassword(password);
		user = api.connect(user);
		log.info("User logged " + user);//receive the logged user
		//get installations
		allInstallations = api.getInstallationClient().getInstallations();//we expect only one
		log.info("Retrieved " + allInstallations.size() + " installations");
		installation = allInstallations.get(0);
		//get installation devices
		registeredDevices = api.getInstallationClient().getInstallationDevices(installation);
		log.info("There are " + registeredDevices.size());
		registeredDevices.stream().forEach(dev -> log.info("Device " + dev.getSerialNumber() + ", name " + dev.getName() + ", type " + dev.getType()));
		//DONT COPY EXACTLY THIS PIECE OF CODE BECAUSE PROBABLY IS NOT THE BEST WAY TO PREPARE DATA BEFORE CALLING SETUP
		//get display serialNum
		long serialNumberMaster = registeredDevices.stream().filter(
					dev -> DeviceTypeEnum.BEVEL_US_DISPLAY == DeviceTypeEnum.getByCode(dev.getType().getId().intValue()))
					.findFirst().orElse(new DeviceDTO(-1L)).getSerialNumber();
		log.info("Display serial number: " + serialNumber);
		//get base serialNum
		long serialNumberSlave = registeredDevices.stream().filter(
					dev -> DeviceTypeEnum.BEVEL_US_BASE == DeviceTypeEnum.getByCode(dev.getType().getId().intValue()))
					.findFirst().orElse(new DeviceDTO(-1L)).getSerialNumber();
		log.info("Base serial number: " + serialNumberSlave);
		//start pairing base and display
		//TODO NOTA DEKALABS: aqui habria que crear el web socket para recibir el evento de listening en primer lugar y en segundo el de pairing,
		// que confirma que display y base se han pareado
		api.getInstallationClient().startPairing(installation.getId(), serialNumberMaster, serialNumberSlave);
		log.info("Display and base paired");
		//user logout
		api.disconnect();
		log.info("User " + user + " logout");
		
	}

}
