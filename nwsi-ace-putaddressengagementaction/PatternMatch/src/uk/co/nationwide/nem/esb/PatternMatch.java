package uk.co.nationwide.nem.esb;

import java.util.regex.Pattern;

/**
 *	This class has a method which matches the regular expression pattern(regex) and the field value.
 * 	Pattern and value both are passed as parameter value while calling the function.
 * 
 *  @author C411123 - Tarun Sharma
 * 	@version 1.0
 * 
 */

public class PatternMatch {

	public static Boolean MatchValueWithRegExp (String pattern , String fieldvalue)
	{
		String patternToBeMatched =pattern;
		Pattern compiledValueOfPattern =Pattern.compile(patternToBeMatched);
		
		String valueToBeMatched = fieldvalue;
		
		if (!compiledValueOfPattern.matcher(valueToBeMatched).matches())
		{
			return false;
		}
		else
		{		
		return true;
		}
	}
	
}
