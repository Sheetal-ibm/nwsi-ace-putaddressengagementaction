package uk.co.nationwide.nem.iban;

public class GenerateIBANWrapper{

	public static String generateIBAN(String SortCode, String AccountNumber){
		GenerateIBANFunction genIBAN = new GenerateIBANFunction();
		String IBAN = null;
		try {
			IBAN = genIBAN.getIBAN(SortCode,AccountNumber);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return IBAN;
	}
	
}