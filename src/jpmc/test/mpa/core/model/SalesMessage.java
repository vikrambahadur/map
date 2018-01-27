package jpmc.test.mpa.core.model;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * @author vikrambahadur
 * Sales Message, which provide the following features ( under domain driven design consideration)
 * <li>validate message, as per pre defined format</li>
 * <li>process sales message and keep update the product type wise container map</li>
 * <li>log details sales and adjustment made to sales</li>
 *
 */
public class SalesMessage extends Message {
	private static final Logger LOGGER = Logger.getLogger( SalesMessage.class.getName() );
	
	//Represent instance of sales message state
	private MessageType messageType;
	private String productType;
	private int quantity;
	private int price;
	private int salesValue;
	private OperationType operationType;
	
	//Hold adjustment details linked with this sales message
	private List<AdjustmentDetails> salesAdjustmentDetails = new ArrayList<>();
	
	//Message formatters as per expected message types
	private static MessageFormat mf1 = new MessageFormat("{0} at {1}p");
	private static MessageFormat mf2 = new MessageFormat("{0} sales of {1}s at {2}p each");
	private static MessageFormat mf3 = new MessageFormat("{0} {1}p {2}s");
	
	/**
	 * Process message, on basis of type of message get values of product sales like price, quantity, product type etc
	 * Create instance of sales message and hold in collection to reuse it in case same product type sales message receive again
	 * In case of adjustment, do the adjustment to sales message and hold into it for reporting purpose
	 * @param values
	 * @param messageType
	 * @param salesMessagesProductTypeWise
	 */
	public static void processMessage(Object[] values, MessageType messageType, Map<String, SalesMessage> salesMessagesProductTypeWise) {
		String productType = null;
		int quantity=0;
		int price=0;
		OperationType operationType = OperationType.ADD;
		if (messageType == MessageType.MT1) {
			productType = (String) values[0];
			price =  Integer.valueOf((String)values[1]);
			quantity = 1;
		} else if (messageType == MessageType.MT2) {
			productType = (String) values[1];
			quantity = Integer.valueOf((String)values[0]);
			price = Integer.valueOf((String)values[2]);
		} else if (messageType == MessageType.MT3) {
			productType = (String) values[2];
			String op = (String)values[0];
			price = Integer.valueOf((String)values[1]);
			if ("Add".equals(op) ){
				operationType = OperationType.ADD;
			} else if ("Subtract".equals(op)) {
				operationType = OperationType.SUBTRACT;
			} else if ("Multiply".equals(op)) {
				operationType = OperationType.MULTIPLY;
			}
		}
		if ( ! salesMessagesProductTypeWise.containsKey(productType)) {
			SalesMessage salesMessage = new SalesMessage();
			salesMessage.messageType = messageType;
			salesMessage.productType = productType;
			salesMessage.quantity = quantity;
			salesMessage.operationType = operationType;
			salesMessage.price = price;
			salesMessage.salesValue = price*quantity;
			salesMessagesProductTypeWise.put(productType, salesMessage);
			
		} else {
			SalesMessage salesMessage = salesMessagesProductTypeWise.get(productType);
			if (messageType == MessageType.MT1 || messageType == MessageType.MT2) {
				salesMessage.setQuantity(salesMessage.getQuantity()+quantity);
				salesMessage.setSalesValue(salesMessage.getSalesValue() + quantity * price);
			}
			if (messageType == MessageType.MT3) {
				int presentSalesValue = salesMessage.getSalesValue();
				if (operationType == OperationType.ADD)
					salesMessage.setSalesValue(presentSalesValue + salesMessage.getQuantity() * price);
				if (operationType == OperationType.SUBTRACT)
					salesMessage.setSalesValue(presentSalesValue - salesMessage.getQuantity() * price);
				if (operationType == OperationType.MULTIPLY)
					salesMessage.setSalesValue(presentSalesValue * salesMessage.getQuantity() * price);
				salesMessage.salesAdjustmentDetails.add(new AdjustmentDetails(operationType, price, presentSalesValue, salesMessage.getSalesValue()));
			}

		}
	}
	
	/**
	 * Validate message as per available formatters,
	 * Parse them to get parameter values and return into SalesMessageParams
	 * @param textMessage
	 * @return
	 */
	public static SalesMessageParams vaidateMessage(String textMessage) {
		Object[] values = null;
		MessageType msgType = null;
		try {
			values = mf2.parse(textMessage);
			msgType = MessageType.MT2;
		} catch (Exception e) {
			try {
				values = mf1.parse(textMessage);
				msgType = MessageType.MT1;
			} catch (Exception e1) {
				try {
					values = mf3.parse(textMessage);
					msgType = MessageType.MT3;
				} catch (Exception e3) {
					throw new RuntimeException("Invalid Message", e3);
				}
			}
			
		}
		SalesMessageParams msgParams = new SalesMessageParams(values, msgType);
		return msgParams;
	}
	
	SalesMessage() {}
	
	public MessageType getMessageType() {
		return messageType;
	}
	public void setMessageType(MessageType messageType) {
		this.messageType = messageType;
	}
	public String getProductType() {
		return productType;
	}
	public void setProductType(String productType) {
		this.productType = productType;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public OperationType getOperationType() {
		return operationType;
	}
	public void setOperationType(OperationType operationType) {
		this.operationType = operationType;
	}
	public void setSalesValue(int salesValue) {
		this.salesValue = salesValue;
	}
	public int getSalesValue() {
		return salesValue;
	}
	public int getPrice() {
		return price;
	}
	
	public List<AdjustmentDetails> getSalesAdjustmentDetails() {
		return this.salesAdjustmentDetails;
	}
	
	// Log the sales details
	public void logSalesDetails() {
		LOGGER.log(Level.INFO, "Product Name = "+this.getProductType() +":: Quantity = "+this.getQuantity() + ":: SalesValue = "+this.getSalesValue());
	}
	// Log adjustment details
	public void logAdjustmentDetails () {
		LOGGER.log(Level.INFO, "*******************Adjustment Details, Product Name = " + this.getProductType());
		this.salesAdjustmentDetails.stream().forEach((adj) -> LOGGER.log(Level.INFO, 
		"Operation = "+adj.getOperationType() +":: Value = "+adj.getValue() + ":: Before Sales Value = "+adj.getBeforeSalesValue()
		+ ":: After Adjustment Sales Value = "+adj.getAfterSalesValue()));
		LOGGER.log(Level.INFO, "*****************************************************************");
	}
}

/**
 * Define the adjustment made during sales processing.
 * Each type of sales (product specific) can hold multiple adjustments
 * @author vikrambahadur
 *
 */
class AdjustmentDetails {
	private OperationType operationType;
	private int value;
	int beforeSalesValue;
	int afterSalesValue;
	public AdjustmentDetails(OperationType operationType, int value, int beforeSalesValue, int afterSalesValue) {
		this.operationType = operationType;
		this.value = value;
		this.beforeSalesValue = beforeSalesValue;
		this.afterSalesValue = afterSalesValue;
	}
	public OperationType getOperationType() {
		return operationType;
	}
	public int getValue() {
		return value;
	}
	public int getBeforeSalesValue() {
		return beforeSalesValue;
	}
	public int getAfterSalesValue() {
		return afterSalesValue;
	}

}
