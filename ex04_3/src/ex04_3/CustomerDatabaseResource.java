package ex04_3;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@Path("/customers")
public class CustomerDatabaseResource {
	protected EuropeCustomerResource europe = new EuropeCustomerResource();
	protected NACustomerResource northamerica = new NACustomerResource();

	@PathParam("{database}-db")
	public Object service(@PathParam("database") final String db) {
		if (db.equals("europe")) {
			return europe;
		} else if (db.equals("northamerica")) {
			return northamerica;
		} else {
			return null;
		}
	}
}
