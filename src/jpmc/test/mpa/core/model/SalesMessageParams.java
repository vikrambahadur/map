package jpmc.test.mpa.core.model;

public class SalesMessageParams {
	
	private Object[] values = null;
	private MessageType msgType = null;
	
	public SalesMessageParams(Object[] values , MessageType msgType) {
		this.values = values;
		this.msgType = msgType;
	}
	
	public Object[] getValues() {
		return this.values;
	}
	
	public MessageType getMessageType() {
		return this.msgType;
	}
}
