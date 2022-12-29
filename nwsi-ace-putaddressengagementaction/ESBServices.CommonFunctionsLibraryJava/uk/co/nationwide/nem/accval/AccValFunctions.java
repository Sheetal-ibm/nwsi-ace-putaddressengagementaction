package uk.co.nationwide.nem.accval;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The AccValFunctions library is a conversion of the PDC account validation
 * library implementing the same functionality in Java without the use of third
 * party conversion tools.
 * 
 * <pre>
 * Example use;
 * 
 * 	Validating an account number, returning the reformatted account number and Scan
 * 	
 * 		AccValResult result = AccValFunctions.QueryAccountNumber("0535/388 213 890");
 * 		
 * 		System.out.printf("%d %-22s %-22s %-22s %-22s\n",
 * 					result.getResult(), result.getAccountNumber(),
 * 					result.getAccountNumberFormatted(),
 * 					result.getAccountType(), result.getScan());
 * 
 * </pre>
 * 
 * 
 * @author Matt Andrews
 * @version 1.1
 * @since 2017-10-18
 */
public class AccValFunctions {

	/***
	 * Validate account number
	 * 
	 * @param Acc
	 *            account number to validate formated as a single string
	 * @param iMulVal
	 *            array of check digits
	 * @param iLen
	 *            number of digits to check
	 * @param iMod
	 *            modulus to use
	 * @return true if the account number number is a valid [otherwise false]
	 * 
	 */
	private static boolean check(String Acc, int[] iMulVal, int iLen, int iMod)
			throws Exception {
		int n;
		int iTotal;

		// Calculate quantity
		iTotal = 0;
		for (n = 0; n < iLen; n++) {
			iTotal += (Acc.charAt(n) - '0') * iMulVal[n];
		}

		return iTotal % iMod == Acc.charAt(iLen) - '0';
	}

	/***
	 * Validate Bexhill account number
	 * 
	 * @param AccNum
	 *            account number to validate formated as a single string
	 * @return true if the account number number is a valid [otherwise false]
	 * 
	 */
	private static Boolean checkBexhill(String AccNum) throws Exception {
		final int[] iMulVal = { 19, 17, 13, 11, 7, 5, 3, 1 };
		final int iLen = iMulVal.length;
		String szAcc;
		int iCD;
		int n;
		int iTotal;
		
		// Setup pointer to part of acc. number to validate
		// Copy first 2 chars
		szAcc = AccNum.substring(0, 2);
		
		// Skip - and append next 6 chars
		szAcc += AccNum.substring(3, 9);
		
		// Calculate check digit (first 2 digits of last section of acc. number)
		iCD = (AccNum.charAt(10) - '0') * 10 + AccNum.charAt(11) - '0';
		
		// Calculate quantity
		iTotal = 0;
		for (n = 0; n < iLen; n++) {
			iTotal += (szAcc.charAt(n) - '0') * iMulVal[n];
		}
		
		// Check check-digit
		if (iCD == 11 - iTotal % 11) {
			return true;
		} else {
			return false;
		}
	}

	/***
	 * Checks the input account number using Capsil check digit routine
	 * 
	 * @param accountNumber
	 *            account number to validate
	 * @return true if the account number number is a valid [otherwise false]
	 */
	private static boolean checkCapsil(String accountNumber) throws Exception {
		final int[] iMulVal = { 2, 21, 8, 1, 4, 13 };
		final char[] cCheckDigitChars = { 'A', 'B', 'C', 'D', 'E', 'F', 'G',
				'H', 'J', 'K', 'L', 'M', 'N', 'P', 'Q', 'R', 'T', 'V', 'W',
				'X', 'Y', 'Z' };
		final char[] cInnerAlphaNumericChars = { '0', '1', '2', '3', '4', '5',
				'6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'G', 'H', 'J',
				'K', 'L', 'M', 'N', 'P', 'R', 'T', 'W', 'X', 'Y' };

		final int iLen = iMulVal.length;
		final int iInnerAlphaNumericCharsLen = cInnerAlphaNumericChars.length;
		int iTotal = 0;
		int value1 = -1;
		int value2 = -1;
		boolean validNumber = false;
		
		for (int n = 0; n < iInnerAlphaNumericCharsLen; n++) {
			// check that the first 2 characters are in the inner alpha numeric
			// char array.
			// if so, store the values.
			if (accountNumber.charAt(0) == cInnerAlphaNumericChars[n]) {
				value1 = n * iMulVal[0];
			}

			if (accountNumber.charAt(1) == cInnerAlphaNumericChars[n]) {
				value2 = n * iMulVal[1];
			}

		}
		if (value1 != -1 && value2 != -1) {
			validNumber = true;
			iTotal = value1 + value2;
		}

		for (int n = 2; n < iLen; n++) {
			iTotal += (accountNumber.charAt(n) - '0') * iMulVal[n];
		}

		return validNumber
				&& accountNumber.charAt(6) == cCheckDigitChars[iTotal % 22];
	}

	/***
	 * Validate Flex Account numbers in Cashlink PANS
	 * 
	 * @param accountNumber
	 *            account number to validate
	 * @return true is the account number is valid [otherwise false]
	 */
	private static boolean checkCashLinkPAN(String accountNumber,
			ReferenceInt iFmtType, Reference<String> TypeAcc) throws Exception {
		
		Boolean bOk = false;
		String FlexNum;

		// just get the flex Acct number from the input PAN
		// and do flex acct check - can be mod 10 or 11
		FlexNum = accountNumber.substring(9, 18);
		bOk = checkSwindon9(FlexNum);

		// Verify check digit
		if (!bOk) {
			bOk = checkSwindon9Mod10(FlexNum);
		}

		// try mod 10 if 11 didn't work
		if (bOk) {
			// validated
			iFmtType.value = 13;
			TypeAcc.set(Constants.FLEXACCOUNT);
		} else {
			// didn't validate as either
			iFmtType.value = 0;
			TypeAcc.set(Constants.OTHER);
		}
		return bOk;
	}

	/***
	 * Validate PODCS Error Suspense account number
	 * 
	 * @param AccNum
	 *            account number to validate formated as a single string
	 * @return true if the account number number is a valid [otherwise false]
	 * 
	 */
	private static Boolean checkErrorSus(String AccNum) throws Exception {
		int i;
		String szErrSusNo;
		szErrSusNo = AccNum.substring(5, 14);

		for (i = 0; i < Constants.nCountSuspenseNo; i++) {
			// Check against our list of valid suspense nos
			if (szErrSusNo.equalsIgnoreCase(Constants.SuspenseNo[i])) {
				return true;
			}

		}

		return false;
	}

	/***
	 * Validate External Reference number.
	 * 
	 * <pre>
	 * Valid characters in format string are:
	 * 9 - matches any numeric in test string
	 * A - matches any alpha in test string
	 * ! - matches any numeric or ' ' in test string
	 * Z - matches any numeric in test string
	 * ? - matches '-', '/' or ' '
	 * other char - matches any identical character in test string
	 * </pre>
	 * 
	 * @param AccNum
	 *            number to validate
	 * @return true if a valid external reference number [otherwise false]
	 * @throws Exception
	 */
	private static boolean checkExtRefNo(String AccNum) throws Exception {
		final int[] iMulVal = { 3, 7, 9, 10, 5, 8, 4, 2 };
		final int iLen = iMulVal.length;
		String Acc;
		int iTotal;
		int iCheck;

		// Setup pointer to part of account number to validate
		Acc = AccNum.substring(1);

		// Calculate quantity
		iTotal = 0;
		for (int n = 0; n < iLen; n++) {
			iTotal += (Acc.charAt(n) - '0') * iMulVal[n];
		}

		// Calculate the check digit
		iCheck = iTotal % 11;
		if (iCheck != 0) {
			iCheck = 11 - iCheck;
		}

		return iCheck == Acc.charAt(iLen) - '0';
	}

	/***
	 * Checks an account number matches the IPOS format
	 * 
	 * @param accountNumber
	 *            account number to validate
	 * @return true if the account number number is a valid [otherwise false]
	 */
	private static boolean checkIPOS(String accountNumber) throws Exception {
		int nStringSize = 0;
		double nLargeIposNo = 0;
		boolean bOk = false;

		// get length of input string..
		nStringSize = accountNumber.length();
		if (nStringSize > 6 && nStringSize < 12) {
			bOk = true;
		}

		switch (nStringSize) {
		case 11: {
			
			// Ipos 10 digits, must be greater than 5000, not greater than
			// 2147483648..
			nLargeIposNo += (accountNumber.charAt(1) - '0') * 1000000000;
			nLargeIposNo += (accountNumber.charAt(2) - '0') * 100000000;
			nLargeIposNo += (accountNumber.charAt(3) - '0') * 10000000;
			nLargeIposNo += (accountNumber.charAt(4) - '0') * 1000000;
			nLargeIposNo += (accountNumber.charAt(5) - '0') * 100000;
			nLargeIposNo += (accountNumber.charAt(6) - '0') * 10000;
			nLargeIposNo += (accountNumber.charAt(7) - '0') * 1000;
			nLargeIposNo += (accountNumber.charAt(8) - '0') * 100;
			nLargeIposNo += (accountNumber.charAt(9) - '0') * 10;
			nLargeIposNo += accountNumber.charAt(10) - '0';
			
			break;
		}
		
		case 10: {
			
			// Ipos 9 digits, must be greater than 5000..
			nLargeIposNo += (accountNumber.charAt(1) - '0') * 100000000;
			nLargeIposNo += (accountNumber.charAt(2) - '0') * 10000000;
			nLargeIposNo += (accountNumber.charAt(3) - '0') * 1000000;
			nLargeIposNo += (accountNumber.charAt(4) - '0') * 100000;
			nLargeIposNo += (accountNumber.charAt(5) - '0') * 10000;
			nLargeIposNo += (accountNumber.charAt(6) - '0') * 1000;
			nLargeIposNo += (accountNumber.charAt(7) - '0') * 100;
			nLargeIposNo += (accountNumber.charAt(8) - '0') * 10;
			nLargeIposNo += accountNumber.charAt(9) - '0';
			
			break;
		}
		
		case 9: {
			
			// Ipos 8 digits, must be greater than 5000..
			nLargeIposNo += (accountNumber.charAt(1) - '0') * 10000000;
			nLargeIposNo += (accountNumber.charAt(2) - '0') * 1000000;
			nLargeIposNo += (accountNumber.charAt(3) - '0') * 100000;
			nLargeIposNo += (accountNumber.charAt(4) - '0') * 10000;
			nLargeIposNo += (accountNumber.charAt(5) - '0') * 1000;
			nLargeIposNo += (accountNumber.charAt(6) - '0') * 100;
			nLargeIposNo += (accountNumber.charAt(7) - '0') * 10;
			nLargeIposNo += accountNumber.charAt(8) - '0';
			
			break;
		}
		
		case 8: {
			
			// Ipos 7 digits, must be greater than 5000..
			nLargeIposNo += (accountNumber.charAt(1) - '0') * 1000000;
			nLargeIposNo += (accountNumber.charAt(2) - '0') * 100000;
			nLargeIposNo += (accountNumber.charAt(3) - '0') * 10000;
			nLargeIposNo += (accountNumber.charAt(4) - '0') * 1000;
			nLargeIposNo += (accountNumber.charAt(5) - '0') * 100;
			nLargeIposNo += (accountNumber.charAt(6) - '0') * 10;
			nLargeIposNo += accountNumber.charAt(7) - '0';
			
			break;
		}
		
		case 7: {
			
			// Ipos 6 digits, must be greater than 5000..
			nLargeIposNo += (accountNumber.charAt(1) - '0') * 100000;
			nLargeIposNo += (accountNumber.charAt(2) - '0') * 10000;
			nLargeIposNo += (accountNumber.charAt(3) - '0') * 1000;
			nLargeIposNo += (accountNumber.charAt(4) - '0') * 100;
			nLargeIposNo += (accountNumber.charAt(5) - '0') * 10;
			nLargeIposNo += accountNumber.charAt(6) - '0';
			
			break;
		}

		}

		bOk = bOk && nLargeIposNo > 179999 && nLargeIposNo <= 2147483648L;
		
		// if we're ok so far, check that the first char is an 'I'...
		if (bOk) {
			bOk = accountNumber.charAt(0) == 'I';
		}

		return bOk;
	}

	/***
	 * Checks an account number matches the Legal and General format
	 * 
	 * @param accountNumber
	 *            account number to validate
	 * @return true if the account number number is a valid [otherwise false]
	 */
	private static boolean checkLAndG(String accountNumber) throws Exception {
		int nStringSize = 0;
		boolean bOk = false;

		// get length of input string..
		nStringSize = accountNumber.length();

		// make sure it's a valid length (10 characters plus the 'L')
		if (nStringSize == 11) {
			bOk = true;
		}

		// check that the first char is an 'L'...
		if (bOk) {
			bOk = accountNumber.charAt(0) == 'L';
		}

		return bOk;
	}

	/***
	 * Validate Maidenhead account number
	 * 
	 * @param accountNumber
	 *            account number to validate
	 * @return true if a valid account number [otherwise false]
	 */
	private static boolean checkMHead(String accountNumber) {
		final int[] iMulVal = { 7, 6, 5, 4, 3, 2 };
		final int iLen = iMulVal.length;
		int iCD;
		int n;
		int iTotal;
		int iRem;

		// Calculate quantity
		iTotal = 0;
		for (n = 0; n < iLen; n++) {
			iTotal += (accountNumber.charAt(n) - '0') * iMulVal[n];
		}

		// Calculate check digit
		iCD = accountNumber.charAt(7) - '0';

		// Check check-digit
		iRem = iTotal % 10;
		if (iRem == 0) // Check digit is 0
		{
			if (iCD == 0) {
				return true;
			} else {
				return false;
			}
		} else {
			if (iCD == 10 - iRem) {
				return true;
			} else {
				return false;
			}
		}
	}

	/***
	 * check a number matches the MPOS format
	 * 
	 * @param accountNumber
	 *            account number to validate
	 * @return true is the account number is valid [otherwise false]
	 */
	private static Boolean checkMPOS(String accountNumber) throws Exception {
		int nStringSize = 0;
		boolean bOk = false;
		
		// get length of input string..
		nStringSize = accountNumber.length();
		if (nStringSize > 4 && nStringSize < 11) {
			bOk = true;
		}

		double nLargeMposNo = 0;
		switch (nStringSize) {
		case 10: {
			
			// Mpos 9 digits, must be greater than 5000..
			nLargeMposNo += (accountNumber.charAt(1) - '0') * 100000000;
			nLargeMposNo += (accountNumber.charAt(2) - '0') * 10000000;
			nLargeMposNo += (accountNumber.charAt(3) - '0') * 1000000;
			nLargeMposNo += (accountNumber.charAt(4) - '0') * 100000;
			nLargeMposNo += (accountNumber.charAt(5) - '0') * 10000;
			nLargeMposNo += (accountNumber.charAt(6) - '0') * 1000;
			nLargeMposNo += (accountNumber.charAt(7) - '0') * 100;
			nLargeMposNo += (accountNumber.charAt(8) - '0') * 10;
			nLargeMposNo += accountNumber.charAt(9) - '0';
			
			break;
		}
		
		case 9: {
			
			// Mpos 8 digits, must be greater than 5000..
			nLargeMposNo += (accountNumber.charAt(1) - '0') * 10000000;
			nLargeMposNo += (accountNumber.charAt(2) - '0') * 1000000;
			nLargeMposNo += (accountNumber.charAt(3) - '0') * 100000;
			nLargeMposNo += (accountNumber.charAt(4) - '0') * 10000;
			nLargeMposNo += (accountNumber.charAt(5) - '0') * 1000;
			nLargeMposNo += (accountNumber.charAt(6) - '0') * 100;
			nLargeMposNo += (accountNumber.charAt(7) - '0') * 10;
			nLargeMposNo += accountNumber.charAt(8) - '0';
			
			break;
		}
		
		case 8: {
			// Mpos 7 digits, must be greater than 5000..
			nLargeMposNo += (accountNumber.charAt(1) - '0') * 1000000;
			nLargeMposNo += (accountNumber.charAt(2) - '0') * 100000;
			nLargeMposNo += (accountNumber.charAt(3) - '0') * 10000;
			nLargeMposNo += (accountNumber.charAt(4) - '0') * 1000;
			nLargeMposNo += (accountNumber.charAt(5) - '0') * 100;
			nLargeMposNo += (accountNumber.charAt(6) - '0') * 10;
			nLargeMposNo += accountNumber.charAt(7) - '0';
			break;
		}
		case 7: {
			// Mpos 6 digits, must be greater than 5000..
			nLargeMposNo += (accountNumber.charAt(1) - '0') * 100000;
			nLargeMposNo += (accountNumber.charAt(2) - '0') * 10000;
			nLargeMposNo += (accountNumber.charAt(3) - '0') * 1000;
			nLargeMposNo += (accountNumber.charAt(4) - '0') * 100;
			nLargeMposNo += (accountNumber.charAt(5) - '0') * 10;
			nLargeMposNo += accountNumber.charAt(6) - '0';
			break;
		}
		case 6: {
			// Mpos 5 digits, must be greater than 5000..
			nLargeMposNo += (accountNumber.charAt(1) - '0') * 10000;
			nLargeMposNo += (accountNumber.charAt(2) - '0') * 1000;
			nLargeMposNo += (accountNumber.charAt(3) - '0') * 100;
			nLargeMposNo += (accountNumber.charAt(4) - '0') * 10;
			nLargeMposNo += accountNumber.charAt(5) - '0';
			break;
		}
		case 5: {
			// Mpos 4 digits, must be greater than 5000..
			nLargeMposNo += (accountNumber.charAt(1) - '0') * 1000;
			nLargeMposNo += (accountNumber.charAt(2) - '0') * 100;
			nLargeMposNo += (accountNumber.charAt(3) - '0') * 10;
			nLargeMposNo += accountNumber.charAt(4) - '0';
			break;
		}

		}

		// MSO numbers are in the range 550001001 to 999999999
		// MPOS have taken the decision to restrict their range to a max of
		// 549999999.
		bOk = bOk && nLargeMposNo > 5000 && nLargeMposNo < 550000000;

		// if we're ok so far, check that the first char is an 'M'...
		if (bOk) {
			bOk = accountNumber.charAt(0) == 'M';
		}

		return bOk;
	}

