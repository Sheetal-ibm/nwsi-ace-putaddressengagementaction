package uk.co.nationwide.nem.formatphone;


/**
 * 
 * The FormatPhone class contains a function to format the incoming phone number.
 * @author Chaitanya Soman, C39099
 * @version 0.1
 * @since 2015-09-23
 * 
 */
public class FormatPhone {
	private static PhoneNode rootNode;
	
	
	/**
	 * The formatNumber() method accepts an unformatted phone number as input and formats it.
	 * The results of the format are stored in the FormatResult object that is returned to the caller.
	 * In the event of successful formatting, the formatted number, an optional extension number, nationality etc. are stored in the FormatResult output
	 * Else, the error message is stored in the FormatResult output.
	 * The caller can inspect the FormatResult output to get all the details in success or failure.
	 * @param number - The original unformatted phone number
	 * @return FormatResult - A Class that represents the result of formatting the phone.
	 */
	public FormatResult formatNumber(String number, String phoneFormatFile, String codesFile){
		FormatResult result = new FormatResult();
		boolean isFilesLoaded = false;
		
		try{
			//Prepare a PhoneNode object if not already prepared
			if(rootNode == null){
				rootNode = new PhoneNode();
				result = rootNode.load(phoneFormatFile, codesFile);
				if(!result.isSuccess()){
					return result;
				}
				isFilesLoaded = true;
			}
			
			//Preliminary formatting of the number before full formatting.
			PhoneNumber pn = new PhoneNumber();
			result = pn.parse(number);
			
			// Complete formatting of the number.
			if(result.isSuccess()){
				result = rootNode.formatNumber(result, true);
			}

			if(result.isSuccess()){
				/**
	             * This snippet derives the area code and local number from the formatted number
	             * The part before the space is considered as area code
	             * The part after space is considered as local number
	             * If there isn't a space then the whole number is set as local number and area code will be blank
	             */
	            String formattedNumberString = result.getFormattedNumber();
	            // Determine the index position of first space character.
	            int firstSpacePosition = formattedNumberString.indexOf(" ");
	            if(firstSpacePosition > -1){ // This formatted number does have spaces
	            	// Set the area code as the number before space
	            	result.setAreaCode(formattedNumberString.substring(0, (firstSpacePosition + 1)));
	            	// Set the local number as everything that follows the space
	            	result.setLocalNumber(formattedNumberString.substring(firstSpacePosition + 1));
	            }else{
	            	// This formatted number doesn't have a space
	            	// Set the entire formatted number as the local number
	            	result.setLocalNumber(result.getFormattedNumber());
	            }
			}
			
		}catch(Exception e){
			result.logError(e.toString(), Results.GENERAL_ERROR);
		}
		result.setFilesLoaded(isFilesLoaded);
		return result;
	}
}
