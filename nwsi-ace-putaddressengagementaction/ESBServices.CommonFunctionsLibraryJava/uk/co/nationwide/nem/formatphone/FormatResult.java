package uk.co.nationwide.nem.formatphone;

/**
 * The FormatResult class stores the output of the formatting process.
 * In the event of a success, it will store the formatted number, number nationality, optional extension
 * In the event of a failure, it will store the error message
 * Therefore the caller can inspect the FormatResult object to get relevant details in success as well as failure.
 * @author Chaitanya Soman, C39099
 * @version 0.1
 * @since 2015-09-23
 *
 */
public class FormatResult {
	
	/***********Format Result attributes*************************/
	private String originalNumber = "";		// The number passed in
    private String formattedNumber = "";	// The formatted number if successful
    private String extension = "";			// Any extension numbers extracted
    private boolean isInternational;			// Flag denoting a non-UK number
    private int code = -1;					// Error code
    private boolean success = true;			// True if successful format, false if not
    private String message = "";			// Textual error description
    private String countryCode = "";		// The country code for an international number
    private String countryName = "";		// The country name for an international number
    private String countryNumber = "";		// The country number for an international number
    private String areaCode = ""; 			// The area code for a UK Domestic number
    private String localNumber = ""; 		// The local number i.e. what appears after the space after area code for a UK Domestic number

    /**
     * The following boolean attribute stores whether the disk files were loaded
     * while creating the PhoneNode or whether one was loaded from the heap.
     */
    private boolean isFilesLoaded = false;
    public boolean isFilesLoaded() {
		return isFilesLoaded;
	}
	public void setFilesLoaded(boolean isFilesLoaded) {
		this.isFilesLoaded = isFilesLoaded;
	}

	
	/********* List of Getters and Setters for above attributes***********/
    	public void logError(String errormessage, int errorcode)
	{
    		//Reset all fields to blank
    		this.setAreaCode("");
    		this.setCountryCode("");
    		this.setCountryName("");
    		this.setCountryNumber("");
    		this.setExtension("");
    		this.setFormattedNumber("");
    		this.setInternational(false);
    		this.setLocalNumber("");

    		// Set the Error Status
    		this.message = errormessage;
    		this.code = errorcode;
    		this.success = false;
	}

	public String getOriginalNumber() {
		return originalNumber;
	}

	public void setOriginalNumber(String originalNumber) {
		this.originalNumber = originalNumber;
	}

	public String getFormattedNumber() {
		return formattedNumber;
	}

	public void setFormattedNumber(String formattedNumber) {
		this.formattedNumber = formattedNumber;
	}

	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}

	public boolean isInternational() {
		return isInternational;
	}

	public void setInternational(boolean isInternational) {
		this.isInternational = isInternational;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getCountryName() {
		return countryName;
	}

	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}

	public String getCountryNumber() {
		return countryNumber;
	}

	public void setCountryNumber(String countryNumber) {
		this.countryNumber = countryNumber;
	}

	public String getAreaCode() {
		return areaCode;
	}

	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}

	public String getLocalNumber() {
		return localNumber;
	}

	public void setLocalNumber(String localNumber) {
		this.localNumber = localNumber;
	}	

}
