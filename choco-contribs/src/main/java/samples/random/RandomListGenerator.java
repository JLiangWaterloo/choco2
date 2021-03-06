/**
 *  Copyright (c) 1999-2010, Ecole des Mines de Nantes
 *  All rights reserved.
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *      * Redistributions of source code must retain the above copyright
 *        notice, this list of conditions and the following disclaimer.
 *      * Redistributions in binary form must reproduce the above copyright
 *        notice, this list of conditions and the following disclaimer in the
 *        documentation and/or other materials provided with the distribution.
 *      * Neither the name of the Ecole des Mines de Nantes nor the
 *        names of its contributors may be used to endorse or promote products
 *        derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS ``AS IS'' AND ANY
 *  EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE REGENTS AND CONTRIBUTORS BE LIABLE FOR ANY
 *  DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package samples.random;

import choco.kernel.common.logging.ChocoLogging;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Random;
import java.util.logging.Logger;

/**
 * This class allows generating integer random lists.
 * A random list is formed of fixed-size tuples, the values of which range from 0 to a limit given by the user.
 * When generating random lists, it is possible to indicate: <ul>
 * <li> if the lists must be UNSTRUCTURED, CONNECTED or BALANCED,
 * <li> if the same tuple can occur several times,
 * <li> if the same value can occur several times in a generated tuple,
 * <li> if the lists must or must not contain a particular tuple. </ul>
 */
public abstract class RandomListGenerator
{
    protected final static Logger LOGGER = ChocoLogging.getMainLogger();

	public static enum Structure {
		/**
		 * Constant used to denote that generated lists have no structure, i.e., are purely random.
		 */
		UNSTRUCTURED,
		/**
		 * Constant used to denote that each value must occur at least in one tuple of the generated lists. 
		 */
		CONNECTED,

		/**
		 * Constant used to denote that generated lists are balanced, 
		 * i.e., all values have the same number of occurrences in the different tuples of the generated lists.
		 */
		BALANCED
	}

	/**
	 * The <code> Random </code> object used to generate lists.
	 */
	protected Random random;

	/**
	 * The seed used to generate random numbers.
	 */
	protected long seed;

	/**
	 * The number of values for each element of the tuples of the generated lists.
	 * The array index corresponds to the order of the elements of the tuples.
	 * The array value gives the number of values for the corresponding element.
	 * For instance, if nbValues[i] = n then it means that the ith element of each tuple can belong to the propagationSet <code> {0,1,...,n-1} </code>.
	 */
	protected int[] nbValues;

	/**
	 * The length of each tuple of the generated lists.
	 */
	protected int tupleLength;

	/**
	 * The type of the generated lists which can be UNSTRUCTURED, CONNECTED or BALANCED.
	 */
	protected Structure type;

	/**
	 * Indicates if the same tuple can occur several times in the generated lists.
	 */
	protected boolean tupleRepetition;

	/**
	 * Indicates if the same value can occur several times in a generated tuple.
	 */
	protected boolean valueRepetition;

	/**
	 * The generated tuples. 
	 */
	protected int[][] tuples;

	/**
	 * A particular tuple, which if not <code> null </code>, must or must not belong to the generated lists. 
	 */
	protected int[] fixedTuple;

	/**
	 * Indicates if the fixed tuple, if not <code> null </code>, must or must not belong to the generated lists. 
	 */
	protected boolean requiredFixedTuple;

	/**
	 * The number of occurrences of each value in the generated lists.
	 */
	protected int[] nbOccurences;

	/**
	 * The maximum number of values for the elements of the generated tuples.
	 */
	protected int nbMaxValues;

	/**
	 * Returns the generated tuples.
     * @return
     */
	public int[][] getTuples()
	{
		return tuples;
	}

	/**
	 * Builds a random list generator.
	 * @param nbValues the number of values for each element of the tuples
	 * @param seed the seed used to generate random numbers
	 */
	public RandomListGenerator(int[] nbValues, long seed)
	{
		this.nbValues = nbValues;
		this.tupleLength = nbValues.length;
		this.seed = seed;
		random = new Random(seed);
		nbMaxValues = nbValues[0];
		for (int i = 1; i < nbValues.length; i++)
			if (nbValues[i] > nbMaxValues)
				nbMaxValues = nbValues[i];
		nbOccurences = new int[nbMaxValues];
	}

	/**
	 * Builds a random list generator.
	 * @param nb the uniform number of values used to build tuples
	 * @param tupleLength the length of each tuple
	 * @param seed the seed used to generate random numbers
	 */
	public RandomListGenerator(int nb, int tupleLength, long seed)
	{
		nbValues = new int[tupleLength];
		Arrays.fill(nbValues, nb);
		this.tupleLength = tupleLength;
		this.seed = seed;
		random = new Random(seed);
		nbMaxValues = nb;
		nbOccurences = new int[nb];
	}