	/***
	 * Check a number matches the MSO format and range, then call the
	 * CheckVerhoeff method to see if it passes that routine.
	 * 
	 * @param accountNumber
	 *            account number to validate
	 * @return true if the account number number is a valid MSO account
	 *         [otherwise false]
	 */
	private static boolean checkMSO(String accountNumber) throws Exception {
		boolean potentiallyValid = false;
		if (accountNumber.length() == 10) {
			potentiallyValid = true;
		}

		// verify that the input starts with M
		potentiallyValid = potentiallyValid & 'M' == accountNumber.charAt(0);

		if (potentiallyValid) {
			// we've already checked that the first character is an 'M', so we
			// now look at the remaining characters.
			// if this convert fails we'll throw the error back to whatever's
			// calling us.
			final long largeMsoNo = Long.valueOf(accountNumber.substring(1,
					1 + accountNumber.length() - 1));

			// MSO must be 9 digits in the range 550001001 to 999999999.
			potentiallyValid = potentiallyValid && largeMsoNo > 550001000
					&& largeMsoNo < 1000000000;

			// now do the checksum passing the number without the leading
			// letter.
			potentiallyValid = potentiallyValid
					& checkVerhoeff(accountNumber.substring(1,
							1 + accountNumber.length() - 1));
		}

		return potentiallyValid;
	}

	/***
	 * Validate Northhampton account number
	 * 
	 * @param AccNum
	 *            account number to validate formated as a single string
	 * @return true if the account number number is a valid [otherwise false]
	 * 
	 */
	private static Boolean checkNorth(String AccNum) throws Exception {
		final int[] iMulVal = { 6, 3, 7, 9, 5, 8, 4, 2 };
		final int iLen = iMulVal.length;
		String Acc;
		int iCD;
		int n;
		int iTotal;
		int iRem;
		// Setup pointer to part of acc. number to validate
		Acc = AccNum.substring(6);
		// Calculate check digit (last 2 digits of acc. number)
		iCD = (AccNum.charAt(15) - '0') * 10 + AccNum.charAt(16) - '0';
		// Calculate quantity
		iTotal = 0;
		for (n = 0; n < iLen; n++) {
			iTotal += (Acc.charAt(n) - '0') * iMulVal[n];
		}
		// Check check-digit
		iRem = iTotal % 11;
		if (iRem == 0) {
			// Check digit is 0
			if (iCD == 0) {
				return true;
			} else {
				return false;
			}
		} else {
			if (iCD == 11 - iRem) {
				return true;
			} else {
				return false;
			}
		}
	}

	/***
	 * Validate PAN account number
	 * 
	 * @param accountNumber
	 *            account number to validate formated as a single string
	 * @return true if the account number number is a valid [otherwise false]
	 * 
	 */
	private static boolean checkPAN(String accountNumber) throws Exception {
		final int iLen = accountNumber.length();
		int iTotal;
		int iSubTotal;
		int n;
		int iCheck;
		// Calculate first total
		iTotal = 0;
		for (n = iLen - 2; n >= 0; n -= 2) {
			iSubTotal = (accountNumber.charAt(n) - '0') * 2;
			// Sum individual digits of sub total
			iTotal += iSubTotal % 10 + iSubTotal / 10;
		}
		for (n = iLen - 3; n >= 0; n -= 2) {
			// Add second total
			iSubTotal = accountNumber.charAt(n) - '0';
			iTotal += iSubTotal % 10 + iSubTotal / 10;
		}
		// Calculate check digit
		iCheck = iTotal % 10;
		if (iCheck != 0) {
			iCheck = 10 - iCheck;
		}

		// Validate check digit
		if (iCheck == accountNumber.charAt(iLen - 1) - '0') {
			return true;
		} else {
			return false;
		}
	}

	/***
	 * Checks an account number matches the Personal Loan approval in principle
	 * format
	 * 
	 * @param accountNumber
	 *            account number to validate
	 * @return true if the account number number is a valid [otherwise false]
	 */
	private static Boolean checkPersonalLoanAIP(String accountNumber)
			throws Exception {
		int nStringSize = 0;
		boolean bOk = false;

		// get length of input string..
		nStringSize = accountNumber.length();
		if (nStringSize != 10) {
			bOk = false;
		} else {
			bOk = accountNumber.charAt(0) == 'P'
					&& accountNumber.charAt(1) == 'L';
		}

		return bOk;
	}

	/***
	 * Checks an account number matches the Portman format
	 * 
	 * @param accountNumber
	 *            account number to validate
	 * @return true if the account number number is a valid [otherwise false]
	 */
	private static boolean checkPORT(String accountNumber) throws Exception {
		int nStringSize = 0;
		boolean bOk = false;

		// get length of input string..
		nStringSize = accountNumber.length();

		// make sure it's a valid length. Originally said between 6 and 21,
		// once CIS started loading real Portman account numbers it they found
		// them all the way down to account zero...
		if (nStringSize > 1 && nStringSize < 21) {
			bOk = true;
		}

		// check that the first char is an 'P'
		if (bOk) {
			bOk = accountNumber.charAt(0) == 'P';
		}

		return bOk;
	}

	/***
	 * Check a number matches the RBSI General Insurance Household format.
	 * 
	 * <pre>
	 * Valid characters in format string are:
	 * 9 - matches any numeric in test string
	 * A - matches any alpha in test string
	 * ! - matches any numeric or ' ' in test string
	 * Z - matches any numeric in test string
	 * ? - matches '-', '/' or ' '
	 * other char - matches any identical character in test string
	 * </pre>
	 * 
	 * @param AccNum
	 *            account number to validate
	 * @return true if the account number number is valid [otherwise false]
	 */
	private static Boolean checkRBSIGeneralInsuranceHousehold(String AccNum)
			throws Exception {
		boolean returnValue = false;
		int nStringSize = 0;

		// get length of input string..
		nStringSize = AccNum.length();
		if (nStringSize > 1 && nStringSize < 10 && AccNum.charAt(0) == 'R') {
			final int accNum = Integer.valueOf(AccNum.substring(1));
			// check the account number is in one of the ranges
			if (accNum >= 63900000 && accNum <= 63999999 || accNum >= 64900000
					&& accNum <= 64999999 || accNum >= 65900000
					&& accNum <= 65999999 || accNum >= 66900000
					&& accNum <= 66999999 || accNum >= 67900000
					&& accNum <= 67999999 || accNum >= 68900000
					&& accNum <= 68999999 || accNum >= 69900000
					&& accNum <= 69989999) {
				returnValue = true;
			}

		}

		return returnValue;
	}

	/***
	 * Validate SCAN account number
	 * 
	 * @param AccNum
	 *            account number to validate formated as a single string
	 * @return true if the account number number is a valid [otherwise false]
	 * 
	 */
	private static boolean checkSCAN(String AccNum) throws Exception {
		final int[] iMulVal = { 0, 0, 7, 6, 5, 8, 9, 4, 5, 6, 7, 8, 9 };
		final int[] iMulValMod10 = { 0, 3, 2, 4, 5, 8, 9, 4, 5, 6, 7, 8, 9 };
		final int iLen = iMulVal.length;
		final int iLenMod10 = iMulValMod10.length;
		String szTmp;
		boolean bValid = false;

		// Copy first 6 chars
		szTmp = AccNum.substring(0, 6);

		// Skip space and append last 8 chars
		szTmp += AccNum.substring(7, 15);

		// If account number is a CO-OP SCAN then use Flexaccount validation
		if (szTmp.substring(0, 6).compareToIgnoreCase(Constants.COOP_SCAN) == 0) {
			// need to ignore the first 5 characters
			bValid = checkSwindon9(szTmp.substring(5));
		} else {
			// 25/8/06 removed mod 10 check - 086086 sort code
			// stopped being used long before we started issuing
			// mod 10 flex numbers, so it's an invalid combination.
			bValid = check(szTmp, iMulVal, iLen, 11);
		}
		for (int i = 0; i < Constants.nCountSortCodeType; i++) {
			// if it passes check digit using 'new' sort code, 9 digit number
			// has to
			// start with either 60 - 64 or 05 - 09. If it doesn't pass check
			// digit
			// and the sort code says it's a flexaccount (ie, old style number),
			// try mod 10.
			if (!bValid) {
				// not valid -if it's a flex account sort code, try mod 10
				if (szTmp.substring(0, 6).equalsIgnoreCase(
						Constants.stabSortCodeType[i].getSortCode())
						&& Constants.stabSortCodeType[i].getAccType()
								.equalsIgnoreCase(Constants.FLEXACCOUNT)) {
					bValid = check(szTmp, iMulValMod10, iLenMod10, 10);
					break;
				}

			} else {
				// is valid - if it's a new sort code, check range.
				if (szTmp.substring(0, 6).compareToIgnoreCase(
						Constants.stabSortCodeType[i].getSortCode()) == 0
						&& Constants.NEWFLEX
								.compareToIgnoreCase(Constants.stabSortCodeType[i]
										.getAccType()) == 0) {
					bValid = bValid && szTmp.charAt(5) == '6'
							&& szTmp.charAt(6) < '5';
					// also, if it passes check digit as a new flex scan, it's
					// not allowed to pass as a mod 10 swindon 9 account
					bValid = bValid & !checkSwindon9Mod10(szTmp.substring(5));
					break;
				} else if (szTmp.substring(0, 6).compareToIgnoreCase(
						Constants.stabSortCodeType[i].getSortCode()) == 0
						&& Constants.NEWCARD
								.compareToIgnoreCase(Constants.stabSortCodeType[i]
										.getAccType()) == 0) {
					bValid = bValid && szTmp.charAt(5) == '0'
							&& szTmp.charAt(6) > '4';
					break;
				}

			}
		}
		return bValid;
	}

	/***
	 * Validate Swindon 8 account number
	 * 
	 * @param accountNumber
	 *            account number to validate formated as a single string
	 * @return true if the account number number is a valid [otherwise false]
	 * 
	 */
	private static boolean checkSwindon8(String accountNumber) throws Exception {
		final int[] iMulVal = { 9, 4, 5, 6, 7, 8, 9 };
		final int iLen = iMulVal.length;

		return check(accountNumber, iMulVal, iLen, 11);
	}

	/***
	 * Validate Swindon 9 account number
	 * 
	 * @param accountNumber
	 *            account number to validate formated as a single string
	 * @return true if the account number number is a valid [otherwise false]
	 * 
	 */
	private static boolean checkSwindon9(String accountNumber) throws Exception {
		final int[] iMulVal = { 8, 9, 4, 5, 6, 7, 8, 9 };
		final int iLen = iMulVal.length;

		return check(accountNumber, iMulVal, iLen, 11);
	}

	/***
	 * Validate Swindon 9 digit account number with modulus 10 (only if mod 11
	 * has already failed).
	 * 
	 * @param accountNumber
	 *            account number to validate formated as a single string
	 * @return true if the account number number is a valid [otherwise false]
	 * 
	 */
	private static boolean checkSwindon9Mod10(String accountNumber)
			throws Exception {
		final int[] iMulVal = { 8, 9, 4, 5, 6, 7, 8, 9 };
		final int iLen = iMulVal.length;

		return check(accountNumber, iMulVal, iLen, 10);
	}

	/***
	 * Validate Nationwide Trust account number
	 * 
	 * @param accountNumber
	 *            account number to validate
	 * @return true is the account number is valid [otherwise false]
	 */
	private static boolean checkTrust(String accountNumber) {
		final int[] iMulVal = { 1, 2, 5, 6, 3, 9 };
		final int iLen = iMulVal.length;
		int n;
		int iTotal;

		// Calculate quantity
		iTotal = 0;
		for (n = 0; n < iLen; n++) {
			iTotal += (accountNumber.charAt(n) - '0') * iMulVal[n];
		}

		// Check check-digit - modulus 11 with 10 taken as zero
		return accountNumber.charAt(n) - '0' == iTotal % 11 % 10;
	}

	/***
	 * Validate mortgage account application number
	 * 
	 * @param accountNumber
	 *            account number to validate formated as a single string
	 * @param TypeAcc
	 *            the account type
	 * @return true if the account number number is a valid [otherwise false]
	 * 
	 */
	private static boolean checkTypeApplNo(String accountNumber,
			Reference<String> TypeAcc) throws Exception {
		final int[] iMulVal = { 29, 11, 23, 7, 19, 5, 17, 3, 13, 1 };
		final Integer iLen = iMulVal.length;
		Integer n;
		Long iTotal = 0l;
		Boolean retVal = false;
		// Calculate quantity

		for (n = 0; n < iLen; n++) {
			iTotal += (accountNumber.charAt(n) - '0') * iMulVal[n];
		}
		// If remainder is 10 use zero as check digit
		if ((iTotal %= 11) == 10) {
			iTotal = 0l;
		}

		retVal = iTotal == accountNumber.charAt(iLen) - '0';
		// 85000001000 – 899999999999 UCB appl nos - change type accordingly
		if (retVal) {

			// Calculate amount of no..
			iTotal = Long.parseLong(accountNumber);
			if (iTotal > 85000000999L && iTotal < 90000000000L) {
				TypeAcc.set(Constants.UCB);
			} else {
				TypeAcc.set(Constants.MORTGAGE);
			}
		}

		return retVal;
	}

