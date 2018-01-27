package jpmc.test.mpa.app;

import javax.xml.ws.Endpoint;

public class SalesMessageServerPublisher {
	
	public static void main(String[] args) {
		   Endpoint.publish("http://localhost:9999/ws/sales", new SalesMessageServer());
	    }
}
