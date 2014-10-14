package skardash.addcontact;

import java.util.ArrayList;

import android.content.ClipData.Item;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

public class Category implements Parcelable {
	private String category;
	private ArrayList<Contact> items;

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public ArrayList<Contact> getItems() {
		return items;
	}

	public void setItems(ArrayList<Contact> items) {
		this.items = items;
	}

	public Category() {

	}

	public Category(String _category) {
		category = _category;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(category);
		Bundle b = new Bundle();
		b.putParcelableArrayList("items", items);//putParcelableArrayList(, );
		dest.writeBundle(b);

	}

	public static final Parcelable.Creator<Category> CREATOR = new Parcelable.Creator<Category>() {
		public Category createFromParcel(Parcel in) {
			Category category = new Category();
			category.category = in.readString();
//			Bundle b = in.readBundle(Item.class.getClassLoader());
//			category.items = b.getParcelableArrayList("items");

			return category;
		}

		@Override
		public Category[] newArray(int size) {
			return new Category[size];
		}
	};
}