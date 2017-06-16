package ex04_2;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("/services")
public class ShoppingApplication extends Application {
	private final Set<Object> singletons = new HashSet<Object>();

	public ShoppingApplication() {
		singletons.add(new CustomerResource());
	}

	@Override
	public Set<Object> getSingletons() {
		return singletons;
	}
}
