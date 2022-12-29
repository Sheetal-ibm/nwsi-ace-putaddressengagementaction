package uk.co.nationwide.nem.iban;

public class GenerateIBANFunction {
	public static String getIBAN(String sortCodeParam, String accountNumberParam) { //throws Exception
		
		if(sortCodeParam == null || sortCodeParam.equalsIgnoreCase("")){
			try {
				throw new Exception("SortCode is missing in the request");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if(accountNumberParam == null || accountNumberParam.equalsIgnoreCase("")){
			try {
				throw new Exception("AccountNumber is missing in the request");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		try{
			
		final String str1 = "23101810"; // "NAIA" where A = 10 up to Z = 35
		final String str2 = "161100"; // "GB00"
		
		String sortCde = sortCodeParam.compareTo("086086") == 0 ? "070116"
                  : sortCodeParam.compareTo("074456") == 0 ? "070116"
                  : sortCodeParam;
		
		String reverseBBAN = str1 + sortCde + accountNumberParam + str2;
		String n = reverseBBAN.substring(0, 9);

	    String ba = reverseBBAN.substring(9);
	    StringBuilder sb = new StringBuilder();
	    int i =0;
	    while(i<reverseBBAN.length()-9)
	    {
	        sb.append(ba.charAt(i));
	        i++;
	    }
	    String m = sb.toString();
	    int intn = Integer.parseInt(n);
        int intr = intn - (97 * (intn / 97));

        String r = Integer.toString(intr);
        String concat2 = r + m;
        int numDigits = 9;
        while (concat2.length() > 2)
        {
            numDigits = concat2.length() > 9 ? 9 : concat2.length();

            n = concat2.substring(0, numDigits);          
            String bab, ma ="";
            StringBuilder sba = new StringBuilder();
            if(concat2.length()>=9)
            {
            bab = concat2.substring(9);
    	    int ia =0;
    	    while(ia<concat2.length()-9)
    	    {
    	        sba.append(bab.charAt(ia));
    	        ia++;
    	    }
    	    ma = sba.toString();
            } 
            
            m = numDigits == 9 ? ma : "";
            intn = Integer.valueOf(n);
            intr = intn - (97 * (intn / 97));
            r = String.valueOf(intr);
            concat2 = r + m;
        }

        int intcd = 98 - Integer.valueOf(concat2);
        String cd = String.valueOf(intcd);

        if (cd.length() == 1)
        {
            cd = "0" + cd;
        }
        
        String iban = "GB" + cd + " "
            + "NAIA" + " "
            + sortCde.substring(0, 4) + " "
            + sortCde.substring(4) + accountNumberParam.substring(0, 2) + " "
            + accountNumberParam.substring(2, 4) + " "
            + accountNumberParam.substring(4);
        return iban;
        
		}catch(Exception e){
			throw e;			
		}
    }

}