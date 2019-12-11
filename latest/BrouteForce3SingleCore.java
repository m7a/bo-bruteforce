import java.io.*;
import java.security.*;

public class BrouteForce3SingleCore implements Controller {

	private final String againstMD5;

	private int stringLength;
	private Thread statusDisplay;

	public BrouteForce3SingleCore(String againstMD5) {
		super();
		this.againstMD5 = againstMD5;
		System.out.println("Trying to crack " + againstMD5 + "...");
		stringLength = 1;
	}

	void startCracking() {
		short[] numberData = new short[againstMD5.length() / 2]; // 0x10
		int j;
		for(int i = 0; i < numberData.length; i++) {
			j = 2 * i;
			numberData[i] = Short.parseShort(againstMD5.substring(j, j + 2), 0x10);
		}
		final byte[] md5 = MiniToolbox.signBytes(numberData);
		final int processors = 1;
		System.out.println("Using " + processors + " Threads to search.");
		System.out.println("Creating and starting threads...");
		final SearchThread[] allSearchers = new SearchThread[processors];
		for(int i = 0; i < allSearchers.length; i++) {
			allSearchers[i] = new SearchThread(this, md5);
			allSearchers[i].start();
		}
		final Thread cancelThread = new Thread() {
			public void run() {
				setName("Ma_Sys.ma Broute Force 3 Cancel Thread");
				BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
				try {
					in.readLine();
				} catch(IOException ex) {
					System.err.println("Aborting via [Enter] not supported -- use CTRL-C");
					ex.printStackTrace();
					return;
				}
				if(statusDisplay.isInterrupted()) {
					System.exit(0);
				} else {
					statusDisplay.interrupt();
				}
			}
		};
		statusDisplay = new Thread() {
			public void run() {
				setName("Ma_Sys.ma Broute Force 3 Status Display Thread");
				long start = System.currentTimeMillis();
				int clear = 0, cursor = 0;
				long doneTotal = 0, max = 0;
				final int diagramSize = 78;
				long[] diagramContent = new long[diagramSize];
				boolean diagramComplete = false;
				while(!isInterrupted()) {
					try {
						Thread.sleep(1000);
					} catch(InterruptedException ex) {
						System.out.println("Status Display Thread interrupted.");
						break;
					}
					char[] clearer = new char[clear];
					for(int i = 0; i < clear; i++) {
						clearer[i] = '\b';
					}
					System.out.print(new String(clearer));
					long doneThisSec = 0;
					for(int i = 0; i < allSearchers.length; i++) {
						doneThisSec += allSearchers[i].fetchDoneWork();
					}
					doneTotal += doneThisSec;
					diagramContent[cursor] = doneThisSec;
					if(doneThisSec > max) {
						max = doneThisSec;
					}
					if(cursor == diagramSize - 1) {
						cursor = 0;
						if(!diagramComplete) {
							diagramComplete = true;
						}
					} else {
						cursor++;
					}
					String toBeDisplayed = "STATUS speed=" + doneThisSec + "/s strlen=" + stringLength;
					clear = toBeDisplayed.length();
					System.out.print(toBeDisplayed);
				}
				final int stringLengthF = stringLength;
				if(cancelThread.getState() != Thread.State.TERMINATED) {
					cancelThread.interrupt();
				}
				for(int i = 0; i < allSearchers.length; i++) {
					allSearchers[i].interrupt();
				}
				System.out.println();
				System.out.println("Statistics");
				long time = System.currentTimeMillis() - start;
				System.out.println("\tTime                 " + time + " ms.");
				System.out.println("\tCombinations total   " + doneTotal);
				System.out.println("\tAvg Combinations/ms  " + (doneTotal / time));
				System.out.println("\tStrlen               " + stringLength);
				System.out.println("\tFully searched       " + (stringLengthF - processors));
				System.out.println("\tMax Combinations/ms  " + (max / 1000));
				System.out.println();
				// draw a diagram
				int diagramHeight = 15; // Lines
				int length, beginIndex;
				if(diagramComplete) {
					beginIndex = cursor;
					length     = diagramSize;
				} else {
					beginIndex = 0;
					length     = cursor;
				}
				int[] xyData = new int[length];
				int j = 0;
				for(int i = beginIndex; i < length; i++) {
					xyData[j] = (int)(((double)diagramContent[i]) / (double)max * diagramHeight);
					j++;
				}
				if(diagramComplete) {
					for(int i = 0; i < cursor; i++) {
						xyData[j] = (int)(((double)diagramContent[i]) / (double)max * diagramHeight);
						j++;
					}
				}
				for(int i = 0; i < diagramHeight; i++) {
					char[] lineContent = new char[xyData.length];
					for(j = 0; j < xyData.length; j++) {
						if(i >= diagramHeight - xyData[j]) {
							lineContent[j] = '#';
						} else {
							lineContent[j] = '_';
						}
					}
					System.out.println(' ' + new String(lineContent));
				}
				// Cancel remaining thread
				if(cancelThread.getState() != Thread.State.TERMINATED) {
					System.out.println();
					System.out.println("Press enter to exit...");
				}
			}
		};
		cancelThread.start();
		statusDisplay.start();
	}

