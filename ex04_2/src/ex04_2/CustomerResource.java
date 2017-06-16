package ex04_2;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

@Path("/customers")
public class CustomerResource {
	private final Map<Integer, Customer> customerDB = new ConcurrentHashMap<Integer, Customer>();
	private final AtomicInteger idCounter = new AtomicInteger();

	// Create customer
	@POST
	@Consumes("application/xml")
	public Response createCustomer(final InputStream is) {
		final Customer customer = readCustomer(is);
		customer.setId(idCounter.incrementAndGet());
		customerDB.put(customer.getId(), customer);
		System.out.println("Created customer " + customer.getId());
		return Response.created(URI.create("/customers/" + customer.getId())).build();
	}

	// get customers with only digits
	@GET
	@Path("{id:\\d+}")
	@Produces("application/xml")
	public StreamingOutput getCustomer(@PathParam("id") final int id) {
		final Customer customer = customerDB.get(id);
		if (customer == null) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
		return new StreamingOutput() {

			@Override
			public void write(final OutputStream os) throws IOException, WebApplicationException {
				outputCustomer(os, customer);
			}
		};
	}

	// update customer with only digit
	@PUT
	@Path("{id:\\d+}")
	@Consumes("application/xml")
	public void updateCustomer(@PathParam("id") final int id, final InputStream is) {
		final Customer update = readCustomer(is);
		final Customer current = customerDB.get(id);
		if (current == null) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
		current.setFirstName(update.getFirstName());
		current.setLastName(update.getLastName());
		current.setStreet(update.getStreet());
		current.setState(update.getState());
		current.setZip(update.getZip());
		current.setCountry(update.getCountry());
	}

	// get customer with first name - last name
	@GET
	@Path("{first:[a-zA-Z]+}-{last:[a-zA-Z]+}")
	@Produces("application/xml")
	public StreamingOutput getCustomerFirstLast(@PathParam("first") final String firstName, @PathParam("last") final String lastName) {
		Customer found = null;
		for (final Customer cust : customerDB.values()) {
			if (cust.getFirstName().equals(firstName) && cust.getLastName().equals(lastName)) {
				found = cust;
				break;
			}
		}
		if (found == null) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
		final Customer customer = found;
		return new StreamingOutput() {
			public void write(final OutputStream outputStream) throws IOException, WebApplicationException {
				outputCustomer(outputStream, customer);
			}
		};
	}

	protected void outputCustomer(final OutputStream os, final Customer cust) throws IOException {
		final PrintStream writer = new PrintStream(os);
		writer.println("<customer id=\"" + cust.getId() + "\">");
		writer.println("   <first-name>" + cust.getFirstName() + "</first-name>");
		writer.println("   <last-name>" + cust.getLastName() + "</last-name>");
		writer.println("   <street>" + cust.getStreet() + "</street>");
		writer.println("   <city>" + cust.getCity() + "</city>");
		writer.println("   <state>" + cust.getState() + "</state>");
		writer.println("   <zip>" + cust.getZip() + "</zip>");
		writer.println("   <country>" + cust.getCountry() + "</country>");
		writer.println("</customer>");
	}

	protected Customer readCustomer(final InputStream is) {
		try {
			final DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			final Document doc = builder.parse(is);
			final Element root = doc.getDocumentElement();
			final Customer cust = new Customer();
			if (root.getAttribute("id") != null && !root.getAttribute("id").trim().equals("")) {
				cust.setId(Integer.valueOf(root.getAttribute("id")));
			}
			final NodeList nodes = root.getChildNodes();
			for (int i = 0; i < nodes.getLength(); i++) {
				final Element element = (Element) nodes.item(i);
				if (element.getTagName().equals("first-name")) {
					cust.setFirstName(element.getTextContent());
				} else if (element.getTagName().equals("last-name")) {
					cust.setLastName(element.getTextContent());
				} else if (element.getTagName().equals("street")) {
					cust.setStreet(element.getTextContent());
				} else if (element.getTagName().equals("city")) {
					cust.setCity(element.getTextContent());
				} else if (element.getTagName().equals("state")) {
					cust.setState(element.getTextContent());
				} else if (element.getTagName().equals("zip")) {
					cust.setZip(element.getTextContent());
				} else if (element.getTagName().equals("country")) {
					cust.setCountry(element.getTextContent());
				}
			}
			return cust;
		} catch (final Exception e) {
			throw new WebApplicationException(e, Response.Status.BAD_REQUEST);
		}
	}
}
