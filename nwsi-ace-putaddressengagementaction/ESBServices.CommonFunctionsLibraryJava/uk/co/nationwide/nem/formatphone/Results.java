package uk.co.nationwide.nem.formatphone;

/**
 * The Results class contains constants that represent the formatting results.
 * @author Chaitanya Soman, C39099
 * @version 0.1
 * @since 2015-09-23
 */
public class Results {
	
	public static final int SUCCESS = 0;
	public static final int GENERAL_ERROR = 1;
	public static final int INVALID_NUMBER = 2;
	public static final int UNKNOWN_NUMBER = 3;
	public static final int FORMAT_FILE_SETTING_MISSING = 4;
	public static final int INTERNATIONAL_CODES_FILE_SETTING_MISSING = 5;
	public static final int FORMAT_FILE_MISSING = 6;
	public static final int INTERNATIONAL_CODES_FILE_MISSING = 7;
	public static final int DUPLICATE_INTERNATIONAL_CODE = 8;
	public static final int LOCAL_NUMBER_TOO_SHORT = 9;
	public static final int LOCAL_NUMBER_TOO_LONG = 10;
}