	public void exit() {
		statusDisplay.interrupt();
	}

	public int requestNewLengthToCrack() {
		// First return 1 (as already initialized) and then 2 to the next thread, etc.
		return stringLength++;
	}

	private static void crack(String md5) {
		new BrouteForce3SingleCore(md5).startCracking();
	}

	private static void usage(String msg) {
		System.out.println(msg);
		System.out.println();
		System.out.println("Usage: java BrouteForce3 --benchmark|--crack|--md5 [md5|string]");
		System.out.println();
		System.out.println("--benchmark");
		System.out.println("\tMakes a benchmark against a md5 of zeroes.");
		System.out.println("\tWrites statistics and current status as usual.");
		System.out.println("--crack");
		System.out.println("\tTries to crack the given MD5.");
		System.out.println("\tIf you really want to do this: Do not use this program.");
		System.out.println("\tIt is WAY to slow for your goal then.");
		System.out.println("--md5");
		System.out.println("\tCalculates the MD5 of the given string.");
		System.out.println();
		System.out.println("Our alphabet:");
		System.out.println(SearchThread.ALPHABET);
		System.out.println();
		System.out.println("When running, the program can be stopped via [Enter].");
	}

	public static void main(String[] args) {
		System.out.println("Broute Force 3.0.0.1, Copyright (c) 2012 Ma_Sys.ma.");
		System.out.println("For further info send an e-mail to Ma_Sys.ma@web.de.");
		System.out.println();
		if(args.length == 0 || args.length > 2) {
			usage("Invalid number of arguments.");
		} else if(args.length == 1) {
			if(args[0].equals("--benchmark")) {
				crack("00000000000000000000000000000000");
			} else {
				usage("When giving only one argument, it must be --benchmark.");
			}
		} else {
			if(args[0].equals("--crack")) {
				if(args[1].length() == 32) {
					crack(args[1]);
				} else {
					usage("An MD5 sum must be given and it must have a length of excactly 32 characters.");
				}
			} else if(args[0].equals("--md5")) {
				MessageDigest md5;
				try {
					md5 = MessageDigest.getInstance("md5");
				} catch(NoSuchAlgorithmException ex) {
					System.err.println("Message Digest could not be initialized.");
					ex.printStackTrace();
					return;
				}
				byte[] checksumBytes = md5.digest(args[1].getBytes());
				StringBuffer checksumString = new StringBuffer(checksumBytes.length * 2);
				for(int i = 0; i < checksumBytes.length; i++) {
					checksumString.append(MiniToolbox.formatAsHex(checksumBytes[i]));
				}
				System.out.println(checksumString.toString());
			}
		}
		System.out.println();
	}

}
