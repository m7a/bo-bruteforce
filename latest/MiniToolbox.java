/**
 * Auszüge aus der Ma_Sys.ma Tools Bibliothek 2
 */
class MiniToolbox {
	
	// Aus den MathUtils
	/**
	 * Macht aus notierten Hexadezimalzahlen (0xff, 0xa5, 0x17, etc.) entsprechende bytes
	 * und fügt dafür entsprechend Vorzeichen ein.
	 * 
	 * @param unsignedInput Nicht vorzeichenbehaftete Hexadezimalzahlen 0-255
	 * @return Vorzeichenbehaftete bytes
	 */
	public static byte[] signBytes(short[] unsignedInput) {
		byte[] ret = new byte[unsignedInput.length];
		for(int i = 0; i < unsignedInput.length; i++) {
			if(unsignedInput[i] > 127) {
				ret[i] = (byte)(unsignedInput[i] - 0x100);
			} else {
				ret[i] = (byte)unsignedInput[i];
			}
		}
		return ret;
	}

	// Aus der Klasse StringUtils
	/**
	 * Formatiert den angegebenen byte als Hexadezimalzahl mit zwei Stellen.
	 * <br>
	 * Beispiel :
	 * <pre>
	 * import ma.tools.StringUtils;
	 * 
	 * public class FormatHexTest {
	 * 	public static void main(String[] args) {
	 * 		byte theByte = 56;
	 * 		System.out.println(StringUtils.formatAsHex(theByte);
	 * 	}
	 * }
	 * </pre>
	 * 
	 * @param number Der zu formatierende byte.
	 * @return Einen String, der die Hexadezimaldarstellung von number enth&auml;lt.
	 */
	public static String formatAsHex(byte number) {
		String ret = null;
		String hex = Integer.toHexString(number);
		int len = hex.length();
		if(len == 1) {
			ret = "0" + hex;
		} else if(len == 2) {
			ret = hex;
		} else if(len > 2) {
			ret = hex.substring(len-2);
		}
		return ret;
	}
}
