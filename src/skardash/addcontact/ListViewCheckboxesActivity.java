package skardash.addcontact;

import java.util.ArrayList;

import skardash.addcontact.R;
import com.google.gson.Gson;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ListViewCheckboxesActivity extends Activity {
	public final static String country_list = "country.list";
	ContactAdapter dataAdapter = null;
	Context ctx;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		// Generate list View from ArrayList
		// Container container =
		// (Container)getIntent().getSerializableExtra("contact_list");
		// ArrayList<Contact> contact_list = container.get_contact_list();
		Intent intent = this.getIntent();
		String json = intent.getStringExtra("contact_list");
		System.out.println("json inside: ");
		System.out.println(json);

		Gson gson = new Gson();
		Container container = (Container) gson.fromJson(json, Container.class);

		// now make it to show the listarray content
		displayListView(container.get_contact_list());
		checkButtonClick();
		selectButtonsClick();
		ctx = this;
	}

	private void DisplayContacts(ArrayList<Contact> contact_list) {
		dataAdapter = new ContactAdapter(this, R.layout.contact_info,
				contact_list);
		ListView listView = (ListView) findViewById(R.id.listView1);
		// Assign adapter to ListView
		listView.setAdapter(dataAdapter);
	}

	private void displayListView(ArrayList<Contact> contact_list) {
		dataAdapter = new ContactAdapter(this, R.layout.contact_info,
				contact_list);
		ListView listView = (ListView) findViewById(R.id.listView1);
		listView.setAdapter(dataAdapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// When clicked, show a toast with the TextView text
				Contact contact = (Contact) parent.getItemAtPosition(position);
//				Toast.makeText(getApplicationContext(),
//						"Clicked on Row: " + contact.get_name(),
//						Toast.LENGTH_LONG).show();
			}
		});
	}

	private class ContactAdapter extends ArrayAdapter<Contact> {
		private ArrayList<Contact> contact_list;

		public ContactAdapter(Context context, int textViewResourceId,
				ArrayList<Contact> contact_list) {
			super(context, textViewResourceId, contact_list);
			this.contact_list = new ArrayList<Contact>();
			this.contact_list.addAll(contact_list);
		}

		private class ContactHolder {
			CheckBox name;
			TextView phone_no;
			TextView email;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ContactHolder holder = null;
			Log.v("ConvertView", String.valueOf(position));
			if (convertView == null) {
				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = vi.inflate(R.layout.contact_info, null);
				holder = new ContactHolder();
				holder.name = (CheckBox) convertView.findViewById(R.id.name);
				holder.phone_no = (TextView) convertView
						.findViewById(R.id.phone_no);
				holder.email = (TextView) convertView.findViewById(R.id.email);

				convertView.setTag(holder);
				holder.name.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						CheckBox cb = (CheckBox) v;
						Contact contact = (Contact) cb.getTag();
//						Toast.makeText(
//								getApplicationContext(),
//								"Clicked on Checkbox: " + cb.getText() + " is "
//										+ cb.isChecked(), Toast.LENGTH_LONG)
//								.show();
						contact.setSelected(cb.isChecked());
					}
				});
			} else {
				holder = (ContactHolder) convertView.getTag();
			}
			Contact contact = contact_list.get(position);
			holder.name.setChecked(contact.isSelected());
			holder.name.setText(contact.get_name());
			holder.phone_no.setText(contact.get_phone_no());
			holder.email.setText(contact.get_email());
			holder.name.setTag(contact);
			return convertView;
		}
	}

	private void selectButtonsClick() {
		Button select_all = (Button) findViewById(R.id.select_all);
		select_all.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ArrayList<Contact> contact_list = dataAdapter.contact_list;
				ListView listView = (ListView) findViewById(R.id.listView1);
				for (int i = 0; i < contact_list.size(); i++) {
					Contact contact = contact_list.get(i);
					contact.setSelected(true);
				}
				displayListView(contact_list);
			}
		});
		

		Button select_none = (Button) findViewById(R.id.select_none);
		select_none.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ArrayList<Contact> contact_list = dataAdapter.contact_list;
				ListView listView = (ListView) findViewById(R.id.listView1);
				for (int i = 0; i < contact_list.size(); i++) {
					Contact contact = contact_list.get(i);
					contact.setSelected(false);
				}
				displayListView(contact_list);
			}
		});
		
	}

	private void checkButtonClick() {
		Button myButton = (Button) findViewById(R.id.save);
		myButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				StringBuffer responseText = new StringBuffer();
				responseText.append("The following were selected...\n");
				ArrayList<Contact> contact_list = dataAdapter.contact_list;
				ArrayList<Contact> selected_contact = new ArrayList<Contact>();
				for (int i = 0; i < contact_list.size(); i++) {
					Contact contact = contact_list.get(i);
					if (contact.isSelected()) {
						responseText.append("\n" + contact.get_name());
						selected_contact.add(contact);
					}
				}
//				Toast.makeText(getApplicationContext(), responseText,
//						Toast.LENGTH_LONG).show();

				Container container = new Container(selected_contact);

				Intent intent = new Intent(ctx, MainActivity.class);

				Gson gson = new Gson();
				String json = gson.toJson(container);

				intent.putExtra("selected_contacts", json);
				startActivity(intent);
			}
		});
	}
}