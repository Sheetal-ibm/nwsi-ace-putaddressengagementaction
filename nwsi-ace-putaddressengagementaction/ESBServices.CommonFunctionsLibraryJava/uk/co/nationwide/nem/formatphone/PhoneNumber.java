package uk.co.nationwide.nem.formatphone;

/**
 * The PhoneNumber class contains a method to pre-format a number by replacing the ISCD "44" for UK Domestic numbers
 * @author Chaitanya Soman, C39099
 * @version 0.1
 * @since 2015-09-23
 */
public class PhoneNumber {
	
	/**
	 * This method accepts an unformatted number as input and pre-formats it.
	 * For example, replacing the "+" symbol with a "0"
	 * @param number A String representation of the unformatted number
	 * @return FormatResult - Contains the result of the pre-formatting.
	 */
	public FormatResult parse(String number){
		FormatResult result = new FormatResult();
		result.setOriginalNumber(number);
		
		number = number.trim();
		
		StringBuffer buffer = new StringBuffer();
		
		//Loop through all characters of the number
		for(int i=0; i<number.length(); i++){
			//Collect numeric characters in a buffer
			if(number.substring(i, i+1).matches("\\d")){
				buffer.append(number.substring(i, i+1));
			}else{
				//Check for + only if it appears at the first position
				if((i==0)&&(number.substring(i, i+1).equals("+"))){
					//International Number - Append a 00 to the buffer
					buffer.append("00");
				}
			}
		}
		
		if(buffer.length() > 4
		   && buffer.toString().substring(0,4).equals("0044")){
			//International code for UK detected.
			//Remove this from the number and replace with single 0
			String parsedStr = "0"+buffer.toString().substring(4,buffer.length());  //--PRB00024651 for +447004433265 number
			//String parsedStr = buffer.toString().replace("0044", "0");
			result.setFormattedNumber(parsedStr);
			result.setExtension("");
			result.setInternational(false);
			result.setCode(Results.SUCCESS);
			result.setSuccess(true);
		}else if(buffer.toString().length() > 2 
			   && buffer.toString().substring(0,2).equals("00")){
				//International number
				//Mark result as international
				//Replace the leading 0
			    String parsedStr = buffer.toString().substring(2,buffer.length());  //--PRB00024651 . If 00000000000 or 0 or 000 as input number
			    if (parsedStr.toString().equals("0")){                             //--PRB00024651
			    	// An empty string is an invalid number
					result.logError("Invalid number passed in.", Results.INVALID_NUMBER);
			    }
			    else {
				//String parsedStr = buffer.toString().replace("00", "");
				result.setFormattedNumber(parsedStr);
				result.setExtension("");
				result.setInternational(true);
				result.setCode(Results.SUCCESS);
				result.setSuccess(true);}
			} else if(buffer.toString().length() > 0
					  && !buffer.toString().equals("00")){
				//Has some numbers so assuming it to be a valid number
				 if (buffer.toString().equals("0")){
				    	// An empty string is an invalid number
						result.logError("Invalid number passed in.", Results.INVALID_NUMBER);
				    }
			      else {
				result.setFormattedNumber(buffer.toString());
				result.setExtension("");
				result.setInternational(false);
				result.setCode(Results.SUCCESS);
				result.setSuccess(true);}
			} else {
				// An empty string is an invalid number
				result.logError("Invalid number passed in.", Results.INVALID_NUMBER);
			}
		
		return result;
	}

}
