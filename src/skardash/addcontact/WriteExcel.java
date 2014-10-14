package skardash.addcontact;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import android.os.Environment;

import jxl.CellView;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.UnderlineStyle;
import jxl.write.Formula;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class WriteExcel {

	private WritableCellFormat timesBoldUnderline;
	private WritableCellFormat times;
	private String inputFile;

	public void setOutputFile(String inputFile) {
		this.inputFile = inputFile;
	}

	public void write() throws IOException, WriteException {
		File file = new File(inputFile);
		WorkbookSettings wbSettings = new WorkbookSettings();

		wbSettings.setLocale(new Locale("en", "EN"));

		WritableWorkbook workbook = Workbook.createWorkbook(file, wbSettings);
		workbook.createSheet("Report", 0);
		WritableSheet excelSheet = workbook.getSheet(0);
		createLabel(excelSheet);
		createContent(excelSheet);

		workbook.write();
		workbook.close();
	}

	private void createLabel(WritableSheet sheet) throws WriteException {
		// Lets create a times font
		WritableFont times10pt = new WritableFont(WritableFont.TIMES, 10);
		// Define the cell format
		times = new WritableCellFormat(times10pt);
		// Lets automatically wrap the cells
		times.setWrap(true);

		// create create a bold font with unterlines
		WritableFont times10ptBoldUnderline = new WritableFont(
				WritableFont.TIMES, 10, WritableFont.BOLD, false,
				UnderlineStyle.SINGLE);
		timesBoldUnderline = new WritableCellFormat(times10ptBoldUnderline);
		// Lets automatically wrap the cells
		timesBoldUnderline.setWrap(true);

		CellView cv = new CellView();
		cv.setFormat(times);
		cv.setFormat(timesBoldUnderline);
		// cv.setAutosize(true);

		// Write a few headers
		addCaption(sheet, 0, 0, "Header 1");
		addCaption(sheet, 1, 0, "This is another header");

	}

	private void createContent(WritableSheet sheet) throws WriteException,
			RowsExceededException {
		// Write a few number
		for (int i = 1; i < 10; i++) {
			// First column
			addNumber(sheet, 0, i, i + 10);
			// Second column
			addNumber(sheet, 1, i, i * i);
		}
		// Lets calculate the sum of it
		StringBuffer buf = new StringBuffer();
		buf.append("SUM(A2:A10)");
		Formula f = new Formula(0, 10, buf.toString());
		sheet.addCell(f);
		buf = new StringBuffer();
		buf.append("SUM(B2:B10)");
		f = new Formula(1, 10, buf.toString());
		sheet.addCell(f);

		// now a bit of text
		for (int i = 12; i < 20; i++) {
			// First column
			addLabel(sheet, 0, i, "Boring text " + i);
			// Second column
			addLabel(sheet, 1, i, "Another text");
		}
	}

	private void addCaption(WritableSheet sheet, int column, int row, String s)
			throws RowsExceededException, WriteException {
		Label label;
		label = new Label(column, row, s, timesBoldUnderline);
		sheet.addCell(label);
	}

	private void addNumber(WritableSheet sheet, int column, int row,
			Integer integer) throws WriteException, RowsExceededException {
		Number number;
		number = new Number(column, row, integer, times);
		sheet.addCell(number);
	}

	private void addLabel(WritableSheet sheet, int column, int row, String s)
			throws WriteException, RowsExceededException {
		Label label;
		label = new Label(column, row, s, times);
		sheet.addCell(label);
	}
	
	
	
	public void arraylist_to_file(String file_path, ArrayList<String[]> str_arr)
			throws Exception {
		String m1 = "fill smth";
		System.out.println(m1);
		WritableWorkbook wb = Workbook.createWorkbook(new File(file_path));
		WritableSheet ws = wb.createSheet("contacts", 1);
		System.out.println("Start writing to file " + file_path);
		System.out.println("Arraylist size " + str_arr.size());
		System.out.println("First element size " + str_arr.get(0).length);
		for (int i = 0; i < str_arr.size(); i++) {
			String[] str_elem = str_arr.get(i);
			for (int j = 0; j < str_elem.length; j++) {
				Label label = new Label(j, i, str_elem[j]);
				ws.addCell(label);
			}
		}
		System.out.println("File writing finished");
		wb.write();
		wb.close();
	}

	public void simple_write(String filename, String[] str_arr)
			throws Exception {
		String folder_path = Environment.getExternalStorageDirectory()
				.toString() + "/ExcelContact/";
		String file_path = folder_path + filename;
		String m1 = "fill smth";
		System.out.println(m1);
		WritableWorkbook wb = Workbook.createWorkbook(new File(file_path));
		WritableSheet ws = wb.createSheet("contacts", 1);
		for (int cnt = 0; cnt < str_arr.length; cnt++) {
			Label label = new Label(0, cnt, str_arr[cnt]);
			ws.addCell(label);
		}
		wb.write();
		wb.close();
	}
	
	public void simple_write2(String filename, String[][] str_arr)
			throws Exception {
		String folder_path = Environment.getExternalStorageDirectory()
				.toString() + "/ExcelContact/";
		String file_path = folder_path + filename;
		String m1 = "fill smth";
		System.out.println(m1);
		WritableWorkbook wb = Workbook.createWorkbook(new File(file_path));
		WritableSheet ws = wb.createSheet("contacts", 1);
		for (int i = 0; i < str_arr.length; i++) {
			for (int j = 0; j < str_arr[i].length; j++) {
				Label label = new Label(j, i, str_arr[i][j]);
				ws.addCell(label);
			}
		}
		wb.write();
		wb.close();
	}

	// public static void main(String[] args) throws Exception {
	// String filename = "larsik.xls";
	// String[] list_of_items = {"a","лимонад","c","d"};
	// WriteExcel test = new WriteExcel();
	// test.setOutputFile(filename);
	// simple_write(list_of_items);
	// System.out.println("Please check the result file under " + filename);
	// }
}