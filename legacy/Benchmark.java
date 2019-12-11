public class Benchmark {
	// This is just for test reasons ... real implementation should be in
	// assembly

	public static void main(String[] args) {
		final long start = System.currentTimeMillis();
		long c = 0;
		final int init = 0x40000;
		final int subIterations = 0x2000;
		int[] results = new int[subIterations];
		int cval = 0, a = 0x2000, b = init;
		//b = 0;
		for(int i = 1; i <= init; i++) {
			for(int j = 0; j < subIterations; j++) {
//				cval = 1 + ((a * cval + b) / c);
//				cval = 1 + (a * cval * init) / (b * c);
//				cval = (cval + b) / a + 1;
//				cval = (cval + b + j) / a;
//				cval = (j + 1) / a;
//				cval = ((cval + 1) * (j / b + 1)) / a;
//				cval = (cval / a) * j + a + 1;
//				cval = cval + (cval + a) / (cval * 10 + 1);
				cval = cval + (j * b) / a;
				if(cval < 0) {
					System.err.println("ALERT!");
					return;
				}/* else if(j % 0x1000 == 0 && i % 0x1000 == 0) {
					System.out.println("i" + i + " j" + j + " cval=" + cval);
				}
*/
				results[j] = cval;
				c++;
			}
/*			System.out.print(
				"ITERATION " + i + ": " + 
				java.util.Arrays.toString(results) +
				" DBG a=" + a + " b=" + b +
				"\n"
			);
*/
			if(cval == 0) {
				System.out.println("TERMINATING HERE");
				long time = System.currentTimeMillis() - start;
				System.out.println("ITERATIONS = " + i + " c = " + c + " in delta t = " + time);
				return;
			}
			a++;
			b--;
			cval = 0;
		}
		System.err.println("INCORRECT TERMINATION");
	}
}
