package uk.co.nationwide.nem.urlencode;
import java.io.UnsupportedEncodingException;

public class FieldEncoder {
	
	public static String Encode(String Value) {
		// TODO Auto-generated method stub
		String encodedValue="";

		try{
		     // String encoded = java.net.URLEncoder.encode("123456 12345678","UTF-8");
		     //  System.out.println(""+ Encode);
			encodedValue = java.net.URLEncoder.encode(Value,"UTF-8");
		     //System.out.println(decode);
		} catch (UnsupportedEncodingException e) {
		     // TODO Auto-generated catch block
		     e.printStackTrace();
		}
		return encodedValue;

	}
	
	}

