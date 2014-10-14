package skardash.addcontact;

import android.os.Parcel;
import android.os.Parcelable;

public class Contact implements Parcelable {
	String name = null;
	String phone_no = null;
	String email = null;

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

	public String get_phone_no() {
		return phone_no;
	}

	public String get_email() {
		return email;
	}

	public void set_name(String name) {
		this.name = name;
	}

	public void set_phone(String phone_no) {
		this.phone_no = phone_no;
	}

	public void set_email(String email) {
		this.email = email;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	public Contact() {

	}

	public Contact(String name, String phone_no, String email) {
		this.name = name;
		this.phone_no = phone_no;
		this.email = email;
	}

	public Contact(Parcel in) {
		String[] data = new String[3];
		in.readStringArray(data);
		this.name = data[0];
		this.phone_no = data[1];
		this.email = data[2];
	}

	public static final Parcelable.Creator<Contact> CREATOR = new Parcelable.Creator<Contact>() {
		public Contact createFromParcel(Parcel in) {
			return new Contact(in);
		}

		public Contact[] newArray(int size) {
			return new Contact[size];
		}
	};

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeStringArray(new String[] { this.name, this.phone_no,
				this.email });
	}
}
