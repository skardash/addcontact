package skardash.addcontact;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XlsValidator {

	private Pattern pattern;
	private Matcher matcher;

	private static final String IMAGE_PATTERN = "([^\\s]+(\\.(?i)(xls))$)";

	public XlsValidator() {
		pattern = Pattern.compile(IMAGE_PATTERN);
	}

	public boolean validate(final String image) {
		matcher = pattern.matcher(image);
		return matcher.matches();
	}
}