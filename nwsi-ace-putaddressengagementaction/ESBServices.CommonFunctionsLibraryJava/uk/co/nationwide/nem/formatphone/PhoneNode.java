package uk.co.nationwide.nem.formatphone;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * The PhoneNode class represents the tree structure that is traversed in order to validate a phone format and reformat it as per UK Domestic phone standards.
 * If the number is international, then the nationality is identified.
 * @author Chaitanya Soman, C39099
 * @version 0.1
 * @since 2015-09-23
 *
 */

public class PhoneNode {
	
	// An enumeration to define the Phone Node type
	private enum NodeType {  NONTERMINAL //More node information is available below this node
						   , TERMINAL    //No more node information is available below this node
						   , DEFAULT};   //If searching its child nodes is unsuccessful
	
	/**************************Phone Node Attributes***********************************/
	private NodeType type = NodeType.NONTERMINAL;					
	private HashMap<String, PhoneNode> childNodes = new HashMap<String, PhoneNode>();  
	private String oldStd;
    private String newStd;											 
    private String oldFormattedStd;
    private String newFormattedStd;
    private String oldLocal;
    private String newLocal;
    private boolean codeNode;
    private String countryNumber;
    private String countryCode;
    private String countryName;

    /*****************************Getter and Setters for above attributes****************/
	public NodeType getType() {
		return type;
	}
	public void setType(NodeType type) {
		this.type = type;
	}
	public HashMap<String, PhoneNode> getChildNodes() {
		return childNodes;
	}
	public String getOldStd() {
		return oldStd;
	}
	public void setOldStd(String oldStd) {
		this.oldStd = oldStd;
	}
	public String getNewStd() {
		return newStd;
	}
	public void setNewStd(String newStd) {
		this.newStd = newStd;
	}
	public String getOldFormattedStd() {
		return oldFormattedStd;
	}
	public void setOldFormattedStd(String oldFormattedStd) {
		this.oldFormattedStd = oldFormattedStd;
	}
	public String getNewFormattedStd() {
		return newFormattedStd;
	}
	public void setNewFormattedStd(String newFormattedStd) {
		this.newFormattedStd = newFormattedStd;
	}
	public String getOldLocal() {
		return oldLocal;
	}
	public void setOldLocal(String oldLocal) {
		this.oldLocal = oldLocal;
	}
	public String getNewLocal() {
		return newLocal;
	}
	public void setNewLocal(String newLocal) {
		this.newLocal = newLocal;
	}
	public boolean isCodeNode() {
		return codeNode;
	}
	public void setCodeNode(boolean codeNode) {
		this.codeNode = codeNode;
	}
	public String getCountryNumber() {
		return countryNumber;
	}
	public void setCountryNumber(String countryNumber) {
		this.countryNumber = countryNumber;
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
	
	/**
	 * This method loads two files from disk - 
	 * 		Phone Formats - This is a .csv file that stores valid UK Domestic phone numbers
	 * 		International Dialing Codes - This is a .xml file that stores valid international codes.
	 * Note:- The method expects two environment variables to store absolute path to the files.
	 * As an outcome of calling this method, the current PhoneNode becomes a root in a tree structure having child nodes that represent each valid number digit from the list of UK Domestic numbers.
	 * @return FormatResult the result of the load.
	 */
	public FormatResult load(String phoneFormatFile, String codesFile){
		FormatResult result = new FormatResult();
		
		// Read the environment parameter value path of UK Domestic Phone list (.csv file)
		//String phoneFormatFile = System.getenv("PHONE_FORMAT_FILE");
		// Read the environment parameter value path of International Dialing Codes (.xml file)
		//String codesFile = System.getenv("INTERNATIONAL_DIALING_CODES");
		
		// Load UK Domestic Phone Codes list and create a tree structure with this (current) PhoneNode as root.
		result = loadFormatFile(phoneFormatFile);
		
		if(result.isSuccess()){
			// Load the International Dial Codes file into a DOM Document Object.
			result = loadInternationalCodesFile(codesFile);
		}
		
		//Return the result
		return result;
	}
	
	/**
	 * This method loads the UK Domestic Phone Numbers from the .csv file.
	 * It then creates a tree structure of PhoneNode objects each representing one digit of a phone number from the list.
	 * This (Current) PhoneNode is the root.
	 * @param formatFile The path and name of the (.csv) file that contains the UK Domestic Phone Numbers.
	 * @return
	 */
	private FormatResult loadFormatFile(String formatFile){
		FormatResult result = new FormatResult();
		
		// Read the file from disk
		// formatFile represents the disk file path of the .csv file that contains valid UK Domestic phone numbers.
		File file = new File(formatFile);
		
		if(file.exists()){
			try {
				//Buffer the file contents as a input stream
				BufferedReader br = new BufferedReader(
								    	new InputStreamReader(
								    			new FileInputStream(file)));
				String nextLine;
				
				while((nextLine = br.readLine())!= null){
					//Get the row contents split by ,
					String[] numbers = nextLine.split(",");
					if(numbers.length == 2){
						//The split results in old number and new number being extracted as separate values
						String oldNum = numbers[0].trim();
						String newNum = numbers[1].trim();
						
						//Construct the node tree as per the digits in the number.
						populateTree(oldNum, newNum);
						
						// Repeat this for each row in the .csv 
						// Thereby resulting in a tree that can be traversed to validate a UK Domestic number
					}
				}
				// Reached the end of the file so close the input stream reader. 
				br.close();
				// No errors detected so far, hence declare success
			} catch (FileNotFoundException e) {
				result.logError(e.toString(), Results.FORMAT_FILE_MISSING);
			} catch (IOException e) {
				result.logError("Could not parse file - "+formatFile+" : "+e.toString(), Results.GENERAL_ERROR);
			}
		}else{
			result.logError("Format file not found - "+formatFile, Results.FORMAT_FILE_MISSING);
		}
		
		return result;
	}
	
	/**
	 * This method loads the international dial codes (.xml) file and creates a DOM Document from the root.
	 * This results in a DOM tree having a parent node per nation.
	 * There are child nodes within the parent that have information such as the dial code, country name etc..
	 * 
	 * @param codesFile - The path and name of the (.xml) disk file that has international codes.
	 * @return FormatResult - Contains the result of the load
	 */
	private FormatResult loadInternationalCodesFile(String codesFile){
		FormatResult result = new FormatResult();
		
		// Read the international codes (.xml) file from disk.
		File file = new File(codesFile);
		
		if(file.exists()){
			
			try {
				// Initialize the platform default DOM DocumentBuilderFactory
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				// Set the defer node expansion property to false. This is a suggested fix for some common DOM parsing defects.
				dbf.setFeature("http://apache.org/xml/features/dom/defer-node-expansion", false);
				// Initialize a DocumentBuilder instance from the platform default factory.
				DocumentBuilder db = dbf.newDocumentBuilder();
				// Parse the (.xml) file into a DOM Document using the DocumentBuilder instance.
				Document doc = db.parse(file);
				
				// Prepare a list of nodes that are "IDDCode"
				NodeList nodeList = doc.getElementsByTagName("IDDCode");
				for(int i=0; i < nodeList.getLength(); i++){
					String name = null;
					String code = null;
					Node node = nodeList.item(i);
					String iddCode = node.getFirstChild().getNodeValue();
					
					// Fetch the 
					while((node = node.getNextSibling())!=null){
						if(node.getNodeName() == "CountryName"){
							name = node.getFirstChild().getNodeValue();
						}
						if(node.getNodeName() == "CountryCode"){
							code = node.getFirstChild().getNodeValue();
						}
					}
					result = populateInternationalTree(iddCode, name, code, result);
					if(!result.isSuccess()){
						break;
					}
				}
		
			} catch (ParserConfigurationException e) {
				result.logError("Could not parse International Codes File - "+codesFile+" : "+e.toString(), Results.GENERAL_ERROR);
			} catch (SAXException e) {
				result.logError("Could not parse International Codes File - "+codesFile+" : "+e.toString(), Results.GENERAL_ERROR);
			} catch (IOException e) {
				result.logError("Could not parse International Codes File - "+codesFile+" : "+e.toString(), Results.GENERAL_ERROR);
			}
			
		}else{
			result.logError("International Codes File Not Found - " + codesFile, Results.INTERNATIONAL_CODES_FILE_MISSING);
		}
		
		return result;
	}
	
	/**
	 * This method populates the international tree with given ISCD number, country name and code
	 * 
	 * @param number  -- International Code Number for example "1" for USA
	 * @param countryName -- Contry Name, for example "United States of America"
	 * @param countryCode -- Country Code, for example "US"
	 */
	private FormatResult populateInternationalTree(String number, String countryName, String countryCode, FormatResult result){
		PhoneNode currentNode = this;
		for (int i = 0; i < number.length(); i++)
		{
			// Locate the child node relating to this number
			PhoneNode child = null;
			String keyStr = number.substring(i, i+1);
			if ( currentNode.getChildNodes().size() > 0 &&
				currentNode.getChildNodes().containsKey(keyStr))
			{
				child = (PhoneNode)currentNode.getChildNodes().get(keyStr);
			}

			// If we have come to the end of this number
			if (i == (number.length() - 1))
			{
				// If the current node has no children or 
				// this last digit hasn't been mapped
				if (child == null)
				{
					// Add the new node as a terminal node
					PhoneNode nod = new PhoneNode();
					nod.setType(NodeType.TERMINAL);
					nod.setCodeNode(true);
					nod.setCountryName(countryName);
					nod.setCountryCode(countryCode);
					nod.setCountryNumber(number);
					currentNode.getChildNodes().put(keyStr, nod);
					currentNode = nod;
				}
					// current node has a child relating to this number already
				else
				{
					// If the child has nodes, we're okay. We set the main child to 
					// be a default node instead of a terminal node
					if (child.getChildNodes().size() > 0)
					{
						child.setType(NodeType.DEFAULT);
						child.setCodeNode(true);
						child.setCountryName(countryName);
						child.setCountryCode(countryCode);
						child.setCountryNumber(number);
						currentNode = child;
					}
						// No other nodes? That means we have two numbers the same
						// Which means the international codes file has duplicates
						// Stick with the first code and log an error
					else
					{
						result.logError("International number {"+number+"} is duplicated in International Codes file.", Results.DUPLICATE_INTERNATIONAL_CODE);
						break;
					}
				}
			}
			else // Else we haven't come to the end of this number
			{
				// If the current node has no children or 
				// this last digit hasn't been mapped,
				// we simply add a new non-terminal node in
				if (child == null)
				{
					// Add the new node as a terminal node
					PhoneNode nod = new PhoneNode();
					nod.setType(NodeType.NONTERMINAL);
					nod.setCodeNode(true);
					nod.setCountryName(countryName);
					nod.setCountryCode(countryCode);
					currentNode.getChildNodes().put(keyStr, nod);
					currentNode = nod;
				}
					// current node has a child relating to this number already
					// Is it marked as terminal?
				else if (child.getType() == NodeType.TERMINAL)
				{
					// Because we haven't finished mapping our current number, this other
					// node becomes a default number.
					child.setType(NodeType.DEFAULT);
					currentNode = child;
				}
				else // this is a non-terminal node that has already been mapped
				{
					currentNode = child;
				}
			}
		}
		return result;
	}
	
	/**
	 * This method populates the UK Domestic tree given the old and new numbers
	 * @param oldNum -- Old Number taken from the loaded file
	 * @param newNum -- New Number taken from the loaded file
	 */
	private void populateTree(String oldNum, String newNum){
		PhoneNode currentNode = this;
		for(int i=0; i < oldNum.length() && currentNode.getType() != NodeType.TERMINAL; i++){
			String keyStr = oldNum.substring(i, i+1);
			if(keyStr.matches("\\d")){
				currentNode.addChildNode(keyStr);
				currentNode = (PhoneNode)currentNode.getChildNodes().get(keyStr);
			}else if(keyStr.equals("X")){
				currentNode.setTerminalNodeValues(oldNum, newNum);
			}
		}
	}
	
	/**
	 * This method sets the "TERMINAL" node values per number loaded from the UK Domestic file
	 * @param oldNum -- The old number
	 * @param newNum -- The new number
	 */
	private void setTerminalNodeValues(String oldNum, String newNum){
		type = NodeType.TERMINAL;
		setNumberValues(oldNum, false);
		setNumberValues(newNum, true);
	}
	
	
	private void setNumberValues(String number, boolean isNew){
		boolean inStd = true;
		
		StringBuffer sbStd = new StringBuffer();
		StringBuffer sbFormatted = new StringBuffer();
		StringBuffer sbLocal = new StringBuffer();
		StringBuffer sbSpaces = new StringBuffer();

        for (int i = 0; i < number.length(); i++)
        {
            String keyStr = number.substring(i,i+1);
            if (keyStr.matches("\\d"))
            {
                if (inStd)
                {
                    sbStd.append(keyStr);
                    sbFormatted.append(keyStr);
                }
                else
                {
                    // shouldn't have a digit in the middle of the local part
                    // raise error and get out
                }
                sbSpaces = new StringBuffer("");
            }
            else if (keyStr.equals(" "))
            {
                // Only pay attention to the spaces if we are still dealing with the std code
                if (inStd)
                {
                    sbFormatted.append(keyStr);
                }
                else
                {
                    // Save the spaces just in case they are part of the trailing XXXX formatting
                    sbSpaces.append(keyStr);
                }
            }
            else if (keyStr.equals("X")) 
            {
                sbLocal.append(sbSpaces.toString());
                sbLocal.append(keyStr);
                inStd = false;
                sbSpaces = new StringBuffer("");
            }
        }

        // Set all the final values
        if(isNew){
        	this.newStd = sbStd.toString();
        	this.newFormattedStd = sbFormatted.toString();
        	this.newLocal = sbLocal.toString();
        }else{
        	this.oldStd = sbStd.toString();
        	this.oldFormattedStd = sbFormatted.toString();
        	this.oldLocal = sbLocal.toString();
        }
	}
	
	private void addChildNode(String keyStr){
		if(childNodes == null){
			childNodes = new HashMap<String, PhoneNode>();
		}
		if(childNodes.size() == 0 || !childNodes.containsKey(keyStr)){
			childNodes.put(keyStr, new PhoneNode());
			type = NodeType.NONTERMINAL;
		}
	}
	
	/**
	 * This method accepts a FormatResult object having a pre-formatted number as input.
	 * It then applies number formatting rules and post-formats the number.
	 * If the number is international, this method will validate the nationality.
	 * If the number is UK Domestic, this method will attempt to format it as per UK Domestic standards and try to extract an extension where applicable.
	 * @param result -- A FormatResult object that contains a pre-formatted number.
	 * @param validate  
	 * @return -- A FormatResult object that contains the post-formatted number.
	 */
	public FormatResult formatNumber(FormatResult result, boolean validate)
    {
        // Find the relevant node to format with by traversing the decision tree using the supplied phone number
        PhoneNode currentNode = this;
        PhoneNode defaultNode = null;
        boolean end = false;

		// Loop through the characters in the phone number
        for (int i = 0; i < result.getFormattedNumber().length() && 
                        currentNode != null && 
                        !end &&
                        currentNode.getType() != NodeType.TERMINAL; i++)
        {
            // Non terminal node, so check if there is another node below
            String keyChar = result.getFormattedNumber().substring(i, i+1);
            if (keyChar.matches("\\d"))
            {
                if (!currentNode.getChildNodes().containsKey(keyChar))
                {
                    // Can't find a sub node for this number and current node is not
                    // terminal, so this number format is not recognised. Return a default
                    // node if one exists.
                    currentNode = defaultNode;
                    end = true;
                }
                else
                {
                    // Move on to sub node
                    currentNode = (PhoneNode)currentNode.getChildNodes().get(keyChar);

                    // Record the latest default node reached
                    if (currentNode.getType() == NodeType.DEFAULT)
                    {
                        defaultNode = currentNode;
                    }
                }
            }
        }

        // Check if we have any node at all
        if ( currentNode == null )
        {
            result.logError("The telephone number is invalid", Results.UNKNOWN_NUMBER);
            return result;
        }
        else
        {
            // Found a relevant node

            // Is it an international code node?
            if (currentNode.codeNode)
            {
                result.setExtension("");
                result.setCode(Results.SUCCESS);
                result.setInternational(true);
                result.setSuccess(true);
                result.setCountryCode(currentNode.getCountryCode());
                result.setCountryName(currentNode.getCountryName());
                result.setCountryNumber(currentNode.getCountryNumber());
                return result;
            }
            else
            {
                // Formatting node!
                /// Format according to the layout provided and extract
                // and extension number
                String remainingNumber = result.getFormattedNumber().substring(currentNode.getOldStd().length());

				if (validate)
				{
					/**************************************************************************************************************************
					 * C39099 : Code changes to fix Defect 535 : SO Increment - 3/4
					 * 
					 * <Problem Description> : Application Declined error page immediately getting displayed 
					 * when Valid Mobile number entered in Additional Info screen as an only one mandatory Telephone number of the customer
					 * 
					 * <Fix Description> : Added logic to replace space characters in the old local number string
					 * before length comparison
					 **************************************************************************************************************************/
					String oldLocalNumStr = currentNode.getOldLocal();
					oldLocalNumStr = oldLocalNumStr.replace(" ", "");
					int localNumberLength = oldLocalNumStr.length();
					
					if (remainingNumber.length() > localNumberLength)
					{
						result.logError("The formatted local number is too long", Results.LOCAL_NUMBER_TOO_LONG);
						return result;
					}

					if (remainingNumber.length() < localNumberLength)
					{
						result.logError("The formatted local number is too short", Results.LOCAL_NUMBER_TOO_SHORT);
						return result;
					}
				}

                // First part of formatted number is the set std code taken from terminal node
                StringBuffer sbFormattedNumber = new StringBuffer(currentNode.getNewFormattedStd());

                // Now have a remaining number that needs to be formatted (spaces reinserted if necessary) and
                // extension number extracted
                int remPos = 0;
                for (int i = 0; remPos < remainingNumber.length() && i < currentNode.getNewLocal().length(); i++)
                {
                    if (currentNode.getNewLocal().substring(i, i+1).equals(" "))
                    {
                        sbFormattedNumber.append(" ");
                    }
                    else
                    {
                        sbFormattedNumber.append(remainingNumber.substring(remPos, (remPos+1)));
                        remPos++;
                    }
                }

                // We have a formatted number!
                result.setFormattedNumber(sbFormattedNumber.toString());

                // Work out any extensions
                if (remPos < remainingNumber.length())
                {
                    result.setExtension(remainingNumber.substring(remPos).trim());
                }
                result.setSuccess(true);
                result.setCode(Results.SUCCESS);
            }
        }

        return result;
    }	
}