	/**
	 * Sets the parameters used to generate random lists.
	 * @param type the type of the generated lists which can be UNSTRUCTURED, CONNECTED or BALANCED
	 * @param tupleRepetition indicates if the same tuple can occur several times in the generated lists
	 * @param valueRepetition indicates if the same value can occur several times in a generated tuple
	 * @param fixedTuple a particular tuple, which if not <code> null </code>, must or must not belong to the generated lists 
	 * @param requiredFixedTuple indicates if the fixed tuple, if not <code> null </code>, must or must not belong to the generated lists 	
	 */
	public void setParameters(Structure type, boolean tupleRepetition, boolean valueRepetition, int[] fixedTuple, boolean requiredFixedTuple)
	{
		this.type = type;
		this.tupleRepetition = tupleRepetition;
		this.valueRepetition = valueRepetition;
		this.fixedTuple = fixedTuple;
		this.requiredFixedTuple = requiredFixedTuple;

		if (computeNbDistinctTuples() == 0)
			throw new IllegalArgumentException("The number of distinct tuples is equal to 0");
		if (fixedTuple != null && !valueRepetition)
		{
			for (int i = 0; i < fixedTuple.length - 1; i++)
				if (fixedTuple[i] >= fixedTuple[i + 1])
					throw new IllegalArgumentException("The fixed tuple must be ordered");
		}
	}

	/**
	 * Updates the number of occurrences of each value in the given tuple
	 * @param tuple a generated tuple
	 */
	protected void updateNbValueOccurencesFor(int[] tuple)
	{
        for (int aTuple : tuple) nbOccurences[aTuple]++;
	}

	/**
	 * Returns the number of combinations of p elements chosen among n elements.
	 * @param n the number of elements that can be selected
	 * @param p the number of elements to select
	 * @return the number of combinations of p elements chosen among n elements
	 */
	protected static double cnp(int n, int p)
	{
		double k = 1;
		for (int i = n; i > (n - p); i--)
			k *= i;
		for (int i = p; i > 1; i--)
			k /= i;
		return k;
	}

	public static double computeNbArrangementsFrom(int[] t)
	{
		double d = 1;
        for (int aT : t) d *= aT;
		return d;
	}

	public static double computeNbCombinationsFrom(int[] t, int i, int j)
	{
		if (i == t.length - 1)
			return t[i] - j;
		double cpt = 0;
		for (int k = j; k < t[i]; k++)
			cpt += computeNbCombinationsFrom(t, i + 1, k + 1);
		return cpt;
	}

	public static double computeNbCombinationsFrom(int[] t)
	{
		int[] tmp = t.clone();
		Arrays.sort(tmp);
		if (tmp[0] == tmp[tmp.length - 1])
			return cnp(tmp[0], tmp.length);
		return computeNbCombinationsFrom(tmp, 0, 0); // CHANGER POUR PLUS EFFICACE
	}

	/**
	 * Reurns the number of distinct tuples (hence, we do not take into account tuple repetition).
     * @return
     */
	protected double computeNbDistinctTuples()
	{
		return (valueRepetition ? computeNbArrangementsFrom(nbValues) : computeNbCombinationsFrom(nbValues));
	}

//	/**
//	 * Reurns the maximum number of tuples that can be built. 
//     */
	//	protected int computeNbMaxElements()
	//	{
	//		if (tupleRepetition)
	//			return Integer.MAX_VALUE;
	//		double d = (valueRepetition ? computeNbArrangementsFrom(nbValues) : computeNbCombinationsFrom(nbValues));
	//		if (d >= Integer.MAX_VALUE)
	//			return Integer.MAX_VALUE;
	//		return (int) Math.ceil(d);
	//	}

	protected String getSelectionDescription()
	{
		return tuples.length + "";
	}

	private String getTypeDescription()
	{
		return type.toString();
	}

	/**
	 * Saves the current generated list in a file.  
	 */
	public void saveElements()
	{

		String name =
			"List_"
				+ Arrays.toString(nbValues)
				+ "_"
				+ tupleLength
				+ "_"
				+ seed
				+ "_"
				+ getSelectionDescription()
				+ "_"
				+ getTypeDescription()
				+ "_"
				+ tupleRepetition
				+ "_"
				+ valueRepetition
				+ (fixedTuple != null ? "_sat" : "")
				+ ".txt";
		PrintWriter out = null;

		try
		{
			out = new PrintWriter(new BufferedWriter(new FileWriter(name)));
		}
		catch (IOException e)
		{
			LOGGER.info(""+e);
			System.exit(1);
		}

        for (int[] tuple : tuples) {
            for (int aTuple : tuple) out.print(aTuple + " ");
            out.println();
        }
		out.close();
	}

	/**
	 * Displays the current random generated list.
	 */
	public void displayTuples()
	{
		double max = (tupleRepetition ? -1 : (valueRepetition ? computeNbArrangementsFrom(nbValues) : computeNbCombinationsFrom(nbValues)));

		LOGGER.info("There are " + tuples.length + " elements " + (max != -1 ? "/  " + max + " elements" : ""));
        StringBuffer st = new StringBuffer();
        for (int[] tuple : tuples) {
            st.append("(");
            for (int j = 0; j < tuple.length; j++) {
                st.append(tuple[j]);
                if (j != tuple.length - 1)
                    st.append(",");
            }
            st.append(") ");
        }
        LOGGER.info(st.toString());
		LOGGER.info("\nOccurrences of values");
		for (int i = 0; i < nbOccurences.length; i++)
			LOGGER.info(" value " + i + " => " + nbOccurences[i]);
	}

	static void main(String[] args)
	{
		int[] t = { 2, 2, 6, 23, 3, 7, 8, 10, 10, 10, 10, 12, 21, 30 };
		LOGGER.info("Combinations = " + computeNbCombinationsFrom(t));
		LOGGER.info("Arrangements = " + computeNbArrangementsFrom(t));

	}
}
