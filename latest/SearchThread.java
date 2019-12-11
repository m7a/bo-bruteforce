import java.io.*;
import java.security.*;

class SearchThread extends Thread {
	
	private static final String alphabet = "abcdefghijklmnopqrstuvwxyz";
	private static final String numbers  = "0123456789";
	private static final String special  = "!§$%&/\\{([)]}=?+*~#-_.:,;><|@€µ";
	public static final String ALPHABET = alphabet + alphabet.toUpperCase() + numbers + special;
	private static final char[] alphabetArray = ALPHABET.toCharArray();

	private final byte[] md5ToCrack;

	private int doneSec;
	private int currentStringLength;

	private Controller controller;

	public SearchThread(Controller controller, byte[] md5ToCrack) {
		super("Ma_Sys.ma Broute Force 3 Search Thread");
		this.md5ToCrack = md5ToCrack;
		this.controller = controller;
		doneSec = 0;
		currentStringLength = controller.requestNewLengthToCrack();
	}
	
	// TODO OPTIMIZE
	public void run() {
		MessageDigest md5;
		try {
			md5 = MessageDigest.getInstance("md5");
		} catch(NoSuchAlgorithmException ex) {
			System.err.println("Message Digest could not be initialized.");
			ex.printStackTrace();
			return;
			// TODO EXIT APPLICATION
		}
		int i, j;
		boolean ok;
		short[] nr;
		char[] cdata;
		byte[] bdata, digest;
		while(!isInterrupted()) {
			nr = new short[currentStringLength];
			for(i = 0; i < currentStringLength; i++) {
				nr[i] = 0;
			}
			while(!isInterrupted()) {
				// Generate String from combination and store it as a byte array
				cdata = new char[currentStringLength];
				j = 0;
				for(i = nr.length-1; i >= 0; i--) {
					cdata[j] = alphabetArray[nr[i]];
					j++;
				}
				bdata = new String(cdata).getBytes();

				// Checksum it and compare it to the stored checksum
				digest = md5.digest(bdata);
				i = 0;
				while(i < 0x10 && md5ToCrack[i] == digest[i]) {
					i++;
				}
				if(i == 0x10) {
					System.out.println();
					System.out.println("Combination found: " + new String(cdata));
					controller.exit();
					return;
				}
				md5.reset();

				// Calculate the next combination
				doneSec++;
				ok = false;
				for(i = 0; i < nr.length; i++) {
					nr[i]++;
					if(nr[i] >= alphabetArray.length) {
						nr[i] = 0;
					} else {
						ok = true;
						break;
					}
				}
				if(!ok) {
					break;
				}
			}
			currentStringLength = controller.requestNewLengthToCrack();
		}
	}

	int fetchDoneWork() {
		int ret = doneSec;
		doneSec = 0;
		return ret;
	}

}
