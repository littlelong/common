package common.xiao.test;

import java.io.IOException;
import java.util.regex.Pattern;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

public class ExcelTest {

	public static final String OFFICE_EXCEL_XLS = "xls";
	public static final String OFFICE_EXCEL_XLSX = "xlsx";

	public static void main(String[] args)
			throws EncryptedDocumentException, InvalidFormatException, IOException {
		String modelType = "abc-24";

		String hour = Pattern.compile("[^-|0-9]*").matcher(modelType).replaceAll("");

		System.out.println(hour);
		System.out.println(Integer.parseInt(hour));
	}


}



