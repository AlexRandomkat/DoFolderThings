package stuff;

import java.util.ArrayList;

/*
 * I made this class because I was annoyed at always having to make new code for getting pseudorandom stuff.
 */

public class Random {
	/**
	 * custom error for use in class Random when someone tries to generate more
	 * things than is possible
	 */
	public static class notEnoughCombinations extends Exception {
		private static final long serialVersionUID = 0L;
		// TODO ^^^read about this thingy

		notEnoughCombinations(String message) {
			super(message);
		}
	}

	/**
	 * This class has methods for generating numbers.
	 */
	public static class Numbers {

		/**
		 * generates n random doubles greater than or equal to 0.0 and less than 1.0
		 * 
		 * @param n
		 *            Number of doubles to be generated
		 * @return a double[] of length n
		 */
		static double[] randomDoubles(int n) {
			double[] randomNumbers = new double[n];

			// generate random numbers
			for (int i = 0; i < n; i++) {
				randomNumbers[i] = Math.random();
			}
			return randomNumbers;
		}

		/**
		 * generates n random doubles greater than or equal to Math.min(bound0,bound1)
		 * and less than Math.max(bound0,bound1) Special case: if bound0==bound1, then
		 * all doubles returned will be bound0.
		 * 
		 * @param n
		 *            Number of doubles to be generated.
		 * @param bound0
		 *            A boundary
		 * @param bound1
		 *            Another boundary
		 * @return A double[] of length n
		 */
		static double[] randomDoubles(int n, double bound0, double bound1) {
			double[] randomNumbers = randomDoubles(n);
			double range = Math.abs(bound0 - bound1);
			double shift = Math.min(bound0, bound1);

			// adjust the scale and position of the random numbers
			for (int i = 0; i < n; i++) {
				randomNumbers[i] = randomNumbers[i] * range + shift;
			}
			return randomNumbers;
		}

		/**
		 * generates n random integers from Math.min(bound0,bound1) to
		 * 1-Math.max(bound0,bound1) Special case: if bound0==bound1, then all ints
		 * returned will be bound0.
		 * 
		 * @param n
		 *            Number of integers to be generated.
		 * @param bound0
		 *            A boundary
		 * @param bound1
		 *            Another boundary
		 * @return A int[] of length n
		 */
		static int[] randomInt(int n, int bound0, int bound1) {
			double[] randomNumbers = randomDoubles(n, bound0, bound1);
			int[] returnThis = new int[n];

			// convert random doubles to random ints
			for (int i = 0; i < n; i++) {
				returnThis[i] = (int) randomNumbers[i];
			}
			return returnThis;
		}

		/**
		 * generates n random integers from Math.min(bound0,bound1) to
		 * 1-Math.max(bound0,bound1) without repeats.
		 * 
		 * @param n
		 *            Number of integers to be generated.
		 * @param bound0
		 *            A boundary
		 * @param bound1
		 *            Another boundary
		 * @return A int[] of length n
		 * @throws notEnoughCombinations
		 *             This is thrown if Math.abs(bound0-bound1) <= n
		 */
		static int[] randomIntNoRepeats(int n, int bound0, int bound1) throws notEnoughCombinations {
			if (Math.abs(bound0 - bound1) < n) {
				throw new notEnoughCombinations("n is too large for the given range.");
			}
			ArrayList<Integer> pull = new ArrayList<Integer>();
			int[] randomNumbers = new int[n];

			// fill pull with numbers from Math.min(bound0,bound1) to
			// 1-Math.max(bound0,bound1)
			for (int i = Math.min(bound0, bound1); i < Math.max(bound0, bound1); i++) {
				pull.add(i);
			}

			// select n numbers from pull in a random manner
			int e;
			for (int i = 0; i < n; i++) {
				e = (int) (Math.random() * pull.size());
				randomNumbers[i] = pull.get(e);
				pull.remove(e);
			}
			return randomNumbers;
		}
	}

	/**
	 * This class has methods for generating strings
	 */
	public static class Strings {
		/**
		 * Returns an array of strings using numbers and lowercase letters. Uses
		 * characters: 0123456789abcdefghijklmnopqrstuvwxyz The strings are listed from
		 * least to greatest, if you converted the characters to ints and smushed the
		 * digits together. e.g. "034d" would be behind "00wx".
		 * 
		 * @param n
		 *            Number of strings to be made.
		 * @param length
		 *            Length of generated strings.
		 * @return A String[] of length n
		 * @throws notEnoughCombinations
		 *             When length is too small for n.
		 */
		public static String[] orderedAlphanumericStrings(int n, int length) throws notEnoughCombinations {
			if (length <= 0) {
				return null;
			}

			String[] build = new String[n];
			if (n > Math.pow(36, length)) {
				throw new notEnoughCombinations("n is too large for the given string length");
			}

			// fill build
			int counter = 0;
			Character temp;
			for (int i = 0; i < n; i++) {
				if (counter < 10) {
					temp = new Character((char) (counter + 48));
					build[i] = temp.toString();
				} else {
					temp = new Character((char) (counter + 87));
					build[i] = temp.toString();
				}
				counter++;
				counter %= 36;
			}
			if (length == 1) {
				return build;
			} else {
				return orderedAlphanumericStrings(n, length, length - 1, build);
			}
		}

		/**
		 * Used as iterative component of orderedAlphanumericStrings(int n,int length)
		 */
		private static String[] orderedAlphanumericStrings(int n, int length, int left, String[] build)
				throws notEnoughCombinations {

			// add stuff to build
			int counter = 0;
			int power = (int) Math.pow(36, length - left);
			int loop = power * 36;
			Character temp;
			for (int i = 0; i < n; i++) {
				if (counter / power < 10) {
					temp = new Character((char) (((int) (counter / power)) + 48));
					build[i] = temp.toString() + build[i];
				} else {
					temp = new Character((char) (((int) (counter / power)) + 87));
					build[i] = temp.toString() + build[i];
				}
				counter++;
				counter %= loop;
			}

			// decide to end or reiterate
			if (left == 1) {
				return build;
			} else {
				return orderedAlphanumericStrings(n, length, left - 1, build);
			}
		}

		/**
		 * Calls orderedAlphanumericStrings(n, length) and shuffles the result.
		 * 
		 * @param n
		 *            Number of strings.
		 * @param length
		 *            Length of each string.
		 * @return A String[] of length n
		 * @throws notEnoughCombinations
		 *             if n is too large for length
		 */
		public static String[] shuffledAlphanumericStrings(int n, int length) throws notEnoughCombinations {
			String[] shuffled = new String[n];
			int[] order = Numbers.randomIntNoRepeats(n, 0, n);
			String[] ordered = orderedAlphanumericStrings(n, length);

			// shuffle ordered strings
			for (int i = 0; i < n; i++) {
				shuffled[i] = ordered[order[i]];
			}

			return shuffled;
		}
	}
}
