import java.io.*;

public class BrouteForce {

	public static final String NUMBERS = "0123456789";
	public static final String HEX = NUMBERS + "abcdef";
	public static final String AZU = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	public static final String AZL = AZU.toLowerCase();
	public static final String SPECIAL1 = " ^#+äöü-.,<>°!\"§$%&/()=?`´'*ÄÜÖ_:;";
	public static final String SPECIAL2 = "′¹²³¼½¬{[]}\\¸–…·@µ€~|";
	public static final String SPECIAL3 = "ł¶ŧ←↓→øþæſðđŋħ̣̣̣̣ĸł»«„“”";

	public static final byte FILE    = 1;
	public static final byte APP     = 2;
	public static final byte AGAINST = 3;

	private byte outType = FILE;
	private BufferedWriter fOut;
	private char[] charset;
	private int width;
	private boolean on;
	private long foundTotal = 0;
	private long foundSec = 0;
	private String againstStr = null;
	private String current = "";

	public BrouteForce(String[] args) {
		super();
		if(args.length != 3) {
			usage();
		}
		
		String toUseNrs  = args[0];
		d("Use numbers: " + toUseNrs);
		String[] toParam = args[1].split("=");
		d("Destination: " + toParam[1]);
		if(toParam[0].equals("--to-app")) {
			outType = APP;	
		} else if(toParam[0].equals("--to-file")) {
			outType = FILE;
		} else {
			outType = AGAINST;
		}

		StringBuffer searchString = new StringBuffer();
		if(toUseNrs.indexOf('1') >= 0) {
			searchString.append(NUMBERS);
		}
		if(toUseNrs.indexOf('2') >= 0) {
			searchString.append(HEX);
		}
		if(toUseNrs.indexOf('3') >= 0) {
			searchString.append(AZU);
		}
		if(toUseNrs.indexOf('4') >= 0) {
			searchString.append(AZL);
		}
		if(toUseNrs.indexOf('5') >= 0) {
			searchString.append(SPECIAL1);
		}
		if(toUseNrs.indexOf('6') >= 0) {
			searchString.append(SPECIAL2);
		}
		if(toUseNrs.indexOf('7') >= 0) {
			searchString.append(SPECIAL3);
		}
		d("Search String :");
		charset = searchString.toString().toCharArray();
		d(" " + searchString.toString());
		width = Integer.parseInt(args[2]);
		d("Password length " + width);
		final long possis = (long)Math.pow(charset.length, width);
		d("Posibilities : " + possis);	
		final int width2 = String.valueOf(possis).length();	

		initOutput(toParam[1]);
		on = true;
		Thread results = new Thread() {
			public void run() {
				while(on) {
					String out2 = String.format(
						"STATUS    %," + width2 + "d/%," + width2 + "d  %,10d/s  %10s", 
						foundTotal, possis, foundSec, current
					);
					StringBuffer out2buf = new StringBuffer(out2);
					for(int i = 80; i > out2.length(); i--) {
						out2buf.append(" ");
					}
					out2 = "\033[7m" + out2buf.toString() + "\033[0m";
					StringBuffer out1buf = new StringBuffer();
					for(int i = 0; i < out2.length(); i++) {
						out1buf.append("\b");
					}
					out1buf.append(out2);
					System.out.print(out1buf.toString());
					foundSec = 0;
					try {
						sleep(1000);
					} catch(Exception ex) {
						d(ex.toString());
					}
				}
			}
		};
		Thread srch = new Thread() {
			public void run() {
				search();
			}
		};
		d("Searching...");
		results.start();
		srch.start();
	}

	private void search() {
		int[] nr = new int[width];
		for(int i = 0; i < width; i++) {
			nr[i] = 0;
		}
		out(nr);
		while(on) {
			boolean ok = false;
			for(int i = 0; i < nr.length && !ok; i++) {
				nr[i]++;
				if(nr[i] >= charset.length) {
					nr[i] = 0;
				} else {
					ok = true;
				}
			}
			if(!ok) {
				on = false;
				break;	
			}
			out(nr);
		}
		o("");
		stopOutput();
		d("Ready.");
		System.exit(0);
	}

	private void initOutput(String outParam) {
		d("Initializing output...");
		if(outType == FILE) {
			File outFile = new File(outParam);
			d("Output goes to file " + outFile.getAbsolutePath() + ".");
			fOut = null;
			try {
				fOut = new BufferedWriter(new FileWriter(outFile));
			} catch(Exception ex) {
				e("Unable to create file.");
				e(ex.toString());
				d("Application closed due a fatal error.");
				System.exit(2);
			}
		} else if(outType == APP) {
		} else {
			againstStr = outParam;
		}
		d("Output initialized.");
	}

	private void out(int[] combination) {
		StringBuffer outThis = new StringBuffer();
		for(int i = combination.length-1; i >= 0; i--) {
			try {
				outThis.append(charset[combination[i]]);
			} catch(Exception ex) {
				e(ex.toString());
				on = false;
			}
		}
		
		foundTotal++;
		foundSec++;
		current = outThis.toString();
		if(outType == FILE) {
			try {
				fOut.write(current);
				fOut.write(" "); // CONFIGURE SEPARATOR HERE!!!
			} catch(Exception ex) {
				e("Unable to write file.");
				e(ex.toString());
				on = false;
			}
		} else if(outType == AGAINST) {
			if(current.equals(againstStr)) {
				on = false;
				d("Password found.");
			}
		}	
	}

	private void stopOutput() {
		d("Stopping output...");
		if(outType == FILE) {
			try {
				fOut.close();
			} catch(Exception ex) {
				e("Unable to close output file.");
				e(ex.toString());
			}
		} else if(outType == APP) {
		}
		d("Output stopped.");
	}

	private void e(String str) {
		o("ERROR     " + str);
	}

	private void d(String str) {
		o("MESSAGE   " + str);
	}

	private void usage() {
		o("USAGE : java BrouteForce [1234567] --to-file=file | --to-app=app | --against=password <password-size>");
		o(" 1      Numbers");
		o(" 2      Hexadecimal numbers");
		o(" 3      A-Z");
		o(" 4      a-z");
		o(" 5      " + SPECIAL1);
		o(" 6      " + SPECIAL2);
		o(" 7      " + SPECIAL3);
		o("--- DO NOT COMBINE THEM WRONG...");
		System.exit(1);
	}

	private static void o(String msg) {
		System.out.println(msg);
	}

	public static void main(String[] args) {
		o("BrouteForce 1.0 Copyright (c) 2010 Ma_Sys.ma");
		o("Further info : Ma_Sys.ma@web.de");
		o("");
		new BrouteForce(args);

	}

}
