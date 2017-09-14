package examples;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import org.apache.log4j.Logger;

import com.greenmomit.api.client.APIClient;
import com.greenmomit.dto.CalendarDTO;
import com.greenmomit.dto.CalendarPeriodDTO;
import com.greenmomit.dto.CountryDTO;
import com.greenmomit.dto.CurrencyDTO;
import com.greenmomit.dto.ErrorDTO;
import com.greenmomit.dto.LanguageDTO;
import com.greenmomit.dto.MeasureDTO;
import com.greenmomit.dto.UserDTO;
import com.greenmomit.exception.APIException;

public class APICalendarExamples {
	/**
	 * Common logger
	 */
	private static Logger log = Logger.getLogger(APICalendarExamples.class);
	
	
	public static void main(String[] args){
		
		APIClient api;
		try {
			//Genero el objeto API y me logo con un usuario existente
			api = new APIClient("http://54.194.234.22:8080/momitbevel/v2/", "345678");
			
			//Creo un usuario para esta prueba
			//1.2 Execute
			UserDTO testUser = generateRandomUser();
			testUser = api.getUserClient().createUser(testUser);
			log.info("User created "+testUser);//receive the created user
			
			//hago login para poder actuar con el api
			api.connect(testUser);
			
			//Busco mis calendarios, que deberian ser 0
			List<CalendarDTO> userCalendars = api.getCalendarClient().getCalendars();
			System.out.println(userCalendars.size());
			
			CalendarDTO calendar = new CalendarDTO();
			calendar.setFriendlyName("TestCalendarapiclient-"+UUID.randomUUID());
			calendar.setTimeZone("Europe/Madrid");
			calendar.setZoneId(12l);
			calendar = api.getCalendarClient().createCalendar(calendar); //retorna el calendario actualizado con  su id 
			System.out.println(calendar);
			
			//En la respuesta ya venia actualizada la info, pero por volver a comprobar vuelvo a hacer la peticion y veo que mis calendarios son 1
			userCalendars = api.getCalendarClient().getCalendars();
			System.out.println("User calendars "+ userCalendars.size());
			
			
			//modifico parametros generales del calendario, por ejemplo el nombre
			CalendarDTO existingCalendar = userCalendars.get(0); //first period of the first calendar
			System.out.println("El nombre de este calendario es  " + existingCalendar.getFriendlyName());
			existingCalendar.setFriendlyName("ModifiedCalendar");
			CalendarDTO modifiedCalendar = api.getCalendarClient().updateCalendar(existingCalendar);//devuelve el calendario actualizado
			System.out.println(modifiedCalendar.getFriendlyName());	 
			
			//aniado un periodo al calendario
			//Add Periods to calendar 
			CalendarPeriodDTO period = new CalendarPeriodDTO(15.5f,Calendar.TUESDAY, 9, 00, 10,00); //los martes de 9 a 10
			period = api.getCalendarClient().createCalendarPeriod(existingCalendar, period); //devuevel el periodo actualizado con su id
			System.out.println(period);
			existingCalendar.addCalendarPeriod(period);
			
			//Modify period
			CalendarPeriodDTO periodToModify = existingCalendar.getPeriods().get(0);
			System.out.println("La hora fin de este periodo "+periodToModify.getId()+" es " +periodToModify.getStopHour());
			periodToModify.setStopHour(15);
			periodToModify = api.getCalendarClient().updateCalendarPeriod(existingCalendar, periodToModify);
			System.out.println("ahora la hora fin es "+ periodToModify.getStopHour());
			
			//Delete period on calendar
			api.getCalendarClient().deleteCalendarPeriod(existingCalendar, periodToModify);
			
			
			//Delete whole calendar
			api.getCalendarClient().deleteCalendar(existingCalendar);
			
			//refresco la info y veo que mis calendarios son 0
			userCalendars = api.getCalendarClient().getCalendars();
			System.out.println("User calendars "+ userCalendars.size());
			
			
			api.disconnect();
			
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (APIException e) {
			log.error("API Controlled Exception " + e.getError());
			
			//Further info of the error is on 
			ErrorDTO error = e.getError();
			error.getCode(); // Use it for translations, maybe?
			error.getInfo(); //Use it for po files translations
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}
	
	public static UserDTO generateRandomUser(){
		//1. First create a user
		//1.1 Prepare
		UserDTO user = new UserDTO();
		user.setEmail("testUser-"+UUID.randomUUID().toString()+"@gmail.com");//random
		user.setPassword("123456"); //valid pass
		user.setLanguage(new LanguageDTO().setCode("es"));
		user.setName("apiGeneralExamples");
		user.setNewsletter(true);
		user.setSurname("apiGeneralExamplesSURNAME");
		
		//
		CountryDTO country = new CountryDTO(22l); //uno que ya conozco el id, aunque podria buscarlo por las peticiones a api.getCountryClient().getall();
		country.setMeasure(new MeasureDTO(11l).setCurrency(new CurrencyDTO(66l))); //lo mismo, los podria haber sacado del catalogo de measureClient y currencyClient
		user.setCountry(country); //select one from catalog
		return user;
	}
}
