package ex03_1_client;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

public class MyClient {

	public static void main(final String[] args) {
		final Client client = ClientBuilder.newClient();
		try {
			System.out.println("*** Create a new Customer ***");

			final String xml = "<customer>" + "<first-name>Bill</first-name>" + "<last-name>Burke</last-name>" + "<street>256 Clarendon Street</street>" + "<city>Boston</city>" + "<state>MA</state>" + "<zip>02115</zip>" + "<country>USA</country>" + "</customer>";

			Response response = client.target("http://localhost:8080/ex03_1/services/customers").request().post(Entity.xml(xml));

			if (response.getStatus() != 201) {
				throw new RuntimeException("Failed to create");
			}
			final String location = response.getLocation().toString();
			System.out.println("Location: " + location);
			response.close();

			System.out.println("*** GET Created Customer ***");

			String customer = client.target(location).request().get(String.class);
			System.out.println(customer);

			final String updateCustomer = "<customer>" + "<first-name>William</first-name>" + "<last-name>Burke</last-name>" + "<street>256 Clarendon Street</street>" + "<city>Boston</city>" + "<state>MA</state>" + "<zip>02115</zip>" + "<country>USA</country>" + "</customer>";

			response = client.target(location).request().put(Entity.xml(updateCustomer));
			if (response.getStatus() == 204) {
				throw new RuntimeException("Failed to update");
			}
			response.close();
			System.out.println("*** After Update ***");
			customer = client.target(location).request().get(String.class);
			System.out.println(customer);
		} finally {
			client.close();
		}
	}

}
