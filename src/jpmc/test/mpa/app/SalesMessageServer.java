package jpmc.test.mpa.app;

import javax.jws.WebMethod;
import javax.jws.WebService;

import jpmc.test.mpa.core.service.SalesMessageService;
/**
 * 
 * @author vikrambahadur
 * Client Interface, web service, publish single method execute to read text message.
 */
@WebService
public class SalesMessageServer {

	SalesMessageService messageService = new SalesMessageService();
	
	@WebMethod
	public int sendMessage(String message) {
		return messageService.execute(message);
		
	}
}