	/***
	 * Validate Newman protection policy number.
	 * 
	 * <pre>
	 * Valid characters in format string are:
	 * 9 - matches any numeric in test string
	 * A - matches any alpha in test string
	 * ! - matches any numeric or ' ' in test string
	 * Z - matches any numeric in test string
	 * ? - matches '-', '/' or ' '
	 * other char - matches any identical character in test string
	 * </pre>
	 * 
	 * @param accountNumber
	 *            account number to validate
	 * @param accountType
	 *            string populated with they type of policy
	 * @return true if the account number number is valid [otherwise false]
	 */
	private static boolean checkTypeNewman(String accountNumber,
			Reference<String> accountType) throws Exception {
		boolean returnValue = false;
		boolean nonZeroDigitFound = false;

		// already checked by the fact that it passed regex, but just for
		// completeness
		returnValue = accountNumber.substring(0, 3).compareToIgnoreCase("NBS") == 0;
		for (final Character testCharacter : accountNumber.substring(5)
				.toCharArray()) {
			returnValue = returnValue & Character.isDigit(testCharacter);
			if (returnValue) {
				// check if it's a non zero digit
				if (testCharacter != '0') {
					nonZeroDigitFound = true;
				}
			}
		}

		// any non zero account number is valid, so as long as at least one
		// character isn't zero we're happy.
		returnValue &= nonZeroDigitFound;
		if (returnValue) {
			// check that the product indicator part of the number is a valid
			// one
			// lifestyle product currently all there is, but coded to make it
			// easier
			// to add other products later.
			final String __dummyScrutVar10 = accountNumber.substring(3, 5);
			if (__dummyScrutVar10.equals("LS")) {
				accountType.set(Constants.NEWMAN);
			} else {
				accountType.set(Constants.OTHER);
				// currently no valid type other than lifestyle.
				returnValue = false;
			}
		}

		return returnValue;
	}

	/***
	 * Validate as much as we can that a number is a valid
	 * 
	 * <pre>
	 * Progen number, and determine the account type.
	 * 95nnnnnnnnn-01  - Legacy Home Insurance
	 * 95nnnnnnnnn-08	- Legacy Payment Protection 
	 * nnnnnnnnn-n1	- Home Insurance 
	 * nnnnnnnnn-n1	- Payment Protection Insurance
	 * </pre>
	 * 
	 * @param accountNumber
	 *            account number to validate
	 * @param TypeAcc
	 *            account type (home or payment protection)
	 * @return <pre>
	 * 	25 if new style progen 
	 * 	26 if legacy policy
	 * "H", "G" or "O" in TypeAcc to indicate if home insurance payment protection or something we don't know about.
	 * </pre>
	 */
	private static int checkTypeProgen(String accountNumber,
			Reference<String> TypeAcc) throws Exception {
		Integer retVal = 0;
		Integer accType = 0;
		// first check the length of the passed in number (legacy or not)
		switch (accountNumber.length()) {
		case 14:
			// legacy...
			retVal = 26;
			// use last 2 digits to determine account type...
			accType = (accountNumber.charAt(12) - '0') * 10
					+ accountNumber.charAt(13) - '0';
			switch (accType) {
			case 1:
				TypeAcc.set(Constants.PROHOME);
				break;
			case 8:
				TypeAcc.set(Constants.PROPAY);
				break;
			default:
				// don't know what type it is...
				TypeAcc.set(Constants.PROOTHER);
				break;

			}
			break;
		case 12:
			// non legacy...
			retVal = 25;
			// use last 2 digits to determine account type...
			accType = (accountNumber.charAt(10) - '0') * 10
					+ accountNumber.charAt(11) - '0';
			switch (accType) {
			case 1:
				TypeAcc.set(Constants.PROHOME);
				break;
			case 3:
				TypeAcc.set(Constants.PROPAY);
				break;
			default:
				// don't know what type it is...
				TypeAcc.set(Constants.PROOTHER);
				break;

			}
			break;
		default:
			// invalid...
			retVal = 0;
			accType = 0;
			TypeAcc.set("");
			break;

		}
		return retVal;
	}

	/***
	 * <pre>
	 * Validate and determine the account type for Swindon account number.  
	 * 
	 * Strips prefix and calls CheckTypeSwindonNoPfx to do the work.
	 * 
	 * 		9999/XX999999(9) -	Investment	XX = 1-79
	 * 						Flexaccount	XX = 60-64 (length is 9)
	 * 						Mortgage	XX = 80-99
	 * </pre>
	 * 
	 * @param accountNumber
	 *            account number to validate formated as a single string
	 * @param TypeAcc
	 *            account type (mortgage, flexaccount or investment)
	 * @return <pre>
	 * 		-3 if acc number invalid
	 * 	6 (Swindon 4/8)
	 * 	7 (Swindon 4/9)
	 * </pre>
	 * 
	 */
	private static int checkTypeSwindon(String accountNumber,
			Reference<String> TypeAcc) throws Exception {
		// Get the account no length
		final String AccNoPfx = accountNumber.substring(5);
		int iRetVal;

		iRetVal = checkTypeSwindonNoPfx(AccNoPfx, TypeAcc);

		if (19 == iRetVal || 20 == iRetVal) {
			return iRetVal - 13;
		} else {
			return iRetVal;
		}
	}

	/***
	 * Validate and determine the account type for Swindon account number and
	 * apply check digit validation.
	 * 
	 * @param accNum
	 *            account number to validate formated as a single string
	 * @param typeAcc
	 *            account type (MORT/BUSINV/BOND/INV/FLEX)
	 * @return account style return code 19 for Swindon 8 and 20 for Swindon 9
	 */
	private static Integer checkTypeSwindonNoPfx(String accNum,
			Reference<String> typeAcc) throws Exception {
		// Get the account no length
		Boolean includeMod10Check = false;
		Integer accountTypeRange = 0;
		Integer accountStyleReturnCode = 0;
		// invalid account
		// get range digits
		if (accNum.length() > 1) {
			accountTypeRange = (int) NumberFormat.getInstance()
					.parse(accNum.substring(0, 2)).doubleValue();
		}

		// check account based on number of digits. the account type can vary
		// depending
		// if 8 digit or 9 digit account. The CDV checks are also potentially
		// different (mod10)
		if (accNum.length() == 9) {
			if (accountTypeRange >= 10 && accountTypeRange <= 39) {
				typeAcc.set(Constants.INVESTMENT);
				includeMod10Check = true;
			} else if (accountTypeRange >= 40 && accountTypeRange <= 49
					|| accountTypeRange >= 65 && accountTypeRange <= 74
					|| accountTypeRange >= 76 && accountTypeRange <= 79) {
				typeAcc.set(Constants.INVESTMENT);
			} else if (accountTypeRange >= 50 && accountTypeRange <= 59) {
				typeAcc.set(Constants.BOND);
				includeMod10Check = true;
			} else if (accountTypeRange >= 60 && accountTypeRange <= 64) {
				typeAcc.set(Constants.FLEXACCOUNT);
				includeMod10Check = true;
			} else if (accountTypeRange == 75) {
				typeAcc.set(Constants.BUSINVEST);
			} else if (accountTypeRange >= 80 && accountTypeRange <= 99) {
				typeAcc.set(Constants.MORTGAGE);
			} else {
				accountStyleReturnCode = -3;
			}
			// invalid range
			if (accountStyleReturnCode != -3) {
				// Verify check digit modulus 11 first
				if (checkSwindon9(accNum)) {
					accountStyleReturnCode = 20;
				} else if (includeMod10Check) {
					if (checkSwindon9Mod10(accNum)) {
						// failed so try mod10
						accountStyleReturnCode = 20;
					}

				}

			}

		} else if (accNum.length() == 8) {
			// 8 digit
			if (accountTypeRange >= 10 && accountTypeRange <= 56
					|| accountTypeRange >= 60 && accountTypeRange <= 74
					|| accountTypeRange >= 76 && accountTypeRange <= 79) {
				typeAcc.set(Constants.INVESTMENT);
			} else if (accountTypeRange >= 57 && accountTypeRange <= 59) {
				typeAcc.set(Constants.BOND);
			} else if (accountTypeRange == 75) {
				typeAcc.set(Constants.BUSINVEST);
			} else if (accountTypeRange >= 80 && accountTypeRange <= 99) {
				typeAcc.set(Constants.MORTGAGE);
			} else {
				accountStyleReturnCode = -3;
			}
			// invalid range
			if (accountStyleReturnCode != -3) {
				// Verify check digit modulus 11 only
				if (checkSwindon8(accNum)) {
					accountStyleReturnCode = 19;
				}
			}
		}

		return accountStyleReturnCode;
	}

	/***
	 * Validate UCB mortgage account number
	 * 
	 * @param accountNumber
	 *            account number to validate
	 * @return true is the account number is valid [otherwise false]
	 */
	private static boolean checkUCB(String accountNumber) throws Exception {
		final int[] iMulVal = { 7, 6, 5, 4, 3, 2 };
		final int iLen = iMulVal.length;
		int n;
		int iTotal;
		final boolean newFormat = accountNumber.length() == 11;
		boolean valid = false;
		if (newFormat) {
			// check starts with '8888'
			if (!accountNumber.substring(0, 4).equalsIgnoreCase("8888")) {
				return false;
			}

			// Calculate quantity
			iTotal = 0;
			for (n = 0; n < iLen; n++) {
				iTotal += (accountNumber.charAt(4 + n) - '0') * iMulVal[n];
			}

			// Check check-digit - 11 minus modulus 11 with 11 taken as zero
			valid = accountNumber.charAt(10) - '0' == (11 - iTotal % 11) % 11;
		} else {
			// Check initial letter is B
			if (accountNumber.charAt(0) != 'B') {
				return false;
			}

			// Calculate quantity
			iTotal = 0;
			for (n = 0; n < iLen; n++) {
				iTotal += (accountNumber.charAt(5 + n) - '0') * iMulVal[n];
			}
			// Check check-digit - 11 minus modulus 11 with 11 taken as zero
			valid = accountNumber.charAt(11) - '0' == (11 - iTotal % 11) % 11;
		}
		return valid;
	}

	/***
	 * checks input account number using the Verhoeff check digit routine. This
	 * operations assumes the final digit of a passed in number to be the check
	 * digit. (currently used by MSO only)
	 * 
	 * @param accountNumber
	 *            account number to validate
	 * @return true if the account number number is valid [otherwise false]
	 */
	private static boolean checkVerhoeff(String accountNumber) throws Exception {
		// we need two arrays to validate these numbers, one of multipliers and
		// one of permutations.
		// In order to calculate a check digit we'd also need another one of
		// multiplicative inverses,
		// but for an already calculated number all we need is the first two,
		// and then the algorithm
		// should eventually give us a check value of one.
		final int[][] multipliers = new int[10][10];
		final int[][] permutations = new int[8][10];

		// populate Multipliers table
		multipliers[0] = new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
		multipliers[1] = new int[] { 1, 2, 3, 4, 0, 6, 7, 8, 9, 5 };
		multipliers[2] = new int[] { 2, 3, 4, 0, 1, 7, 8, 9, 5, 6 };
		multipliers[3] = new int[] { 3, 4, 0, 1, 2, 8, 9, 5, 6, 7 };
		multipliers[4] = new int[] { 4, 0, 1, 2, 3, 9, 5, 6, 7, 8 };
		multipliers[5] = new int[] { 5, 9, 8, 7, 6, 0, 4, 3, 2, 1 };
		multipliers[6] = new int[] { 6, 5, 9, 8, 7, 1, 0, 4, 3, 2 };
		multipliers[7] = new int[] { 7, 6, 5, 9, 8, 2, 1, 0, 4, 3 };
		multipliers[8] = new int[] { 8, 7, 6, 5, 9, 3, 2, 1, 0, 4 };
		multipliers[9] = new int[] { 9, 8, 7, 6, 5, 4, 3, 2, 1, 0 };

		// populate permutations table
		permutations[0] = new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
		permutations[1] = new int[] { 1, 5, 7, 6, 2, 8, 3, 0, 9, 4 };
		permutations[2] = new int[] { 5, 8, 0, 3, 7, 9, 6, 1, 4, 2 };
		permutations[3] = new int[] { 8, 9, 1, 6, 0, 4, 3, 5, 2, 7 };
		permutations[4] = new int[] { 9, 4, 5, 3, 1, 2, 6, 8, 7, 0 };
		permutations[5] = new int[] { 4, 2, 8, 6, 5, 7, 3, 9, 0, 1 };
		permutations[6] = new int[] { 2, 7, 9, 3, 8, 0, 6, 4, 1, 5 };
		permutations[7] = new int[] { 7, 0, 4, 6, 9, 1, 3, 2, 5, 8 };

		// first we reverse the order of the remaining input digits
		final int[] reversedInput = new int[accountNumber.length()];
		for (int count = 0; count < accountNumber.length(); count++) {
			reversedInput[count] = Integer.parseInt(Character
					.toString(accountNumber.charAt(accountNumber.length()
							- (count + 1))));
		}

		// now we use a combination of the digit position, the digit it's self
		// and the
		// output from the previous lookup to find the next value from the two
		// arrays
		// as we step through the reversed account
		int lookupValue = 0;
		for (int digitPosition = 0; digitPosition < reversedInput.length; digitPosition++) {
			// we need mod 8 of digit position for permuations[a as we only have
			// 8 permutations rows
			lookupValue = multipliers[lookupValue][permutations[digitPosition % 8][reversedInput[digitPosition]]];
		}

		return lookupValue == 0;
	}

	/***
	 * Test whether one string matches the format of another.
	 * 
	 * <pre>
	 * Valid characters in format string are:
	 * 9 - matches any numeric in test string
	 * A - matches any alpha in test string
	 * ! - matches any numeric or ' ' in test string
	 * Z - matches any numeric in test string
	 * ? - matches '-', '/' or ' '
	 * other char - matches any identical character in test string
	 * </pre>
	 * 
	 * @param Format
	 *            format string to match
	 * @param Test
	 *            string to test
	 * @return true if test string matches format string [otherwise false]
	 * @throws Exception
	 */
	private static Boolean matchFormat(String Format, String Test)
			throws Exception {
		final int iLen = Format.length();
		int i;
		final String digitString = "0123456789";
		final String alphaStringLower = "abcdefghijklmnopqrstuvwxyz";
		final String alphaStringUpper = alphaStringLower.toUpperCase(Locale.UK);
		// Strings must be the same length to match
		if (iLen != Test.length()) {
			return false;
		}

		for (i = 0; i < iLen; i++) {
			// Test each character in test string
			// Test for numeric in test string
			if (Format.charAt(i) == '9' || Format.charAt(i) == 'Z') {
				if (digitString.indexOf(Test.charAt(i)) == -1) {
					return false;
				}

			} else // not found
			if (Format.charAt(i) == 'A') {
				// Test for alpha in test string
				if (alphaStringLower.indexOf(Test.charAt(i)) == -1
						&& alphaStringUpper.indexOf(Test.charAt(i)) == -1) {
					return false;
				}

			} else // not found
			if (Format.charAt(i) == 'X') {
				// test for alpha numeric in test string
				if (alphaStringLower.indexOf(Test.charAt(i)) == -1
						&& alphaStringUpper.indexOf(Test.charAt(i)) == -1
						&& digitString.indexOf(Test.charAt(i)) == -1) {
					return false;
				}

			} else // not found
			if (Format.charAt(i) == 'W') {
				// test for alpha numeric or separator in test string
				if (alphaStringLower.indexOf(Test.charAt(i)) == -1
						&& alphaStringUpper.indexOf(Test.charAt(i)) == -1
						&& digitString.indexOf(Test.charAt(i)) == -1
						&& Test.charAt(i) != ' ' && Test.charAt(i) != '-'
						&& Test.charAt(i) != '/') {
					return false;
				}

			} else // not found
			if (Format.charAt(i) == '?') {
				// Test for ' ' or '-' or '/' in test string
				if (Test.charAt(i) != ' ' && Test.charAt(i) != '-'
						&& Test.charAt(i) != '/') {
					return false;
				}

			} else if (Format.charAt(i) == '!') {
				// Test for ' ' or numeric in test string
				if (Test.charAt(i) != ' '
						&& digitString.indexOf(Test.charAt(i)) == -1) {
					return false;
				}

			} else if (Test.charAt(i) != Format.charAt(i)) {
				return false;
			}

		}
		return true;
	}

