package vdel.modelchecker;

//--------------------------------------
// Systematically generate combinations.
//--------------------------------------

import vdel.modelchecker.ConstraintEvaluator;
import vdel.utilities.Utilities;
import vdel.scanners.BaseScanner;
import java.io.StringReader;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GenCombinationGenerator {

    private int[] a;
    private int n;
    private int r;
    private BigInteger numLeft;
    private BigInteger total;

    //------------
    // Constructor
    //------------
    public GenCombinationGenerator(int n, int r) {
        if (r > n) {
            throw new IllegalArgumentException();
        }
        if (n < 1) {
            throw new IllegalArgumentException();
        }
        this.n = n;
        this.r = r;
        a = new int[r];
        BigInteger nFact = getFactorial(n);
        BigInteger rFact = getFactorial(r);
        BigInteger nminusrFact = getFactorial(n - r);
        total = nFact.divide(rFact.multiply(nminusrFact));
        reset();
    }

    //------
    // Reset
    //------
    private void reset() {
        for (int i = 0; i < a.length; i++) {
            a[i] = i;
        }
        numLeft = new BigInteger(total.toString());
    }

    //------------------------------------------------
    // Return number of combinations not yet generated
    //------------------------------------------------
    public BigInteger getNumLeft() {
        return numLeft;
    }

    //-----------------------------
    // Are there more combinations?
    //-----------------------------
    public boolean hasMore() {
        return numLeft.compareTo(BigInteger.ZERO) == 1;
    }

    //------------------------------------
    // Return total number of combinations
    //------------------------------------
    public BigInteger getTotal() {
        return total;
    }

    //------------------
    // Compute factorial
    //------------------
    private static BigInteger getFactorial(int n) {
        BigInteger fact = BigInteger.ONE;
        for (int i = n; i > 1; i--) {
            fact = fact.multiply(new BigInteger(Integer.toString(i)));
        }
        return fact;
    }

    //--------------------------------------------------------
    // Generate next combination (algorithm from Rosen p. 286)
    //--------------------------------------------------------
    public int[] getNext() {

        if (numLeft.equals(total)) {
            numLeft = numLeft.subtract(BigInteger.ONE);
            return a;
        }

        int i = r - 1;
        while (a[i] == n - r + i) {
            i--;
        }
        a[i] = a[i] + 1;
        for (int j = i + 1; j < r; j++) {
            a[j] = a[i] + j - i;
        }

        numLeft = numLeft.subtract(BigInteger.ONE);
        return a;

    }

    public String[] generateAll(String[] elements, String[] partition) {
        int[] indices;
        ArrayList<String> output = new ArrayList<String>();

        StringBuffer combination;

        loop1:
        while (this.hasMore()) {
            combination = new StringBuffer();
            indices = this.getNext();
            //if indices array contains more that one representative from each partition,
            //then drop the returned indices as inappropriate
            //use the following flag to check that the indices contain one element per partition
            boolean indicesOkFlag = true;
            for (int j = 0; j < partition.length; j++) {
                String[] limits = partition[j].split(",");
                int count = 0;
                for (int i = 0; i < indices.length; i++) {
                    if ((indices[i] >= Integer.parseInt(limits[0])) && (indices[i] <= Integer.parseInt(limits[1]))) {
                        count++;
                    }
                }

                if (count != 1) {
                    //that is, there are more that than one elements from this partition
                    // so we ingnore the set contained in the returned indices
                    indicesOkFlag = false;
                    break; //no need to continue, we just proceed to reject the indices generated
                } else {
                    continue;
                }
            }

            if (indicesOkFlag) {
                //everything is ok with this indices
                //so process it
                String[] vars = new String[indices.length];
                for (int i = 0; i < indices.length; i++) {
                    combination.append(elements[indices[i]] + ((i < indices.length - 1) ? "," : ""));
                    vars[i] = elements[indices[i]];
                }
                System.out.println(combination.toString());
                //Now, before we add to the combination we need to evaluate the constraints!
                //get each constraint

                String aConstraint, lVar;
                //  boolean constraintsOk = true; 
                // Map<String, String> constraintVarMap = new TreeMap<String, String>();

                /********************
                 * Remember to stress that the programmer must ensure that the correct order is followed in writting the constraints
                 * for example, if constraint A requires that another constraint B be satisfied first, then constraint B will have to 
                 * written down before constraint A in the constrain specification
                 */
                //the following outer for loop is there to ensure that irrespective of the order in which 
                // the constraints are placed in the map, we still follow the order of priority in the constraints
                // which is given by the order in which they were written in the del code
                
                // a constraint entry in the map has the following structure
                //the key of the map is the whole constraint expression
                // the value of an entry is an arraylist: the first element of the arraylist is 
                // the lVar or the variable to which the value of the constraint expression is assigned to
                // (in the case expressions that are not "dynamic" constraints, the lVar is set to Utilities.NOLVALUE
                //...the second index onwards in the arraylist contains the list of all the variables involved in the 
                // the expression. This list enables us to look for the corresponding "var=value" string in var array, and 
                // so extract the value from var, using the value to replace the corresponding variables in this expression
                // HOWEVER, THIS COULD BE HAVE BEEN DONE IN A BETTER WAY. THERE IS NO NEED TO KEEP THE LIST OF VARIABLES
                // ALL WE NEED TO DO IS TO SPLIT A vars ENTRY BY "=", THEN REPLACE ALL OCCURRENCES OF THE LEFT HAND SIDE (INDEX
                // 0 OF THE SPLIT, WITH THE RIGHT HAND SIDE (INDEX 1 OF THE SPLIT). IN THE CALL TO REPLACE ALL, WE MUST ENSURE
                // THAT THE REGULAR EXPRESSION PASSED AS THE FIRST ARGUMENT (WHAT TO REPLACE) IS CONSTRUCTED IN SUCH A WAY THAT 
                // THE STRING TO BE REPLACED MUST NOT BE FOLLOWED BY AN ALPHANUMERIC CHARACTER (IF IT DOES THEN WE RUN THE RISK 
                // OF REPLACING A MATCHING SUBSTRING OF A LONGER IDENTIFIER (FOR EXAMPLE IF WE HAVE BOTH IDENTIFIERS "a" and "ab"
                //  WE COULD END UP REPLACING THE "a" IN "ab") TO PREVENT THIS, THE REGULAR EXPRESSION FOR THE STRING TO BE MATCHED
                // SHOULD BE TAILED BY A \W (I.E. SAYING THAT THE STRING IS FOLLLOWED BY A NON ALPHANUMERIC CHARACTER).
       
                loop2:
                for (Integer i = 0; i < Utilities.constraints.size(); i++) {
                    Iterator constraintIterator = Utilities.constraints.get(i).entrySet().iterator();

                    loop3:
                    while (constraintIterator.hasNext()) {
                        Map.Entry constraintEntry = (Map.Entry) constraintIterator.next();
                        aConstraint = constraintEntry.getKey().toString();
                        //to get the variables involved in it
                        String rVal;// = new String[((ArrayList<String>) constraintEntry.getValue()).size() - 1];
                        lVar = ((ArrayList<String>) constraintEntry.getValue()).get(0);
                        //fish out all the other entries (i.e. the actual variables contained in the expression
                        for (int j = 0; j < ((ArrayList<String>) constraintEntry.getValue()).size() - 1; j++) {
                            rVal = ((ArrayList<String>) constraintEntry.getValue()).get(j + 1);
                            // okay, we know rVal is involved, fish out it's value from vars
                            for (int k = 0; k < vars.length; k++) {
                                if (vars[k].startsWith(rVal)) {
                                    // constraintVarMap.put(rVal, vars[k].split("=")[1]);  //puts variable-value pair
                                    //now replace the set of values in the constraint
                                    aConstraint = aConstraint.replaceAll(rVal, (vars[k].split("="))[1]);
                                }
                            }
                        }
                        //now evaluate this constraint
                        System.out.println(aConstraint);
                        aConstraint = "[".concat(aConstraint).concat(";]"); // to put it in the right format for our constant evaluator
                        ConstraintEvaluator ce = new ConstraintEvaluator(new BaseScanner(new StringReader(aConstraint)));
                        String result = "";
                        try {
                            result = ce.parse().value.toString();
                        } catch (Exception ex) {
                            Logger.getLogger(GenCombinationGenerator.class.getName()).log(Level.SEVERE, null, ex);
                        }

                        if (lVar.equals(Utilities.NOLVALUE)) { //that means it is not a dynamic constraint i.e. no lVariable to receive the value of the constraint expression
                            // we simply check whether true was returned. if no, we discard this generated value. otherwise we continue with other constraints
                            if (result.equalsIgnoreCase("false")) {
                                // delete this combination
                                //note that I do not only rely on the new instantiation at the top of loop 1, cause this will let the previous object float for some time in memory but still containing the heavy load of previous generated combination
                                combination = combination.delete(0, combination.length());
                                continue loop1; // i.e. generate another set of state values
                            } else {
                                continue loop2; //i.e. continue testing with the next constraint
                            }
                        } else {
                            //NOTE THAT I DID NOT INSERT THE LVALUE INTO THE CORRESPONDING VARIABLE ENTRY IN THE SYMBOL TABLE (IN THE LIST OF POSSIBLE VALUES OF THIS VARIABLE)
                            // I THINK THAT IT IS NOT NECESSARY BUT WE SHALL SEE WHETHER IT WILL BE USEFUL AT SOME POINT IN TIME
                            
                            //assign the result value to the lVar
                            result = lVar.concat("=").concat(result);  //outputs sth like sum=10
                            //append the lVar and its value to the combination generated
                            if (combination.length() == 0) {
                                combination.append(result);  //i.e. do not add a comma when this is the first element to enter into the combination
                            } else {
                                combination.append(",").append(result);
                            }
                        }
                        System.out.println("The result is: " + result);

                    }

                }
                //replace this set of values in the constraint
                //evaluate the constraint
                //update as required
                //find the correct position for this variable, from the symbol table, and insert this value in that position in the generated states
                // get this position, put in it into, say n. Then we obtain the index of the nth comma (which seperates the positions), and place this variable right after that index
                //Integer.parseInt(Utilities.symbolTable.get(lVar).get(1));
                String[] validValues = combination.toString().split(",");
                combination.delete(0, combination.length());  // clears this buffer inorder to reuse it
                Iterator itValidValues = Utilities.symbolTable.entrySet().iterator();
                while (itValidValues.hasNext()) {  // the tree map makes guarantess on the order of this map, and it is already sorted
                    Map.Entry pairsSymbols = (Map.Entry) itValidValues.next();
                    for (int i = 0; i < validValues.length; i++) {
                        if (validValues[i].trim().startsWith(pairsSymbols.getKey().toString().concat("="))) {
                            combination.append(validValues[i].trim()).append(",");
                        }
                    }
                }
                //remove the last comma
                combination.deleteCharAt(combination.length() - 1);
                output.add(combination.toString());
            }


        }
        return output.toArray(new String[0]);
    }

    public static void main(String[] args) {
        String[] elements = {"sum=8", "sum=9", "product=16", "product=20", "x", "\u00acx", "y", "\u00acy"};
        String[] partition = new String[]{"0,1", "2,3", "4,5", "6,7"};
        GenCombinationGenerator x = new GenCombinationGenerator(elements.length, partition.length);
        String[] out = x.generateAll(elements, partition);

        for (int i = 0; i < out.length; i++) {
            String string = out[i];
            System.out.println(string);

        }

    }
    
//    public static void main(String[] args) {
//        String[] elements = {"sum=8", "sum=9", "product=16", "product=20", "x=3", "x=4", "y=4", "y=5"};
//        int[] indices;
//        ArrayList<String> l = new ArrayList<String>();
//        String[] partition = new String[]{"0,1", "2,3", "4,5", "6,7"};
////        String[] elements = {"0", "1", "2", "3", "0", "1", "2", "3", "0", "1", "2", "3", "0", "1", "2", "3"};
////        int[] indices;
////        ArrayList<String> l = new ArrayList<String>();
////        String[] partition = new String[]{"0,0", "2,2", "8,11", "12,15"};
//        //in the constructor for CombinationGenerator, observe that the no of items selected in each go should be the same 
//        // as the no of partitions, otherwise the constraint that 'one element must be selected
//        // from each partition' will fail
//        GenCombinationGenerator x = new GenCombinationGenerator(elements.length, 4);
//        StringBuffer combination;
//        while (x.hasMore()) {
//            combination = new StringBuffer();
//            indices = x.getNext();
//            //if indices array contains more that one representative from each partition,
//            //then drop the returned indices as inappropriate
//            //use the following flag to check that the indices contain one element per partition
//            boolean indicesOkFlag = true;
//            for (int j = 0; j < partition.length; j++) {
//                String[] limits = partition[j].split(",");
//                int count = 0;
//                for (int i = 0; i < indices.length; i++) {
//                    if ((indices[i] >= Integer.parseInt(limits[0])) && (indices[i] <= Integer.parseInt(limits[1]))) {
//                        count++;
//                    }
//                }
//
//                if (count != 1) {
//                    //that is, there are more that than one elements from this partition
//                    // so we ingnore the set contained in the returned indices
//                    indicesOkFlag = false;
//                    break; //no need to continue, we just proceed to reject the indices generated
//                } else {
//                    continue;
//                }
//            }
//
//            if (indicesOkFlag) {
//                //everything is ok with this indices
//                //so process it
//                for (int i = 0; i < indices.length; i++) {
//                    combination.append(elements[indices[i]] + ((i < indices.length - 1) ? "," : ""));
//                }
//                System.out.println(combination.toString());
//            }
//
//
//        }
//    }
//    public String[] generateAll(String[] elements) {
//        //String[] elements = { "b", "c", "\u00acc"};
//        int[] indices;
//       // ArrayList<String> l = new ArrayList<String>();
//        
//        GenCombinationGenerator x = new GenCombinationGenerator(elements.length, 2);
//        StringBuffer combination = new StringBuffer();
//        while (x.hasMore()) {
//            combination = new StringBuffer();
//            indices = x.getNext();
//            for (int i = 0; i < indices.length; i++) {
//                combination.append(elements[indices[i]] + ((i < indices.length-1) ? ",": ""));
//            }
//            System.out.println(combination.toString());
//        }
//        return combination.toString().split(",");
//    }
}
