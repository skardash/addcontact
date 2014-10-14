package skardash.addcontact;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

public class Container_alt {
	private ArrayList<Contact_alt> contact_list;

	public Container_alt(ArrayList<Contact_alt> container) {
		this.contact_list = container;
	}

	public ArrayList<Contact_alt> get_contact_list() {
		return contact_list;
	}
	
}