	/***
	 * <pre>
	 * Re-format a string replacing delimiting character with the preferred character and stripping zeroes and spaces where specified.  The following mask chars are actioned:
	 * 	' ' - removes space at specific position
	 * 	! - removes space or zero and sets return to true
	 * 	Z - removes zero and sets return to true
	 * 	? - replaces char with preferred delimiter
	 * 	A - converts char to upper case	 *
	 * </pre>
	 * 
	 * @param Format
	 *            format string
	 * @param Test
	 *            string to reformat
	 * @param cDelim
	 *            delimiting character
	 * @return <pre>
	 * 		true if number was shortened by removing zeroes [otherwise false]. 
	 * 		Indicates Swindon 4/8 number keyed as 4/9
	 * 
	 * 		Side effects:	Test is re-formated
	 * </pre>
	 * 
	 */
	private static boolean reFormat(String Format, Reference<String> Test,
			char cDelim) throws Exception {
		final int iLen = Format.length();
		int iIn;
		int iOut;
		boolean bShortened = false;
		String StoreChar;
		// Strings must be the same length
		if (iLen != Test.get().length()) {
			return false;
		}

		StringBuilder stringManipulator;

		for (iIn = iOut = 0; iIn < iLen; iIn++) {
			// Reformat each character
			if (Format.charAt(iIn) == '?') {
				stringManipulator = new StringBuilder(Test.get());
				stringManipulator.deleteCharAt(iOut);
				stringManipulator.insert(iOut, cDelim);
				Test.set(stringManipulator.toString());
				iOut++;
			} else // Remove zeroes and spaces
			// and set shortened flag
			if ((Format.charAt(iIn) == 'Z' || Format.charAt(iIn) == '!')
					&& (Test.get().charAt(iIn) == '0' || Test.get().charAt(iIn) == ' ')) {
				// get rid of space, don't increase iOut so we know it's been
				// shortened.
				stringManipulator = new StringBuilder(Test.get());
				stringManipulator.deleteCharAt(iOut);
				Test.set(stringManipulator.toString());
			} else if (Format.charAt(iIn) == 'A' || Format.charAt(iIn) == 'X'
					|| Format.charAt(iIn) == 'W') {
				// Convert lower to upper case

				StoreChar = String.valueOf(Test.get().charAt(iOut));
				stringManipulator = new StringBuilder(Test.get());
				stringManipulator.setCharAt(iOut,
						StoreChar.toUpperCase(Locale.UK).toCharArray()[0]);
				Test.set(stringManipulator.toString());

				// final StringBuilder newTest = new StringBuilder(Test.get());

				// Test.set(newTest.substring(0, iOut - 1)
				// + StoreChar.toString().toUpperCase(Locale.UK)
				// + newTest.substring(iOut + 1));
				iOut++;
			} else if (Format.charAt(iIn) == ' ') {
				// Ignore blanks
				// get rid of space, don't increase iOut so we know it's been
				// shortened.
				stringManipulator = new StringBuilder(Test.get());
				stringManipulator.deleteCharAt(iOut);
				Test.set(stringManipulator.toString());

			} else {
				iOut++;
			}
		}
		// if we shortened the string, make sure we don't leave stuff in
		// there...
		if (iOut < iIn) {
			bShortened = true;
		}

		return bShortened;
	}

	/***
	 * <pre>
	 * Determine the account type for VISA issued PANs.  this also
	 * includes numbers which would previously have been CardMSPans
	 * but which are now being issued by VisaPlus.  If it is not
	 * ours it is other.  
	 * 
	 * Determined from ISO number
	 * </pre>
	 * 
	 * @param AccNum
	 *            account number to validate
	 * @param TypeAcc
	 *            account type (flexaccount or other)
	 * @return <pre>
	 * 	10 + either 'F' or 'C' if Nationwide Visa or (ex?)CardMS pan
	 * 	10 + 'O' for non nationwide beginning with 4
	 * 	0 + 'O' for non nationwide not beginning with 4
	 * </pre>
	 */
	private static int type16PAN(String AccNum, Reference<String> TypeAcc)
			throws Exception {
		String szIsoCode;
		int panResult = 0;
		// not a valid Visa Pan, possibly a valid 'something else' one.
		szIsoCode = AccNum.substring(0, 6);
		// modified in 1.33.15 to remove the selectWorldMasterCardIsoCode case
		// from the Creditcard
		// bit - consuming systems not ready for mastercard and have issues with
		// it.
		final String __dummyScrutVar2 = szIsoCode;
		if (__dummyScrutVar2.equals(Constants.ClassicIsoCode)
				|| __dummyScrutVar2.equals(Constants.GoldIsoCode)
				|| __dummyScrutVar2.equals(Constants.AvaIsoCode)
				|| __dummyScrutVar2.equals(Constants.StandardIsoCode)
				|| __dummyScrutVar2.equals(Constants.ChargecardIsoCode)) {
			TypeAcc.set(Constants.CREDITCARD);
		} else if (__dummyScrutVar2.equals(Constants.VisaIsoCode)
				|| __dummyScrutVar2.equals(Constants.GoldDebitVisaIsoCode)
				|| __dummyScrutVar2.equals(Constants.CashCardIsoCode)
				|| __dummyScrutVar2.equals(Constants.VoyagerCardIsoCode1)
				|| __dummyScrutVar2.equals(Constants.VoyagerCardIsoCode2)
				|| __dummyScrutVar2.equals(Constants.VoyagerCardIsoCode3)
				|| __dummyScrutVar2.equals(Constants.VoyagerCardIsoCode5)
				|| __dummyScrutVar2.equals(Constants.VoyagerCardIsoCode6)
				|| __dummyScrutVar2.equals(Constants.VoyagerCardIsoCode7)
				|| __dummyScrutVar2.equals(Constants.FlexOnex2xIsoCode)
				|| __dummyScrutVar2.equals(Constants.FlexOneCashCardIsoCode)
				|| __dummyScrutVar2.equals(Constants.FlexStudent)
				|| __dummyScrutVar2.equals(Constants.FlexGraduate)
				|| __dummyScrutVar2.equals(Constants.BusinessDebitVisaCard)) {
			TypeAcc.set(Constants.FLEXACCOUNT);
		} else if (__dummyScrutVar2.equals(Constants.CashBuilderIsoCode)
				|| __dummyScrutVar2.equals(Constants.InvestDirectIsoCode)
				|| __dummyScrutVar2.equals(Constants.SmartIsoCode)
				|| __dummyScrutVar2.equals(Constants.RegularSavingsIsoCode)
				|| __dummyScrutVar2.equals(Constants.SavingsOneAdultIsoCode)
				|| __dummyScrutVar2.equals(Constants.SavingsOneChildIsoCode)
				|| __dummyScrutVar2.equals(Constants.RegionalBrandATMIsoCode) 
				|| __dummyScrutVar2.equals(Constants.SOSAPSavingsIsoCode)) {
			TypeAcc.set(Constants.INVESTMENT);
		} else {
			// Not Nationwide ISO, it is another
			TypeAcc.set(Constants.OTHER);
		}
		// Amended in 1.33.14.0 to support PANs beginning with 5 - these are
		// issued by Mastercard rather than VISA
		// amended again in 1.33.15 to remove this validation - consuming
		// systems not ready for
		// mastercard and have issues with it passing validation. should really
		// return something
		// other than 10 for mastercard anyway as that means visa.
		if (szIsoCode.charAt(0) == '4') {
			// Valid VISA PAN style number or (ex?)CardMS number
			panResult = 10;
		}

		return panResult;
	}

	/***
	 * <pre>
	 * Used to be TypeCardMSPan - Determine the account type for 
	 * 18 digit numbers that pass Pan validation.  This pretty much
	 * means 'traditional' CARDMS PANs.  If it is not ours it is other.
	 * 
	 * Determined from ISO number
	 * </pre>
	 * 
	 * @param AccNum
	 *            account number to validate
	 * @param TypeAcc
	 *            account type (flexaccount or other)
	 * @return <pre>
	 * 	11 if Nationwide
	 * 	-3 if other
	 * </pre>
	 * 
	 */
	private static Integer type18PAN(String AccNum, Reference<String> TypeAcc)
			throws Exception {

		String szIsoCode;
		Integer panResult = -3;

		// invalid number
		szIsoCode = AccNum.substring(0, 6);
		if (szIsoCode.equals(Constants.InvestIsoCode)) {
			TypeAcc.set(Constants.INVESTMENT);
			panResult = 11;
		} else if (szIsoCode.equals(Constants.FlexCGCIsoCode)
				|| szIsoCode.equals(Constants.FlexATMIsoCode)) {
			TypeAcc.set(Constants.FLEXACCOUNT);
			panResult = 11;
		} else {
			// Not Nationwide ISO, it is an 'other'
			TypeAcc.set(Constants.OTHER);
		}
		return panResult;
	}

	/*
	 * <pre> Determine the account type for Bexhill account number.
	 * XX-999999-99999 Investment XX = 1-11, 14-19 Mortgage XX = 12-13 </pre>
	 * 
	 * @param AccNum account number to validate
	 * 
	 * @param TypeAcc account type (mortgage or investment)
	 * 
	 * @return <pre> 1 if valid Bexhill -3 if other </pre>
	 */
	private static int typeBexhill(String AccNum, Reference<String> TypeAcc)
			throws Exception {
		// Obtain the account type field
		final Integer iType = (AccNum.charAt(0) - '0') * 10 + AccNum.charAt(1)
				- '0';
		if (iType >= 1 && iType <= 11 || iType >= 14 && iType <= 19) {
			TypeAcc.set(Constants.INVESTMENT);
		} else if (iType == 12 || iType == 13) {
			TypeAcc.set(Constants.MORTGAGE);
		} else {
			return -3;
		}
		return 1;
	}

	/***
	 * Determine the account type for Maidenhead account number
	 * 
	 * <pre>
	 * 	999999-9XX -	Investment	XX = 10-49
	 * 					Mortgage	XX = 50-65
	 * </pre>
	 * 
	 * @param accountNumber
	 *            account number to validate
	 * @param TypeAcc
	 *            account type (mortgage or investment)
	 * @return 3 if valid Maidenhead [otherwise -3]
	 */
	private static int typeMHead(String accountNumber, Reference<String> TypeAcc) {
		// Obtain the account type field
		final int iType = (accountNumber.charAt(8) - '0') * 10
				+ accountNumber.charAt(9) - '0';

		if (iType >= 10 && iType <= 49) {
			TypeAcc.set(Constants.INVESTMENT);
		} else if (iType >= 50 && iType <= 65) {
			TypeAcc.set(Constants.MORTGAGE);
		} else {
			return -3; // Invalid characters in account type
		}

		return 3; // Valid Maidenhead style number
	}

	private static int typeNorth(String AccNum, Reference<String> TypeAcc)
			throws Exception {
		switch (AccNum.charAt(4)) {
		case '1':
			TypeAcc.set(Constants.INVESTMENT);
			break;
		case '4':

			if (AccNum.substring(0, 3).equalsIgnoreCase("099")
					|| AccNum.substring(0, 3).equalsIgnoreCase("060")) {
				TypeAcc.set(Constants.MORTGAGE);
			} else {
				return -3;
			}
			break;
		default:
			return -3;

		}
		return 2;
	}

	/*
	 * <pre> Determine the account type for sort code account number. Do not
	 * allow flexaccounts for new DLL entry point: must be key by PAN or Swindon
	 * number </pre>
	 * 
	 * @param AccNum account number to validate
	 * 
	 * @param TypeAcc account type (mortgage or investment)
	 * 
	 * @param bDisallowFlex Flexaccounts SCANs not normally used but allowed for
	 * old entry point
	 * 
	 * @return <pre> 8 if valid SCAN, -3 if Flexaccount or non-Nationwide
	 * 
	 * Note:
	 * 
	 * Allow CO-OP SCAN return 8, SCAN and type F, FLEXACCOUNT Check sort code
	 * is within Nationwide's range Search table for specific Sort Codes Default
	 * any others to type I, legacy investment </pre>
	 */
	private static int typeSCAN(String AccNum, Reference<String> TypeAcc)
			throws Exception {
		Integer i;
		String szSortCode;
		szSortCode = AccNum.substring(0, 6);
		// Allow CO-OP SCAN for old call type
		if (szSortCode.compareToIgnoreCase(Constants.COOP_SCAN) == 0) {
			TypeAcc.set(Constants.FLEXACCOUNT);
			return 8;
		}

		if (Long.parseLong(szSortCode) < Long.parseLong(Constants.MinSortCode)
				|| Long.parseLong(szSortCode) > Long
						.parseLong(Constants.MaxSortCode)) {
			return -3;
		}

		for (i = 0; i < Constants.nCountSortCodeType; i++) {
			// Not a Nationwide sort code
			if (szSortCode.compareTo(Constants.stabSortCodeType[i]
					.getSortCode()) == 0) {
				// Get the account type from the table
				TypeAcc.set(Constants.stabSortCodeType[i].getAccType());
				if (TypeAcc.get().compareToIgnoreCase(Constants.BUSINVEST) == 0
						|| TypeAcc.get().compareToIgnoreCase(
								Constants.PORTINVEST) == 0) {
					if (AccNum.charAt(7) != '7') {
						return -3;
					}

				} else // range = 70 - 79
				// Account number outside Business/portfolio range
				if (TypeAcc.get().compareToIgnoreCase(Constants.FLEXACCOUNT) == 0
						|| TypeAcc.get().compareToIgnoreCase(Constants.NEWFLEX) == 0) {
					if (AccNum.charAt(5) != '6' || AccNum.charAt(7) > '4') {
						return -3;
					} else {
						// FlexAccount scan outside 60 - 64 account code range
						// if this is a renum account sort code and it's in the
						// flex range,
						// we want to return it as a flex.
						TypeAcc.set(Constants.FLEXACCOUNT);
					}
				} else if (TypeAcc.get().compareToIgnoreCase(Constants.NEWCARD) == 0) {
					if (AccNum.charAt(5) == '0') {
						TypeAcc.set(Constants.INVESTMENT);
					}

				}

				return 8;
			}

		}

		// flex acc scans now allowed - works for mod 11 and mod 10
		// matches something in table
		// Not in table, assume it is an investment
		TypeAcc.set(Constants.INVESTMENT);
		return 8;
	}

