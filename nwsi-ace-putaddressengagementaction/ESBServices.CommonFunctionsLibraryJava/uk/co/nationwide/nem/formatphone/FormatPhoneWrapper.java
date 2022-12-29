package uk.co.nationwide.nem.formatphone;

import com.ibm.broker.plugin.MbElement;
import com.ibm.broker.plugin.MbException;
import com.ibm.broker.plugin.MbXMLNSC;

/**
 * A wrapper class that encapsulates the phone formatting functional classes.
 * This is a utility class that exposes the phone formatting java functionality to ESQL
 * @author c39099
 * @version 0.1
 */
public class FormatPhoneWrapper {
	
	/**
	 * A static method that wraps the entry point to the phone formatting java functionality.
	 * 
	 * This invokes the formatNumber() method of the FormatPhone.java class.
	 * 
	 * The FormatResult returned by formatNumber is then stored as a tree of elements in the
	 * MbElement which is the last input to this method. This results in a tree structure getting
	 * created in the ESQL REFERENCE that is input to this method as an MbElement[].
	 * 
	 * @param unformattedNumber - The String unformatted telephone number
	 * @param phoneFormatFile - The String phone codes file name and path
	 * @param codesFile - The String ISCD codes file name and path
	 * @param resultTreeList - The MbElement array that represents an ESQL REFERENCE type
	 */
	public static void callFormatPhone(String unformattedNumber, String phoneFormatFile, String codesFile, MbElement[] resultTreeList){
		// Initialize a FormatPhone object
		FormatPhone fp = new FormatPhone(); 
		
		// Invoke the formatNumber method passing it the following three as input - 
		// 1) The unformatted phone number
		// 2) The phone codes file name and path
		// 3) The ISCD codes file name and path
		// The results of the formatting are returned in the FormatResult object
		FormatResult fr = fp.formatNumber(unformattedNumber, phoneFormatFile, codesFile);
		
		// Check whether the FormatResult returned above is non null
		// Also check that the ESQL REFERENCE provided as MbElement is non null
		if((fr != null) && (resultTreeList != null) && (resultTreeList.length > 0)){
			// Construct a tree comprising of FormatResult results within the MbElement
			try {
				MbElement resultTree = resultTreeList[0];
				// Set the code
				MbElement nextElement = resultTree.createElementAsFirstChild(MbXMLNSC.PARSER_NAME);
				nextElement.setName("CODE");
				nextElement.setValue(fr.getCode());
				// Set the country code
				if(!fr.getCountryCode().trim().equals("")){
					nextElement = resultTree.createElementAsLastChild(MbXMLNSC.PARSER_NAME);
					nextElement.setName("COUNTRYCODE");
					nextElement.setValue(fr.getCountryCode());
				}
				// Set the country name
				if(!fr.getCountryName().trim().equals("")){
					nextElement = resultTree.createElementAsLastChild(MbXMLNSC.PARSER_NAME);
					nextElement.setName("COUNTRYNAME");
					nextElement.setValue(fr.getCountryName());
				}
				// Set the country ID
				if(!fr.getCountryNumber().trim().equals("")){
					nextElement = resultTree.createElementAsLastChild(MbXMLNSC.PARSER_NAME);
					nextElement.setName("COUNTRYNUMBER");
					nextElement.setValue(fr.getCountryNumber());
				}
				// Set the extension number
				if(!fr.getExtension().trim().equals("")){
					nextElement = resultTree.createElementAsLastChild(MbXMLNSC.PARSER_NAME);
					nextElement.setName("EXTENSION");
					nextElement.setValue(fr.getExtension());
				}
				// Set the formatted number
				if(!fr.getFormattedNumber().trim().equals("")){
					nextElement = resultTree.createElementAsLastChild(MbXMLNSC.PARSER_NAME);
					nextElement.setName("FORMATTEDNUMBER");
					nextElement.setValue(fr.getFormattedNumber());
				}
				// Set the area code
				if(!fr.getAreaCode().trim().equals("")){
					nextElement = resultTree.createElementAsLastChild(MbXMLNSC.PARSER_NAME);
					nextElement.setName("AREACODE");
					nextElement.setValue(fr.getAreaCode());
				}
				// Set the local number
				if(!fr.getLocalNumber().trim().equals("")){
					nextElement = resultTree.createElementAsLastChild(MbXMLNSC.PARSER_NAME);
					nextElement.setName("LOCALNUMBER");
					nextElement.setValue(fr.getLocalNumber());
				}
				// Set the message -- This is useful in case of errors
				if(!fr.getMessage().trim().equals("")){
					nextElement = resultTree.createElementAsLastChild(MbXMLNSC.PARSER_NAME);
					nextElement.setName("MESSAGE");
					nextElement.setValue(fr.getMessage());
				}
				// Set the original input number
				if(!fr.getOriginalNumber().trim().equals("")){
					nextElement = resultTree.createElementAsLastChild(MbXMLNSC.PARSER_NAME);
					nextElement.setName("ORIGINALNUMBER");
					nextElement.setValue(fr.getOriginalNumber());
				}
				// Set whether the formatting was successful
				nextElement = resultTree.createElementAsLastChild(MbXMLNSC.PARSER_NAME);
				nextElement.setName("ISSUCCESS");
				nextElement.setValue(fr.isSuccess());
				// Set whether the number is an international number	
				nextElement = resultTree.createElementAsLastChild(MbXMLNSC.PARSER_NAME);
				nextElement.setName("ISINTERNATIONAL");
				nextElement.setValue(fr.isInternational());
				// Set whether disk files were loaded to create PhoneNode
				nextElement = resultTree.createElementAsLastChild(MbXMLNSC.PARSER_NAME);
				nextElement.setName("ISFILESLOADED");
				nextElement.setValue(fr.isFilesLoaded());
				
				
			} catch (MbException e) {
				// Do Nothing
			}
		}
	}
}
