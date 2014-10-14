package skardash.addcontact;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

public class Container {
	private ArrayList<Contact> contact_list;

	public Container(ArrayList<Contact> container) {
		this.contact_list = container;
	}

	public ArrayList<Contact> get_contact_list() {
		return contact_list;
	}
	
}