	/**
	 * Validate account number string. Returns: account format type correctly
	 * formatted account number the type of account.
	 * 
	 * @param NewEntryPoint
	 *            indicates application numbers, CashLink PANs and Flex SCANs
	 *            are not allowed
	 * @param AccountNumber
	 *            account number to validate
	 * @param FormatAcct
	 *            formatted account number
	 * @param AccountType
	 *            account type
	 * 
	 *            <pre>
	 * 	"A" - Personal Loan Approval In Principal number
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
	 * @return <pre>
	 * 	0 - account number invalid
	 *  	-1 - too few digits in account number
	 * -2 - too many digits in account number
	 * -3 - number not allowed for this entry point
	 * or invalid digits in account type part
	 * or account number type:
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
	 * 55 - TSYS Id
	 * </pre>
	 * @throws Exception
	 */
	private static int Validate(boolean NewEntryPoint, String AccountNumber,
			Reference<String> FormatAcct, Reference<String> AccountType)
			throws Exception {

		final ReferenceInt iFmtType = new ReferenceInt();
		int iAccLen = 0;

		// Make the account number upper case
		AccountNumber = AccountNumber.toUpperCase();

		// Get the account number length
		iAccLen = AccountNumber.length();

		FormatAcct.set("");
		AccountType.set(Constants.OTHER);

		final Pattern patternStakeholderPension = Pattern.compile(
				Constants.stakeholderPensionRegex, Pattern.CASE_INSENSITIVE);
		final Pattern patternMellonClientNo = Pattern.compile(
				Constants._szMellonClientNo, Pattern.CASE_INSENSITIVE);
		final Pattern patternNewmanPolicyRegex = Pattern.compile(
				Constants.newmanPolicyRegex, Pattern.CASE_INSENSITIVE);
		final Pattern patternNirvanaCommercialRegex = Pattern.compile(
				Constants.nirvanaCommercialRegex, Pattern.CASE_INSENSITIVE);
		final Pattern patternNirvanaTravelRegex = Pattern.compile(
				Constants.nirvanaTravelRegex, Pattern.CASE_INSENSITIVE);

		final Pattern patternRBSIGeneralInsuranceRegex = Pattern.compile(
				Constants.RBSIGeneralInsuranceRegex, Pattern.CASE_INSENSITIVE);
		final Pattern patternMellonClientCutDownRegex = Pattern.compile(
				Constants.mellonClientCutDownRegex, Pattern.CASE_INSENSITIVE);
		final Pattern patternNirvanaMotorRegex = Pattern.compile(
				Constants.nirvanaMotorRegex, Pattern.CASE_INSENSITIVE);
		final Pattern patternConiferSavingsRegex = Pattern.compile(
				Constants.coniferSavingsRegex, Pattern.CASE_INSENSITIVE);
		final Pattern patternSwindon48NoSepRex = Pattern.compile(
				Constants.swindon48NoSepRex, Pattern.CASE_INSENSITIVE);
		final Pattern patternDerbyshireBuildingSocietyRegex = Pattern.compile(
				Constants.derbyshireBuildingSocietyRegex,
				Pattern.CASE_INSENSITIVE);
		final Pattern patternCheshireBuildingSocietyRegex = Pattern.compile(
				Constants.cheshireBuildingSocietyRegex,
				Pattern.CASE_INSENSITIVE);
		final Pattern patternDunfermlineBuildingSocietyRegex = Pattern.compile(
				Constants.dunfermlineBuildingSocietyRegex,
				Pattern.CASE_INSENSITIVE);		
		final Pattern patternTsysIdRegex = Pattern.compile(
				Constants.tsysIdRegex,
				Pattern.CASE_INSENSITIVE);

		switch (iAccLen) {

		case 0:
		case 1:
			iFmtType.value = -1;
			break;
		case 2:
			// Portman 1 character
			if (iFmtType.value == 0
					&& matchFormat(Constants._szPortman1, AccountNumber)) {
				FormatAcct.set(AccountNumber);
				// reformat to make sure the leading P is a capital one..
				reFormat(Constants._szPortman1, FormatAcct, ' ');

				// other than ensuring it's the right length and starts with
				// a 'p' there's nothing to check - just set the fmt type and
				// type ack
				if (checkPORT(FormatAcct.get())) {
					iFmtType.value = 40;
					AccountType.set(Constants.PORTMAN);
				}

			}

			break;

		case 3:
			// Portman 2 character
			if (iFmtType.value == 0
					&& matchFormat(Constants._szPortman2, AccountNumber)) {
				FormatAcct.set(AccountNumber);
				// reformat to make sure the leading P is a capital one..
				reFormat(Constants._szPortman2, FormatAcct, ' ');

				// other than ensuring it's the right length and starts with
				// a 'p' there's nothing to check - just set the fmt type and
				// type ack
				if (checkPORT(FormatAcct.get())) {
					iFmtType.value = 40;
					AccountType.set(Constants.PORTMAN);
				}

			}

			break;

		case 4:
			// Portman 3 character
			if (iFmtType.value == 0
					&& matchFormat(Constants._szPortman3, AccountNumber)) {
				FormatAcct.set(AccountNumber);
				// reformat to make sure the leading P is a capital one..
				reFormat(Constants._szPortman3, FormatAcct, ' ');

				// other than ensuring it's the right length and starts with
				// a 'p' there's nothing to check - just set the fmt type and
				// type ack
				if (checkPORT(FormatAcct.get())) {
					iFmtType.value = 40;
					AccountType.set(Constants.PORTMAN);
				}

			}

			if (iFmtType.value < 1
					&& patternNirvanaTravelRegex.matcher(AccountNumber)
							.matches()) {
				// just want to make sure everything's upper case for the
				// formatted version
				FormatAcct.set(AccountNumber);
				iFmtType.value = 44;
				AccountType.set(Constants.NIRVANA);
			}

			break;

		case 5:
			// could be MPOS no or Portman 4 character
			if (matchFormat(Constants._szMPOS4, AccountNumber)) {
				FormatAcct.set(AccountNumber);
				// no separator char needed - just reformat to make sure any
				// letter is upper case.
				reFormat(Constants._szMPOS4, FormatAcct, ' ');

				if (checkMPOS(FormatAcct.get())) {
					iFmtType.value = 27;
					AccountType.set(Constants.MPOS);
				}

			}

			if (iFmtType.value == 0
					&& matchFormat(Constants._szPortman4, AccountNumber)) {
				FormatAcct.set(AccountNumber);
				// reformat to make sure the leading P is a capital one..
				reFormat(Constants._szPortman4, FormatAcct, ' ');

				// other than ensuring it's the right length and starts with
				// a 'p' there's nothing to check - just set the fmt type and
				// type ack
				if (checkPORT(FormatAcct.get())) {
					iFmtType.value = 40;
					AccountType.set(Constants.PORTMAN);
				}

			}

			if (iFmtType.value < 1
					&& patternNirvanaTravelRegex.matcher(AccountNumber)
							.matches()) {
				// just want to make sure everything's upper case for the
				// formatted version
				FormatAcct.set(AccountNumber);
				iFmtType.value = 44;
				AccountType.set(Constants.NIRVANA);
			}

			break;

		case 6:
			// could be MPOS no or Portman 5 character
			if (matchFormat(Constants._szMPOS5, AccountNumber)) {
				FormatAcct.set(AccountNumber);
				// no separator char needed - just reformat to make sure any
				// letter is upper case.
				reFormat(Constants._szMPOS5, FormatAcct, ' ');

				if (checkMPOS(FormatAcct.get())) {
					iFmtType.value = 28;
					AccountType.set(Constants.MPOS);
				}

			}

			if (iFmtType.value == 0
					&& matchFormat(Constants._szPortman5, AccountNumber)) {
				FormatAcct.set(AccountNumber);
				// reformat to make sure the leading P is a capital one..
				reFormat(Constants._szPortman5, FormatAcct, ' ');

				// other than ensuring it's the right length and starts with
				// a 'p' there's nothing to check - just set the fmt type and
				// type ack
				if (checkPORT(FormatAcct.get())) {
					iFmtType.value = 40;
					AccountType.set(Constants.PORTMAN);
				}

			}

			if (iFmtType.value < 1
					&& patternNirvanaTravelRegex.matcher(AccountNumber)
							.matches()) {
				// just want to make sure everything's upper case for the
				// formatted version
				FormatAcct.set(AccountNumber);
				iFmtType.value = 44;
				AccountType.set(Constants.NIRVANA);
			}

			break;

		case 7:
			// could be MPOS no
			// or IPOS 6 digit no
			// or capsil without segment number
			// or Portman 6 character
			if (matchFormat(Constants._szMPOS6, AccountNumber)) {
				FormatAcct.set(AccountNumber);
				// no separator char needed - just reformat to make sure any
				// letter is upper case.
				reFormat(Constants._szMPOS6, FormatAcct, ' ');

				if (checkMPOS(FormatAcct.get())) {
					iFmtType.value = 24;
					AccountType.set(Constants.MPOS);
				}

			}

			if (iFmtType.value == 0
					&& matchFormat(Constants._szIPOS6, AccountNumber)) {
				// try IPOS6
				FormatAcct.set(AccountNumber);
				// no separator char needed - just reformat to make sure any
				// letter is upper case.
				reFormat(Constants._szIPOS6, FormatAcct, ' ');
				if (checkIPOS(FormatAcct.get())) {
					iFmtType.value = 34;
					AccountType.set(Constants.IPOS);
				}

			}

			if (iFmtType.value == 0
					&& matchFormat(Constants._szCapsil2, AccountNumber)) {
				// try Capsil without segment number
				FormatAcct.set(AccountNumber);

				// reformat to make sure any alpha char is upper case
				reFormat(Constants._szCapsil2, FormatAcct, ' ');
				if (checkCapsil(FormatAcct.get())) {
					// add 000 to end of number - segment 000 exists for all
					// capsil accounts
					FormatAcct.set(FormatAcct.get() + "000");
					iFmtType.value = 39;
					AccountType.set(Constants.CAPSIL);
				}

			}

			if (iFmtType.value == 0
					&& matchFormat(Constants._szPortman6, AccountNumber)) {
				FormatAcct.set(AccountNumber);
				// reformat to make sure the leading P is a capital one..
				reFormat(Constants._szPortman6, FormatAcct, ' ');

				// other than ensuring it's the right length and starts with
				// a 'p' there's nothing to check - just set the fmt type and
				// type ack
				if (checkPORT(FormatAcct.get())) {
					iFmtType.value = 40;
					AccountType.set(Constants.PORTMAN);
				}

			}

			if (iFmtType.value < 1
					&& patternNirvanaTravelRegex.matcher(AccountNumber)
							.matches()) {
				// just want to make sure everything's upper case for the
				// formatted version
				FormatAcct.set(AccountNumber);
				iFmtType.value = 44;
				AccountType.set(Constants.NIRVANA);
			}

			break;

		case 8:
			// NBS 8 digit no spaces, no prefix
			// or MPOS 7 digit no (no checksum for this - check last)
			// or IPOS 7 digit no (no checksum, just leading I).
			// or Portman 7 digit (no checksum, just leading P).
			// or Capsil2 post L&G ownership
			if (matchFormat(Constants._szSwindon8NoPfx, AccountNumber)) {
				FormatAcct.set(AccountNumber);
				// if this matched, no chars to reformat so we won't bother
				// calling it!
				// do check digit and determine type
				iFmtType.value = checkTypeSwindonNoPfx(FormatAcct.get(),
						AccountType);
			} else if (matchFormat(Constants._szMPOS7, AccountNumber)) {
				// try MPOS
				FormatAcct.set(AccountNumber);
				// no separator char needed - just reformat to make sure any
				// letter is upper case.
				reFormat(Constants._szMPOS7, FormatAcct, ' ');

				if (checkMPOS(FormatAcct.get())) {
					iFmtType.value = 23;
					AccountType.set(Constants.MPOS);
				}

			}

			if (iFmtType.value == 0
					&& matchFormat(Constants._szIPOS7, AccountNumber)) {
				// try IPOS7
				FormatAcct.set(AccountNumber);
				// no separator char needed - just reformat to make sure any
				// letter is upper case.
				reFormat(Constants._szIPOS7, FormatAcct, ' ');
				if (checkIPOS(FormatAcct.get())) {
					iFmtType.value = 35;
					AccountType.set(Constants.IPOS);
				}

			}

			if (iFmtType.value == 0
					&& matchFormat(Constants._szPortman7, AccountNumber)) {
				FormatAcct.set(AccountNumber);
				// reformat to make sure the leading P is a capital one..
				reFormat(Constants._szPortman7, FormatAcct, ' ');
				// other than ensuring it's the right length and starts with
				// a 'p' there's nothing to check - just set the fmt type and
				// type ack
				if (checkPORT(FormatAcct.get())) {
					iFmtType.value = 40;
					AccountType.set(Constants.PORTMAN);
				}

			}

			if (iFmtType.value == 0
					&& matchFormat(Constants._szCapsil2_LG, AccountNumber)) {

				if (AccountNumber.substring(0, 1).equals("L")) {
					// try Capsil without segment number - strip leading 'L'
					FormatAcct.set(AccountNumber.substring(1));
					// reformat to make sure any alpha char is upper case
					reFormat(Constants._szCapsil2, FormatAcct, ' ');
					if (checkCapsil(FormatAcct.get())) {
						// add 000 to end of number - segment 000 exists for all
						// capsil accounts
						FormatAcct.set(FormatAcct.get() + "000");
						iFmtType.value = 39;
						AccountType.set(Constants.CAPSIL);
					}

				}

			}

			if (iFmtType.value < 1
					&& patternNirvanaTravelRegex.matcher(AccountNumber)
							.matches()) {
				// just want to make sure everything's upper case for the
				// formatted version
				FormatAcct.set(AccountNumber);
				iFmtType.value = 44;
				AccountType.set(Constants.NIRVANA);
			}

			break;

		case 9:
			// Maidenhead without hyphen or Nationwide Trust
			// or NBS 9 digit no spaces, no prefix
			// or MPOS 8 digit no (no checksum for this - check last)
			// or IPOS 8 digit no (no checksum, starts with I)
			// or Portman 8 character (no checksum, starts with P)
			// or Nirvana travel insurance or RBSI general household insurance
			Boolean bMatched;
			String szFormattedAccountNumber;
			bMatched = false;
			if (matchFormat(Constants._szSwindon9NoPfx, AccountNumber)) {
				// all 3 match same format
				szFormattedAccountNumber = AccountNumber;
				final Reference<String> tempFormattedAcctNumber = new Reference<String>(
						AccountNumber);
				reFormat(Constants._szSwindon9NoPfx, tempFormattedAcctNumber,
						Constants._cSwindon);

				// do check digit and determine type
				iFmtType.value = checkTypeSwindonNoPfx(
						tempFormattedAcctNumber.get(), AccountType);

				// formating will remove a single leading zero if it's there,
				// resulting in
				// the possible creation of a valid 8 digit nbs acc no.
				if (20 == iFmtType.value || 19 == iFmtType.value) {
					bMatched = true;
					FormatAcct.set(tempFormattedAcctNumber.get());
				}

			}

			if (!bMatched
					&& matchFormat(Constants._szMaidenhead2, AccountNumber)) {
				// check maidenthead2
				// Add hyphen to account number
				szFormattedAccountNumber = AccountNumber.substring(0, 6);
				szFormattedAccountNumber += "-";
				szFormattedAccountNumber += AccountNumber.substring(6);
				if (checkMHead(szFormattedAccountNumber)) {
					// Verify check digit
					iFmtType.value = typeMHead(szFormattedAccountNumber,
							AccountType);
				}

				if (3 == iFmtType.value) {
					bMatched = true;
					FormatAcct.set(szFormattedAccountNumber);
				}

			}

			if (!bMatched
					&& matchFormat(Constants._szMaidenhead2, AccountNumber)) {
				// could be TrustLoan (same format as maidenhead2)
				szFormattedAccountNumber = AccountNumber;
				if (checkTrust(szFormattedAccountNumber)) {
					// Verify check digit
					iFmtType.value = 16;
					bMatched = true;
					AccountType.set(Constants.TRUSTLOAN);
					FormatAcct.set(szFormattedAccountNumber);
				}

			}

			if (!bMatched && matchFormat(Constants._szMPOS8, AccountNumber)) {
				// not matched any format so far - try MPOS
				FormatAcct.set(AccountNumber);
				// no separator char needed - just reformat to make sure any
				// letter is upper case.
				reFormat(Constants._szMPOS8, FormatAcct, ' ');

				if (checkMPOS(FormatAcct.get())) {
					iFmtType.value = 22;
					bMatched = true;
					AccountType.set(Constants.MPOS);
				}

			}

			if (!bMatched && matchFormat(Constants._szIPOS8, AccountNumber)) {
				// try IPOS8
				FormatAcct.set(AccountNumber);
				// reformat to make sure the I is a capital one..
				reFormat(Constants._szIPOS8, FormatAcct, ' ');

				if (checkIPOS(FormatAcct.get())) {
					iFmtType.value = 36;
					AccountType.set(Constants.IPOS);
				}

			}

			if (iFmtType.value < 1
					&& matchFormat(Constants._szPortman8, AccountNumber)) {
				FormatAcct.set(AccountNumber);
				// reformat to make sure the leading P is a capital one..
				reFormat(Constants._szPortman8, FormatAcct, ' ');

				// other than ensuring it's the right length and starts with
				// a 'p' there's nothing to check - just set the fmt type and
				// type ack
				if (checkPORT(FormatAcct.get())) {
					iFmtType.value = 40;
					AccountType.set(Constants.PORTMAN);
				}

			}

			if (iFmtType.value < 1
					&& patternNirvanaTravelRegex.matcher(AccountNumber)
							.matches()) {
				// just want to make sure everything's upper case for the
				// formatted version
				FormatAcct.set(AccountNumber);
				iFmtType.value = 44;
				AccountType.set(Constants.NIRVANA);
			}

			// RBSI General Insurance
			if (iFmtType.value == 0
					&& patternRBSIGeneralInsuranceRegex.matcher(AccountNumber)
							.matches()) {
				// no separator char needed - just reformat to make sure any
				// letter is upper case.
				FormatAcct.set(AccountNumber.toUpperCase());
				if (checkRBSIGeneralInsuranceHousehold(FormatAcct.get())) {
					iFmtType.value = 51;
					AccountType.set(Constants.RBSIGENINS);
					bMatched = true;
				}
			}

			break;

		case 10:
			// Maidenhead or External Ref No
			// or 8 digit NBS with spaces, no Prefix
			// or MSO 9 digit no (verhoeff checksum)
			// or MPOS 9 digit no (no checksum for this)
			// or IPOS 9 digit (no checksum, just a leading I).
			// or Portman 9 character (no checksum, just a leading P)
			if (matchFormat(Constants._szSwindon8SNoPfx, AccountNumber)) {
				FormatAcct.set(AccountNumber);
				reFormat(Constants._szSwindon8SNoPfx, FormatAcct,
						Constants._cSwindon);

				// do check digit and determine type
				iFmtType.value = checkTypeSwindonNoPfx(FormatAcct.get(),
						AccountType);
			} else {
				if (matchFormat(Constants._szMaidenhead, AccountNumber)) {
					FormatAcct.set(AccountNumber);

					reFormat(Constants._szMaidenhead, FormatAcct,
							Constants._cMaidenhead);
					if (checkMHead(FormatAcct.get())) {
						// Verify check digit
						iFmtType.value = typeMHead(FormatAcct.get(),
								AccountType);
					}

				} else if (NewEntryPoint) {
					if (matchFormat(Constants._szExtRefNo, AccountNumber)) {
						FormatAcct.set(FormatAcct.get() + AccountNumber);
						if (checkExtRefNo(FormatAcct.get())) {
							// Verify check digit
							iFmtType.value = 14;
							AccountType.set(Constants.OTHER);
						}

					}

				}

			}
			// Cannot tell the type so make other
			if (matchFormat(Constants._szCapsil, AccountNumber)) {
				FormatAcct.set(AccountNumber);
				// no separator char needed - just reformat to make sure any
				// letter is upper case.
				reFormat(Constants._szCapsil, FormatAcct, ' ');

				if (checkCapsil(FormatAcct.get())) {
					iFmtType.value = 31;
					AccountType.set(Constants.CAPSIL);
				}

			}

			if (matchFormat(Constants._szPersonalLoanAIP, AccountNumber)) {
				FormatAcct.set(AccountNumber);
				// no separator char needed - just reformat to make sure any
				// letter is upper case.
				reFormat(Constants._szPersonalLoanAIP, FormatAcct, ' ');
				if (checkPersonalLoanAIP(FormatAcct.get())) {
					// Verify check digit
					iFmtType.value = 30;
					AccountType.set(Constants.PERSLOANAIP);
				}

			}

			if (iFmtType.value == 0) {
				// not matched any format so far - try MPOS
				if (matchFormat(Constants._szMPOS9, AccountNumber)) {
					FormatAcct.set(AccountNumber);
					// no separator char needed - just reformat to make sure any
					// letter is upper case.
					reFormat(Constants._szMPOS9, FormatAcct, ' ');

					// MSO and MPOS look the same, except that
					// mso pass verhoeff checksum and fall into the
					// range 550001000 to 9999999999 with an 'M' in front
					if (checkMSO(AccountNumber)) {
						iFmtType.value = 49;
						// mso
						AccountType.set(Constants.MSO);
					} else if (checkMPOS(FormatAcct.get())) {
						iFmtType.value = 21;
						AccountType.set(Constants.MPOS);
					}

				}

			}

			if (iFmtType.value == 0) {
				// still not found a match, try IPOS 9 digit
				if (matchFormat(Constants._szIPOS9, AccountNumber)) {
					FormatAcct.set(AccountNumber);
					// reformat to make sure the I is a capital one..
					reFormat(Constants._szIPOS9, FormatAcct, ' ');

					if (checkIPOS(FormatAcct.get())) {
						iFmtType.value = 37;
						AccountType.set(Constants.IPOS);
					}

				}

			}

			if (iFmtType.value == 0
					&& matchFormat(Constants._szPortman9, AccountNumber)) {
				FormatAcct.set(AccountNumber);
				// reformat to make sure the leading P is a capital one..
				reFormat(Constants._szPortman9, FormatAcct, ' ');

				// other than ensuring it's the right length and starts with
				// a 'p' there's nothing to check - just set the fmt type and
				// type ack
				if (checkPORT(FormatAcct.get())) {
					iFmtType.value = 40;
					AccountType.set(Constants.PORTMAN);
				}

			}

			if (iFmtType.value == 0
					&& patternMellonClientCutDownRegex.matcher(AccountNumber)
							.matches()) {
				// no check digit - rely on format.
				FormatAcct.set(AccountNumber);
				iFmtType.value = 43;
				AccountType.set(Constants.EQUITY);
			}

			if (iFmtType.value < 1
					&& patternNirvanaTravelRegex.matcher(AccountNumber)
							.matches()) {
				// just want to make sure everything's upper case for the
				// formatted version
				FormatAcct.set(AccountNumber);
				iFmtType.value = 44;
				AccountType.set(Constants.NIRVANA);
			}

			break;

		case 11:
			// Mortgage Application Number or
			// 9 digit NBS with spaces, no Prefix
			// or IPOS 10 digit
			// or Mellon type A or Mellon Type B.
			// or portman 10 character or
			// Legal and General number
			// May 2010 could be a conifer number with trailing spaces dropped
			// or Derbyshire 8 digits
			if (matchFormat(Constants._szSwindon9SNoPfx, AccountNumber)) {
				FormatAcct.set(AccountNumber);
				reFormat(Constants._szSwindon9SNoPfx, FormatAcct,
						Constants._cSwindon);
				// do check digit and determine type
				iFmtType.value = checkTypeSwindonNoPfx(FormatAcct.get(),
						AccountType);
			} else if (matchFormat(Constants._szMtgApplNo, AccountNumber)) {
				if (NewEntryPoint) {
					FormatAcct.set(AccountNumber);
					if (checkTypeApplNo(FormatAcct.get(), AccountType)) {
						// Verify check digit
						iFmtType.value = 12;
					} else {
						if (checkUCB(FormatAcct.get())) {
							// same, but ucb has to start 8888
							iFmtType.value = 18;
							AccountType.set(Constants.UCB);
						}

					}
				} else {
					// for compatibility return error to old entry point
					iFmtType.value = -3;
				}
			}

			if (iFmtType.value == 0
					&& matchFormat(Constants._szPortman10, AccountNumber)) {
				FormatAcct.set(AccountNumber);
				// reformat to make sure the leading P is a capital one..
				reFormat(Constants._szPortman10, FormatAcct, ' ');

				// other than ensuring it's the right length and starts with
				// a 'p' there's nothing to check - just set the fmt type and
				// type ack
				if (checkPORT(FormatAcct.get())) {
					iFmtType.value = 40;
					AccountType.set(Constants.PORTMAN);
				}

			}

			if (iFmtType.value == 0
					&& (matchFormat(Constants._szLAndGProtection, AccountNumber) || matchFormat(
							Constants._szLAndGGEB, AccountNumber))) {
				FormatAcct.set(AccountNumber);
				// reformat to make sure any alpha characters are capitals..
				reFormat(Constants._szLAndGGEB, FormatAcct, ' ');

				// other than ensuring it's the right length and starts with
				// a 'p' there's nothing to check - just set the fmt type and
				// type ack
				if (checkLAndG(FormatAcct.get())) {
					iFmtType.value = 41;
					AccountType.set(Constants.LANDG);
				}

			}

			if (iFmtType.value == 0
					&& matchFormat(Constants._szIPOS10, AccountNumber)) {
				FormatAcct.set(AccountNumber);
				// no separator char needed - just reformat to make sure any
				// letter is upper case.
				reFormat(Constants._szIPOS10, FormatAcct, ' ');

				if (checkIPOS(FormatAcct.get())) {
					iFmtType.value = 38;
					AccountType.set(Constants.IPOS);
				}

			}

			// not matched any format so far - try MellonB
			if (iFmtType.value == 0
					&& matchFormat(Constants._szMellonB, AccountNumber)) {
				FormatAcct.set(AccountNumber);
				// no separator char needed - just reformat to make sure any
				// letter is upper case.
				reFormat(Constants._szMellonB, FormatAcct, Constants._cMellon);

				iFmtType.value = 33;
				AccountType.set(Constants.MELLON);
			}

			// still no, try Capsil post L&G ownership
			if (iFmtType.value == 0
					&& matchFormat(Constants._szCapsil_LG, AccountNumber)) {

				if (AccountNumber.substring(0, 1).equals("L")) {
					// get rid of the leading 'L'
					FormatAcct.set(AccountNumber.substring(1));
					// no separator char needed - just reformat to make sure any
					// letter is upper case.
					reFormat(Constants._szCapsil, FormatAcct, ' ');

					if (checkCapsil(FormatAcct.get())) {
						iFmtType.value = 31;
						AccountType.set(Constants.CAPSIL);
					}

				}

			}

			if (iFmtType.value < 1
					&& patternNirvanaTravelRegex.matcher(AccountNumber)
							.matches()) {
				// just want to make sure everything's upper case for the
				// formatted version
				FormatAcct.set(AccountNumber);
				iFmtType.value = 44;
				AccountType.set(Constants.NIRVANA);
			}

			// try LV motor insurance
			if (iFmtType.value == 0
					&& patternNirvanaMotorRegex.matcher(AccountNumber)
							.matches()) {
				// there's a check digit for this which LV will tell us about
				// one day, allegedly.
				// for now, just go by the format mask.
				FormatAcct.set(AccountNumber);
				iFmtType.value = 46;
				AccountType.set(Constants.NIRVANA);
			}

			// not matched any format so far - try MellonA
			if (iFmtType.value == 0
					&& matchFormat(Constants._szMellonA, AccountNumber)) {
				FormatAcct.set(AccountNumber);
				// no separator char needed - just reformat to make sure any
				// letter is upper case.
				reFormat(Constants._szMellonA, FormatAcct, ' ');

				iFmtType.value = 32;
				AccountType.set(Constants.MELLON);
			}

			if (iFmtType.value < 1
					&& patternConiferSavingsRegex.matcher(AccountNumber)
							.matches()) {
				// conifer (third party) type number - no check digit.
				// just want to make sure everything's upper case for the
				// formatted version
				FormatAcct.set(AccountNumber);
				iFmtType.value = 48;
				AccountType.set(Constants.CONIFERSAVINGS);
			}

			if (iFmtType.value < 1
					&& patternDerbyshireBuildingSocietyRegex.matcher(
							AccountNumber).matches()) {
				// nothing to check the regex didn't just do
				// just want to make sure everything's upper case for the
				// formatted version
				FormatAcct.set(AccountNumber);
				iFmtType.value = 53;
				AccountType.set(Constants.REGBRAND);
			}

			break;

		case 12:
			// Swindon 3/8, progen none legacy, portman 11 digit..
			// or Mellon A or B with a leading 'L' (post L&G ownership)
			// dec 2008 added Swindon 4/8 without the separator.
			// may 2010 could be a conifer number with a single trailing space
			// dropped
			if (matchFormat(Constants._szSwindon38, AccountNumber)) {
				FormatAcct.set("0");
				// Make into 4/8
				FormatAcct.set(FormatAcct.get() + AccountNumber);
				reFormat(Constants._szSwindon48, FormatAcct,
						Constants._cSwindon);

				// Verify check digit and determine type
				iFmtType.value = checkTypeSwindon(FormatAcct.get(), AccountType);
			} else if (matchFormat(Constants._szPROGEN12, AccountNumber)) {
				// no formating to do, just put the AccountNumber in the
				// FormatAcct...
				FormatAcct.set(FormatAcct.get() + AccountNumber);

				// check all we can, and determine type
				iFmtType.value = checkTypeProgen(FormatAcct.get(), AccountType);
			}

			if (iFmtType.value < 1
					&& patternSwindon48NoSepRex.matcher(AccountNumber)
							.matches()) {
				// reformat to swindon 4/8 with separator char
				// FormatAcct.set(Pattern.Replace(AccountNumber,
				// Constants.swindon48NoSepRex,
				// Constants.swindon48NoSepFormatPattern,
				// Pattern.CASE_INSENSITIVE ));
				// Replaced the line above with the line below
				FormatAcct.set(AccountNumber.replaceAll(
						Constants.swindon48NoSepRex,
						Constants.swindon48NoSepFormatPattern));

				// Then call the exiting code to verify check digit and
				// determine type
				iFmtType.value = checkTypeSwindon(FormatAcct.get(), AccountType);
			}

			if (iFmtType.value < 1
					&& matchFormat(Constants._szPortman11, AccountNumber)) {
				FormatAcct.set(AccountNumber);
				// reformat to make sure the leading P is a capital one..
				reFormat(Constants._szPortman11, FormatAcct, ' ');

				// other than ensuring it's the right length and starts with
				// a 'p' there's nothing to check - just set the fmt type and
				// type ack
				if (checkPORT(FormatAcct.get())) {
					iFmtType.value = 40;
					AccountType.set(Constants.PORTMAN);
				}

			}

			if (iFmtType.value < 1
					&& matchFormat(Constants._szMellonB_LG, AccountNumber)) {
				if (AccountNumber.substring(0, 1).equals("L")) {
					// get rid of the leading 'L'
					FormatAcct.set(AccountNumber.substring(1));

					// no separator char needed - just reformat to make sure any
					// letter is upper case.
					reFormat(Constants._szMellonB, FormatAcct, ' ');

					iFmtType.value = 33;
					AccountType.set(Constants.MELLON);
				}

			}

			if (NewEntryPoint) {
				if (iFmtType.value < 1
						&& matchFormat(Constants._szUCBMortgageNo,
								AccountNumber)) {
					FormatAcct.set(AccountNumber);
					reFormat(Constants._szUCBMortgageNo, FormatAcct,
							Constants._cUCB);

					if (checkUCB(FormatAcct.get())) {
						// Verify check digit
						iFmtType.value = 18;
						AccountType.set(Constants.UCB);
					}

				}

			}

			if (iFmtType.value < 1
					&& patternNirvanaTravelRegex.matcher(AccountNumber)
							.matches()) {
				// just want to make sure everything's upper case for the
				// formatted version
				FormatAcct.set(AccountNumber);
				iFmtType.value = 44;
				AccountType.set(Constants.NIRVANA);
			}

			if (iFmtType.value < 1
					&& patternStakeholderPension.matcher(AccountNumber)
							.matches()) {
				// blobby type number - no check digit.
				// just want to make sure everything's upper case for the
				// formatted version
				FormatAcct.set(AccountNumber);
				iFmtType.value = 47;
				AccountType.set(Constants.STAKEHOLDERPENSION);
			}

			if (iFmtType.value < 1
					&& matchFormat(Constants._szMellonA_LG, AccountNumber)) {
				if (AccountNumber.substring(0, 1).equals("L")) {
					// get rid of the leading 'L'
					FormatAcct.set(AccountNumber.substring(1));
					// no separator char needed - just reformat to make sure any
					// letter is upper case.
					reFormat(Constants._szMellonA, FormatAcct, ' ');

					iFmtType.value = 32;
					AccountType.set(Constants.MELLON);
				}

			}

			if (iFmtType.value < 1
					&& patternConiferSavingsRegex.matcher(AccountNumber)
							.matches()) {
				// conifer (third party) type number - no check digit.
				// just want to make sure everything's upper case for the
				// formatted version
				FormatAcct.set(AccountNumber);
				iFmtType.value = 48;
				AccountType.set(Constants.CONIFERSAVINGS);
			}

			if (iFmtType.value < 1
					&& patternCheshireBuildingSocietyRegex.matcher(
							AccountNumber).matches()) {
				// nothing to check the regex didn't just do
				// just want to make sure everything's upper case for the
				// formatted version
				FormatAcct.set(AccountNumber);
				iFmtType.value = 52;
				AccountType.set(Constants.REGBRAND);
			}

			if (iFmtType.value < 1
					&& patternDerbyshireBuildingSocietyRegex.matcher(
							AccountNumber).matches()) {
				// nothing to check the regex didn't just do
				// just want to make sure everything's upper case for the
				// formatted version
				FormatAcct.set(AccountNumber);
				iFmtType.value = 53;
				AccountType.set(Constants.REGBRAND);
			}

			if (iFmtType.value < 1
					&& patternDunfermlineBuildingSocietyRegex.matcher(
							AccountNumber).matches()) {
				// nothing to check the regex didn't just do
				// just want to make sure everything's upper case for the
				// formatted version
				FormatAcct.set(AccountNumber);
				iFmtType.value = 54;
				AccountType.set(Constants.REGBRAND);
			}

			break;

		case 13:
			// Swindon 3/9 or Swindon 4/8 or Bexhill without hyphens
			// or VISA 13 digit or portman 12 character
			// 3/5/2002 - could be 4/9 without space or / - same format as
			// Bexhill.
			// 29/03/2010 - conifer third party savings account validation.
			// or cheshire 10 digit or Dunfermline 9 digit
			if (matchFormat(Constants._szSwindon39, AccountNumber)) {
				FormatAcct.set("0");

				// Make into 4/9
				FormatAcct.set(FormatAcct.get() + AccountNumber);

				// Number may be shortened if leading zero keyed
				reFormat(Constants._szSwindon49, FormatAcct,
						Constants._cSwindon);

				// Verify check digit and determine type
				iFmtType.value = checkTypeSwindon(FormatAcct.get(), AccountType);
			}

			if (iFmtType.value < 1
					&& matchFormat(Constants._szSwindon48, AccountNumber)) {
				FormatAcct.set(FormatAcct.get() + AccountNumber);
				reFormat(Constants._szSwindon48, FormatAcct,
						Constants._cSwindon);

				// Verify check digit and determine type
				iFmtType.value = checkTypeSwindon(FormatAcct.get(), AccountType);
			}

			if (iFmtType.value < 1
					&& matchFormat(Constants._szBexhill2, AccountNumber)) {
				// Could be Bexhill w/o hyphens or VISA 13 digit
				FormatAcct.set(AccountNumber.substring(0, 2));
				FormatAcct.set(FormatAcct.get() + "-");
				FormatAcct
						.set(FormatAcct.get() + AccountNumber.substring(2, 8));
				FormatAcct.set(FormatAcct.get() + "-");
				FormatAcct.set(FormatAcct.get() + AccountNumber.substring(8));
				if (checkBexhill(FormatAcct.get())) {
					// Verify check digit
					iFmtType.value = typeBexhill(FormatAcct.get(), FormatAcct);
				} else {
					iFmtType.value = -3;
				}
				// can pass CheckBexhill, fail TypeBexhill with this ret code
				if (iFmtType.value == -3) {
					// Not a valid Bexhill number
					// Could be either swindon 4/9 no space or 13 digit visa
					// Try 13 digit visa first as if it's a swindon 4/9 the
					// user can just enter it with the / if neccessary.
					FormatAcct.set(AccountNumber);
					if (matchFormat(Constants._szVISA_13, AccountNumber)) {
						// Try VISA 13 digit
						if (checkPAN(FormatAcct.get())) {
							// Verify check digit
							iFmtType.value = 9;
							AccountType.set(Constants.OTHER);
						} else {
							// try swindon 4/9...
							FormatAcct.set(AccountNumber.substring(0, 4));
							FormatAcct.set(FormatAcct.get() + "/");
							FormatAcct.set(FormatAcct.get()
									+ AccountNumber.substring(4));

							// Verify check digit and determine type
							iFmtType.value = checkTypeSwindon(FormatAcct.get(),
									AccountType);
						}
					}
				}
			}

			if (iFmtType.value < 1
					&& matchFormat(Constants._szPortman12, AccountNumber)) {
				FormatAcct.set(AccountNumber);
				// reformat to make sure the leading P is a capital one..
				reFormat(Constants._szPortman12, FormatAcct, ' ');

				// other than ensuring it's the right length and starts with
				// a 'p' there's nothing to check - just set the fmt type and
				// type ack
				if (checkPORT(FormatAcct.get())) {
					iFmtType.value = 40;
					AccountType.set(Constants.PORTMAN);
				}

			}

			if (iFmtType.value < 1
					&& patternStakeholderPension.matcher(AccountNumber)
							.matches()) {
				// Mellon / L&G stakeholder pension type number - no check
				// digit.
				// just want to make sure everything's upper case for the
				// formatted version
				FormatAcct.set(AccountNumber);
				iFmtType.value = 47;
				AccountType.set(Constants.STAKEHOLDERPENSION);
			}

			if (iFmtType.value < 1
					&& patternConiferSavingsRegex.matcher(AccountNumber)
							.matches()) {
				// conifer (third party) type number - no check digit.
				// just want to make sure everything's upper case for the
				// formatted version
				FormatAcct.set(AccountNumber);
				iFmtType.value = 48;
				AccountType.set(Constants.CONIFERSAVINGS);
			}

			if (iFmtType.value < 1
					&& patternCheshireBuildingSocietyRegex.matcher(
							AccountNumber).matches()) {
				// nothing to check the regex didn't just do
				// just want to make sure everything's upper case for the
				// formatted version
				FormatAcct.set(AccountNumber);
				iFmtType.value = 52;
				AccountType.set(Constants.REGBRAND);
			}

			if (iFmtType.value < 1
					&& patternDunfermlineBuildingSocietyRegex.matcher(
							AccountNumber).matches()) {
				// nothing to check the regex didn't just do
				// just want to make sure everything's upper case for the
				// formatted version
				FormatAcct.set(AccountNumber);
				iFmtType.value = 54;
				AccountType.set(Constants.REGBRAND);
			}

			if (iFmtType.value < 1
					&& patternTsysIdRegex.matcher(
							AccountNumber).matches()) {
				// nothing to check the regex didn't just do
				// just want to make sure everything's upper case for the
				// formatted version
				FormatAcct.set(AccountNumber);
				iFmtType.value = 55;
				AccountType.set(Constants.CREDITCARD);
			}
			
			break;

		case 14:
			// Swindon 4/9 or Northampton without hyphens or
			// SCAN without the space or Swindon 999/99 999 999
			// Error Suspense 9999/99999999E or Legacy progen number
			// or portman 13 character or Newman policy number or Cheshire 11
			// digits
			if (matchFormat(Constants._szSwindon49, AccountNumber)) {
				FormatAcct.set(AccountNumber);
				// Number may be shortened if leading zero keyed
				reFormat(Constants._szSwindon49, FormatAcct,
						Constants._cSwindon);

				// Verify check digit and determine type
				iFmtType.value = checkTypeSwindon(FormatAcct.get(), AccountType);
			}

			if (iFmtType.value < 1
					&& matchFormat(Constants._szSwindonSpace38, AccountNumber)) {
				FormatAcct.set("0");

				// Make into 4/8
				FormatAcct.set(FormatAcct.get() + AccountNumber);
				reFormat(Constants._szSwindonSpace48, FormatAcct,
						Constants._cSwindon);

				// Verify check digit and determine type
				iFmtType.value = checkTypeSwindon(FormatAcct.get(), AccountType);
			}

			// both SCAN no space and Northampton 2 are just 14 digits.
			if (iFmtType.value < 1
					&& matchFormat(Constants._szNorthampton2, AccountNumber)) {
				// Try a SCAN without the space
				FormatAcct.set("");
				// Clear field
				FormatAcct.set(AccountNumber.substring(0, 6));
				FormatAcct.set(FormatAcct.get() + " ");
				FormatAcct.set(FormatAcct.get()
						+ AccountNumber.substring(6, 14));
				if (checkSCAN(FormatAcct.get())) {
					// Verify check digit
					iFmtType.value = typeSCAN(FormatAcct.get(), AccountType);
				}

				if (iFmtType.value < 1) {
					// Not a scan - try Northampton
					FormatAcct.set("");
					// Clear field
					FormatAcct.set(AccountNumber.substring(0, 3));
					FormatAcct.set(FormatAcct.get() + "-");
					FormatAcct.set(FormatAcct.get()
							+ AccountNumber.substring(3, 4));
					FormatAcct.set(FormatAcct.get() + "-");
					FormatAcct.set(FormatAcct.get()
							+ AccountNumber.substring(4, 12));
					FormatAcct.set(FormatAcct.get() + "-");
					FormatAcct.set(FormatAcct.get()
							+ AccountNumber.substring(12));
					if (checkNorth(FormatAcct.get())) {
						// Verify check digit
						iFmtType.value = typeNorth(FormatAcct.get(),
								AccountType);
					}

				}

			}

			if (iFmtType.value < 1
					&& matchFormat(Constants._szPROGEN14, AccountNumber)) {
				// no formating to do, just put the AccNum in the FmtAcc...
				FormatAcct.set(AccountNumber);

				// check all we can, and determine type
				iFmtType.value = checkTypeProgen(FormatAcct.get(), AccountType);
			} else if (NewEntryPoint) {
				if (iFmtType.value < 1
						&& matchFormat(Constants._szErrorSuspense,
								AccountNumber)) {
					FormatAcct.set(FormatAcct.get() + AccountNumber);
					reFormat(Constants._szErrorSuspense, FormatAcct,
							Constants._cSwindon);
					if (checkErrorSus(FormatAcct.get())) {
						// Verify valid suspense no
						iFmtType.value = 15;
						AccountType.set(Constants.SUSPENSE);
					}
				}
			}

			if (iFmtType.value < 1
					&& matchFormat(Constants._szPortman13, AccountNumber)) {
				FormatAcct.set(AccountNumber);
				// reformat to make sure the leading P is a capital one..
				reFormat(Constants._szPortman13, FormatAcct, ' ');

				// other than ensuring it's the right length and starts with
				// a 'p' there's nothing to check - just set the fmt type and
				// type ack
				if (checkPORT(FormatAcct.get())) {
					iFmtType.value = 40;
					AccountType.set(Constants.PORTMAN);
				}
			}

			if (iFmtType.value < 1
					&& patternNirvanaCommercialRegex.matcher(AccountNumber)
							.matches()) {
				// just want to make sure everything's upper case for the
				// formatted version
				FormatAcct.set(AccountNumber);
				iFmtType.value = 45;
				AccountType.set(Constants.NIRVANA);
			}

			if (iFmtType.value < 1
					&& patternStakeholderPension.matcher(AccountNumber)
							.matches()) {
				// blobby type number - no check digit.
				// just want to make sure everything's upper case for the
				// formatted version
				FormatAcct.set(AccountNumber);
				iFmtType.value = 47;
				AccountType.set(Constants.STAKEHOLDERPENSION);
			}

			if (iFmtType.value < 1
					&& patternNewmanPolicyRegex.matcher(AccountNumber)
							.matches()) {
				// already made upper case when we entered this method - don't
				// need to here.
				// call check type to make sure it's got a valid policy type
				// identifier.
				if (checkTypeNewman(FormatAcct.get(), AccountType)) {
					iFmtType.value = 50;
				} else {
					// not a valid newman number, reset stuff.
					iFmtType.value = 0;
					FormatAcct.set("");
				}
			}

			if (iFmtType.value < 1
					&& patternCheshireBuildingSocietyRegex.matcher(
							AccountNumber).matches()) {
				// nothing to check the regex didn't just do
				// just want to make sure everything's upper case for the
				// formatted version
				FormatAcct.set(AccountNumber);
				iFmtType.value = 52;
				AccountType.set(Constants.REGBRAND);
			}

			//TFR - SAMM Defect#1046 - Support for Travel Insurance Acc no's (Ledger Location=86)
			if (iFmtType.value < 1
					&& matchFormat(Constants._szTrvlInsNo, AccountNumber)) {
				FormatAcct.set(AccountNumber);
				iFmtType.value = 57;
				AccountType.set(Constants.TRAVELINS);
			}
			
			break;

		case 15:
			// Bexhill or SCAN or Swindon 9999/99 999 999 or
			// Swindon 999/999 999 999 or Mellon client
			// or portman 14 character
			if (matchFormat(Constants._szBexhill, AccountNumber)) {
				FormatAcct.set(AccountNumber);
				reFormat(Constants._szBexhill, FormatAcct, Constants._cBexhill);
				if (checkBexhill(FormatAcct.get())) {
					// Verify check digit
					iFmtType.value = typeBexhill(FormatAcct.get(), AccountType);
				}
			}

			if (iFmtType.value < 1
					&& matchFormat(Constants._szSCAN, AccountNumber)) {
				FormatAcct.set(AccountNumber);
				reFormat(Constants._szSCAN, FormatAcct, Constants._cSCAN);

				if (checkSCAN(FormatAcct.get())) {
					// Verify check digit
					iFmtType.value = typeSCAN(FormatAcct.get(), AccountType);
				}

			}

			if (iFmtType.value < 1
					&& matchFormat(Constants._szSwindonSpace48, AccountNumber)) {
				FormatAcct.set(AccountNumber);
				reFormat(Constants._szSwindonSpace48, FormatAcct,
						Constants._cSwindon);

				// Verify check digit and determine type
				iFmtType.value = checkTypeSwindon(FormatAcct.get(), AccountType);
			}

			if (iFmtType.value < 1
					&& matchFormat(Constants._szSwindonSpace39, AccountNumber)) {
				// Make into 4/9
				FormatAcct.set("0" + AccountNumber);

				// Number may be shortened if leading zero keyed
				reFormat(Constants._szSwindonSpace49, FormatAcct,
						Constants._cSwindon);

				// Verify check digit and determine type
				iFmtType.value = checkTypeSwindon(FormatAcct.get(), AccountType);
			} else if (NewEntryPoint) {
				if (iFmtType.value < 1
						&& matchFormat(Constants._szMellonClientNo,
								AccountNumber)) {
					FormatAcct.set(AccountNumber);
					reFormat(Constants._szMellonClientNo, FormatAcct,
							Constants._cSwindon);
					iFmtType.value = 17;
					AccountType.set(Constants.EQUITY);

					// need to do nasty hack here if the number starts '69/'
					// to reformat to nnnaannnnn where the first 69/ is dropped
					// along with the final / and it's following digit
					if (patternMellonClientNo.matcher(FormatAcct.get())
							.matches()) {
						// reformat it to cut down version
						FormatAcct
								.set(Pattern
										.compile(Constants.mellonClientRegex)
										.matcher(FormatAcct.get())
										.replaceAll(
												Constants.mellonClientRegexFormatPattern));
						// change the fmttype to say it's a cut down style.
						iFmtType.value = 43;
					}

				}

			}

			if (iFmtType.value < 1
					&& matchFormat(Constants._szPortman14, AccountNumber)) {
				FormatAcct.set(AccountNumber);
				// reformat to make sure the leading P is a capital one..
				reFormat(Constants._szPortman14, FormatAcct, ' ');

				// other than ensuring it's the right length and starts with
				// a 'p' there's nothing to check - just set the fmt type and
				// type ack
				if (checkPORT(FormatAcct.get())) {
					iFmtType.value = 40;
					AccountType.set(Constants.PORTMAN);
				}

			}

			if (iFmtType.value < 1
					&& patternStakeholderPension.matcher(AccountNumber)
							.matches()) {
				// blobby type number - no check digit.
				// just want to make sure everything's upper case for the
				// formatted version
				FormatAcct.set(AccountNumber);
				iFmtType.value = 47;
				AccountType.set(Constants.STAKEHOLDERPENSION);
			}

			break;

		case 16: // VISA 16 digit or VISA 13 digit with spaces
			// Swindon 4/9 keyed with spacing e.g. 9999/999 999 999
			// or Mellon Client post L&G ownership
			// or portman 15 character
			if (matchFormat(Constants._szVISA_16, AccountNumber)) {
				FormatAcct.set(AccountNumber);
				if (checkPAN(FormatAcct.get())) // Verify check digit
				{
					iFmtType.value = type16PAN(FormatAcct.get(), AccountType);
				}
			}

			if (iFmtType.value < 1
					&& matchFormat(Constants._szSwindonSpace49, AccountNumber)) {
				FormatAcct.set(AccountNumber);

				// Number may be shortened if leading zero or space keyed
				reFormat(Constants._szSwindonSpace49, FormatAcct,
						Constants._cSwindon);

				// Verify check digit and determine type
				iFmtType.value = checkTypeSwindon(FormatAcct.get(), AccountType);
			}

			if (iFmtType.value < 1
					&& matchFormat(Constants._szVISA_Space13, AccountNumber)) {
				FormatAcct.set(AccountNumber);
				reFormat(Constants._szVISA_Space13, FormatAcct, Constants._cPAN);
				if (checkPAN(FormatAcct.get())) // Verify check digit
				{
					iFmtType.value = 9;
					AccountType.set(Constants.OTHER);
				}
			}

			if (iFmtType.value < 1
					&& matchFormat(Constants._szMellonClient_LG, AccountNumber)) {
				if (AccountNumber.substring(0, 1).equalsIgnoreCase("L")) {
					// get rid of the leading 'L'
					FormatAcct.set(AccountNumber.substring(1));

					// then just do regular Mellon client stuff
					reFormat(Constants._szMellonClientNo, FormatAcct,
							Constants._cSwindon);
					iFmtType.value = 17;
					AccountType.set(Constants.EQUITY);
				}
			}

			if (iFmtType.value < 1
					&& matchFormat(Constants._szPortman15, AccountNumber)) {
				FormatAcct.set(AccountNumber);

				// reformat to make sure the leading P is a capital one..
				reFormat(Constants._szPortman15, FormatAcct, ' ');

				// other than ensuring it's the right length and starts with
				// a 'p' there's nothing to check - just set the fmt type and
				// type ack
				if (checkPORT(FormatAcct.get())) {
					iFmtType.value = 40;
					AccountType.set(Constants.PORTMAN);
				}
			}

			/*
			 * if ((iFmtType < 1) && ((Regex.Match(AccountNumber,
			 * stakeholderPensionRegex, RegexOptions.IgnoreCase).Success))) {
			 * //blobby type number - no check digit. // just want to make sure
			 * everything's upper case for the formatted version
			 * FormatAcct.set(AccountNumber); iFmtType.value = 47;
			 * AccountType.set(Constants.STAKEHOLDERPENSION); }
			 */

			break;

		case 17:
			// Northhampton or SCAN with hyphens
			// or portman 16 character
			if (matchFormat(Constants._szSCAN_Hyphen, AccountNumber)) {
				FormatAcct.set(AccountNumber);
				// if a hypen has been used as a separator char this will go
				// bang when we strip them
				FormatAcct
						.set(Pattern
								.compile(
										"(\\d{2})[ -]{0,1}(\\d{2})[ -]{0,1}(\\d{2})[/ -]{0,1}(\\d{8})")
								.matcher(FormatAcct.get())
								.replaceAll("$1$2$3 $4"));

				if (checkSCAN(FormatAcct.get())) {
					// Verify check digit
					iFmtType.value = typeSCAN(FormatAcct.get(), AccountType);
				}
			}

			if (iFmtType.value < 1
					&& matchFormat(Constants._szNorthampton, AccountNumber)) {
				FormatAcct.set(AccountNumber);
				reFormat(Constants._szNorthampton, FormatAcct,
						Constants._cNorthampton);
				if (checkNorth(FormatAcct.get())) {
					// Verify check digit
					iFmtType.value = typeNorth(FormatAcct.get(), AccountType);
				}
			}

			if (iFmtType.value < 1
					&& matchFormat(Constants._szPortman16, AccountNumber)) {
				FormatAcct.set(AccountNumber);
				// reformat to make sure the leading P is a capital one..
				reFormat(Constants._szPortman16, FormatAcct, ' ');

				// other than ensuring it's the right length and starts with
				// a 'p' there's nothing to check - just set the fmt type and
				// type ack
				if (checkPORT(FormatAcct.get())) {
					iFmtType.value = 40;
					AccountType.set(Constants.PORTMAN);
				}
			}

			if (iFmtType.value < 1
					&& patternStakeholderPension.matcher(AccountNumber)
							.matches()) {
				// blobby type number - no check digit.
				// just want to make sure everything's upper case for the
				// formatted version
				FormatAcct.set(AccountNumber);
				iFmtType.value = 47;
				AccountType.set(Constants.STAKEHOLDERPENSION);
			}

			break;

		case 18:
			// Cardms PAN or portman 17 character
			if (matchFormat(Constants._szCardmsPAN, AccountNumber)) {
				FormatAcct.set(AccountNumber);
				if (checkPAN(FormatAcct.get())) {
					// Verify check digit
					iFmtType.value = type18PAN(FormatAcct.get(), AccountType);
				}
			}

			if (iFmtType.value < 1
					&& matchFormat(Constants._szPortman17, AccountNumber)) {
				FormatAcct.set(AccountNumber);
				// reformat to make sure the leading P is a capital one..
				reFormat(Constants._szPortman17, FormatAcct, ' ');

				// other than ensuring it's the right length and starts with
				// a 'p' there's nothing to check - just set the fmt type and
				// type ack
				if (checkPORT(FormatAcct.get())) {
					iFmtType.value = 40;
					AccountType.set(Constants.PORTMAN);
				}

			}

			if (iFmtType.value < 1
					&& patternStakeholderPension.matcher(AccountNumber)
							.matches()) {
				// blobby type number - no check digit.
				// just want to make sure everything's upper case for the
				// formatted version
				FormatAcct.set(AccountNumber);
				iFmtType.value = 47;
				AccountType.set(Constants.STAKEHOLDERPENSION);
			}

			break;

		case 19:
			// VISA 16 digit PAN with spaces or CashLink PAN
			// or portman 18 character
			if (matchFormat(Constants._szVISA_Space16, AccountNumber)) {
				FormatAcct.set(AccountNumber);
				reFormat(Constants._szVISA_Space16, FormatAcct, Constants._cPAN);

				if (checkPAN(FormatAcct.get())) {
					// Verify check digit
					iFmtType.value = type16PAN(FormatAcct.get(), AccountType);
				}

			} else if (NewEntryPoint
					&& matchFormat(Constants._szCashLinkPAN, AccountNumber)
					&& AccountNumber.charAt(10) <= '4') {
				// Only 60-64 numbers
				// no need to format - already in correct style
				FormatAcct.set(AccountNumber.substring(0, 19));
				// do the check
				checkCashLinkPAN(FormatAcct.get(), iFmtType, AccountType);
			}

			if (iFmtType.value < 1
					&& matchFormat(Constants._szPortman18, AccountNumber)) {
				FormatAcct.set(AccountNumber);
				// reformat to make sure the leading P is a capital one..
				reFormat(Constants._szPortman18, FormatAcct, ' ');

				// other than ensuring it's the right length and starts with
				// a 'p' there's nothing to check - just set the fmt type and
				// type ack
				if (checkPORT(FormatAcct.get())) {
					iFmtType.value = 40;
					AccountType.set(Constants.PORTMAN);
				}

			}

			if (iFmtType.value < 1
					&& patternStakeholderPension.matcher(AccountNumber)
							.matches()) {
				// blobby type number - no check digit.
				// just want to make sure everything's upper case for the
				// formatted version
				FormatAcct.set(AccountNumber);
				iFmtType.value = 47;
				AccountType.set(Constants.STAKEHOLDERPENSION);
			}

			break;

		case 20:
			// CashLink PAN with hyphen or portman 19 character
			if (NewEntryPoint
					&& matchFormat(Constants._szCashLinkPAN_Hy, AccountNumber)
					&& AccountNumber.charAt(10) <= '4') {
				// Only 60-64 numbers
				// Format the CashLinkPAN
				FormatAcct.set(AccountNumber.substring(0, 18));
				FormatAcct.set(FormatAcct.get()
						+ AccountNumber.substring(19, 20));

				// and do the check
				checkCashLinkPAN(FormatAcct.get(), iFmtType, AccountType);
			} else {
				// for compatibility return error to old entry point
				iFmtType.value = -3;
			}

			if (iFmtType.value < 1
					&& matchFormat(Constants._szPortman19, AccountNumber)) {
				FormatAcct.set(AccountNumber);
				// reformat to make sure the leading P is a capital one..
				reFormat(Constants._szPortman19, FormatAcct, ' ');

				// other than ensuring it's the right length and starts with
				// a 'p' there's nothing to check - just set the fmt type and
				// type ack
				if (checkPORT(FormatAcct.get())) {
					iFmtType.value = 40;
					AccountType.set(Constants.PORTMAN);
				}

			}
			break;
		case 22: {
			// CashLink PAN with 3 hyphens/spaces

			if (NewEntryPoint) {
				if (matchFormat(Constants._szCashLinkPAN_Hys, AccountNumber)
						&& AccountNumber.charAt(12) <= '4') {
					String t = AccountNumber.substring(0, 6);

					t += AccountNumber.substring(7, 3);
					t += AccountNumber.substring(11, 9);
					t += AccountNumber.substring(21, 1);

					FormatAcct.set(t);

					// and do the check
					checkCashLinkPAN(FormatAcct.get(), iFmtType, AccountType);
				}
			} else {
				iFmtType.value = 3;
			}
		}
			break;

		default: {
			// Too many digits in account number
			iFmtType.value = -2;
		}
		}

		if (iFmtType.value < 1) {
			// invalid no.
			AccountType.set(Constants.OTHER);
			FormatAcct.set("");
		}

		return iFmtType.value;
	}

