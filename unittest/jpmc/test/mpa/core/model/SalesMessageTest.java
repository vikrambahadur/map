package jpmc.test.mpa.core.model;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;

public class SalesMessageTest {

	@org.junit.Test
	public void testVaidateMessageType1() {
		SalesMessageParams messageParams = SalesMessage.vaidateMessage("apple at 10p");
		Assert.assertEquals(MessageType.MT1, messageParams.getMessageType());
		Assert.assertEquals("apple", messageParams.getValues()[0]);
		Assert.assertEquals("10", messageParams.getValues()[1]);
	}
	
	@org.junit.Test
	public void testVaidateMessageType2() {
		SalesMessageParams messageParams = SalesMessage.vaidateMessage("20 sales of apples at 10p each");
		Assert.assertEquals(MessageType.MT2, messageParams.getMessageType());
		Assert.assertEquals("20", messageParams.getValues()[0]);
		Assert.assertEquals("apple", messageParams.getValues()[1]);
		Assert.assertEquals("10", messageParams.getValues()[2]);
	}
	
	@org.junit.Test
	public void testVaidateMessageType3() {
		SalesMessageParams messageParams = SalesMessage.vaidateMessage("Add 20p apples");
		Assert.assertEquals(MessageType.MT3, messageParams.getMessageType());
		Assert.assertEquals("Add", messageParams.getValues()[0]);
		Assert.assertEquals("20", messageParams.getValues()[1]);
	}
	
	@org.junit.Test
	public void testProcessMessageType1() {
		Map<String, SalesMessage> salesMessagesProductTypeWise = new HashMap<>();
		SalesMessageParams messageParams1 = SalesMessage.vaidateMessage("apple at 10p");
		SalesMessageParams messageParams2 = SalesMessage.vaidateMessage("apple at 10p");
		
		SalesMessage.processMessage(messageParams1.getValues(), messageParams1.getMessageType(), salesMessagesProductTypeWise);
		SalesMessage.processMessage(messageParams2.getValues(), messageParams2.getMessageType(), salesMessagesProductTypeWise);
		
		Assert.assertEquals(20, salesMessagesProductTypeWise.get("apple").getSalesValue());
	}
	
	@org.junit.Test
	public void testProcessMessageType1And2() {
		Map<String, SalesMessage> salesMessagesProductTypeWise = new HashMap<>();
		SalesMessageParams messageParams1 = SalesMessage.vaidateMessage("apple at 10p");
		SalesMessageParams messageParams2 = SalesMessage.vaidateMessage("20 sales of apples at 10p each");
		
		SalesMessage.processMessage(messageParams1.getValues(), messageParams1.getMessageType(), salesMessagesProductTypeWise);
		SalesMessage.processMessage(messageParams2.getValues(), messageParams2.getMessageType(), salesMessagesProductTypeWise);
		
		Assert.assertEquals(210, salesMessagesProductTypeWise.get("apple").getSalesValue());
		Assert.assertEquals(21, salesMessagesProductTypeWise.get("apple").getQuantity());
	}
	
	@org.junit.Test
	public void testProcessMessageType1And2And3() {
		Map<String, SalesMessage> salesMessagesProductTypeWise = new HashMap<>();
		SalesMessageParams messageParams1 = SalesMessage.vaidateMessage("apple at 10p");
		SalesMessageParams messageParams2 = SalesMessage.vaidateMessage("20 sales of apples at 10p each");
		SalesMessageParams messageParams3 = SalesMessage.vaidateMessage("Add 20p apples");
		
		SalesMessage.processMessage(messageParams1.getValues(), messageParams1.getMessageType(), salesMessagesProductTypeWise);
		SalesMessage.processMessage(messageParams2.getValues(), messageParams2.getMessageType(), salesMessagesProductTypeWise);
		SalesMessage.processMessage(messageParams3.getValues(), messageParams3.getMessageType(), salesMessagesProductTypeWise);
		
		Assert.assertEquals(210+21*20, salesMessagesProductTypeWise.get("apple").getSalesValue());
		Assert.assertEquals(21, salesMessagesProductTypeWise.get("apple").getQuantity());
		
	}
	
	@org.junit.Test
	public void testProcessMessageType1And2And3WithAdjustLog() {
		Map<String, SalesMessage> salesMessagesProductTypeWise = new HashMap<>();
		SalesMessageParams messageParams1 = SalesMessage.vaidateMessage("apple at 10p");
		SalesMessageParams messageParams2 = SalesMessage.vaidateMessage("20 sales of apples at 10p each");
		SalesMessageParams messageParams3 = SalesMessage.vaidateMessage("Add 20p apples");
		
		SalesMessage.processMessage(messageParams1.getValues(), messageParams1.getMessageType(), salesMessagesProductTypeWise);
		SalesMessage.processMessage(messageParams2.getValues(), messageParams2.getMessageType(), salesMessagesProductTypeWise);
		SalesMessage.processMessage(messageParams3.getValues(), messageParams3.getMessageType(), salesMessagesProductTypeWise);
		
		Assert.assertEquals(210+21*20, salesMessagesProductTypeWise.get("apple").getSalesValue());
		Assert.assertEquals(21, salesMessagesProductTypeWise.get("apple").getQuantity());
		Assert.assertEquals(OperationType.ADD,salesMessagesProductTypeWise.get("apple").getSalesAdjustmentDetails().get(0).getOperationType());
		
	}
}
