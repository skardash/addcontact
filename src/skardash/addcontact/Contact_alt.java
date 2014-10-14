package skardash.addcontact;
import java.util.ArrayList;
import java.util.Arrays;

public class Contact_alt{
	String name = "No name";
	ArrayList<String> phone_no = new ArrayList<String>();
	ArrayList<String> email = new ArrayList<String>();

	boolean selected = false;

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public String get_name() {
		return name;
	}

	public ArrayList<String> get_phone_no() {
		return phone_no;
	}

	public ArrayList<String> get_email() {
		return email;
	}

	public void set_name(String name) {
		this.name = name;
	}

	public void add_phone(String phone_no_elem) {
		this.phone_no.add(phone_no_elem);
	}

	public void add_email(String email_elem) {
		this.email.add(email_elem);
	}
	
	public void print_arraylist(String caption, ArrayList<String> arrlist) {
		System.out.print(caption + ": " );
		if (arrlist.size() > 0) {
			for (int cnt = 0; cnt < arrlist.size() - 1; cnt++) {
				System.out.print(arrlist.get(cnt) + "; ");
			}
			System.out.println(arrlist.get(arrlist.size() - 1));
		} else {
			System.out.println("No items found");
		}
	}
	
	public ArrayList<String> parse_string(String str) {
		return (new ArrayList<String>(Arrays.asList(str.replaceAll("\\s+","").split(";"))));
	}
	
	public void print_out_contact() {
		System.out.println("Name: " + this.name);
		print_arraylist("Phone(s)", phone_no);
		print_arraylist("Email(s)", email);
	}
	
	public String arrlist_to_string(ArrayList<String> arrstr) {
		String str = "";
		if (arrstr.size() > 0) {
			for (int cnt = 0; cnt < arrstr.size() - 1; cnt++) {
				str += arrstr.get(cnt) + "; ";
			}
			str += arrstr.get(arrstr.size() - 1);
		}
		return str;
	}

	public Contact_alt() {
		
	}

	public Contact_alt(String name, String phone_no, String email) {
		this.name = name;
		this.phone_no = parse_string(phone_no);
		this.email = parse_string(email);
	}
}