	/*
	 * <pre> function to derive the sort code needed to make a 'new' Swindon
	 * style number pass validation. </pre>
	 * 
	 * @param AccNum account number to derive a sort code for
	 * 
	 * @param FmtAcc formatted sort code / account number
	 * 
	 * @return <pre> -2 to 5 -2 - account number is not 8 or 9 digits long -1 -
	 * account number is a valid ‘old’ Swindon style account number 0 - account
	 * number not a valid Swindon style account number 1 - valid ‘new’ style
	 * Flex account number 2 - valid ‘new’ style Investment account number 3 -
	 * valid ‘new’ style Bond number - unused 4 - valid ‘new’ style Business
	 * Investor account number - unused Note:
	 * 
	 * if AccNum is a valid new style account number, FmtAcc is populated with
	 * the sort code / account number format </pre>
	 */
	public static DeriveNewAccountSortCodeResult DeriveNewAccountSortCode(
			String AccNum) {

		final DeriveNewAccountSortCodeResult result = new DeriveNewAccountSortCodeResult(
				AccNum);

		try {
			final Reference<String> fmtAcc = new Reference<String>("");
			// value meaning it doesn't pass regex
			Integer returnValue = -3;

			final Reference<String> TypeAcc = new Reference<String>("");
			String OldCashBuilder = "";
			double accountNumberValue = 0;

			boolean scanValidated = false;

			final Pattern testExpression = Pattern.compile("^\\d{9}$",
					Pattern.CASE_INSENSITIVE);

			// first check the number passed in is 9 digits.

			final Matcher matcher = testExpression.matcher(AccNum);
			if (matcher.find()) {
				returnValue = 0;
				
				// value meaning it passes regex, not valid.
				final String patternMatch = matcher.group(0);
				fmtAcc.set(patternMatch);
				
				// patternMatch = Match.mk(testExpression, AccNum, 0);
				// FmtAcc.setValue(patternMatch.getValue());
				accountNumberValue = Double.parseDouble(fmtAcc.get());
				OldCashBuilder = "070030" + " " + fmtAcc.get().substring(1);
				
				// check if the account number is an 'old' swindon style number

				final Integer accountIndicator = checkTypeSwindonNoPfx(
						fmtAcc.get(), TypeAcc);

				if (accountIndicator == 20
						&& TypeAcc.get()
								.equalsIgnoreCase(Constants.FLEXACCOUNT)) {
					// valid old Swindon Style number
					returnValue = -1;

					// old 9 digit - make it a scan.
					fmtAcc.set("070116" + " " + fmtAcc.get().substring(1));
				} else if (fmtAcc.get().charAt(0) == '0' && checkSCAN(OldCashBuilder)) {
					
					// check if it passes as an old cashbuilder account
					fmtAcc.set(OldCashBuilder);
					returnValue = -2;
					
				} else if (accountNumberValue > 49999999
						&& accountNumberValue < 100000000
						|| accountNumberValue > 599999999
						&& accountNumberValue < 650000000) {
					// try and derive a new sort code that will make it valid
					Character firstAccNumDigit;
					
					// need to drop the first number before we try and work out
					// a sort code
					firstAccNumDigit = AccNum.charAt(0);
					AccNum = AccNum.substring(1);
					for (final String sortCode : Constants.newSortCodes) {
						// see if it validates
						if (sortCode.charAt(5) == firstAccNumDigit) {
							fmtAcc.set(sortCode.substring(0, 5)
									+ firstAccNumDigit + " " + AccNum);
							scanValidated = checkSCAN(fmtAcc.get());
						} else {
							scanValidated = false;
						}
						if (scanValidated) {
							// we got a match

							returnValue = typeSCAN(fmtAcc.get(), TypeAcc);

							if (TypeAcc.get().equals(Constants.FLEXACCOUNT)) {
								// flex
								returnValue = 1;
							} else if (TypeAcc.get().equals(
									Constants.INVESTMENT)) {
								// investment account
								returnValue = 2;
							} else {
								// something we don't think a new account number
								// should be
								returnValue = 0;
								fmtAcc.set("");
							}
							break;
						} else {
							// then break out - no point checking any further
							fmtAcc.set("");
						}
					}
				} else {
					// number outside current valid ranges
					returnValue = -4;
					fmtAcc.set("");
				}
			}

			result.setSortCode(fmtAcc.get());
			result.setResult(returnValue);
		} catch (final Exception e) {

		}

		return result;
	}

