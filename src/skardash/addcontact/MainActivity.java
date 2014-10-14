package skardash.addcontact;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.RawContacts;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.gson.Gson;

public class MainActivity extends ActionBarActivity {
	// public String filepath;
	public static String last_open_directory = null;
	public String filepath;
	public int starting_number_of_loads;
	public int number_of_loads;
	private SharedPreferences prefs;
	public static final int MAXLOADS = 400;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		prefs = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		starting_number_of_loads = prefs.getInt("ExcelContactLoads", 0);
		number_of_loads = starting_number_of_loads;
		copy_asset_file();
		Intent intent_recieved = getIntent();
		String json_contacts = intent_recieved
				.getStringExtra("selected_contacts");
		if (json_contacts != null) {
			Gson gson = new Gson();
			Container container = (Container) gson.fromJson(json_contacts,
					Container.class);
			ArrayList<Contact> contact_list = container.get_contact_list();
			add_contacts(contact_list);
			String combined = "";
			for (int cnt = 0; cnt < contact_list.size(); cnt++) {
				combined += contact_list.get(cnt).get_name() + "/";
			}
			intent_recieved.removeExtra("selected_contacts");
			// txt.setText(combined);
		}
		Contact_alt contact_alt = new Contact_alt("Gorilla", "+123;+456", "a@b;c@d");
		SaveContact_mail_alt(contact_alt);
	}

	public void open_contacts(View view) {
		showContacts();
	}

	void showContacts() {
		Intent i = new Intent();
		i.setComponent(new ComponentName("com.android.contacts",
				"com.android.contacts.DialtactsContactsEntryActivity"));
		i.setAction("android.intent.action.MAIN");
		i.addCategory("android.intent.category.LAUNCHER");
		i.addCategory("android.intent.category.DEFAULT");
		startActivity(i);
	}

	public void contacts_added() {
		alert_box("Contacts were successfully loaded!");
	}

	public void alert_box(String message) {
		AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
		dlgAlert.setMessage(message);
		dlgAlert.setPositiveButton("OK", null);
		dlgAlert.setCancelable(true);
		dlgAlert.create().show();
		dlgAlert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// dismiss the dialog
			}
		});
	}

	public void getNumber(ContentResolver cr) {
		Cursor phones = cr.query(
				ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null,
				null, null);
		// use the cursor to access the contacts
		while (phones.moveToNext()) {
			String name = phones
					.getString(phones
							.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
			// get display name
			String phoneNumber = phones
					.getString(phones
							.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
			// get phone number
			System.out.println(name + "/" + phoneNumber);
		}
	} 
	
	public ArrayList<String[]> get_contact_data() {
		ContentResolver cr = this.getContentResolver();
		Cursor phones = cr.query(
				ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null,
				null, null);
		
		ArrayList<String[]> arrstring = new ArrayList<String[]>();
		System.out.println("Starting the loop");
		int loopcnt = 0;
		while (phones.moveToNext()) {
			System.out.println("Inside loop " + loopcnt++);
			ArrayList<String> arrstr_elem = new ArrayList<String>();
			String name = phones
					.getString(phones
							.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
			arrstr_elem.add(name);
			
			// get display name
			String phoneNumber = phones
					.getString(phones
							.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
			arrstr_elem.add(phoneNumber);
			System.out.println("Arraylist created");
			String[] str_elem = new String[arrstr_elem.size()];
			str_elem = arrstr_elem.toArray(str_elem);
			System.out.println("element converted");
			arrstring.add(str_elem);
			System.out.println("element added");
			// get phone number
		}
		return arrstring;
	}
	
	public void create_excel_file(String filename) {
		String folder_path = Environment.getExternalStorageDirectory()
				.toString() + "/ExcelContact/";
		try {
		    String fileName = folder_path + filename;
		    WritableWorkbook workbook = Workbook.createWorkbook(new File(fileName));
		    workbook.createSheet("Sheet1", 0);
		    workbook.createSheet("Sheet2", 1);
		    workbook.createSheet("Sheet3", 2);
		    workbook.write();
		    workbook.close();
		} catch (Exception e) {

		}
	}
	
	public void print_data(ArrayList<String[]> str) {
		System.out.println("Data printing started");
		for (int i = 0; i < str.size(); i++) {
			String[] str_elem = str.get(i);
			for (int j = 0; j < str_elem.length-1; j++) {
				System.out.print(str_elem[j] + "/");
			}
			System.out.print(str_elem[str_elem.length-1]);
			System.out.println();
		}
		System.out.println("Data printing finished");
	}
	
	public String generate_file_name() {
		String date = new SimpleDateFormat("_dd_MM_yy_HH_mm_ss").format(new Date());
		String result = "contacts" + date + ".xls";
		return result;
	}
	
	public void export_to_file(ArrayList<String[]> str) throws Exception {
		String folder_path = Environment.getExternalStorageDirectory()
				.toString() + "/ExcelContact/";
		String filename = generate_file_name();
		String filepath = folder_path + filename;
//		String[] list_of_items = {"a","лимонад","c","d"};
		String[] caption = {"Name", "Mobile", "Email"};
//		ArrayList<String[]> string_joint =
		str.add(0, caption);
		print_data(str);
		WriteExcel test = new WriteExcel();
		test.setOutputFile(filepath);
		test.arraylist_to_file(filepath, str);
	}
	
	

	public void OnExportFileClick(View view) throws Exception {
//		// we switch to the page from where list of contacts available
//		create_excel_file("somefile.xls");
//		getNumber(this.getContentResolver()); 
		
//		export_to_file();
		
//		print_data(get_contact_data());
		export_to_file(get_contact_data());
	}

	public void OnOpenFileClick(View view) {
		OpenFileDialog fileDialog = new OpenFileDialog(this,
				last_open_directory);
		fileDialog
				.setOpenDialogListener(new OpenFileDialog.OpenDialogListener() {
					@Override
					// public void OnSelectedFile(String fileName) {
					public void OnSelectedFile(File file) {
						// filepath = fileName;
						filepath = file.toString();
						last_open_directory = file.getParentFile().toString();
						// last_open_directory = "";
						react_on_return();
					}
				});
		fileDialog.show();
	}

	public void react_on_return() {
		// TextView t = (TextView) findViewById(R.id.path);
		// t.setText(filepath);
		load_file();
//		load_file_alt();
	}

	public void copy_asset_file() {
		String folder_path = Environment.getExternalStorageDirectory()
				.toString() + "/ExcelContact";
		File f = new File(folder_path);
		if (!f.isDirectory()) {
			create_folder(folder_path);
			copyAssets(folder_path);
		}
	}

	public void load_file_alt() {
		Gson gson = new Gson();

		XlsValidator xlsval = new XlsValidator();
		String file_path = filepath;

		if (xlsval.validate(file_path)) {
			ArrayList<Contact_alt> contact_list = get_data_list_alt(file_path);
			if (contact_list.size() > 0) {
				Container_alt container = new Container_alt(contact_list);
				output_contacts_alt(contact_list);
				String json = gson.toJson(container);
				Intent intent = new Intent(this,
						ListViewCheckboxesActivity.class);
				intent.putExtra("contact_list", json);
				startActivity(intent);
			} else {
				alert_box("Wrong file format or file is empty.");
			}
		} else {
			String message = "File not opened. Use *.xls files for import";
			alert_box(message);
		}
	}

	public void load_file() {
		Gson gson = new Gson();

		XlsValidator xlsval = new XlsValidator();
		String file_path = filepath;

		if (xlsval.validate(file_path)) {
			ArrayList<Contact> contact_list = get_data_list(file_path);
			if (contact_list.size() > 0) {
				Container container = new Container(contact_list);
				output_contacts(contact_list);
				String json = gson.toJson(container);
				Intent intent = new Intent(this,
						ListViewCheckboxesActivity.class);
				intent.putExtra("contact_list", json);
				startActivity(intent);
			} else {
				alert_box("Wrong file format or file is empty.");
			}
		} else {
			String message = "File not opened. Use *.xls files for import";
			alert_box(message);
		}
	}

	public void loadClick(View view) {
		load_file();
	}

	public void output_contacts(ArrayList<Contact> contactlist) {
		if (contactlist != null) {
			for (int cnt = 0; cnt < contactlist.size(); cnt++) {
				Contact contact = contactlist.get(cnt);
				System.out.println(contact.get_name() + "/"
						+ contact.get_phone_no() + "/" + contact.get_email());
			}
		}
	}
	
	public void output_contacts_alt(ArrayList<Contact_alt> contactlist) {
		if (contactlist != null) {
			for (int cnt = 0; cnt < contactlist.size(); cnt++) {
				Contact_alt contact = contactlist.get(cnt);
				System.out.println(contact.get_name() + "/"
						+ contact.get_phone_no() + "/" + contact.get_email());
			}
		}
	}
	
	public ArrayList<Contact_alt> get_data_list_alt(String source_file) {
		ArrayList<Contact_alt> contact_list = new ArrayList<Contact_alt>();
		File inputWorkbook = new File(source_file);
		if (inputWorkbook.exists()) {
			Workbook w;
			try {
				w = Workbook.getWorkbook(inputWorkbook);
				// Get the first sheet
				Sheet sheet = w.getSheet(0);
				// sheet.getCell(0, curr_row).getContents();
				int curr_row = 1;
				while (!sheet.getCell(0, curr_row).getContents().equals("")) {
					// SaveContact(sheet.getCell(0, curr_row).getContents(),
					// sheet.getCell(1, curr_row).getContents());
					String name_str = sheet.getCell(0, curr_row).getContents();
					String phone_str = sheet.getCell(1, curr_row).getContents();
					String email_str = sheet.getCell(0, curr_row).getContents();
					Contact_alt contact = new Contact_alt(name_str, phone_str, email_str);
					contact.setSelected(true);
					contact_list.add(contact);
					curr_row++;
				}
				// SaveContact();
			} catch (BiffException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return contact_list;
	}

	public ArrayList<Contact> get_data_list(String source_file) {
		ArrayList<Contact> contact_list = new ArrayList<Contact>();
		File inputWorkbook = new File(source_file);
		if (inputWorkbook.exists()) {
			Workbook w;
			try {
				w = Workbook.getWorkbook(inputWorkbook);
				// Get the first sheet
				Sheet sheet = w.getSheet(0);
				// sheet.getCell(0, curr_row).getContents();
				int curr_row = 1;
				while (!sheet.getCell(0, curr_row).getContents().equals("")) {
					// SaveContact(sheet.getCell(0, curr_row).getContents(),
					// sheet.getCell(1, curr_row).getContents());
					Contact contact = new Contact();
					contact.set_name(sheet.getCell(0, curr_row).getContents());
					contact.set_phone(sheet.getCell(1, curr_row).getContents());
					contact.set_email(sheet.getCell(2, curr_row).getContents());
					contact.setSelected(true);
					contact_list.add(contact);
					curr_row++;
				}

				// SaveContact();
			} catch (BiffException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return contact_list;
	}

	public void excel_to_contact(String source_file) {
		File inputWorkbook = new File(source_file);
		if (inputWorkbook.exists()) {
			Workbook w;
			try {
				w = Workbook.getWorkbook(inputWorkbook);
				// Get the first sheet
				Sheet sheet = w.getSheet(0);
				// sheet.getCell(0, curr_row).getContents();
				int curr_row = 1;
				while (!sheet.getCell(0, curr_row).getContents().equals("")) {
					// SaveContact(sheet.getCell(0, curr_row).getContents(),
					// sheet.getCell(1, curr_row).getContents());
					if (number_of_loads > MAXLOADS)
						break;
					SaveContact_mail(sheet.getCell(0, curr_row).getContents(),
							sheet.getCell(1, curr_row).getContents(), sheet
									.getCell(2, curr_row).getContents());
					number_of_loads++;
					curr_row++;
				}
				// SaveContact();
			} catch (BiffException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void create_folder(String folder_path) {
		File folder = new File(folder_path);
		boolean success = true;
		if (!folder.exists()) {
			System.out.println("inside creating directory");
			success = folder.mkdir();
			if (success) {
				// Do something on success
				System.out.println(folder_path + " created");
			} else {
				System.out.println("failed to create " + folder_path);
				// Do something else on failure
			}

		} else {
			System.out.println(folder_path + " exists");
		}
	}

	private void copyAssets(String destination_folder) {
		AssetManager assetManager = getAssets();
		String[] files = null;
		try {
			files = assetManager.list("");
		} catch (IOException e) {
			Log.e("tag", "Failed to get asset file list.", e);
		}

		XlsValidator img = new XlsValidator();
		for (String filename : files) {
			if (img.validate(filename)) {
				System.out.println(filename + " is copying");
				InputStream in = null;
				OutputStream out = null;
				try {
					in = assetManager.open(filename);
					File outFile = new File(destination_folder, filename);
					out = new FileOutputStream(outFile);
					copyFile(in, out);
					in.close();
					in = null;
					out.flush();
					out.close();
					out = null;
				} catch (IOException e) {
					Log.e("tag", "Failed to copy asset file: " + filename, e);
				}
			}
		}
		System.out.println("file copied to " + destination_folder);
	}

	private void copyFile(InputStream in, OutputStream out) throws IOException {
		byte[] buffer = new byte[1024];
		int read;
		while ((read = in.read(buffer)) != -1) {
			out.write(buffer, 0, read);
		}
	}

	private void addContact2() {
		ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
		int rawContactInsertIndex = ops.size();

		ops.add(ContentProviderOperation
				.newInsert(ContactsContract.RawContacts.CONTENT_URI)
				.withValue(RawContacts.ACCOUNT_TYPE, null)
				.withValue(RawContacts.ACCOUNT_NAME, null).build());

		// INSERT NAME
		ops.add(ContentProviderOperation
				.newInsert(ContactsContract.Data.CONTENT_URI)
				.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,
						rawContactInsertIndex)
				.withValue(
						ContactsContract.Data.MIMETYPE,
						ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
				.withValue(
						ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
						"My") // Name of the person
				.withValue(
						ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME,
						"sexy") // Name of the person
				.withValue(
						ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME,
						"darling") // Name of the person
				.build());

		ContentProviderResult[] res;
		try {
			res = getContentResolver().applyBatch(ContactsContract.AUTHORITY,
					ops);
			if (res != null && res[0] != null) {
				Uri newContactUri = res[0].uri;
				// 02-20 22:21:09 URI added
				// contact:content://com.android.contacts/raw_contacts/612
				Log.d("fuckduck", "URI added contact:" + newContactUri);
			} else
				Log.e("fuckduck", "Contact not added.");
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OperationApplicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void addContact() {
		ContentProviderResult[] results;
		ArrayList<ContentProviderOperation> op_list = new ArrayList<ContentProviderOperation>();
		op_list.add(ContentProviderOperation
				.newInsert(ContactsContract.RawContacts.CONTENT_URI)
				.withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
				.withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
				// .withValue(RawContacts.AGGREGATION_MODE,
				// RawContacts.AGGREGATION_MODE_DEFAULT)
				.build());

		// first and last names
		op_list.add(ContentProviderOperation.newInsert(Data.CONTENT_URI)
				.withValueBackReference(Data.RAW_CONTACT_ID, 0)
				.withValue(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE)
				.withValue(StructuredName.GIVEN_NAME, "kolobok")
				.withValue(StructuredName.FAMILY_NAME, "pisyun").build());

		op_list.add(ContentProviderOperation
				.newInsert(Data.CONTENT_URI)
				.withValueBackReference(Data.RAW_CONTACT_ID, 0)
				.withValue(
						ContactsContract.Data.MIMETYPE,
						ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
				.withValue(ContactsContract.CommonDataKinds.Phone.NUMBER,
						"1234567")
				.withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
						Phone.TYPE_HOME).build());
		op_list.add(ContentProviderOperation
				.newInsert(Data.CONTENT_URI)
				.withValueBackReference(Data.RAW_CONTACT_ID, 0)
				.withValue(
						ContactsContract.Data.MIMETYPE,
						ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
				.withValue(ContactsContract.CommonDataKinds.Email.DATA,
						"abc@xyz.com")
				.withValue(ContactsContract.CommonDataKinds.Email.TYPE,
						Email.TYPE_WORK).build());

		try {
			results = getContentResolver().applyBatch(
					ContactsContract.AUTHORITY, op_list);
			int a = 1;
		} catch (Exception e) {
			e.printStackTrace();
		}
		int a = 1;
	}

	boolean SaveContact(String name, String phone_no) {
		// Get text
		String szFirstname = "OLOLO", // et1.getText().toString(),
		szLastname = "TROLOLO", szPhone = "654321", szMobile = phone_no, szCompany = "nsu", szEmail = "skardash@gmail.com", szWeb = "http://yandex.ru";
		// Read contact image (in resources) to byte array
		// Drawable dimg = getResources().getDrawable(R.drawable.face);

		// Bitmap bitmapOrg = BitmapFactory.decodeResource(getResources(),
		// R.drawable.face);
		// ByteArrayOutputStream stream = new ByteArrayOutputStream();
		// bitmapOrg.compress(Bitmap.CompressFormat.JPEG, 100, stream);

		// Create a new contact entry!
		String szFullname = name; // szFirstname + " " + szLastname;

		ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
		int rawContactInsertIndex = ops.size();

		ops.add(ContentProviderOperation
				.newInsert(ContactsContract.RawContacts.CONTENT_URI)
				.withValue(RawContacts.ACCOUNT_TYPE, null)
				.withValue(RawContacts.ACCOUNT_NAME, null).build());
		// INSERT NAME
		ops.add(ContentProviderOperation
				.newInsert(ContactsContract.Data.CONTENT_URI)
				.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,
						rawContactInsertIndex)
				.withValue(
						ContactsContract.Data.MIMETYPE,
						ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
				.withValue(
						ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
						szFullname) // Name of the person
				// .withValue(
				// ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME,
				// szLastname) // Name of the person
				// .withValue(
				// ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME,
				// szFirstname) // Name of the person
				.build());
		// // INSERT PHONE
		// ops.add(ContentProviderOperation
		// .newInsert(ContactsContract.Data.CONTENT_URI)
		// .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,
		// rawContactInsertIndex)
		// .withValue(
		// ContactsContract.Data.MIMETYPE,
		// ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
		// .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER,
		// szPhone)
		// // Number of the person
		// .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
		// ContactsContract.CommonDataKinds.Phone.TYPE_WORK)
		// .build()); //
		// INSERT MOBILE
		ops.add(ContentProviderOperation
				.newInsert(ContactsContract.Data.CONTENT_URI)
				.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,
						rawContactInsertIndex)
				.withValue(
						ContactsContract.Data.MIMETYPE,
						ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
				.withValue(ContactsContract.CommonDataKinds.Phone.NUMBER,
						szMobile)
				// Number of the person
				.withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
						ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
				.build()); //
		/*
		 * //INSERT FAX
		 * ops.add(ContentProviderOperation.newInsert(ContactsContract
		 * .Data.CONTENT_URI)
		 * .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,
		 * rawContactInsertIndex) .withValue(ContactsContract.Data.MIMETYPE,
		 * ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
		 * .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, szFax)
		 * .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
		 * ContactsContract.CommonDataKinds.Phone.TYPE_FAX_WORK) .build()); //
		 */
		// INSERT EMAIL
		// ops.add(ContentProviderOperation
		// .newInsert(ContactsContract.Data.CONTENT_URI)
		// .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,
		// rawContactInsertIndex)
		// .withValue(
		// ContactsContract.Data.MIMETYPE,
		// ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
		// .withValue(ContactsContract.CommonDataKinds.Email.DATA, szEmail)
		// .withValue(ContactsContract.CommonDataKinds.Email.TYPE,
		// ContactsContract.CommonDataKinds.Email.TYPE_WORK)
		// .build()); //
		// INSERT WEBSITE
		// ops.add(ContentProviderOperation
		// .newInsert(ContactsContract.Data.CONTENT_URI)
		// .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,
		// rawContactInsertIndex)
		// .withValue(
		// ContactsContract.Data.MIMETYPE,
		// ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE)
		// .withValue(ContactsContract.CommonDataKinds.Website.URL, szWeb)
		// //
		// .withValue(ContactsContract.CommonDataKinds.Website.TYPE,
		// ContactsContract.CommonDataKinds.Website.TYPE_WORK)
		// .build()); //
		// /*
		// * //INSERT ADDRESS: FULL, STREET, CITY, REGION, POSTCODE, COUNTRY
		// * ops.add
		// *
		// (ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI
		// * ) .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,
		// * rawContactInsertIndex)
		// * .withValue(ContactsContract.Data.MIMETYPE,ContactsContract
		// * .CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE)
		// * .withValue(ContactsContract
		// * .CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS, m_szAddress)
		// * .withValue(ContactsContract.CommonDataKinds.StructuredPostal.TYPE,
		// * ContactsContract.CommonDataKinds.StructuredPostal.TYPE_WORK)
		// *
		// .withValue(ContactsContract.CommonDataKinds.StructuredPostal.STREET,
		// * m_szStreet)
		// * .withValue(ContactsContract.CommonDataKinds.StructuredPostal.CITY,
		// * m_szCity)
		// *
		// .withValue(ContactsContract.CommonDataKinds.StructuredPostal.REGION,
		// * m_szState)
		// * .withValue(ContactsContract.CommonDataKinds.StructuredPostal
		// * .POSTCODE, m_szZip)
		// *
		// .withValue(ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY,
		// * m_szCountry) //.withValue(StructuredPostal.TYPE,
		// * StructuredPostal.TYPE_WORK) .build());
		// */
		// // INSERT COMPANY / JOB
		// ops.add(ContentProviderOperation
		// .newInsert(ContactsContract.Data.CONTENT_URI)
		// .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,
		// rawContactInsertIndex)
		// .withValue(
		// ContactsContract.Data.MIMETYPE,
		// ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE)
		// .withValue(
		// ContactsContract.CommonDataKinds.Organization.COMPANY,
		// szCompany)
		// .withValue(ContactsContract.CommonDataKinds.Organization.TYPE,
		// ContactsContract.CommonDataKinds.Organization.TYPE_WORK)
		// /*
		// * .withValue(ContactsContract.CommonDataKinds.Organization.TITLE
		// * , m_szJob)
		// * .withValue(ContactsContract.CommonDataKinds.Organization
		// * .TYPE,
		// * ContactsContract.CommonDataKinds.Organization.TYPE_WORK)
		// */
		// .build());
		// /*
		// * //INSERT NOTE
		// * ops.add(ContentProviderOperation.newInsert(ContactsContract
		// * .Data.CONTENT_URI)
		// * .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,
		// * rawContactInsertIndex) .withValue(ContactsContract.Data.MIMETYPE,
		// * ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE)
		// * .withValue(ContactsContract.CommonDataKinds.Note.NOTE, m_szText)
		// * .build()); //
		// */
		// // Add a custom colomn to identify this contact
		// /*
		// * ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.
		// * CONTENT_URI)/ *
		// .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,
		// * rawContactInsertIndex) .withValue(ContactsContract.Data.MIMETYPE,
		// * MIMETYPE_RADUTOKEN) .withValue(ContactsContract.Data.DATA1,
		// szToken)
		// * .build());
		// */
		// // INSERT imAGE
		//
		// ops.add(ContentProviderOperation
		// .newInsert(ContactsContract.Data.CONTENT_URI)
		// .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,
		// rawContactInsertIndex)
		// .withValue(
		// ContactsContract.Data.MIMETYPE,
		// ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE)
		// .withValue(ContactsContract.CommonDataKinds.Photo.PHOTO,
		// stream.toByteArray()).build());

		// SAVE CONTACT IN BCR Structure
		Uri newContactUri = null;
		// PUSH EVERYTHING TO CONTACTS
		try {
			ContentProviderResult[] res = getContentResolver().applyBatch(
					ContactsContract.AUTHORITY, ops);
			if (res != null && res[0] != null) {
				newContactUri = res[0].uri;
				// 02-20 22:21:09 URI added
				// contact:content://com.android.contacts/raw_contacts/612
				Log.d("lol", "URI added contact:" + newContactUri);
			} else
				Log.e("lol", "Contact not added.");
		} catch (RemoteException e) {
			// error
			Log.e("lol", "Error (1) adding contact.");
			newContactUri = null;
		} catch (OperationApplicationException e) {
			// error
			Log.e("lol", "Error (2) adding contact.");
			newContactUri = null;
		}
		Log.d("lol", "Contact added to system contacts.");

		if (newContactUri == null) {
			Log.e("lol", "Error creating contact");
			return false;
		}

		return true;
	}

	public void add_contacts(ArrayList<Contact> contacts) {
		boolean loads_exceeded = false;
		if (contacts.size() > 0) {
			for (int cnt = 0; cnt < contacts.size(); cnt++) {
				System.out.println("Contact no " + number_of_loads
						+ " is on the way");
				if (number_of_loads > MAXLOADS) {
					String message = "Sorry, you out exceed number of contacts you can add for free version.";
					if (number_of_loads == starting_number_of_loads) {
						message += " No contacts were added.";
					} else {
						message += " Only first "
								+ (number_of_loads - starting_number_of_loads)
								+ " contacts were added.";
					}

					message += " Please buy the full version of software";
					alert_box(message);
					loads_exceeded = true;
					break;
				}
				add_contact(contacts.get(cnt));
				System.out.println("Contact no " + number_of_loads + " added");
			}
			if (!loads_exceeded) {
				contacts_added();
			}
			Editor editPrefs = prefs.edit();
			editPrefs.putInt("ExcelContactLoads", number_of_loads);
			editPrefs.commit();
		}
	}

	public void add_contact(Contact contact) {
		SaveContact_mail(contact.get_name(), contact.get_phone_no(),
				contact.get_email());
		number_of_loads++;
	}
	
	boolean SaveContact_mail_alt(Contact_alt contact) {
		String name = contact.get_name();
		ArrayList<String> phone_no = contact.get_phone_no();
		ArrayList<String> email = contact.get_email();
		// String szFirstname = "OLOLO", // et1.getText().toString(),
		// szLastname = "TROLOLO", szPhone = "654321", szMobile = phone_no,
		// szCompany = "nsu", szEmail = email, szWeb = "http://yandex.ru";
		// String szFullname = name; // szFirstname + " " + szLastname;

		ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
		int rawContactInsertIndex = ops.size();

		ops.add(ContentProviderOperation
				.newInsert(ContactsContract.RawContacts.CONTENT_URI)
				.withValue(RawContacts.ACCOUNT_TYPE, null)
				.withValue(RawContacts.ACCOUNT_NAME, null).build());
		
		// INSERT NAME
		ops.add(ContentProviderOperation
				.newInsert(ContactsContract.Data.CONTENT_URI)
				.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,
						rawContactInsertIndex)
				.withValue(
						ContactsContract.Data.MIMETYPE,
						ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
				.withValue(
						ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
						name) // Name of the person
				// .withValue(
				// ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME,
				// szLastname) // Name of the person
				// .withValue(
				// ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME,
				// szFirstname) // Name of the person
				.build());
		// // INSERT PHONE
		// ops.add(ContentProviderOperation
		// .newInsert(ContactsContract.Data.CONTENT_URI)
		// .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,
		// rawContactInsertIndex)
		// .withValue(
		// ContactsContract.Data.MIMETYPE,
		// ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
		// .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER,
		// szPhone)
		// // Number of the person
		// .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
		// ContactsContract.CommonDataKinds.Phone.TYPE_WORK)
		// .build()); //
		// INSERT MOBILE
		for (int cnt = 0; cnt < phone_no.size(); cnt++) {
			ops.add(ContentProviderOperation
					.newInsert(ContactsContract.Data.CONTENT_URI)
					.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,
							rawContactInsertIndex)
					.withValue(
							ContactsContract.Data.MIMETYPE,
							ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
					.withValue(ContactsContract.CommonDataKinds.Phone.NUMBER,
							phone_no.get(cnt))
					// Number of the person
					.withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
							ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
					.build()); //
		}
		/*
		 * //INSERT FAX
		 * ops.add(ContentProviderOperation.newInsert(ContactsContract
		 * .Data.CONTENT_URI)
		 * .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,
		 * rawContactInsertIndex) .withValue(ContactsContract.Data.MIMETYPE,
		 * ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
		 * .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, szFax)
		 * .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
		 * ContactsContract.CommonDataKinds.Phone.TYPE_FAX_WORK) .build()); //
		 */
		// INSERT EMAIL
		for (int cnt = 0; cnt < email.size(); cnt++) {
			ops.add(ContentProviderOperation
					.newInsert(ContactsContract.Data.CONTENT_URI)
					.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,
							rawContactInsertIndex)
					.withValue(
							ContactsContract.Data.MIMETYPE,
							ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
					.withValue(ContactsContract.CommonDataKinds.Email.DATA, email.get(cnt))
					.withValue(ContactsContract.CommonDataKinds.Email.TYPE,
							ContactsContract.CommonDataKinds.Email.TYPE_WORK)
					.build()); //
		}
		// INSERT WEBSITE
		// ops.add(ContentProviderOperation
		// .newInsert(ContactsContract.Data.CONTENT_URI)
		// .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,
		// rawContactInsertIndex)
		// .withValue(
		// ContactsContract.Data.MIMETYPE,
		// ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE)
		// .withValue(ContactsContract.CommonDataKinds.Website.URL, szWeb)
		// //
		// .withValue(ContactsContract.CommonDataKinds.Website.TYPE,
		// ContactsContract.CommonDataKinds.Website.TYPE_WORK)
		// .build()); //
		// /*
		// * //INSERT ADDRESS: FULL, STREET, CITY, REGION, POSTCODE, COUNTRY
		// * ops.add
		// *
		// (ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI
		// * ) .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,
		// * rawContactInsertIndex)
		// * .withValue(ContactsContract.Data.MIMETYPE,ContactsContract
		// * .CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE)
		// * .withValue(ContactsContract
		// * .CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS, m_szAddress)
		// * .withValue(ContactsContract.CommonDataKinds.StructuredPostal.TYPE,
		// * ContactsContract.CommonDataKinds.StructuredPostal.TYPE_WORK)
		// *
		// .withValue(ContactsContract.CommonDataKinds.StructuredPostal.STREET,
		// * m_szStreet)
		// * .withValue(ContactsContract.CommonDataKinds.StructuredPostal.CITY,
		// * m_szCity)
		// *
		// .withValue(ContactsContract.CommonDataKinds.StructuredPostal.REGION,
		// * m_szState)
		// * .withValue(ContactsContract.CommonDataKinds.StructuredPostal
		// * .POSTCODE, m_szZip)
		// *
		// .withValue(ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY,
		// * m_szCountry) //.withValue(StructuredPostal.TYPE,
		// * StructuredPostal.TYPE_WORK) .build());
		// */
		// // INSERT COMPANY / JOB
		// ops.add(ContentProviderOperation
		// .newInsert(ContactsContract.Data.CONTENT_URI)
		// .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,
		// rawContactInsertIndex)
		// .withValue(
		// ContactsContract.Data.MIMETYPE,
		// ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE)
		// .withValue(
		// ContactsContract.CommonDataKinds.Organization.COMPANY,
		// szCompany)
		// .withValue(ContactsContract.CommonDataKinds.Organization.TYPE,
		// ContactsContract.CommonDataKinds.Organization.TYPE_WORK)
		// /*
		// * .withValue(ContactsContract.CommonDataKinds.Organization.TITLE
		// * , m_szJob)
		// * .withValue(ContactsContract.CommonDataKinds.Organization
		// * .TYPE,
		// * ContactsContract.CommonDataKinds.Organization.TYPE_WORK)
		// */
		// .build());
		// /*
		// * //INSERT NOTE
		// * ops.add(ContentProviderOperation.newInsert(ContactsContract
		// * .Data.CONTENT_URI)
		// * .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,
		// * rawContactInsertIndex) .withValue(ContactsContract.Data.MIMETYPE,
		// * ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE)
		// * .withValue(ContactsContract.CommonDataKinds.Note.NOTE, m_szText)
		// * .build()); //
		// */
		// // Add a custom colomn to identify this contact
		// /*
		// * ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.
		// * CONTENT_URI)
		// * .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,
		// * rawContactInsertIndex) .withValue(ContactsContract.Data.MIMETYPE,
		// * MIMETYPE_RADUTOKEN) .withValue(ContactsContract.Data.DATA1,
		// szToken)
		// * .build());
		// */
		// // INSERT imAGE
		//
		// ops.add(ContentProviderOperation
		// .newInsert(ContactsContract.Data.CONTENT_URI)
		// .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,
		// rawContactInsertIndex)
		// .withValue(
		// ContactsContract.Data.MIMETYPE,
		// ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE)
		// .withValue(ContactsContract.CommonDataKinds.Photo.PHOTO,
		// stream.toByteArray()).build());

		// SAVE CONTACT IN BCR Structure
		Uri newContactUri = null;
		// PUSH EVERYTHING TO CONTACTS
		try {
			ContentProviderResult[] res = getContentResolver().applyBatch(
					ContactsContract.AUTHORITY, ops);
			if (res != null && res[0] != null) {
				newContactUri = res[0].uri;
				// 02-20 22:21:09 URI added
				// contact:content://com.android.contacts/raw_contacts/612
				Log.d("lol", "URI added contact:" + newContactUri);
			} else
				Log.e("lol", "Contact not added.");
		} catch (RemoteException e) {
			// error
			Log.e("lol", "Error (1) adding contact.");
			newContactUri = null;
		} catch (OperationApplicationException e) {
			// error
			Log.e("lol", "Error (2) adding contact.");
			newContactUri = null;
		}
		Log.d("lol", "Contact added to system contacts.");
		if (newContactUri == null) {
			Log.e("lol", "Error creating contact");
			return false;
		}

		return true;
	}


	boolean SaveContact_mail(String name, String phone_no, String email) {
		// String szFirstname = "OLOLO", // et1.getText().toString(),
		// szLastname = "TROLOLO", szPhone = "654321", szMobile = phone_no,
		// szCompany = "nsu", szEmail = email, szWeb = "http://yandex.ru";
		// String szFullname = name; // szFirstname + " " + szLastname;

		ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
		int rawContactInsertIndex = ops.size();

		ops.add(ContentProviderOperation
				.newInsert(ContactsContract.RawContacts.CONTENT_URI)
				.withValue(RawContacts.ACCOUNT_TYPE, null)
				.withValue(RawContacts.ACCOUNT_NAME, null).build());
		// INSERT NAME
		ops.add(ContentProviderOperation
				.newInsert(ContactsContract.Data.CONTENT_URI)
				.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,
						rawContactInsertIndex)
				.withValue(
						ContactsContract.Data.MIMETYPE,
						ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
				.withValue(
						ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
						name) // Name of the person
				// .withValue(
				// ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME,
				// szLastname) // Name of the person
				// .withValue(
				// ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME,
				// szFirstname) // Name of the person
				.build());
		// // INSERT PHONE
		// ops.add(ContentProviderOperation
		// .newInsert(ContactsContract.Data.CONTENT_URI)
		// .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,
		// rawContactInsertIndex)
		// .withValue(
		// ContactsContract.Data.MIMETYPE,
		// ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
		// .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER,
		// szPhone)
		// // Number of the person
		// .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
		// ContactsContract.CommonDataKinds.Phone.TYPE_WORK)
		// .build()); //
		// INSERT MOBILE
		ops.add(ContentProviderOperation
				.newInsert(ContactsContract.Data.CONTENT_URI)
				.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,
						rawContactInsertIndex)
				.withValue(
						ContactsContract.Data.MIMETYPE,
						ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
				.withValue(ContactsContract.CommonDataKinds.Phone.NUMBER,
						phone_no)
				// Number of the person
				.withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
						ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
				.build()); //
		/*
		 * //INSERT FAX
		 * ops.add(ContentProviderOperation.newInsert(ContactsContract
		 * .Data.CONTENT_URI)
		 * .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,
		 * rawContactInsertIndex) .withValue(ContactsContract.Data.MIMETYPE,
		 * ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
		 * .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, szFax)
		 * .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
		 * ContactsContract.CommonDataKinds.Phone.TYPE_FAX_WORK) .build()); //
		 */
		// INSERT EMAIL
		ops.add(ContentProviderOperation
				.newInsert(ContactsContract.Data.CONTENT_URI)
				.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,
						rawContactInsertIndex)
				.withValue(
						ContactsContract.Data.MIMETYPE,
						ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
				.withValue(ContactsContract.CommonDataKinds.Email.DATA, email)
				.withValue(ContactsContract.CommonDataKinds.Email.TYPE,
						ContactsContract.CommonDataKinds.Email.TYPE_WORK)
				.build()); //
		// INSERT WEBSITE
		// ops.add(ContentProviderOperation
		// .newInsert(ContactsContract.Data.CONTENT_URI)
		// .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,
		// rawContactInsertIndex)
		// .withValue(
		// ContactsContract.Data.MIMETYPE,
		// ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE)
		// .withValue(ContactsContract.CommonDataKinds.Website.URL, szWeb)
		// //
		// .withValue(ContactsContract.CommonDataKinds.Website.TYPE,
		// ContactsContract.CommonDataKinds.Website.TYPE_WORK)
		// .build()); //
		// /*
		// * //INSERT ADDRESS: FULL, STREET, CITY, REGION, POSTCODE, COUNTRY
		// * ops.add
		// *
		// (ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI
		// * ) .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,
		// * rawContactInsertIndex)
		// * .withValue(ContactsContract.Data.MIMETYPE,ContactsContract
		// * .CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE)
		// * .withValue(ContactsContract
		// * .CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS, m_szAddress)
		// * .withValue(ContactsContract.CommonDataKinds.StructuredPostal.TYPE,
		// * ContactsContract.CommonDataKinds.StructuredPostal.TYPE_WORK)
		// *
		// .withValue(ContactsContract.CommonDataKinds.StructuredPostal.STREET,
		// * m_szStreet)
		// * .withValue(ContactsContract.CommonDataKinds.StructuredPostal.CITY,
		// * m_szCity)
		// *
		// .withValue(ContactsContract.CommonDataKinds.StructuredPostal.REGION,
		// * m_szState)
		// * .withValue(ContactsContract.CommonDataKinds.StructuredPostal
		// * .POSTCODE, m_szZip)
		// *
		// .withValue(ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY,
		// * m_szCountry) //.withValue(StructuredPostal.TYPE,
		// * StructuredPostal.TYPE_WORK) .build());
		// */
		// // INSERT COMPANY / JOB
		// ops.add(ContentProviderOperation
		// .newInsert(ContactsContract.Data.CONTENT_URI)
		// .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,
		// rawContactInsertIndex)
		// .withValue(
		// ContactsContract.Data.MIMETYPE,
		// ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE)
		// .withValue(
		// ContactsContract.CommonDataKinds.Organization.COMPANY,
		// szCompany)
		// .withValue(ContactsContract.CommonDataKinds.Organization.TYPE,
		// ContactsContract.CommonDataKinds.Organization.TYPE_WORK)
		// /*
		// * .withValue(ContactsContract.CommonDataKinds.Organization.TITLE
		// * , m_szJob)
		// * .withValue(ContactsContract.CommonDataKinds.Organization
		// * .TYPE,
		// * ContactsContract.CommonDataKinds.Organization.TYPE_WORK)
		// */
		// .build());
		// /*
		// * //INSERT NOTE
		// * ops.add(ContentProviderOperation.newInsert(ContactsContract
		// * .Data.CONTENT_URI)
		// * .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,
		// * rawContactInsertIndex) .withValue(ContactsContract.Data.MIMETYPE,
		// * ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE)
		// * .withValue(ContactsContract.CommonDataKinds.Note.NOTE, m_szText)
		// * .build()); //
		// */
		// // Add a custom colomn to identify this contact
		// /*
		// * ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.
		// * CONTENT_URI)
		// * .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,
		// * rawContactInsertIndex) .withValue(ContactsContract.Data.MIMETYPE,
		// * MIMETYPE_RADUTOKEN) .withValue(ContactsContract.Data.DATA1,
		// szToken)
		// * .build());
		// */
		// // INSERT imAGE
		//
		// ops.add(ContentProviderOperation
		// .newInsert(ContactsContract.Data.CONTENT_URI)
		// .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,
		// rawContactInsertIndex)
		// .withValue(
		// ContactsContract.Data.MIMETYPE,
		// ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE)
		// .withValue(ContactsContract.CommonDataKinds.Photo.PHOTO,
		// stream.toByteArray()).build());

		// SAVE CONTACT IN BCR Structure
		Uri newContactUri = null;
		// PUSH EVERYTHING TO CONTACTS
		try {
			ContentProviderResult[] res = getContentResolver().applyBatch(
					ContactsContract.AUTHORITY, ops);
			if (res != null && res[0] != null) {
				newContactUri = res[0].uri;
				// 02-20 22:21:09 URI added
				// contact:content://com.android.contacts/raw_contacts/612
				Log.d("lol", "URI added contact:" + newContactUri);
			} else
				Log.e("lol", "Contact not added.");
		} catch (RemoteException e) {
			// error
			Log.e("lol", "Error (1) adding contact.");
			newContactUri = null;
		} catch (OperationApplicationException e) {
			// error
			Log.e("lol", "Error (2) adding contact.");
			newContactUri = null;
		}
		Log.d("lol", "Contact added to system contacts.");
		if (newContactUri == null) {
			Log.e("lol", "Error creating contact");
			return false;
		}

		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.mainmenu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
