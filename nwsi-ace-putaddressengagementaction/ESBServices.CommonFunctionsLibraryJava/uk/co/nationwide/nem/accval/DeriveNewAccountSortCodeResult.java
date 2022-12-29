package uk.co.nationwide.nem.accval;

/**
* The DeriveNewAccountSortCodeResult class contain the results of a call to the DeriveNewAccountSortCode operation within the AccValFunctions library.
* 
* @author  Matt Andrews
* @version 1.0
* @since   2015-03-07
*/
public class DeriveNewAccountSortCodeResult {

	int result;
	String accountNumber;
	String scan;
	String errorDetails;
	
	DeriveNewAccountSortCodeResult(String accountNumber)
	{
		this.accountNumber = accountNumber;
	}
	
	/**
	 * Set the initial search account details
	 * 
	 * @param value
	 *            the initial search account details
	 */
	void setAccountNumber(String value) {
		this.accountNumber = value;
	}

	/**
	 * Get the query account number
	 */
	public String getAccountNumber() {
		return this.accountNumber;
	}
	
	/**
	 * Set any internal error detail
	 * 
	 * @param value
	 *            the error detail
	 */
	void setErrorDetails(String value) {
		this.errorDetails = value;
	}
	
	/**
	 * Get any internal error detail
	 */
	public String getErrorDetails() {
		return this.errorDetails;
	}
	
	/**
	 * Set the query result sort code for the account number
	 * 
	 * @param value
	 *            the derived account number sort code
	 */
	void setSortCode(String value) {
		this.scan = value;
	}
	
	/**
	 * get the derived account number sort code
	 */
	public String getSortCode() {
		return this.scan;
	}
	
	/**
	 * set the call result code
	 * 
	 * @param value
	 *            the operation call result code
	 */
	void setResult(int value) {
		this.result = value;
	}
	
	/**
	 * get the operation call result code
	 */
	public int getResult() {
		return this.result;
	}
}