	/**
	 * Validate account number string. Returns account format type, account type
	 * and correctly formatted account number.
	 * 
	 * @param accNum
	 *            account number to validate
	 * @return <pre>
	 * An {@link AccValResult} object containing the details of the validation;
	 * 
	 * 	Scan					The account number is Scan style
	 *  Result					-3 to 51 (see Validate operation for detail)
	 *  AccountNumberFormatted	The formatted account number
	 *  AccountType				The account type
	 *  ErrorDetails			Any error message produced
	 * 
	 * 
	 * </pre>
	 */
	public static AccValResult QueryAccountNumber(String accNum) {

		final AccValResult result = new AccValResult(accNum);

		String deriveAccount = "";
		String exceptionDetail = "";
		int accountFormatType = 0;

		final Reference<String> fmtAcc = new Reference<String>("");
		final Reference<String> typeAcc = new Reference<String>("");
		final Reference<String> scan = new Reference<String>("");

		try {
		
			// Call the standard validation routine specifying application
			// numbers
			// allowed
			accountFormatType = AccValFunctions.Validate(true, accNum, fmtAcc,
					typeAcc);

			if (accountFormatType > 0) {
				if (accountFormatType == 5 || accountFormatType == 7) {
					// Swindon 3/9 or Swindon 4/9; remove prefix before calling
					// derive method
					deriveAccount = fmtAcc.get().substring(5);
					final DeriveNewAccountSortCodeResult deriveNewAccountSortCodeResult = DeriveNewAccountSortCode(deriveAccount);
					scan.set(deriveNewAccountSortCodeResult.getSortCode());
				} else if (accountFormatType == 20) {
					// Swindon 9 no prefix, no need to remove prefix. use 9
					// digit number
					deriveAccount = fmtAcc.get();
					final DeriveNewAccountSortCodeResult deriveNewAccountSortCodeResult = DeriveNewAccountSortCode(deriveAccount);
					scan.set(deriveNewAccountSortCodeResult.getSortCode());
				} else if (accountFormatType == 8) {
					// SCAN; already a scan so no need to derive.
					scan.set(fmtAcc.get());
				}
			} else {
				// no valid account found. Check format and call derive method
				// if
				// Swindon 3/9, 4/9 or 9 no prefix

				// if prefix style account and 9 digits
				if (matchFormat(Constants._szSwindon39, accNum)
						|| matchFormat(Constants._szSwindon49, accNum)) {
					// extract 9 digits from account
					deriveAccount = accNum.substring(accNum.length() - 9);
				} else if (matchFormat(Constants._szSwindon9NoPfx, accNum)) {
					deriveAccount = accNum;
				}

				// call derive
				final DeriveNewAccountSortCodeResult deriveNewAccountSortCodeResult = DeriveNewAccountSortCode(deriveAccount);
				scan.set(deriveNewAccountSortCodeResult.getSortCode());

				if (scan.get() != null && !scan.get().isEmpty()) {
					// scan found and original call to accval type returned
					// invalid
					// account so call again with scan
					accountFormatType = AccValFunctions.Validate(true,
							scan.get(), fmtAcc, typeAcc);
				}
			}

			// set return parameters
			if (accountFormatType < 1) // invalid no.
			{
				typeAcc.set(Constants.OTHER);
				// don't return a format as we can't guarantee it'll be the
				// right one
				fmtAcc.set("");
				// don't return a scan
				scan.set("");
			}
		} catch (final Exception e) {
			exceptionDetail = e.getMessage();
		}

		result.setScan(scan.get());
		result.setResult(accountFormatType);
		result.setAccountNumberFormatted(fmtAcc.get());
		result.setAccountType(typeAcc.get());
		result.setErrorDetails(exceptionDetail);

		return result;
	}
}
