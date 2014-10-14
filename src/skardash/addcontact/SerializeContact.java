package skardash.addcontact;

import java.util.ArrayList;

public class SerializeContact {
	private static int CONTACT_SIZE = 3;
	public static String[] contact_list_to_array(ArrayList<Contact> contact_list) {
		String[] result = new String[contact_list.size() * CONTACT_SIZE];
		for (int cnt = 0; cnt < contact_list.size(); cnt++) {
			result[cnt * CONTACT_SIZE] = contact_list.get(cnt).get_name();
			result[cnt * CONTACT_SIZE + 1] = contact_list.get(cnt)
					.get_phone_no();
			result[cnt * CONTACT_SIZE + 2] = contact_list.get(cnt).get_email();
		}
		return result;
	}
	
	public static ArrayList<Contact> array_to_contact_list(String[] strarr) {
		ArrayList<Contact> result = new ArrayList<Contact>();
		for (int cnt = 0; cnt < strarr.length/CONTACT_SIZE; cnt++) {
			Contact contact = new Contact();
			contact.set_name(strarr[cnt * CONTACT_SIZE]);
			contact.set_phone(strarr[cnt * CONTACT_SIZE + 1]);
			contact.set_email(strarr[cnt * CONTACT_SIZE + 2]);
			result.add(contact);
		}
		return result;
	}

}
