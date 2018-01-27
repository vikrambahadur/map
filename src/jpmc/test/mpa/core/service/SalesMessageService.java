package jpmc.test.mpa.core.service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import jpmc.test.mpa.core.model.MessageType;
import jpmc.test.mpa.core.model.SalesMessage;
import jpmc.test.mpa.core.model.SalesMessageParams;

/**
 * Sales message service, defined all the containers to hold in bound messages and consolidated messages.
 * Provides single public method <b>execute</b> to handle the message execution
 * Depends on SalesMessage class to do message processing.
 * @author vikrambahadur
 *
 */
public class SalesMessageService {
	
	private static final Logger LOGGER = Logger.getLogger( SalesMessageService.class.getName() );
	
	//Represent the database table to hold all the receive messages
	private static Map<Integer, String> messageTable = new LinkedHashMap<>();
	
	//Consolidated sales container hold them product type wise
	private static Map<String, SalesMessage> salesMessagesProductTypeWise = new HashMap<>();
	
	private static int LOGS_SALES_COUNTER = 10;
	private static int LOGS_ADJUSTMENT_COUNTER = 50;
	private static int AUTO_ID = 1;
	
	public SalesMessageService() {
	}

	/**
	 * Execute message, a) validate, b) record, c) consume and d) report details
	 * Return message id to caller (client), can be used to track message.
	 * @param textMessage
	 * @return
	 */
	public int execute(String textMessage) {
		// Check if messages do not need to consume, return -1
		if (messageTable.size()+1 > LOGS_ADJUSTMENT_COUNTER) {
			return -1;
		}
		
		SalesMessageParams  msgParams = SalesMessage.vaidateMessage(textMessage);
		int messageId = recordMessage(textMessage);
		MessageCosumer msgConsumer = new MessageCosumer(msgParams, salesMessagesProductTypeWise);
		//Thread th = new Thread(msgConsumer);
		//th.start();
		msgConsumer.run();
		reportDetials();
		return messageId;
	}
	
	// Insert the message, right now just log
	private int recordMessage(String message) {
		LOGGER.log(Level.INFO, "Record Message :: "+ message);
		messageTable.put(AUTO_ID++, message);
		return AUTO_ID;
		
	}
	
	/**
	 * report the details of sales and adjustment made
	 */
	private void reportDetials() {
		if (messageTable.size() % LOGS_SALES_COUNTER == 0) {
			LOGGER.log(Level.INFO,"************** Reporting Details ********************");
			SalesMessageService.salesMessagesProductTypeWise.values().stream().forEach((msg) -> msg.logSalesDetails());
			LOGGER.log(Level.INFO,"*****************************************************");
		}
		if (messageTable.size() == LOGS_ADJUSTMENT_COUNTER) {
			LOGGER.log(Level.INFO, "::................GOING TO PAUSE...............:: ");
			LOGGER.log(Level.INFO,"************** Reporting Final Details ********************");
			salesMessagesProductTypeWise.values().stream().forEach((msg) -> msg.logAdjustmentDetails());
			LOGGER.log(Level.INFO,"**********************************************************");
		}
		
	}
	/**
	 * Message consumer, right now single threaded
	 * @author vikrambahadur
	 *
	 */
	class MessageCosumer {//implements Runnable  {
		private Object[] values = null;  
		private MessageType msgType = null;
		private Map<String, SalesMessage> salesMessagesProductTypeWise = null;
		
		public MessageCosumer(SalesMessageParams msgParams, Map<String, SalesMessage> salesMessagesProductTypeWise) {
			this.values = msgParams.getValues();
			this.msgType = msgParams.getMessageType();
			this.salesMessagesProductTypeWise = salesMessagesProductTypeWise;
		}
		
		//@Override
		public void run() {
			SalesMessage.processMessage(values, msgType, salesMessagesProductTypeWise);
		}
		
		
	}
	
}
