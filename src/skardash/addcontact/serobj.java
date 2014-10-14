package skardash.addcontact;

import java.io.Serializable;
import java.util.ArrayList;

public class serobj implements Serializable {
	public int i = 12;
	ArrayList<Contact> intlist = null;

	public serobj() {
		intlist = new ArrayList<Contact>();
		intlist.add(new Contact("a","b","c"));
	}

}
