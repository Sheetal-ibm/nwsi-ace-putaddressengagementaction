package uk.co.nationwide.nem.urldecode;
import java.io.UnsupportedEncodingException;

public class FieldDecoder {
	
	public static String Decode(String EncodedValue) {
		// TODO Auto-generated method stub
		String decode="";

		try{
		     // String encoded = java.net.URLEncoder.encode("123456 12345678","UTF-8");
		     //  System.out.println(""+ Encode);
		     decode = java.net.URLDecoder.decode(EncodedValue, "UTF-8");
		     //System.out.println(decode);
		} catch (UnsupportedEncodingException e) {
		     // TODO Auto-generated catch block
			decode = "UTF-8 decoding error";
		     e.printStackTrace();
		}
		return decode;

	}
	
	}

