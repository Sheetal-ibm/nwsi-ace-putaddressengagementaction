package uk.co.nationwide.nem.accval;

/**
 * The AccValResult class contain the results of a call to the
 * QueryAccountNumber operation within the AccValFunctions library.
 * 
 * @author Matt Andrews
 * @version 1.0
 * @since 2015-03-07
 */
public class AccValResult {

	/**
	 * Holds the result of calling accval
	 * 
	 */
	int result;
	
	/**
	 * The account number which was passed as input to accVal
	 */
	String accountNumber;
	
	/**
	 * The account number which has been formatted by accVal
	 */
	String accountNumberFormatted;
	
	/**
	 * The derived account type
	 */
	String accountType;
	
	/**
	 * The derived SCAN
	 */
	String scan;
	
	/**
	 * The error details issued by accVal
	 */
	String errorDetails;

	/**
	 * Creates an instance of the AccValResult class
	 * 
	 * @param accountNumber
	 *            The account number which was passed as input to accVal
	 * @since 1.0
	 */
	public AccValResult(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	/**
	 * Set the query account number
	 * 
	 * @param value
	 *            the original account number passed in
	 */
	public void setAccountNumber(String value) {
		this.accountNumber = value;
	}

	/**
	 * Get the original query account number
	 * 
	 * @return the original query account number
	 */
	public String getAccountNumber() {
		return this.accountNumber;
	}

	/**
	 * Set the accval formatted account number
	 * 
	 * @param value
	 *            the formatted account number as determined by accval
	 */
	public void setAccountNumberFormatted(String value) {
		this.accountNumberFormatted = value;
	}

	/**
	 * Get the formatted account number as determined by accval
	 * 
	 * @return the formatted account number as determined by accval
	 */
	public String getAccountNumberFormatted() {
		return this.accountNumberFormatted;
	}

	/**
	 * Set the accval derived account type, <see>accountType</see>
	 * 
	 * @param value
	 *            the accval derived account type
	 */
	public void setAccountType(String value) {
		this.accountType = value;
	}

	/**
	 * Get the accval derived account type <see>accountType</see>
	 * 
	 * * <pre>
	 * "A" - Personal Loan Approval In Principal number
	 * "B" - Banking/portfolio investments
	 * "C" - Classic or Gold credit cards
	 * "D" - Mellon account format A
	 * "E" - Equity account managed by Mellon
	 * "F" - Flexaccount
	 * "G" - Capsil Account number
	 * "H" - PROGEN Home insurance number
	 * "I" - Unisys investment
	 * "J" - Bonds
	 * "K" - PROGEN payment Protection number
	 * "L" - Progen genaral Insurance Number
	 * "M" - ICL mortgage
	 * "N" - Creditor insurance
	 * "O" - 13 digit VISA or not recognised
	 * "P" - Personal loan
	 * "Q" - MPOS mortgage case number
	 * "R" - RBS investment
	 * "S" - Suspense account
	 * "T" - Nationwide Trust personal loan
	 * "U" - Mortgage account held by UCB
	 * "V" - IPOS investment case number
	 * "W" - Portman Account Number
	 * "X" - L&G number
	 * "Z" - Stakeholder Pension (Mellon or L&G) number
	 * "1" - Conifer number
	 * "2" - MSO number 
	 * "3" - Newman protection policy
	 * "4" - RBSI General household insurance policy
	 * "5" - regional brand number
	 * </pre>
	 * 
	 * @return the accval derived account type
	 */
	public String getAccountType() {
		return this.accountType;
	}

	/**
	 * Set the internal error details
	 * 
	 * @param value
	 *            the internal error details
	 */
	public void setErrorDetails(String value) {
		this.errorDetails = value;
	}

	/**
	 * Get the internal error details
	 * 
	 * @return the internal error details
	 */
	public String getErrorDetails() {
		return this.errorDetails;
	}

	/**
	 * Set the accval derived scan
	 * 
	 * @param value
	 *            the accval derived scan
	 */
	public void setScan(String value) {
		this.scan = value;
	}

	/**
	 * Get the accval derived scan
	 * 
	 * @return the accval derived scan
	 */
	public String getScan() {
		return this.scan;
	}

	/**
	 * Set the accval result <see>result</see>
	 * 
	 * @param value
	 *            the accval result
	 */
	public void setResult(int value) {
		this.result = value;
	}

	/**
	 * Get the accval result <see>result</see>
	 * 
	 * <pre>
	 * 	0 - account number invalid
	 * -1 - too few digits in account number
	 * -2 - too many digits in account number
	 * -3 - number not allowed for this entry point
	 * 
	 * or invalid digits in account type part
	 * or account number type:
	 * 
	 * 1 - Bexhill
	 * 2 - Northampton
	 * 3 - Maidenhead
	 * 4 - no longer returned
	 * 5 - no longer returned
	 * 6 - Swindon 4/8
	 * 7 - Swindon 4/9
	 * 8 - SCAN 
	 * 9 - VISA 13 digit PAN
	 * 10 - VISA 16 digit PAN
	 * 11 - Cardms PAN
	 * 12 - Mortgage Application
	 * 13 - CashLink PAN
	 * 14 - External Reference No (for ex-Anglia acc)
	 * 15 - Error Suspense No
	 * 16 - Nationwide Trust No
	 * 17 - Mellon Client No
	 * 18 - UCB Mortgage No
	 * 19 - Swindon 8 no prefix
	 * 20 - Swindon 9 no prefix
	 * 21 - MPOS 10 chars (M + 9)
	 * 22 - MPOS 9 chars (M + 8)
	 * 23 - MPOS 8 chars (M + 7)
	 * 24 - MPOS 7 chars (M + 6)
	 * 25 - Progen account number
	 * 26 - Legacy Progen number
	 * 27 - MPOS 5 chars (M + 4)
	 * 28 - MPOS 6 chars (M + 5)
	 * 29 - MPOS 11 chars (M + 10)
	 * 30 - Personal Loan Approval In Principal
	 * 31 - Capsil account
	 * 32 - Mellon account format A
	 * 33 - Mellon Account format B
	 * 34 - IPOS 7 chars (I + 6)
	 * 35 - IPOS 8 chars (I + 7)
	 * 36 - IPOS 9 chars (I + 8)
	 * 37 - IPOS 10 chars (I + 9)
	 * 38 - IPOS 11 chars (I + 10)
	 * 39 - Capsil account without segment number
	 * 40 - Portman 
	 * 41 - Legal and General number
	 * 42 - Portman 8 digit savings (only in branched version, but reserved)
	 * 43 - mellon client cut down format (nnnaannnnn)
	 * 44 - LV Motor insurance
	 * 45 - LV Travel Insurance
	 * 46 - LV Comercial Insurance
	 * 47 - StakeholderPension (L&G, Mellon)
	 * 48 - Conifer account number (pants name, format only validation)
	 * 49 - MSO application number (verhoeff check sum)
	 * 50 - Newman protection policy
	 * 51 - RBSI General Household insurance number
	 * 52 - cheshire building society number
	 * 53 - Derbyshire building society number
	 * 54 - Dunfermline building society number
	 * </pre>
	 * @return the accval result
	 */
	public int getResult() {
		return this.result;
	}
}
