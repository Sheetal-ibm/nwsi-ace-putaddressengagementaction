package uk.co.nationwide.nem.accval;

/**
 * The AccValWrapper class contain the public static calls QueryAccountNumber
 * operation within the AccValFunctions library.
 * 
 * 
 * MattA - 11-05-2018 
 * Removed instancing of AccValFunctions and this is not required and can be 
 * accessed as a static
 * 
 * @author Rohit Rana
 * @version 1.0
 * @since 27/04/2015
 */
public class AccValWrapper {
	
	/**
	 * 
	 * 
	 * @param value
	 *            the original account number passed in
	 */
	public static Long getResult(String Accountnumber) {
		AccValResult result = AccValFunctions.QueryAccountNumber(Accountnumber);
		return (long) result.getResult();

	}

	/**
	 * 
	 * 
	 * @param value
	 *            the original account number passed in
	 */
	public static String getAccountNumber(String Accountnumber) {
		AccValResult result = AccValFunctions.QueryAccountNumber(Accountnumber);
		return result.getAccountNumber();
	}

	/**
	 * 
	 * 
	 * @param value
	 *            the original account number passed in
	 */
	public static String getAccountNumberFormatted(String Accountnumber) {
		AccValResult result = AccValFunctions.QueryAccountNumber(Accountnumber);
		return result.getAccountNumberFormatted();
	}

	/**
	 * 
	 * 
	 * @param value
	 *            the original account number passed in
	 */
	public static String getAccountType(String Accountnumber) {
		AccValResult result = AccValFunctions.QueryAccountNumber(Accountnumber);
		return result.getAccountType();
	}

	/**
	 * 
	 * 
	 * @param value
	 *            the original account number passed in
	 */
	public static String getScan(String Accountnumber) {
		AccValResult result = AccValFunctions.QueryAccountNumber(Accountnumber);
		return result.getScan();
	}

	/**
	 * 
	 * 
	 * @param value
	 *            the original account number passed in
	 */
	public static String getSortCode(String Accountnumber) {
		DeriveNewAccountSortCodeResult result = AccValFunctions
				.DeriveNewAccountSortCode(Accountnumber);
		return result.getSortCode();
	}

}
