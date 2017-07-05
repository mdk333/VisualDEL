package vdel.utilities;


import java.io.File;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Formatter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import org.apache.commons.lang3.StringUtils;
import vdel.EpistemicKeypad;
import vdel.modelchecker.ConstraintEvaluator;
import vdel.modelchecker.EpistemicConstraintEvaluator;
import vdel.modelchecker.GenCombinationGenerator;
import vdel.modelchecker.ModelChecker;
import vdel.scanners.BaseScanner;
import vdel.scanners.ModelCheckerScanner;

public class Utilities {

    public static ArrayList<String> boolNamesList = new ArrayList<>();
    public static ArrayList<Object[]> truthFalseStateHistory = new ArrayList<>();
    public static ArrayList<String> inConstraintIdentifiers = new ArrayList<>();  //stores the 
    public static ArrayList<String> inEpistemicConstraintIdentifiers = new ArrayList<>();  //stores the 
    public static ArrayList<String> agentNamesList = new ArrayList<>();
    public static Map<Integer, Map<String, ArrayList<String>>> constraints = new TreeMap<>();
    public static Map<Integer, Map<String, ArrayList<String>>> epistemicConstraints = new TreeMap<>();
    public static Map<String, ArrayList<String>> symbolTable = new TreeMap<>();
    public static Map<String, ArrayList<String>> agentVarMap = new TreeMap<>();  //keeps the mapping of which variable is controlled by which agent
    public static Map<String, Map<String, ArrayList<String>>> varValuesStateMap = new TreeMap<>();
    //the qualified state map tells: for each state, the list of all agents and the successor states of each of those agents...
    // will be very instrumental in drawing and updating the graphs
    public static Map<Integer, Map<String, ArrayList<Integer>>> qualifiedStateMap = new TreeMap<>();
    public static Map<Integer, String[]> stateLabelMap = new TreeMap<>();
    public static Integer stateNumber = 0;
    public static Integer constraintPriority = 0;
    public static Integer epistemicConstraintPriority = 0;
    public static String[] colors = {"black", "red", "greenyellow", "orange", "yellow", "forestgreen", "mediumaquamarine", "skyblue", "gray"};
    public static StringBuffer dotGraph = new StringBuffer();
    public static Integer positionCount = 0;
    private static String[] stateValues;
    private static String[] partition;
    public static String NOLVALUE = "#none#";
    public static Integer noOfDynamicVars = 0;
    public static String dotFileName = "ouput.dot"; //by default
    public static boolean thereWasError = false;
    public static String successorStateSeperator = "<--->";

//    public static void generateDot() {
//        //Display agentVarMap
//        Iterator it = agentVarMap.entrySet().iterator();
//        int colorIndex = -1;
//
//        //initialising the dotGraph
//        dotGraph.append("graph G { \n");
//        //System.out.println("here1");
//        while (it.hasNext()) {
//            Map.Entry pairs = (Map.Entry) it.next();
//            //System.out.println("here2");
//            //now we've got another agent and the variable it controls. Down the line we'll see all the possible values of that variable and the states where they hold
//            //so we change edge color to signify this agent
//            colorIndex++;
//            System.out.println("******************************************************");
//            System.out.println("color allocations");
//            System.out.println("--------------------");
//            System.out.println("Agent " + pairs.getKey() + "==> " + colors[colorIndex]);
//            // dotGraph.append("edge [color=" + colors[colorIndex] + "];\n"); 
//            //System.out.println("Agent " + pairs.getKey() + " OBSERVES " + StringUtils.remove(StringUtils.remove(pairs.getValue().toString(), '['), ']'));
//
//            //vars represent the set of position numbers in the generated state label on which this agent has control
//            String[] vars = StringUtils.split(StringUtils.remove(StringUtils.remove(pairs.getValue().toString(), '['), ']'), ',');
//
//            //iterate through all the states
//            Map stateLabelMapAnchor = new TreeMap(stateLabelMap);
//            Iterator itStateLabelMap = stateLabelMapAnchor.entrySet().iterator();
//
//            while (itStateLabelMap.hasNext()) {
//                Map.Entry pairsStateLabel = (Map.Entry) itStateLabelMap.next();
//                String[] s = (String[]) pairsStateLabel.getValue();
//                // StringUtils.trim(s[]);
//                //get the values at those positions in this state which are controlled by the current agent
////                ArrayList posValues = new ArrayList<String>();
////                for (int i = 0; i < vars.length; i++) {
////                    posValues.add(StringUtils.trim(s[new Integer(StringUtils.trim(vars[i]))]));
////                }
//                //now compare posValues in this state to those of all other states, and connect it to all those states where all posValues are the same
//                Map tempStateLabelMap = new TreeMap(stateLabelMapAnchor);  //duplicate the anchor (which also gets trimmed for avoid reverse (duplicate entries))
//                Iterator itTempStateLabelMap = tempStateLabelMap.entrySet().iterator();
//                while (itTempStateLabelMap.hasNext()) {
//                    Map.Entry pairsTempStateLabel = (Map.Entry) itTempStateLabelMap.next();
//                    String[] sTemp = (String[]) pairsTempStateLabel.getValue();
//                    // Object[] posArrayValue = posValues.toArray();
//                    boolean flag = true; //used to detect if the loop executed fully and all values encountered in the given positions in the two states matched
//                    for (int i = 0; i < vars.length; i++) {
//                        //because the format is "var=val" format, we retrieve each world, split by'=' and take the second value
//                        if (!(sTemp[Integer.valueOf(StringUtils.trim(vars[i]))].split("="))[1].equalsIgnoreCase((s[Integer.valueOf(StringUtils.trim(vars[i]))].split("="))[1])) {
//                            flag = false;
//                            break;
//                        }
//                    }
//                    if (flag) {
//                        //this means the loop executed fully and all the position values are matched
//                        //so we connect up the two states concerned
//                        if (!StringUtils.contains(dotGraph.subSequence(0, dotGraph.length() - 1),
//                                (pairsStateLabel.getKey() + "--" + pairsTempStateLabel.getKey() + " [color=" + colors[colorIndex] + "];\n").subSequence(0, (pairsStateLabel.getKey() + "--" + pairsTempStateLabel.getKey() + " [color=" + colors[colorIndex] + "];\n").length() - 1))) {
//                            dotGraph.append(pairsStateLabel.getKey() + "--" + pairsTempStateLabel.getKey() + " [color=" + colors[colorIndex] + "];\n");
//                        }
//                    }
//                    //whether they matched or not, remove the other state (in the temp) from the search consideration
//                    itTempStateLabelMap.remove();
//                }
//                itStateLabelMap.remove();  // remove also the anchor state, so this agent does not redo the reverse pairs (e.g 2--4, later redone as 4--2)
//            }
//
////            for (int i = 0; i < vars.length; i++) {
////                //System.out.println("here3"); System.out.println("==>" + vars[i]);
////                Iterator itValStates = ((Map) varValuesStateMap.get(StringUtils.trim(vars[i]))).entrySet().iterator();
////                //System.out.println("here3*");
////                while (itValStates.hasNext()) {
////
////                    Map.Entry pairsValStates = (Map.Entry) itValStates.next();
////
////                    //System.out.println("Value \"" + pairsValStates.getKey() + "\" holds in the following states: ");
////                    //Iterator itArrayList = ((ArrayList)pairsValStates.getValue()).iterator();  //now to display the values in each array list
////                    Object[] states = ((ArrayList) pairsValStates.getValue()).toArray();  //now to display the values in each array list
////                    //System.out.println("here4");
////                    for (int j = 0; j < states.length; j++) {
////                        //System.out.println("here5");
////                        for (int k = j; k < states.length; k++) {
////                            //System.out.println("here6");
////                            //check first if that edge has been drawn for that agent before
////                            if (!StringUtils.contains(dotGraph.subSequence(0, dotGraph.length() - 1),
////                                    (states[j] + "--" + (String) states[k] + " [color=" + colors[colorIndex] + "];\n").subSequence(0, (states[j] + "--" + (String) states[k] + " [color=" + colors[colorIndex] + "];\n").length() - 1))) {
////                                dotGraph.append((String) states[j] + "--" + (String) states[k] + " [color=" + colors[colorIndex] + "];\n");
////                            }
////
////                        }
////                    }
////                    //while(itArrayList.hasNext()){
////                    //	String[] st
////                    //	System.out.println(itArrayList.next());
////                    //}
////                    //System.out.println("here7");
////                    // itValStates.remove(); // avoids a ConcurrentModificationException
////                }
////            }
//            //System.out.println("here8");
//            // it.remove(); // avoids a ConcurrentModificationException
//        }
//        //now label the nodes with their values
//        Iterator itStateLabel = stateLabelMap.entrySet().iterator();
//        StringBuffer sb = new StringBuffer();
//        while (itStateLabel.hasNext()) {
//
//            Map.Entry pairsSL = (Map.Entry) itStateLabel.next();
//            for (int i = 0; i < ((String[]) pairsSL.getValue()).length; i++) {
//                sb.append(((String[]) pairsSL.getValue())[i]).append(", ");
//
//            }
//            sb.delete(sb.length() - 2, sb.length() - 1);  //remove the last comma and space
//
//            dotGraph.append(pairsSL.getKey() + "[label=\"" + sb.toString() + "\"];\n");
//            System.out.println("State " + pairsSL.getKey() + " == " + sb.toString());
//            sb.delete(0, sb.length() - 1); // to start afresh
//
//            // itStateLabel.remove(); // avoids a ConcurrentModificationException
//        }
//        dotGraph.append("}\n");
//        try {
//            Formatter formatter = new Formatter(new File("/home/knight/Documents/CSAD/finalproject/codes/cup/generated_dots/" + Utilities.dotFileName));
//            formatter.format(dotGraph.toString(), "%s");
//            formatter.close();
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//
//    }
    
    
    public static void generateDot() {
        //Display agentVarMap
        // TreeMap tempQualifiedStateMap = new TreeMap(qualifiedStateMap); // duplicates this object to avoid tampering with the original as this method will modify the map in use
        TreeMap tempQualifiedStateMap = new TreeMap<Integer, Map<String, ArrayList<Integer>>>(); // duplicates this object to avoid tampering with the original as this method will modify the map in use
        // tempQualifiedStateMap.putAll(qualifiedStateMap);
        /////////////////

        // make a copy of the qualified state map. the of the constructor that takes a map to be copied from does not
        // work well because in this case we are copying from a static variable
        Iterator itStatesMap = qualifiedStateMap.entrySet().iterator();
        while (itStatesMap.hasNext()) {
            Map.Entry state = (Map.Entry) itStatesMap.next();
            tempQualifiedStateMap.put((Integer) state.getKey(), new TreeMap<String, ArrayList<Integer>>());
            Iterator itAgents = ((TreeMap) state.getValue()).entrySet().iterator();
            //build a big conjuction expression for all the successor states of all agents
            while (itAgents.hasNext()) {
                Map.Entry agent = (Map.Entry) itAgents.next();
                Iterator itSuccessors = ((ArrayList<Integer>) (agent.getValue())).iterator();
                ((TreeMap<String, ArrayList<Integer>>) tempQualifiedStateMap.get((Integer) state.getKey())).put((String) agent.getKey(), new ArrayList<Integer>());
                while (itSuccessors.hasNext()) {
                    ((TreeMap<String, ArrayList<Integer>>) tempQualifiedStateMap.get((Integer) state.getKey())).get((String) agent.getKey()).add((Integer) itSuccessors.next());
                    // String[] stateLabel = stateLabelMap.get((Integer) itSuccessors.next());
                }
            }
        }
//        


        Iterator itStates = tempQualifiedStateMap.entrySet().iterator();
        int colorIndex = -1;
        // Integer holdSuccessorState = 0;

        //initialising the dotGraph
        dotGraph.append("graph G { \n");
        //System.out.println("here1");
        while (itStates.hasNext()) {
            Map.Entry state = (Map.Entry) itStates.next();
            colorIndex = -1;
            Iterator itAgents = ((TreeMap) state.getValue()).entrySet().iterator();

            while (itAgents.hasNext()) {
                Map.Entry agent = (Map.Entry) itAgents.next();
                colorIndex++;

                System.out.println("******************************************************");
                System.out.println("color allocations");
                System.out.println("--------------------");

                System.out.println("Agent " + agent.getKey() + "==> " + colors[colorIndex]);

                Iterator itSuccessorStates = ((ArrayList<Integer>) agent.getValue()).iterator();

                while (itSuccessorStates.hasNext()) {
                    Integer holdSuccessorState = (Integer) itSuccessorStates.next();
                    //make the connection for this agent to this state
                    dotGraph.append(state.getKey()).append("--").append(holdSuccessorState).append(" [color=").append(colors[colorIndex]).append("];\n");

                    //remove the reverse pair so we don't have relations like 2--4 and 4--2
                    if (holdSuccessorState != state.getKey()) {
                        ((TreeMap<String, ArrayList<Integer>>) tempQualifiedStateMap.get(holdSuccessorState)).get((String) agent.getKey()).remove((Integer) state.getKey());
                    }
                }
            }

            String[] label = stateLabelMap.get((Integer) state.getKey());
            //now label the nodes with their values
            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < label.length; i++) {
                sb.append(label[i]).append(", ");

            }
            sb.delete(sb.length() - 2, sb.length() - 1);  //remove the last comma and space

            dotGraph.append(state.getKey()).append("[label=\"").append(state.getKey()).append(": ").append(sb.toString()).append("\"];\n");
            //    System.out.println("State " + pairsSL.getKey() + " == " + sb.toString());
            sb.delete(0, sb.length() - 1); // to start afresh

        }

        dotGraph.append("}\n");
        try {
            String baseDir = System.getProperty("user.dir").replace('\\', '/');
            Formatter formatter = new Formatter(new File(baseDir + "/dots/" + Utilities.dotFileName));
            formatter.format(dotGraph.toString(), "%s");
            formatter.close();
            System.out.println("the output file done");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        dotGraph.delete(0, dotGraph.length());

    }

    public static void generateDot(TreeMap pseudoQualifiedStateMap, String fileName) {
        //Display agentVarMap
        TreeMap tempQualifiedStateMap = new TreeMap(pseudoQualifiedStateMap); // duplicates this object to avoid tampering with the original as this method will modify the map in use

        Iterator itStates = tempQualifiedStateMap.entrySet().iterator();
        int colorIndex = -1;
        // Integer holdSuccessorState = 0;

        //initialising the dotGraph
        dotGraph.append("graph G { \n");
        //System.out.println("here1");
        while (itStates.hasNext()) {
            Map.Entry state = (Map.Entry) itStates.next();
            colorIndex = -1;
            Iterator itAgents = ((TreeMap) state.getValue()).entrySet().iterator();

            while (itAgents.hasNext()) {
                Map.Entry agent = (Map.Entry) itAgents.next();
                colorIndex++;

                System.out.println("******************************************************");
                System.out.println("color allocations");
                System.out.println("--------------------");

                System.out.println("Agent " + agent.getKey() + "==> " + colors[colorIndex]);

                Iterator itSuccessorStates = ((ArrayList<Integer>) agent.getValue()).iterator();

                while (itSuccessorStates.hasNext()) {
                    Integer holdSuccessorState = (Integer) itSuccessorStates.next();
                    //make the connection for this agent to this state
                    dotGraph.append(state.getKey()).append("--").append(holdSuccessorState).append(" [color=").append(colors[colorIndex]).append("];\n");

                    //remove the reverse pair so we don't have relations like 2--4 and 4--2
                    if (holdSuccessorState != state.getKey()) {
                        ((TreeMap<String, ArrayList<Integer>>) tempQualifiedStateMap.get(holdSuccessorState)).get((String) agent.getKey()).remove((Integer) state.getKey());
                    }
                }
            }

            String[] label = stateLabelMap.get((Integer) state.getKey());
            //now label the nodes with their values
            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < label.length; i++) {
                sb.append(label[i]).append(", ");

            }
            sb.delete(sb.length() - 2, sb.length() - 1);  //remove the last comma and space

            dotGraph.append(state.getKey()).append("[label=\"").append(state.getKey()).append(": ").append(sb.toString()).append("\"];\n");
            //    System.out.println("State " + pairsSL.getKey() + " == " + sb.toString());
            sb.delete(0, sb.length() - 1); // to start afresh

        }

        dotGraph.append("}\n");
        try {
            String baseDir = System.getProperty("user.dir").replace('\\', '/');
            Formatter formatter = new Formatter(new File(baseDir + "/dots/" + fileName));
            formatter.format(dotGraph.toString(), "%s");
            formatter.close();
            System.out.println("the output file done");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        dotGraph.delete(0, dotGraph.length());
    }

    public static void generateGraph() {
        // THIS METHOD WILL PREPARE THE qualifiedStateMap, which will be needed for property checking, searching and update of the graph
        Iterator it = agentVarMap.entrySet().iterator();

        while (it.hasNext()) {
            //AGENT SELECTION
            Map.Entry pairs = (Map.Entry) it.next();

            //now we've got another agent and the variable it controls. Down the line we'll see all the possible values of that variable and the states where they hold

            //vars represent the set of position numbers in the generated state label on which this agent has control
            //String[] vars = StringUtils.split(StringUtils.remove(StringUtils.remove(pairs.getValue().toString(), '['), ']'), ',');

            //KEEP AN EYE ON THE FOLLOWING LINE TO ENSURE THAT IT DOES NOT THROW NULL POINTER EXCEPTION WHEN 
            // AN AGENT DOES NOT HAVE ANY VARIABLE IT OBSERVES
            String[] vars = StringUtils.split(StringUtils.remove(StringUtils.remove(pairs.getValue().toString(), '['), ']'), ',');

            //iterate through all the states
            Map stateLabelMapAnchor = new TreeMap(stateLabelMap);
            Iterator itStateLabelMap = stateLabelMapAnchor.entrySet().iterator();

            while (itStateLabelMap.hasNext()) {
                //STATE ID SELECTION
                Map.Entry pairsStateLabel = (Map.Entry) itStateLabelMap.next();
                String[] s = (String[]) pairsStateLabel.getValue();

                //now compare posValues in this state to those of all other states, and connect it to all those states where all posValues are the same
                Map tempStateLabelMap = new TreeMap(stateLabelMapAnchor);  //duplicate the anchor (which also gets trimmed for avoid reverse (duplicate entries))
                Iterator itTempStateLabelMap = tempStateLabelMap.entrySet().iterator();
                while (itTempStateLabelMap.hasNext()) {
                    Map.Entry pairsTempStateLabel = (Map.Entry) itTempStateLabelMap.next();
                    String[] sTemp = (String[]) pairsTempStateLabel.getValue();
                    // Object[] posArrayValue = posValues.toArray();
                    boolean flag = true; //used to detect if the loop executed fully and all values encountered in the given positions in the two states matched
                    // Iterate through all the variables that this agent observes
                    //NOT THAT IF VARS IS EMPTY (I.E. THE AGENT DOES NOT OBSERVE ANY VARIABLE,
                    //THEN THIS ENTIRE "FOR" BLOCK IS SKIPPED AND FLAG REMAINS TRUE, THEREBY FORCING THIS
                    // AGENT TO HAVE ALL STATES AS ITS SUCCESSORS...THIS IS INSTRUMENTAL IN SOME PUZZLES
                    for (int i = 0; i < vars.length; i++) {
                        //search for this variable in the left hand side of each state value (id=val) of this state
                        for (int j = 0; j < sTemp.length; j++) {
                            if (StringUtils.trim(vars[i]).equalsIgnoreCase(sTemp[j].split("=")[0])) {
                                //now we've found a match (of this observed variable) among the state fields (id=val)
                                // so we now compare the right hand sides of the corresponding indices (which contains the value
                                // of the variable for the current state (s) and the successor state in question (sTemp)
                                // the same index (j) will work for both since the map from which sTemp was obtained is just
                                // a copy of the map to which s belongs
                                if (!sTemp[j].split("=")[1].equalsIgnoreCase(s[j].split("=")[1])) {
                                    flag = false;
                                    break;
                                }
                            }

                        }
                        //because the format is "var=val" format, we retrieve each world, split by'=' and take the second value
//                        if (!(sTemp[Integer.valueOf(StringUtils.trim(vars[i]))].split("="))[1].equalsIgnoreCase((s[Integer.valueOf(StringUtils.trim(vars[i]))].split("="))[1])) {
//                            flag = false;
//                            break;  //no need checking other variable(s) in the state
//                        }
                    }
                    if (flag) {
                        //this means the loop executed fully and all the position values are matched
                        //so we connect up the two states concerned
                        if (qualifiedStateMap.containsKey((Integer) pairsStateLabel.getKey())) {
                            if (qualifiedStateMap.get((Integer) pairsStateLabel.getKey()).containsKey((String) pairs.getKey())) {
                                if (!qualifiedStateMap.get((Integer) pairsStateLabel.getKey()).get((String) pairs.getKey()).contains((Integer) pairsTempStateLabel.getKey())) {
                                    qualifiedStateMap.get((Integer) pairsStateLabel.getKey()).get((String) pairs.getKey()).add(/*successor state for this agent id*/(Integer) pairsTempStateLabel.getKey());
                                }
                            } else {
                                qualifiedStateMap.get((Integer) pairsStateLabel.getKey()).put(/*agent id*/(String) pairs.getKey(), new ArrayList<Integer>());
                                qualifiedStateMap.get((Integer) pairsStateLabel.getKey()).get((String) pairs.getKey()).add(/*successor state for this agent id*/(Integer) pairsTempStateLabel.getKey());
                            }
                        } else {
                            qualifiedStateMap.put(/*state id*/(Integer) pairsStateLabel.getKey(), new TreeMap<String, ArrayList<Integer>>());
                            qualifiedStateMap.get((Integer) pairsStateLabel.getKey()).put(/*agent id*/(String) pairs.getKey(), new ArrayList<Integer>());
                            qualifiedStateMap.get((Integer) pairsStateLabel.getKey()).get((String) pairs.getKey()).add(/*successor state for this agent id*/(Integer) pairsTempStateLabel.getKey());

                        }
                        //  dotGraph.append(pairsStateLabel.getKey() + "--" + pairsTempStateLabel.getKey() + " [color=" + colors[colorIndex] + "];\n");
                    }
                    //whether they matched or not, remove the other state (in the temp) from the search consideration
                    // itTempStateLabelMap.remove();
                }
                // I NEED NOT THEN REMOVE THIS SINCE I WANT THE REVERSE TO OCCUR
                //     itStateLabelMap.remove();  // remove also the anchor state, so this agent does not redo the reverse pairs (e.g 2--4, later redone as 4--2)
            }

        }

    }

    public static void displayStateLabelMap() {
        //Display stateLabelMap
        StringBuilder sb = new StringBuilder();
        Iterator itStateLabel = stateLabelMap.entrySet().iterator();

        while (itStateLabel.hasNext()) {

            Map.Entry pairsSL = (Map.Entry) itStateLabel.next();
            for (int i = 0; i < ((String[]) pairsSL.getValue()).length; i++) {
                sb.append(((String[]) pairsSL.getValue())[i]).append(", ");

            }
            sb.delete(sb.length() - 2, sb.length() - 1);  //remove the last comma and space

            System.out.println("State " + pairsSL.getKey() + " == " + sb.toString());
            sb.delete(0, sb.length() - 1); // to start afresh

            // itStateLabel.remove(); // avoids a ConcurrentModificationException
        }
    }

    public static void displayVarValuesStateMap() {

        //Display varValuesStateMap
        Iterator itVarValues = varValuesStateMap.entrySet().iterator();
        Map mapHold;
        System.out.println();
        while (itVarValues.hasNext()) {
            Map.Entry pairsVarVals = (Map.Entry) itVarValues.next();
            System.out.println("Variable == " + pairsVarVals.getKey());
            System.out.println("--------------------------------");
            //Iterator itValStates = pairsVarVals.getValue().entrySet().iterator();
            mapHold = (Map) pairsVarVals.getValue();
            Iterator itValStates = mapHold.entrySet().iterator();
            while (itValStates.hasNext()) {
                Map.Entry pairsValStates = (Map.Entry) itValStates.next();
                System.out.println("Value \"" + pairsValStates.getKey() + "\" holds in the following states: ");
                Iterator itArrayList = ((ArrayList) pairsValStates.getValue()).iterator();  //now to display the values in each array list
                while (itArrayList.hasNext()) {
                    System.out.println(itArrayList.next());
                }
                //  itValStates.remove(); // avoids a ConcurrentModificationException
            }
            // itVarValues.remove(); // avoids a ConcurrentModificationException
        }

    }

    public static void processAtoms() {
        //I'm coming to replace the following line with a generic combination generator
        //such that we construct the class with just the symbol table
        //the symbol table contains the list of all the atoms, their type and their range of values
        //in principle there is no need to specify range of values of a boolean type
        // this should also serve to handle the enum type ;)

        //  BoolCombinationGenerator bcg = new BoolCombinationGenerator(boolNamesList.size());
        Utilities.generateStateValues();
        GenCombinationGenerator bcg = new GenCombinationGenerator(Utilities.stateValues.length, Utilities.partition.length);
        String[] s = new String[0];
        String[] worlds = bcg.generateAll(Utilities.stateValues, Utilities.partition);
        ArrayList<String> stateVar = new ArrayList<String>();
        Iterator stateIterator;
        String stringWONeg = ""; // String without negative sign
        //System.out.println("Here 1");
        for (int i = 0; i < worlds.length; i++) {
            String string = worlds[i];

            //split the comma separated label and attach a state number to them
            stateVar = new ArrayList(Arrays.asList(StringUtils.split(string, ',')));
            String[] stateVarArray = StringUtils.split(string, ',');

            //System.out.println("Here 1");
            stateNumber++;
            stateLabelMap.put(stateNumber, stateVarArray);
            stateIterator = stateVar.iterator();
            //System.out.println("Here 2");
            while (stateIterator.hasNext()) {
                string = (String) stateIterator.next();
                stringWONeg = StringUtils.remove(string, '\u00ac');  //for the case of int type, to discover the variable proper (where values are expressed as say p=10) we split by '=' and pick the first element (which in this case is "p")
                if (varValuesStateMap.containsKey(stringWONeg)) {
                    //  	System.out.println("Here 3");
                    if (varValuesStateMap.get(stringWONeg).containsKey(string)) {
                        varValuesStateMap.get(stringWONeg).get(string).add(stateNumber.toString()); //add the state number for this variable value
                        //varValuesStateMap.get(stringWONeg).put(string, add(knId)));
                        //	  	System.out.println("Here 4");
                    } else {
                        varValuesStateMap.get(stringWONeg).put(string, new ArrayList<String>());
                        varValuesStateMap.get(stringWONeg).get(string).add(stateNumber.toString());
                        //	  	System.out.println("Here 5");
                    }
                } else {
                    varValuesStateMap.put(stringWONeg, new TreeMap<String, ArrayList<String>>());
                    varValuesStateMap.get(stringWONeg).put(string, new ArrayList<String>());
                    varValuesStateMap.get(stringWONeg).get(string).add(stateNumber.toString());
                    //	  System.out.println("Here 6");

                }
            }
            //System.out.println(string);
        }
    }

    public static void solveEpistemicConstraints() {
        //this is where I'll call the constraint evaluator parser
        //for each of the epistemic constraints in the list of constraints
        String aConstraint, lVar, rVal;


        for (Integer i = 0; i < Utilities.epistemicConstraints.size(); i++) {
            //Iterator constraintIterator = Utilities.epistemicConstraints.get(i).entrySet().iterator();
            String[] itConstraint = Utilities.epistemicConstraints.get(i).keySet().toArray(new String[0]);
            // aConstraint = constraintEntry.getKey().toString();

            aConstraint = itConstraint[0];

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
            //to get the variables involved in it
            // String rVal = new String[((ArrayList<String>) constraintEntry.getValue()).size() - 1];
            //  lVar = ((ArrayList<String>) constraintEntry.getValue()).get(0);
            //fish out all the other entries
            //now evaluate this constraint
            aConstraint = aConstraint.replaceAll("\\[", "%");
            aConstraint = aConstraint.replaceAll("]", "%");
            System.out.println(aConstraint);
            //  aConstraint = "[".concat(aConstraint).concat(";]"); // to put it in the right format for our constant evaluator
            EpistemicConstraintEvaluator ece = new EpistemicConstraintEvaluator(new BaseScanner(new StringReader(aConstraint)));
            //String result = "";
            try {
                ece.parse();  //no return value, the graph is updated automatically once an epistemic constraint is evaluated fully
            } catch (Exception ex) {
                ex.printStackTrace();
            }


        }


    }

    public static void updateGraph(Object[] truthAndFalseStates) {
        //called from the parse tree after evaluating each epistemic constraint
        HashSet<Integer> truthStates = (HashSet<Integer>) truthAndFalseStates[0];
        HashSet<Integer> falseStates = (HashSet<Integer>) truthAndFalseStates[1];
        //remove all but only those states contained in the truth set
        Iterator itFalseStates = falseStates.iterator();
        while (itFalseStates.hasNext()) {
            qualifiedStateMap.remove((Integer) itFalseStates.next());
        }
        // qualifiedStateMap.entrySet().retainAll((Collection<Integer>)truthStates);

        //but some of the remaining states in the qualifiedStateMap still see a link
        //between them and the removed states. So we go now to all the remaining states and remove the 
        //links they have to the false states

        //iterate through all the states

        Iterator itStates = qualifiedStateMap.entrySet().iterator();
        while (itStates.hasNext()) {
            Map.Entry state = (Map.Entry) itStates.next();
            Iterator itAgents = ((TreeMap) state.getValue()).entrySet().iterator();
            while (itAgents.hasNext()) {
                ((ArrayList<Integer>) ((Map.Entry) itAgents.next()).getValue()).removeAll(falseStates);
            }
            // ((ArrayList<Integer>) ((TreeMap) state.getValue()).get(agentId)).iterator();

        }

        // and we are done now, ready to plot the updated graph
    }

    public static Object[] solveGlobalBooleanExpression(String booleanExpression) {
        //StringBuilder expression = new StringBuilder();
        String result = "";
        String boolExpr;

        //    booleanExpression = StringUtils.remove(StringUtils.remove(booleanExpression, "["), ";]");
        String holdBooleanExpression = booleanExpression;
        // the first index of the below object will keep the state ids where the given expression is true
        // the second index will keep the state ids where the given expression is false
        Object[] objStates = new Object[]{new HashSet<Integer>(), new HashSet<Integer>()};

        //iterate through all the states
        Iterator itStates = qualifiedStateMap.entrySet().iterator();
        while (itStates.hasNext()) {
            Map.Entry state = (Map.Entry) itStates.next();

            String[] stateLabel = stateLabelMap.get((Integer) state.getKey());
            //use the inEpistemicConstraints identifiers to fish out the relevant fields from this state
            for (int i = 0; i < stateLabel.length; i++) {
                //each state label is sth like q=5...
                //we split according to the equal sign, and replace all q with 5 (for example) in the boolean expression
                booleanExpression = booleanExpression.replaceAll(StringUtils.split(stateLabel[i], "=")[0].trim().concat("="), StringUtils.split(stateLabel[i], "=")[1].trim().concat("="));
            }

            // Now add back those begining '[' and ending ';]' to now complete the boolean expression for parsing
            boolExpr = "[".concat(booleanExpression).concat(";]");
            //restore it in readiness for the next state
            booleanExpression = holdBooleanExpression;
            System.out.println("@@@@@@@@@@@@@@@@@@@@The pre-epist boolean expressionis " + boolExpr);
            //now evaluate the resulting boolean expression
            ConstraintEvaluator ce = new ConstraintEvaluator(new BaseScanner(new StringReader(boolExpr)));

            try {
                result = ce.parse().value.toString();
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            //check if result is true
            if (result.equalsIgnoreCase("true")) {
                //update the list of states where K(phi) is true
                ((HashSet<Integer>) objStates[0]).add((Integer) state.getKey());
            } else {
                ((HashSet<Integer>) objStates[1]).add((Integer) state.getKey());
            }

        }
        return objStates;
    }

    public static Object[] solveGlobalBooleanExpressionMC(String booleanExpression) {
        // This version is called from the model checker! The only difference is that it uses a different 
        //scanner
        //StringBuilder expression = new StringBuilder();
        String result = "";
        String boolExpr;

        //    booleanExpression = StringUtils.remove(StringUtils.remove(booleanExpression, "["), ";]");
        String holdBooleanExpression = booleanExpression;
        // the first index of the below object will keep the state ids where the given expression is true
        // the second index will keep the state ids where the given expression is false
        Object[] objStates = new Object[]{new HashSet<Integer>(), new HashSet<Integer>()};

        //iterate through all the states
        Iterator itStates = qualifiedStateMap.entrySet().iterator();
        while (itStates.hasNext()) {
            Map.Entry state = (Map.Entry) itStates.next();

            String[] stateLabel = stateLabelMap.get((Integer) state.getKey());
            //use the inEpistemicConstraints identifiers to fish out the relevant fields from this state
            for (int i = 0; i < stateLabel.length; i++) {
                //each state label is sth like q=5...
                //we split according to the equal sign, and replace all q with 5 (for example) in the boolean expression
                booleanExpression = booleanExpression.replaceAll(StringUtils.split(stateLabel[i], "=")[0].trim().concat("="), StringUtils.split(stateLabel[i], "=")[1].trim().concat("="));
            }

            // Now add back those begining '[' and ending ';]' to now complete the boolean expression for parsing
            boolExpr = "[".concat(booleanExpression).concat(";]");
            //restore it in readiness for the next state
            booleanExpression = holdBooleanExpression;
            System.out.println("@@@@@@@@@@@@@@@@@@@@The pre-epist boolean expressionis " + boolExpr);
            //now evaluate the resulting boolean expression
            ConstraintEvaluator ce = new ConstraintEvaluator(new ModelCheckerScanner(new StringReader(boolExpr)));

            try {
                result = ce.parse().value.toString();
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            //check if result is true
            if (result.equalsIgnoreCase("true")) {
                //update the list of states where K(phi) is true
                ((HashSet<Integer>) objStates[0]).add((Integer) state.getKey());
            } else {
                ((HashSet<Integer>) objStates[1]).add((Integer) state.getKey());
            }

        }
        //add this to the history, for use later in model checking
        //: NOTE THIS: I'm commenting out the following so that when we have a boolean property that is false,
        // it only produces the state in which that property is false as counter model
      ////  truthFalseStateHistory.add(objStates);
        return objStates;
    }

    public static void processFacts(String agId, String knId) {
        //An important job this method does is to associate an agent with a position held by a variable which the agent is said to control, in the symbol table
        // this position will be maintained when the state variables will be generated and we will use it to draw the graphs and for model checking
        //String[] s = symbolTable.get(knId).toArray(new String[0]);
        if (agentVarMap.containsKey(agId)) {
            // boolNamesList.toArray(new String[0]);
            agentVarMap.get(agId).add(knId);  //access that second slot that contains the integer values (that is, the position of this variable in the list of state variables); we then insert the positon number of the variable this agent controls
        } else {
            agentVarMap.put(agId, new ArrayList<String>());
            agentVarMap.get(agId).add(knId);
        }

    }

    public static void processEpistemicFacts(String epistemicFact) {

        //line up the epistemic constraints
        Utilities.epistemicConstraints.put(Utilities.epistemicConstraintPriority, new TreeMap<String, ArrayList<String>>());
        Utilities.inEpistemicConstraintIdentifiers.add(0, Utilities.NOLVALUE);  // adds the lvar at the first position in the list of identifiers. In this expression, there are no lvalues, thus the Utilities.NOVALUE entry      
        Utilities.epistemicConstraints.get(Utilities.epistemicConstraintPriority).put(epistemicFact, new ArrayList<String>(Utilities.inEpistemicConstraintIdentifiers));

        //clear the inConstraintIdentifiers variable in readiness for another constraint
        Utilities.inEpistemicConstraintIdentifiers.clear();
        //increament the constraintsPriority in readiness for the next constraint
        Utilities.epistemicConstraintPriority++;

        //select a state s
        //get all the successor states v following this agent's accessibility
        //test the boolean expression on these states
        //i.e. substitute the state variables values in the expression, for each state tested
        //use the AND operator to concatenate expressions for all successor states v
        // execute the resulting expression and return the value
        // if returned value is true, save the originating state s  (vice versa for NOT KNOWS)
        // else discard it
    }

    public static void processBoolDeclaration(String boolId) {
        symbolTable.put(boolId, new ArrayList<String>());
        symbolTable.get(boolId).add("boolean"); //the first slot denotes the type of the identifier
        symbolTable.get(boolId).add(Utilities.positionCount.toString());// the second slot is for the position of this variable in the eventual string label for the states
        symbolTable.get(boolId).add(boolId + "=false");// at the third place we start putting the possible values for this variable
        symbolTable.get(boolId).add(boolId + "=true");  // so it appears like q=true
        //refresh positions of variables
        Utilities.refreshPositions(symbolTable);
        Utilities.positionCount++;  //increament it for the next round
        //remove the following later, because we can do a boiler plate function that can generate it from the 'symbolTable'
        boolNamesList.add(boolId);  // you might have to later use a single symbol table in which you specify
        // the name of the identifier, its type and other information about the identifier...
        //System.out.println("The symbol is " + boolId); 
        //symbolTable.entrySet().
    }

    public static void processIntDeclaration(String intId, Double lowerLimit, Double upperLimit) {
        symbolTable.put(intId, new ArrayList<String>());
        symbolTable.get(intId).add("integer"); //the first slot denotes the type of the identifier
        symbolTable.get(intId).add(Utilities.positionCount.toString());// the second slot is for the position of this variable in the eventual string label for the states
        //NOW GENERATE ALL THE POSSIBLE VALUES, FROM LOWER LIMIT TO UPPER LIMIT
        for (Double i = lowerLimit; i <= upperLimit; i++) {
            symbolTable.get(intId).add(intId + "=" + i);// at the third place we start putting the possible values for this variable            
        }

        //refresh positions of variables
        Utilities.refreshPositions(symbolTable);
        Utilities.positionCount++;  //increament it for the next round

    }

    public static void processEnumDeclaration(String enumId, String enumValueList) {
        symbolTable.put(enumId, new ArrayList<String>());
        symbolTable.get(enumId).add("enum"); //the first slot denotes the type of the identifier
        symbolTable.get(enumId).add(Utilities.positionCount.toString());// the second slot is for the position of this variable in the eventual string label for the states
        //NOW GENERATE ALL THE POSSIBLE VALUES FROM THE ENUM VALUES
        String[] enumValues = enumValueList.split(",");
        for (int i = 0; i < enumValues.length; i++) {
            symbolTable.get(enumId).add(enumId + "=" + enumValues[i]);
        }

        //refresh positions of variables
        Utilities.refreshPositions(symbolTable);
        Utilities.positionCount++;  //increament it for the next round

    }

    public static void processDynamicDeclaration(String lVar) {
        symbolTable.put(lVar, new ArrayList<String>());
        symbolTable.get(lVar).add("dynamic"); //the first slot denotes the type of the identifier
        symbolTable.get(lVar).add(Utilities.positionCount.toString());// the second slot is for the position of this variable in the eventual string label for the states
        // the two Utilities.NOVALUE entries hold the place of the start and end values for this variable. recall that for boolean these values are just 'true' and 'false' and nothing more
        // for in this value will be the begining and end range that will then help us to generate the possible values for that variable
        //symbolTable.get(lVar).add(lVar + Utilities.NOLVALUE);// at the third place we start putting the possible values for this variable
        //symbolTable.get(lVar).add(lVar + Utilities.NOLVALUE);  // so it appears like q=true
        //refresh positions of variables
        Utilities.refreshPositions(symbolTable);
        Utilities.positionCount++;  //increament it for the next round
        Utilities.noOfDynamicVars++;
        //remove the following later, because we can do a boiler plate function that can generate it from the 'symbolTable'
        //boolNamesList.add(boolId);  // you might have to later use a single symbol table in which you specify
        // the name of the identifier, its type and other information about the identifier...
        //System.out.println("The symbol is " + boolId); 
//symbolTable.entrySet().
    }

    public static void processAgentDeclaration(String agentId) {

        agentNamesList.add(agentId);
        //very important addition follows, since it will guarantee that all the declared
        // agents have an entry which contains the positions they observe
        // if an agent ends up not observing any position at all, then it's own array list becomes
        // empty; a consequence of this is that when the graph is generated, such agent will have all the states as its
        // successor, for every state
        agentVarMap.put(agentId, new ArrayList<String>());
    }

    public static void processBoolExpression(String be) {
        Utilities.constraints.put(Utilities.constraintPriority, new TreeMap<String, ArrayList<String>>());
        Utilities.inConstraintIdentifiers.add(0, Utilities.NOLVALUE);  // adds the lvar at the first position in the list of identifiers. In this expression, there are no lvalues, thus the Utilities.NOVALUE entry      
        Utilities.constraints.get(Utilities.constraintPriority).put(be, new ArrayList<String>(Utilities.inConstraintIdentifiers));

        // Utilities.constraints.get(Utilities.constraintPriority).get(be).add(Utilities.inConstraintIdentifiers);  // no lvar for static constraints
        //clear the inConstraintIdentifiers variable in readiness for another constraint
        Utilities.inConstraintIdentifiers.clear();
        //increament the constraintsPriority in readiness for the next constraint
        Utilities.constraintPriority++;
    }

    public static void processDynamicExpression(String id, String e) {
        //first insert the lVar into the symbol table
        Utilities.processDynamicDeclaration(id);
        // the difference between this and the static_constraint is that the lvar is no longer none
        Utilities.constraints.put(Utilities.constraintPriority, new TreeMap<String, ArrayList<String>>());
        Utilities.inConstraintIdentifiers.add(0, id);  // adds the lvar at the first position in the list of identifiers
        Utilities.constraints.get(Utilities.constraintPriority).put(e, new ArrayList<String>(Utilities.inConstraintIdentifiers));

        //Utilities.constraints.get(Utilities.constraintPriority).get(e).add(Utilities.inConstraintIdentifiers);  // no lvar for static constraints
        //clear the inConstraintIdentifiers variable in readiness for another constraint
        Utilities.inConstraintIdentifiers.clear();
        //increament the constraintsPriority in readiness for the next constraint
        Utilities.constraintPriority++;
    }

    //This method is a helper method which extracts the possible values of all the variables from the
    // symbol table, it then keeps these, together with the partition markers, so the combination
    // generator can use these information to generate all the possible combinations of the state values
    // The partition markers tell us where the possible values of a given variable start and end in the 
    // one long array containing all the values of all variables in the symbol table. The combination
    // generator uses these partition markers to ensure that no generated combination contains more than
    // one value from the same partition, thus we prevents values of a given variable from taking more that 
    // slot in a given generated state label's values list (e.g having sth like p and ~p in a given state label is 
    // is prevented.
    public static void generateStateValues() {
        //using the symbol table
        //Display varValuesStateMap
        Iterator itSymbols = Utilities.symbolTable.entrySet().iterator();

        String[] partitions = new String[Utilities.symbolTable.size() - Utilities.noOfDynamicVars];  // the number of variables tell us the number of partitions
        ArrayList<String> elementsList = new ArrayList<String>();
        int startIndex = 0;
        int endIndex = -1;
        int partitionIndex = 0;
        while (itSymbols.hasNext()) {
            Map.Entry pairsSymbols = (Map.Entry) itSymbols.next();
            String[] vals = ((ArrayList<String>) pairsSymbols.getValue()).toArray(new String[0]);
            if (!vals[0].equalsIgnoreCase("dynamic")) {  //only create partition for non-dynamic variables because at this point the dynamic variables have no values yet
                for (int i = 2; i < vals.length; i++) {  // i starts at 2 because 0 contains the type of the variable, 1 contains the position of that variable in the (to be generated) label, 2 onwards contains the possible values for this variable
                    elementsList.add(vals[i]);
                    endIndex++;
                }
                partitions[partitionIndex] = startIndex + "," + endIndex;
                partitionIndex++;
                startIndex = endIndex + 1;
            }
        }

        Utilities.stateValues = elementsList.toArray(new String[0]);
        Utilities.partition = partitions;

    }

    public static Integer refreshPositions(Map map) {
        Iterator itSymbols = map.entrySet().iterator();
        Integer count = -1;
        while (itSymbols.hasNext()) {
            count++;
            ((ArrayList<String>) ((Map.Entry) itSymbols.next()).getValue()).set(1, count.toString());
//            if(((Map.Entry) itSymbols.next()).getKey().toString().equalsIgnoreCase(key)){
//              return count;
//            } 
        }
        return count;   // I see now no need for this return value
    }

    public static Object[] epistemicExpressionAND(Object[] firstArg, Object[] secondArg) {
        ((HashSet) firstArg[0]).retainAll((HashSet) secondArg[0]);
        HashSet temp2 = new HashSet(Utilities.qualifiedStateMap.keySet());
        temp2.removeAll((HashSet) firstArg[0]);
        firstArg[1] = temp2;

        return firstArg;
    }

    public static Object[] epistemicExpressionANDMC(Object[] firstArg, Object[] secondArg) {
        ((HashSet) firstArg[0]).retainAll((HashSet) secondArg[0]);
        HashSet temp2 = new HashSet(Utilities.qualifiedStateMap.keySet());
        temp2.removeAll((HashSet) firstArg[0]);
        firstArg[1] = temp2;

        //store in history, to aid in finding counter model in model checking
        truthFalseStateHistory.add(firstArg);
        return firstArg;
    }

    public static Object[] epistemicExpressionOR(Object[] firstArg, Object[] secondArg) {
        ((HashSet) firstArg[0]).addAll((HashSet) secondArg[0]);
        HashSet temp2 = new HashSet(Utilities.qualifiedStateMap.keySet());
        temp2.removeAll((HashSet) firstArg[0]);
        firstArg[1] = temp2;
        return firstArg;
    }

    public static Object[] epistemicExpressionORMC(Object[] firstArg, Object[] secondArg) {
        ((HashSet) firstArg[0]).addAll((HashSet) secondArg[0]);
        HashSet temp2 = new HashSet(Utilities.qualifiedStateMap.keySet());
        temp2.removeAll((HashSet) firstArg[0]);
        firstArg[1] = temp2;

        //store in history, to aid in finding counter model in model checking
        truthFalseStateHistory.add(firstArg);
        return firstArg;
    }

    public static Object[] epistemicExpressionIMPLIES(Object[] firstArg, Object[] secondArg) {
        //NOT(firstArg)
        Object temp = firstArg[0];
        firstArg[0] = firstArg[1];
        firstArg[1] = temp;

        //implies --> i.e SET UNION
        ((HashSet) firstArg[0]).addAll((HashSet) secondArg[0]);
        //the complement set
        HashSet temp2 = new HashSet(Utilities.qualifiedStateMap.keySet());
        temp2.removeAll((HashSet) firstArg[0]);
        firstArg[1] = temp2;
        return firstArg;
    }

    public static Object[] epistemicExpressionIMPLIESMC(Object[] firstArg, Object[] secondArg) {
        //NOT(firstArg)
        Object temp = firstArg[0];
        firstArg[0] = firstArg[1];
        firstArg[1] = temp;

        //implies --> i.e SET UNION
        ((HashSet) firstArg[0]).addAll((HashSet) secondArg[0]);
        //the complement set
        HashSet temp2 = new HashSet(Utilities.qualifiedStateMap.keySet());
        temp2.removeAll((HashSet) firstArg[0]);
        firstArg[1] = temp2;

        //store in history, to aid in finding counter model in model checking
        truthFalseStateHistory.add(firstArg);
        return firstArg;
    }

    public static Object[] knowsEpistemicExpression(String agentId, Object[] objStates) {
        // This method is used to solve recursive K expressions
        // object states already contains the set of states where 'lower' expressions are true
        // (in addition to where they are false)
        // the lowest expression is the boolean expression part of the epistemic_expr production
        // the boolean category is solved using the knowsBooleanExpression method which returns 
        // the set of states where the given agent knows the boolean expression. The knowsBooleanExpression
        // method is always the first to be executed in the chain of recursive functions, so it keeps
        //returning the set of states that we use in high up functions like this one
        StringBuilder expression = new StringBuilder();
        // String result = "";
        // the first index of the below object will keep the state ids where the given expression is true
        // the second index will keep the state ids where the given expression is false
        Object[] anotherObjStates = new Object[]{new HashSet<>(), new HashSet<Integer>()};

        //iterate through all the states
        Iterator itStates = qualifiedStateMap.entrySet().iterator();
        HashSet<Integer> successorStates = new HashSet<>();
        while (itStates.hasNext()) {
            Map.Entry state = (Map.Entry) itStates.next();
            Iterator itSuccessors = ((ArrayList) ((TreeMap) state.getValue()).get(agentId)).iterator();
            while (itSuccessors.hasNext()) {
                //String[] stateLabel = stateLabelMap.get((Integer)itSuccessors.next());
                //build a set of the successor states of this agent
                successorStates.add((Integer) itSuccessors.next());

            }
            //now check if all the successor states are all contained in the 
            // set of states where "knows" is true
            if (((HashSet) objStates[0]).containsAll(successorStates)) {
                ((HashSet<Integer>) anotherObjStates[0]).add((Integer) state.getKey());
            } else {
                ((HashSet<Integer>) anotherObjStates[1]).add((Integer) state.getKey());
            }
            //now clean up the successorStates set to make it ready to capture the successor states of the next state
            // this issue of not cleaning up a used object before next round has appeared other times here
            //giving rise to bugs; be more careful about it
            successorStates.clear();

        }
        return anotherObjStates;
    }

    public static Object[] knowsEpistemicExpressionMC(String agentId, Object[] objStates) {
        // This method is used to solve recursive K expressions
        // object states already contains the set of states where 'lower' expressions are true
        // (in addition to where they are false)
        // the lowest expression is the boolean expression part of the epistemic_expr production
        // the boolean category is solved using the knowsBooleanExpression method which returns 
        // the set of states where the given agent knows the boolean expression. The knowsBooleanExpression
        // method is always the first to be executed in the chain of recursive functions, so it keeps
        //returning the set of states that we use in high up functions like this one
        StringBuilder expression = new StringBuilder();
        // String result = "";
        // the first index of the below object will keep the state ids where the given expression is true
        // the second index will keep the state ids where the given expression is false
        Object[] anotherObjStates = new Object[]{new HashSet<>(), new HashSet<>()};

        //iterate through all the states
        Iterator itStates = qualifiedStateMap.entrySet().iterator();
        HashSet<Integer> successorStates = new HashSet<>();
        while (itStates.hasNext()) {
            Map.Entry state = (Map.Entry) itStates.next();
            Iterator itSuccessors = ((ArrayList) ((TreeMap) state.getValue()).get(agentId)).iterator();
            while (itSuccessors.hasNext()) {
                //String[] stateLabel = stateLabelMap.get((Integer)itSuccessors.next());
                //build a set of the successor states of this agent
                successorStates.add((Integer) itSuccessors.next());

            }
            //now check if all the successor states are all contained in the 
            // set of states where "knows" is true
            if (((HashSet) objStates[0]).containsAll(successorStates)) {
                ((HashSet<Integer>) anotherObjStates[0]).add((Integer) state.getKey());
            } else {
                ((HashSet<Integer>) anotherObjStates[1]).add((Integer) state.getKey());
            }
            //now clean up the successorStates set to make it ready to capture the successor states of the next state
            // this issue of not cleaning up a used object before next round has appeared other times here
            //giving rise to bugs; be more careful about it
            successorStates.clear();

        }

        //store in history, to aid in finding counter model in model checking
        truthFalseStateHistory.add(anotherObjStates);
        return anotherObjStates;
    }

    //for handling stuff like IDENTIFIER knows IDENTIFIER e.g sum knows x
    public static Object[] knowsPHIExpression(String agentId, String phiExpression) {
        StringBuilder expression = new StringBuilder();
        String result = "";
        String holdPhi = phiExpression;

        // the first index of the below object will keep the state ids where the given expression is true
        // the second index will keep the state ids where the given expression is false
        Object[] objStates = new Object[]{new HashSet<>(), new HashSet<>()};

        //iterate through all the states
        Iterator itStates = qualifiedStateMap.entrySet().iterator();
        while (itStates.hasNext()) {
            Map.Entry state = (Map.Entry) itStates.next();
            Iterator itSuccessors = ((ArrayList) ((TreeMap) state.getValue()).get(agentId)).iterator();

            //for the phi expression, say "liv knows s", what I want to do is to rewrite it as
            // "liv knows s==val" where val is the value of s at the selected state (that is the state for which 
            // we would examine the successors.
            // so I take the values from this state (e.g s=10) split it and replace it with "s==10" so we can then evaluate
            // or check it against all successor states (itself being a successor state). Any deviation from this value
            // among the successor states means that liv does not know s at that state, so we drop it as usual.
            //HOWEVER, THERE IS A PROBLEM, THE REPLACEMENT OF STRING WILL CAUSE PROBLEMS IF WE HAVE 
            // VARIABLES LIKE "ab" and "a": in this case if we replace "a" before replacing "ab" we could get a 
            // string like "a==5b". So we must adjust the replace all regex argument to say that whatever string identifier is 
            // followed by NO alphanumeric character

            String[] stateSelfLabel = stateLabelMap.get((Integer) state.getKey());
            for (int i = 0; i < stateSelfLabel.length; i++) {
                phiExpression = phiExpression.replaceAll(stateSelfLabel[i].split("=")[0].trim(), stateSelfLabel[i].split("=")[0].trim().concat("==").concat(stateSelfLabel[i].split("=")[1].trim()));
            }
            String holdReWrittenPhi = phiExpression;
            // within the following while loop, each state will now subtitute the "s" or lvar with their own value, and evaluate the boolean expression
            while (itSuccessors.hasNext()) {
                String[] stateLabel = stateLabelMap.get((Integer) itSuccessors.next());
                //use the inEpistemicConstraints identifiers to fish out the relevant fields from this state
                for (int i = 0; i < stateLabel.length; i++) {
                    //each state label is sth like q=5...
                    //we split according to the equal sign, and replace all q with 5 (for example) in the boolean expression
                    phiExpression = phiExpression.replaceAll(StringUtils.split(stateLabel[i], "=")[0].trim(), StringUtils.split(stateLabel[i], "=")[1].trim());
                }
                //make a conjuction of all the expressions for each of the successor states
                expression.append("(").append(phiExpression).append(")&&");
                phiExpression = holdReWrittenPhi;
            }
            //remove the last && symbol
            expression.delete(expression.length() - 2, expression.length());

            System.out.println("@@@@@@@@@@@@@@@@@@@@Teh boolean expressionis " + expression.toString());
            //now evaluate the resulting boolean expression
            ConstraintEvaluator ce = new ConstraintEvaluator(new BaseScanner(new StringReader("[".concat(expression.toString()).concat(";]"))));

            try {
                result = ce.parse().value.toString();
                System.out.println("The result is ===> " + result);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            expression.delete(0, expression.length());
            phiExpression = holdPhi;
            //check if result is true
            if (result.equalsIgnoreCase("true")) {
                //update the list of states where K(phi) is true
                ((HashSet<Integer>) objStates[0]).add((Integer) state.getKey());
            } else {
//                ArrayList<Integer> aList = new ArrayList<Integer>();
//                Collection collect = (Collection)aList;
//                HashSet<Integer> set = new HashSet<Integer>(); 
//                     
                //update the list of states where K(phi) is false
                ((HashSet<Integer>) objStates[1]).add((Integer) state.getKey());
                //  ((HashSet)Utilities.qualifiedStateMap.keySet()).removeAll(ep[0]);
            }

        }
        return objStates;
    }

    //for handling stuff like IDENTIFIER knows IDENTIFIER e.g sum knows x
    public static Object[] knowsPHIExpressionMC(String agentId, String phiExpression) {
        StringBuilder expression = new StringBuilder();
        String result = "";
        String holdPhi = phiExpression;

        // the first index of the below object will keep the state ids where the given expression is true
        // the second index will keep the state ids where the given expression is false
        Object[] objStates = new Object[]{new HashSet<>(), new HashSet<>()};

        //iterate through all the states
        Iterator itStates = qualifiedStateMap.entrySet().iterator();
        while (itStates.hasNext()) {
            Map.Entry state = (Map.Entry) itStates.next();
            Iterator itSuccessors = ((ArrayList) ((TreeMap) state.getValue()).get(agentId)).iterator();

            //for the phi expression, say "liv knows s", what I want to do is to rewrite it as
            // "liv knows s==val" where val is the value of s at the selected state (that is the state for which 
            // we would examine the successors.
            // so I take the values from this state (e.g s=10) split it and replace it with "s==10" so we can then evaluate
            // or check it against all successor states (itself being a successor state). Any deviation from this value
            // among the successor states means that liv does not know s at that state, so we drop it as usual.
            //HOWEVER, THERE IS A PROBLEM, THE REPLACEMENT OF STRING WILL CAUSE PROBLEMS IF WE HAVE 
            // VARIABLES LIKE "ab" and "a": in this case if we replace "a" before replacing "ab" we could get a 
            // string like "a==5b". So we must adjust the replace all regex argument to say that whatever string identifier is 
            // followed by NO alphanumeric character

            String[] stateSelfLabel = stateLabelMap.get((Integer) state.getKey());
            for (int i = 0; i < stateSelfLabel.length; i++) {
                phiExpression = phiExpression.replaceAll(stateSelfLabel[i].split("=")[0].trim(), stateSelfLabel[i].split("=")[0].trim().concat("==").concat(stateSelfLabel[i].split("=")[1].trim()));
            }
            String holdReWrittenPhi = phiExpression;
            // within the following while loop, each state will now subtitute the "s" or lvar with their own value, and evaluate the boolean expression
            while (itSuccessors.hasNext()) {
                String[] stateLabel = stateLabelMap.get((Integer) itSuccessors.next());
                //use the inEpistemicConstraints identifiers to fish out the relevant fields from this state
                for (int i = 0; i < stateLabel.length; i++) {
                    //each state label is sth like q=5...
                    //we split according to the equal sign, and replace all q with 5 (for example) in the boolean expression
                    phiExpression = phiExpression.replaceAll(StringUtils.split(stateLabel[i], "=")[0].trim(), StringUtils.split(stateLabel[i], "=")[1].trim());
                }
                //make a conjuction of all the expressions for each of the successor states
                expression.append("(").append(phiExpression).append(")&&");
                phiExpression = holdReWrittenPhi;
            }
            //remove the last && symbol
            expression.delete(expression.length() - 2, expression.length());

            System.out.println("@@@@@@@@@@@@@@@@@@@@Teh boolean expressionis " + expression.toString());
            //now evaluate the resulting boolean expression
            ConstraintEvaluator ce = new ConstraintEvaluator(new ModelCheckerScanner(new StringReader("[".concat(expression.toString()).concat(";]"))));

            try {
                result = ce.parse().value.toString();
                System.out.println("The result is ===> " + result);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            expression.delete(0, expression.length());
            phiExpression = holdPhi;
            //check if result is true
            if (result.equalsIgnoreCase("true")) {
                //update the list of states where K(phi) is true
                ((HashSet<Integer>) objStates[0]).add((Integer) state.getKey());
            } else {
//                ArrayList<Integer> aList = new ArrayList<Integer>();
//                Collection collect = (Collection)aList;
//                HashSet<Integer> set = new HashSet<Integer>(); 
//                     
                //update the list of states where K(phi) is false
                ((HashSet<Integer>) objStates[1]).add((Integer) state.getKey());
                //  ((HashSet)Utilities.qualifiedStateMap.keySet()).removeAll(ep[0]);
            }

        }

        //store in history, to aid in finding counter model in model checking
        truthFalseStateHistory.add(objStates);
        return objStates;
    }

    public static Object[] knowsBooleanExpression(String agentId, String booleanExpression) {
        StringBuilder expression = new StringBuilder();
        String result = "";
        String boolExpr = "";
        //make a conjuction of all the expressions for each of the successor states
        //but before then remove the delimiting starting '[' and ending ';]' which the lexer uses to switch to the appropriate state so as to return symbols from 
        // the appropriate symbol class (there are different symbol classes for the main parser, the constraint evaluator (which handles boolean expressions and arithmetic expressions) and the 
        // epistemic constraint evaluator class which handles epistemic constraints, but also calls the constraint evaluator as subroutine.
        // since we build an conjuction of many boolean expressions, we need to remove the delimiting begining and ending symbols ('[' and ';]')
        // and do all the conjuctions, then replace them at the end of the entire conjucted expression string.
        // so we start by removing them...

        //booleanExpression = StringUtils.remove(StringUtils.remove(booleanExpression, "["), ";]");
        String holdBooleanExpression = booleanExpression;
        // the first index of the below object will keep the state ids where the given expression is true
        // the second index will keep the state ids where the given expression is false
        Object[] objStates = new Object[]{new HashSet<>(), new HashSet<>()};

        //iterate through all the states
        Iterator itStates = qualifiedStateMap.entrySet().iterator();
        while (itStates.hasNext()) {
            Map.Entry state = (Map.Entry) itStates.next();
            Iterator itSuccessors = ((ArrayList) ((TreeMap) state.getValue()).get(agentId)).iterator();
            while (itSuccessors.hasNext()) {
                String[] stateLabel = stateLabelMap.get((Integer) itSuccessors.next());
                //use the inEpistemicConstraints identifiers to fish out the relevant fields from this state
                for (int i = 0; i < stateLabel.length; i++) {
                    //each state label is sth like q=5...
                    //we split according to the equal sign, and replace all q with 5 (for example) in the boolean expression
                    booleanExpression = booleanExpression.replaceAll(StringUtils.split(stateLabel[i], "=")[0].trim().concat("="), StringUtils.split(stateLabel[i], "=")[1].trim().concat("="));
                }

                expression.append("(").append(booleanExpression).append(")&&");
                booleanExpression = holdBooleanExpression;
            }
            //remove the last && symbol
            expression.delete(expression.length() - 2, expression.length());
            // Now add back those begining '[' and ending ';]' to now complete the boolean expression for parsing
            boolExpr = "[".concat(expression.toString()).concat(";]");
            System.out.println("@@@@@@@@@@@@@@@@@@@@Teh boolean expressionis " + boolExpr);
            //now evaluate the resulting boolean expression
            ConstraintEvaluator ce = new ConstraintEvaluator(new BaseScanner(new StringReader(boolExpr)));

            try {
                result = ce.parse().value.toString();
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            //check if result is true
            if (result.equalsIgnoreCase("true")) {
                //update the list of states where K(phi) is true
                ((HashSet<Integer>) objStates[0]).add((Integer) state.getKey());
            } else {
//                ArrayList<Integer> aList = new ArrayList<Integer>();
//                Collection collect = (Collection)aList;
//                HashSet<Integer> set = new HashSet<Integer>(); 
//                     
                //update the list of states where K(phi) is false
                ((HashSet<Integer>) objStates[1]).add((Integer) state.getKey());
                //  ((HashSet)Utilities.qualifiedStateMap.keySet()).removeAll(ep[0]);
            }
            expression.delete(0, expression.length());

        }
        return objStates;
    }

    public static Object[] knowsBooleanExpressionMC(String agentId, String booleanExpression) {
        StringBuilder expression = new StringBuilder();
        String result = "";
        String boolExpr = "";
        //make a conjuction of all the expressions for each of the successor states
        //but before then remove the delimiting starting '[' and ending ';]' which the lexer uses to switch to the appropriate state so as to return symbols from 
        // the appropriate symbol class (there are different symbol classes for the main parser, the constraint evaluator (which handles boolean expressions and arithmetic expressions) and the 
        // epistemic constraint evaluator class which handles epistemic constraints, but also calls the constraint evaluator as subroutine.
        // since we build an conjuction of many boolean expressions, we need to remove the delimiting begining and ending symbols ('[' and ';]')
        // and do all the conjuctions, then replace them at the end of the entire conjucted expression string.
        // so we start by removing them...

        //booleanExpression = StringUtils.remove(StringUtils.remove(booleanExpression, "["), ";]");
        String holdBooleanExpression = booleanExpression;
        // the first index of the below object will keep the state ids where the given expression is true
        // the second index will keep the state ids where the given expression is false
        Object[] objStates = new Object[]{new HashSet<>(), new HashSet<>()};

        //iterate through all the states
        Iterator itStates = qualifiedStateMap.entrySet().iterator();
        while (itStates.hasNext()) {
            Map.Entry state = (Map.Entry) itStates.next();
            Iterator itSuccessors = ((ArrayList) ((TreeMap) state.getValue()).get(agentId)).iterator();
            while (itSuccessors.hasNext()) {
                String[] stateLabel = stateLabelMap.get((Integer) itSuccessors.next());
                //use the inEpistemicConstraints identifiers to fish out the relevant fields from this state
                for (int i = 0; i < stateLabel.length; i++) {
                    //each state label is sth like q=5...
                    //we split according to the equal sign, and replace all q with 5 (for example) in the boolean expression
                    booleanExpression = booleanExpression.replaceAll(StringUtils.split(stateLabel[i], "=")[0].trim().concat("="), StringUtils.split(stateLabel[i], "=")[1].trim().concat("="));
                }

                expression.append("(").append(booleanExpression).append(")&&");
                booleanExpression = holdBooleanExpression;
            }
            //remove the last && symbol
            expression.delete(expression.length() - 2, expression.length());
            // Now add back those begining '[' and ending ';]' to now complete the boolean expression for parsing
            boolExpr = "[".concat(expression.toString()).concat(";]");
            System.out.println("@@@@@@@@@@@@@@@@@@@@Teh boolean expressionis " + boolExpr);
            //now evaluate the resulting boolean expression
            ConstraintEvaluator ce = new ConstraintEvaluator(new ModelCheckerScanner(new StringReader(boolExpr)));

            try {
                result = ce.parse().value.toString();
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            //check if result is true
            if (result.equalsIgnoreCase("true")) {
                //update the list of states where K(phi) is true
                ((HashSet<Integer>) objStates[0]).add((Integer) state.getKey());
            } else {
//                ArrayList<Integer> aList = new ArrayList<Integer>();
//                Collection collect = (Collection)aList;
//                HashSet<Integer> set = new HashSet<Integer>(); 
//                     
                //update the list of states where K(phi) is false
                ((HashSet<Integer>) objStates[1]).add((Integer) state.getKey());
                //  ((HashSet)Utilities.qualifiedStateMap.keySet()).removeAll(ep[0]);
            }
            expression.delete(0, expression.length());

        }

        //store in history, to aid in finding counter model in model checking
        truthFalseStateHistory.add(objStates);
        return objStates;
    }

    public static Object[] epistemicExpressionNOTKNOWSBE(String agentId, String boolExpr) {
        //IDENTIFIER:id NOT KNOWS boolean_expr:be 
        // called in epistemicConstraintEvaluator
        // boolExpr = "[".concat(boolExpr).concat(";]");
        Object[] obj = Utilities.knowsBooleanExpression(agentId, boolExpr);
        Object temp = obj[0];
        obj[0] = obj[1];
        obj[1] = temp;
        return obj;
    }

    public static Object[] epistemicExpressionNOTKNOWSBEMC(String agentId, String boolExpr) {
        //IDENTIFIER:id NOT KNOWS boolean_expr:be 
        // called in epistemicConstraintEvaluator
        // boolExpr = "[".concat(boolExpr).concat(";]");
        Object[] obj = Utilities.knowsBooleanExpressionMC(agentId, boolExpr);
        Object temp = obj[0];
        obj[0] = obj[1];
        obj[1] = temp;

        //store in history, to aid in finding counter model in model checking
        truthFalseStateHistory.add(obj);
        return obj;
    }

    public static Object[] epistemicExpressionNOTKNOWSPHI(String agentId, String phiExpr) {
        //IDENTIFIER:ag NOT KNOWS BRACKET phi:p BRACKET
        // called in epistemicConstraintEvaluator
        Object[] obj = Utilities.knowsPHIExpression(agentId, phiExpr);
        Object temp = obj[0];
        obj[0] = obj[1];
        obj[1] = temp;
        return obj;
    }

    public static Object[] epistemicExpressionNOTKNOWSPHIMC(String agentId, String phiExpr) {
        //IDENTIFIER:ag NOT KNOWS BRACKET phi:p BRACKET
        // called in epistemicConstraintEvaluator
        Object[] obj = Utilities.knowsPHIExpressionMC(agentId, phiExpr);
        Object temp = obj[0];
        obj[0] = obj[1];
        obj[1] = temp;

        //store in history, to aid in finding counter model in model checking
        truthFalseStateHistory.add(obj);
        return obj;
    }

    public static Object[] epistemicExpressionNOTKNOWSEPIEXPR(String agentId, Object[] epistemicExpr) {
        //IDENTIFIER:ag NOT KNOWS BRACKET phi:p BRACKET
        // called in epistemicConstraintEvaluator
        Object[] obj = Utilities.knowsEpistemicExpression(agentId, epistemicExpr);
        Object temp = obj[0];
        obj[0] = obj[1];
        obj[1] = temp;
        return obj;
    }

    public static Object[] epistemicExpressionNOTKNOWSEPIEXPRMC(String agentId, Object[] epistemicExpr) {
        //IDENTIFIER:ag NOT KNOWS BRACKET phi:p BRACKET
        // called in epistemicConstraintEvaluator
        Object[] obj = Utilities.knowsEpistemicExpression(agentId, epistemicExpr);
        Object temp = obj[0];
        obj[0] = obj[1];
        obj[1] = temp;

        //store in history, to aid in finding counter model in model checking
        truthFalseStateHistory.add(obj);
        return obj;
    }

    public static Object[] everyBodyKnowsEpistemicExpression(Object[] objStates) {
        // This method is used to solve recursive E expressions
        // object states already contains the set of states where 'lower' expressions are true
        // (in addition to where they are false)
        // the lowest expression is the boolean expression part of the epistemic_expr production
        // the boolean category is solved using the knowsBooleanExpression method which returns 
        // the set of states where the given agent knows the boolean expression. The knowsBooleanExpression
        // method is always the first to be executed in the chain of recursive functions, so it keeps
        //returning the set of states that we use in high up functions like this one
        StringBuilder expression = new StringBuilder();
        // String result = "";
        // the first index of the below object will keep the state ids where the given expression is true
        // the second index will keep the state ids where the given expression is false
        Object[] anotherObjStates = new Object[]{new HashSet<>(), new HashSet<Integer>()};

        //iterate through all the states
        Iterator itStates = qualifiedStateMap.entrySet().iterator();
        HashSet<Integer> successorStates = new HashSet<>();
        while (itStates.hasNext()) {
            Map.Entry state = (Map.Entry) itStates.next();
            Iterator itAgents = ((TreeMap) state.getValue()).entrySet().iterator();
            //all the agents at each state know the given constraint
            while (itAgents.hasNext()) {
                Iterator itSuccessors = ((ArrayList<Integer>) (((Map.Entry) itAgents.next()).getValue())).iterator();
                while (itSuccessors.hasNext()) {
                    //String[] stateLabel = stateLabelMap.get((Integer)itSuccessors.next());
                    //build a set of the successor states of this agent
                    successorStates.add((Integer) itSuccessors.next());

                }
            }
            //now check if all the successor states are all contained in the 
            // set of states where "knows" is true
            if (((HashSet) objStates[0]).containsAll(successorStates)) {
                ((HashSet<Integer>) anotherObjStates[0]).add((Integer) state.getKey());
            } else {
                ((HashSet<Integer>) anotherObjStates[1]).add((Integer) state.getKey());
            }
            //now clean up the successorStates set to make it ready to capture the successor states of the next state
            // this issue of not cleaning up a used object before next round has appeared other times here
            //giving rise to bugs; be more careful about it
            successorStates.clear();

        }
        return anotherObjStates;
    }

    public static Object[] everyBodyKnowsEpistemicExpressionMC(Object[] objStates) {
        // This method is used to solve recursive E expressions
        // object states already contains the set of states where 'lower' expressions are true
        // (in addition to where they are false)
        // the lowest expression is the boolean expression part of the epistemic_expr production
        // the boolean category is solved using the knowsBooleanExpression method which returns 
        // the set of states where the given agent knows the boolean expression. The knowsBooleanExpression
        // method is always the first to be executed in the chain of recursive functions, so it keeps
        //returning the set of states that we use in high up functions like this one
        StringBuilder expression = new StringBuilder();
        // String result = "";
        // the first index of the below object will keep the state ids where the given expression is true
        // the second index will keep the state ids where the given expression is false
        Object[] anotherObjStates = new Object[]{new HashSet<>(), new HashSet<>()};

        //iterate through all the states
        Iterator itStates = qualifiedStateMap.entrySet().iterator();
        HashSet<Integer> successorStates = new HashSet<>();
        while (itStates.hasNext()) {
            Map.Entry state = (Map.Entry) itStates.next();
            Iterator itAgents = ((TreeMap) state.getValue()).entrySet().iterator();
            //all the agents at each state know the given constraint
            while (itAgents.hasNext()) {
                Iterator itSuccessors = ((ArrayList<Integer>) (((Map.Entry) itAgents.next()).getValue())).iterator();
                while (itSuccessors.hasNext()) {
                    //String[] stateLabel = stateLabelMap.get((Integer)itSuccessors.next());
                    //build a set of the successor states of this agent
                    successorStates.add((Integer) itSuccessors.next());

                }
            }
            //now check if all the successor states are all contained in the 
            // set of states where "knows" is true
            if (((HashSet) objStates[0]).containsAll(successorStates)) {
                ((HashSet<Integer>) anotherObjStates[0]).add((Integer) state.getKey());
            } else {
                ((HashSet<Integer>) anotherObjStates[1]).add((Integer) state.getKey());
            }
            //now clean up the successorStates set to make it ready to capture the successor states of the next state
            // this issue of not cleaning up a used object before next round has appeared other times here
            //giving rise to bugs; be more careful about it
            successorStates.clear();

        }

        //store in history, to aid in finding counter model in model checking
        truthFalseStateHistory.add(anotherObjStates);
        return anotherObjStates;
    }

    public static Object[] epistemicExpressionNOTEveryBodyKnowsEPIEXPR(Object[] epistemicExpr) {
        //IDENTIFIER:ag NOT KNOWS BRACKET phi:p BRACKET
        // called in epistemicConstraintEvaluator
        Object[] obj = Utilities.everyBodyKnowsEpistemicExpression(epistemicExpr);
        Object temp = obj[0];
        obj[0] = obj[1];
        obj[1] = temp;
        return obj;
    }

    public static Object[] epistemicExpressionNOTEveryBodyKnowsEPIEXPRMC(Object[] epistemicExpr) {
        //IDENTIFIER:ag NOT KNOWS BRACKET phi:p BRACKET
        // called in epistemicConstraintEvaluator
        Object[] obj = Utilities.everyBodyKnowsEpistemicExpression(epistemicExpr);
        Object temp = obj[0];
        obj[0] = obj[1];
        obj[1] = temp;

        //store in history, to aid in finding counter model in model checking
        truthFalseStateHistory.add(obj);
        return obj;
    }

    public static Object[] everyBodyKnowsBooleanExpression(String booleanExpression) {
        StringBuilder expression = new StringBuilder();
        String result = "";
        String boolExpr = "";
        //make a conjuction of all the expressions for each of the successor states
        //but before then remove the delimiting starting '[' and ending ';]' which the lexer uses to switch to the appropriate state so as to return symbols from 
        // the appropriate symbol class (there are different symbol classes for the main parser, the constraint evaluator (which handles boolean expressions and arithmetic expressions) and the 
        // epistemic constraint evaluator class which handles epistemic constraints, but also calls the constraint evaluator as subroutine.
        // since we build an conjuction of many boolean expressions, we need to remove the delimiting begining and ending symbols ('[' and ';]')
        // and do all the conjuctions, then replace them at the end of the entire conjucted expression string.
        // so we start by removing them...

        //booleanExpression = StringUtils.remove(StringUtils.remove(booleanExpression, "["), ";]");
        String holdBooleanExpression = booleanExpression;
        // the first index of the below object will keep the state ids where the given expression is true
        // the second index will keep the state ids where the given expression is false
        Object[] objStates = new Object[]{new HashSet<>(), new HashSet<Integer>()};

        //iterate through all the states
        Iterator itStates = qualifiedStateMap.entrySet().iterator();
        while (itStates.hasNext()) {
            Map.Entry state = (Map.Entry) itStates.next();
            Iterator itAgents = ((TreeMap) state.getValue()).entrySet().iterator();
            //build a big conjuction expression for all the successor states of all agents
            while (itAgents.hasNext()) {
                Iterator itSuccessors = ((ArrayList<Integer>) (((Map.Entry) itAgents.next()).getValue())).iterator();
                while (itSuccessors.hasNext()) {
                    String[] stateLabel = stateLabelMap.get((Integer) itSuccessors.next());
                    //use the inEpistemicConstraints identifiers to fish out the relevant fields from this state
                    for (int i = 0; i < stateLabel.length; i++) {
                        //each state label is sth like q=5...
                        //we split according to the equal sign, and replace all q with 5 (for example) in the boolean expression
                        booleanExpression = booleanExpression.replaceAll(StringUtils.split(stateLabel[i], "=")[0].trim().concat("="), StringUtils.split(stateLabel[i], "=")[1].trim().concat("="));
                    }

                    expression.append("(").append(booleanExpression).append(")&&");
                    booleanExpression = holdBooleanExpression;
                }
            }
            //remove the last && symbol
            expression.delete(expression.length() - 2, expression.length());
            // Now add back those begining '[' and ending ';]' to now complete the boolean expression for parsing
            boolExpr = "[".concat(expression.toString()).concat(";]");
            System.out.println("@@@@@@@@@@@@@@@@@@@@Teh boolean expressionis " + boolExpr);
            //now evaluate the resulting boolean expression
            ConstraintEvaluator ce = new ConstraintEvaluator(new BaseScanner(new StringReader(boolExpr)));

            try {
                result = ce.parse().value.toString();
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            //check if result is true
            if (result.equalsIgnoreCase("true")) {
                //update the list of states where K(phi) is true
                ((HashSet<Integer>) objStates[0]).add((Integer) state.getKey());
            } else {
//                ArrayList<Integer> aList = new ArrayList<Integer>();
//                Collection collect = (Collection)aList;
//                HashSet<Integer> set = new HashSet<Integer>(); 
//                     
                //update the list of states where K(phi) is false
                ((HashSet<Integer>) objStates[1]).add((Integer) state.getKey());
                //  ((HashSet)Utilities.qualifiedStateMap.keySet()).removeAll(ep[0]);
            }
            expression.delete(0, expression.length());

        }
        return objStates;
    }

    public static Object[] everyBodyKnowsBooleanExpressionMC(String booleanExpression) {
        StringBuilder expression = new StringBuilder();
        String result = "";
        String boolExpr = "";
        //make a conjuction of all the expressions for each of the successor states
        //but before then remove the delimiting starting '[' and ending ';]' which the lexer uses to switch to the appropriate state so as to return symbols from 
        // the appropriate symbol class (there are different symbol classes for the main parser, the constraint evaluator (which handles boolean expressions and arithmetic expressions) and the 
        // epistemic constraint evaluator class which handles epistemic constraints, but also calls the constraint evaluator as subroutine.
        // since we build an conjuction of many boolean expressions, we need to remove the delimiting begining and ending symbols ('[' and ';]')
        // and do all the conjuctions, then replace them at the end of the entire conjucted expression string.
        // so we start by removing them...

        //booleanExpression = StringUtils.remove(StringUtils.remove(booleanExpression, "["), ";]");
        String holdBooleanExpression = booleanExpression;
        // the first index of the below object will keep the state ids where the given expression is true
        // the second index will keep the state ids where the given expression is false
        Object[] objStates = new Object[]{new HashSet<Integer>(), new HashSet<Integer>()};

        //iterate through all the states
        Iterator itStates = qualifiedStateMap.entrySet().iterator();
        while (itStates.hasNext()) {
            Map.Entry state = (Map.Entry) itStates.next();
            Iterator itAgents = ((TreeMap) state.getValue()).entrySet().iterator();
            //build a big conjuction expression for all the successor states of all agents
            while (itAgents.hasNext()) {
                Iterator itSuccessors = ((ArrayList<Integer>) (((Map.Entry) itAgents.next()).getValue())).iterator();
                while (itSuccessors.hasNext()) {
                    String[] stateLabel = stateLabelMap.get((Integer) itSuccessors.next());
                    //use the inEpistemicConstraints identifiers to fish out the relevant fields from this state
                    for (int i = 0; i < stateLabel.length; i++) {
                        //each state label is sth like q=5...
                        //we split according to the equal sign, and replace all q with 5 (for example) in the boolean expression
                        booleanExpression = booleanExpression.replaceAll(StringUtils.split(stateLabel[i], "=")[0].trim().concat("="), StringUtils.split(stateLabel[i], "=")[1].trim().concat("="));
                    }

                    expression.append("(").append(booleanExpression).append(")&&");
                    booleanExpression = holdBooleanExpression;
                }
            }
            //remove the last && symbol
            expression.delete(expression.length() - 2, expression.length());
            // Now add back those begining '[' and ending ';]' to now complete the boolean expression for parsing
            boolExpr = "[".concat(expression.toString()).concat(";]");
            System.out.println("@@@@@@@@@@@@@@@@@@@@Teh boolean expressionis " + boolExpr);
            //now evaluate the resulting boolean expression
            ConstraintEvaluator ce = new ConstraintEvaluator(new ModelCheckerScanner(new StringReader(boolExpr)));

            try {
                result = ce.parse().value.toString();
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            //check if result is true
            if (result.equalsIgnoreCase("true")) {
                //update the list of states where K(phi) is true
                ((HashSet<Integer>) objStates[0]).add((Integer) state.getKey());
            } else {
//                ArrayList<Integer> aList = new ArrayList<Integer>();
//                Collection collect = (Collection)aList;
//                HashSet<Integer> set = new HashSet<Integer>(); 
//                     
                //update the list of states where K(phi) is false
                ((HashSet<Integer>) objStates[1]).add((Integer) state.getKey());
                //  ((HashSet)Utilities.qualifiedStateMap.keySet()).removeAll(ep[0]);
            }
            expression.delete(0, expression.length());

        }

        //store in history, to aid in finding counter model in model checking
        truthFalseStateHistory.add(objStates);
        return objStates;
    }

    public static Object[] epistemicExpressionNOTEveryBodyKNOWSBE(String boolExpr) {
        //IDENTIFIER:id NOT KNOWS boolean_expr:be 
        // called in epistemicConstraintEvaluator
        // boolExpr = "[".concat(boolExpr).concat(";]");
        Object[] obj = Utilities.everyBodyKnowsBooleanExpression(boolExpr);
        Object temp = obj[0];
        obj[0] = obj[1];
        obj[1] = temp;
        return obj;
    }

    public static Object[] epistemicExpressionNOTEveryBodyKNOWSBEMC(String boolExpr) {
        //IDENTIFIER:id NOT KNOWS boolean_expr:be 
        // called in epistemicConstraintEvaluator
        // boolExpr = "[".concat(boolExpr).concat(";]");
        Object[] obj = Utilities.everyBodyKnowsBooleanExpressionMC(boolExpr);
        Object temp = obj[0];
        obj[0] = obj[1];
        obj[1] = temp;

        //store in history, to aid in finding counter model in model checking
        truthFalseStateHistory.add(obj);
        return obj;
    }

    public static Object[] everyBodyKnowsPHIExpression(String phiExpression) {
        StringBuilder expression = new StringBuilder();
        String result = "";
        String holdPhi = phiExpression;

        // the first index of the below object will keep the state ids where the given expression is true
        // the second index will keep the state ids where the given expression is false
        Object[] objStates = new Object[]{new HashSet<>(), new HashSet<>()};

        //iterate through all the states
        Iterator itStates = qualifiedStateMap.entrySet().iterator();
        while (itStates.hasNext()) {
            Map.Entry state = (Map.Entry) itStates.next();
            //     Iterator itSuccessors = ((ArrayList) ((TreeMap) state.getValue()).get(agentId)).iterator();

            //for the phi expression, say "liv knows s", what I want to do is to rewrite it as
            // "liv knows s==val" where val is the value of s at the selected state (that is the state for which 
            // we would examine the successors.
            // so I take the values from this state (e.g s=10) split it and replace it with "s==10" so we can then evaluate
            // or check it against all successor states (itself being a successor state). Any deviation from this value
            // among the successor states means that liv does not know s at that state, so we drop it as usual.
            //HOWEVER, THERE IS A PROBLEM, THE REPLACEMENT OF STRING WILL CAUSE PROBLEMS IF WE HAVE 
            // VARIABLES LIKE "ab" and "a": in this case if we replace "a" before replacing "ab" we could get a 
            // string like "a==5b". So we must adjust the replace all regex argument to say that whatever string identifier is 
            // followed by NO alphanumeric character

            String[] stateSelfLabel = stateLabelMap.get((Integer) state.getKey());
            for (int i = 0; i < stateSelfLabel.length; i++) {
                phiExpression = phiExpression.replaceAll(stateSelfLabel[i].split("=")[0].trim(), stateSelfLabel[i].split("=")[0].trim().concat("==").concat(stateSelfLabel[i].split("=")[1].trim()));
            }
            String holdReWrittenPhi = phiExpression;

            Iterator itAgents = ((TreeMap) state.getValue()).entrySet().iterator();
            //build a big conjuction expression for all the successor states of all agents
            while (itAgents.hasNext()) {
                // within the following while loop, each state will now subtitute the "s" or lvar with their own value, and evaluate the boolean expression
                Iterator itSuccessors = ((ArrayList<Integer>) (((Map.Entry) itAgents.next()).getValue())).iterator();
                while (itSuccessors.hasNext()) {
                    String[] stateLabel = stateLabelMap.get((Integer) itSuccessors.next());
                    //use the inEpistemicConstraints identifiers to fish out the relevant fields from this state
                    for (int i = 0; i < stateLabel.length; i++) {
                        //each state label is sth like q=5...
                        //we split according to the equal sign, and replace all q with 5 (for example) in the boolean expression
                        phiExpression = phiExpression.replaceAll(StringUtils.split(stateLabel[i], "=")[0].trim(), StringUtils.split(stateLabel[i], "=")[1].trim());
                    }
                    //make a conjuction of all the expressions for each of the successor states
                    expression.append("(").append(phiExpression).append(")&&");
                    phiExpression = holdReWrittenPhi;
                }
            }
            //remove the last && symbol
            expression.delete(expression.length() - 2, expression.length());

            System.out.println("@@@@@@@@@@@@@@@@@@@@Teh boolean expressionis " + expression.toString());
            //now evaluate the resulting boolean expression
            ConstraintEvaluator ce = new ConstraintEvaluator(new BaseScanner(new StringReader("[".concat(expression.toString()).concat(";]"))));

            try {
                result = ce.parse().value.toString();
                System.out.println("The result is ===> " + result);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            expression.delete(0, expression.length());
            phiExpression = holdPhi;
            //check if result is true
            if (result.equalsIgnoreCase("true")) {
                //update the list of states where K(phi) is true
                ((HashSet<Integer>) objStates[0]).add((Integer) state.getKey());
            } else {
//                ArrayList<Integer> aList = new ArrayList<Integer>();
//                Collection collect = (Collection)aList;
//                HashSet<Integer> set = new HashSet<Integer>(); 
//                     
                //update the list of states where K(phi) is false
                ((HashSet<Integer>) objStates[1]).add((Integer) state.getKey());
                //  ((HashSet)Utilities.qualifiedStateMap.keySet()).removeAll(ep[0]);
            }

        }
        return objStates;
    }

    public static Object[] everyBodyKnowsPHIExpressionMC(String phiExpression) {
        StringBuilder expression = new StringBuilder();
        String result = "";
        String holdPhi = phiExpression;

        // the first index of the below object will keep the state ids where the given expression is true
        // the second index will keep the state ids where the given expression is false
        Object[] objStates = new Object[]{new HashSet<>(), new HashSet<>()};

        //iterate through all the states
        Iterator itStates = qualifiedStateMap.entrySet().iterator();
        while (itStates.hasNext()) {
            Map.Entry state = (Map.Entry) itStates.next();
            //     Iterator itSuccessors = ((ArrayList) ((TreeMap) state.getValue()).get(agentId)).iterator();

            //for the phi expression, say "liv knows s", what I want to do is to rewrite it as
            // "liv knows s==val" where val is the value of s at the selected state (that is the state for which 
            // we would examine the successors.
            // so I take the values from this state (e.g s=10) split it and replace it with "s==10" so we can then evaluate
            // or check it against all successor states (itself being a successor state). Any deviation from this value
            // among the successor states means that liv does not know s at that state, so we drop it as usual.
            //HOWEVER, THERE IS A PROBLEM, THE REPLACEMENT OF STRING WILL CAUSE PROBLEMS IF WE HAVE 
            // VARIABLES LIKE "ab" and "a": in this case if we replace "a" before replacing "ab" we could get a 
            // string like "a==5b". So we must adjust the replace all regex argument to say that whatever string identifier is 
            // followed by NO alphanumeric character

            String[] stateSelfLabel = stateLabelMap.get((Integer) state.getKey());
            for (int i = 0; i < stateSelfLabel.length; i++) {
                phiExpression = phiExpression.replaceAll(stateSelfLabel[i].split("=")[0].trim(), stateSelfLabel[i].split("=")[0].trim().concat("==").concat(stateSelfLabel[i].split("=")[1].trim()));
            }
            String holdReWrittenPhi = phiExpression;

            Iterator itAgents = ((TreeMap) state.getValue()).entrySet().iterator();
            //build a big conjuction expression for all the successor states of all agents
            while (itAgents.hasNext()) {
                // within the following while loop, each state will now subtitute the "s" or lvar with their own value, and evaluate the boolean expression
                Iterator itSuccessors = ((ArrayList<Integer>) (((Map.Entry) itAgents.next()).getValue())).iterator();
                while (itSuccessors.hasNext()) {
                    String[] stateLabel = stateLabelMap.get((Integer) itSuccessors.next());
                    //use the inEpistemicConstraints identifiers to fish out the relevant fields from this state
                    for (int i = 0; i < stateLabel.length; i++) {
                        //each state label is sth like q=5...
                        //we split according to the equal sign, and replace all q with 5 (for example) in the boolean expression
                        phiExpression = phiExpression.replaceAll(StringUtils.split(stateLabel[i], "=")[0].trim(), StringUtils.split(stateLabel[i], "=")[1].trim());
                    }
                    //make a conjuction of all the expressions for each of the successor states
                    expression.append("(").append(phiExpression).append(")&&");
                    phiExpression = holdReWrittenPhi;
                }
            }
            //remove the last && symbol
            expression.delete(expression.length() - 2, expression.length());

            System.out.println("@@@@@@@@@@@@@@@@@@@@Teh boolean expressionis " + expression.toString());
            //now evaluate the resulting boolean expression
            ConstraintEvaluator ce = new ConstraintEvaluator(new ModelCheckerScanner(new StringReader("[".concat(expression.toString()).concat(";]"))));

            try {
                result = ce.parse().value.toString();
                System.out.println("The result is ===> " + result);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            expression.delete(0, expression.length());
            phiExpression = holdPhi;
            //check if result is true
            if (result.equalsIgnoreCase("true")) {
                //update the list of states where K(phi) is true
                ((HashSet<Integer>) objStates[0]).add((Integer) state.getKey());
            } else {
//                ArrayList<Integer> aList = new ArrayList<Integer>();
//                Collection collect = (Collection)aList;
//                HashSet<Integer> set = new HashSet<Integer>(); 
//                     
                //update the list of states where K(phi) is false
                ((HashSet<Integer>) objStates[1]).add((Integer) state.getKey());
                //  ((HashSet)Utilities.qualifiedStateMap.keySet()).removeAll(ep[0]);
            }

        }

        //store in history, to aid in finding counter model in model checking
        truthFalseStateHistory.add(objStates);
        return objStates;
    }

    public static Object[] epistemicExpressionNOTEveryBodyKNOWSPHI(String phiExpr) {
        //IDENTIFIER:ag NOT KNOWS BRACKET phi:p BRACKET
        // called in epistemicConstraintEvaluator
        Object[] obj = Utilities.everyBodyKnowsPHIExpression(phiExpr);
        Object temp = obj[0];
        obj[0] = obj[1];
        obj[1] = temp;
        return obj;
    }

    public static Object[] epistemicExpressionNOTEveryBodyKNOWSPHIMC(String phiExpr) {
        //IDENTIFIER:ag NOT KNOWS BRACKET phi:p BRACKET
        // called in epistemicConstraintEvaluator
        Object[] obj = Utilities.everyBodyKnowsPHIExpressionMC(phiExpr);
        Object temp = obj[0];
        obj[0] = obj[1];
        obj[1] = temp;

        //store in history, to aid in finding counter model in model checking
        truthFalseStateHistory.add(obj);
        return obj;
    }

    public static Object[] distKnowsEpistemicExpression(Object[] objStates) {
        // This method is used to solve recursive D expressions
        // object states already contains the set of states where 'lower' expressions are true
        // (in addition to where they are false)
        // the lowest expression is the boolean expression part of the epistemic_expr production
        // the boolean category is solved using the knowsBooleanExpression method which returns 
        // the set of states where the given agent knows the boolean expression. The knowsBooleanExpression
        // method is always the first to be executed in the chain of recursive functions, so it keeps
        //returning the set of states that we use in high up functions like this one
        // StringBuilder expression = new StringBuilder();
        // String result = "";
        // the first index of the below object will keep the state ids where the given expression is true
        // the second index will keep the state ids where the given expression is false
        Object[] anotherObjStates = new Object[]{new HashSet<>(), new HashSet<>()};

        //iterate through all the states
        Iterator itStates = qualifiedStateMap.entrySet().iterator();
        HashSet<Integer> successorStates = new HashSet<>();
        while (itStates.hasNext()) {
            Map.Entry state = (Map.Entry) itStates.next();
            Iterator itAgents = ((TreeMap) state.getValue()).entrySet().iterator();
            //all the agents at each state know the given constraint
            while (itAgents.hasNext()) {
                Iterator itSuccessors = ((ArrayList<Integer>) (((Map.Entry) itAgents.next()).getValue())).iterator();
                while (itSuccessors.hasNext()) {
                    //String[] stateLabel = stateLabelMap.get((Integer)itSuccessors.next());
                    //build a set of the successor states of this agent
                    successorStates.add((Integer) itSuccessors.next());

                }
            }
            //now in contrast with recursive "knows", instead of checking whether the truth set 
            // constains all the successor states, we check whether the successor states has
            // all the elements in truth set (i.e is the truth set a subset of the successor states)
            if (successorStates.containsAll(((HashSet) objStates[0]))) {
                ((HashSet<Integer>) anotherObjStates[0]).add((Integer) state.getKey());
            } else {
                ((HashSet<Integer>) anotherObjStates[1]).add((Integer) state.getKey());
            }
            //now clean up the successorStates set to make it ready to capture the successor states of the next state
            // this issue of not cleaning up a used object before next round has appeared other times here
            //giving rise to bugs; be more careful about it
            successorStates.clear();

        }
        return anotherObjStates;
    }

    public static Object[] distKnowsEpistemicExpressionMC(Object[] objStates) {
        // This method is used to solve recursive D expressions
        // object states already contains the set of states where 'lower' expressions are true
        // (in addition to where they are false)
        // the lowest expression is the boolean expression part of the epistemic_expr production
        // the boolean category is solved using the knowsBooleanExpression method which returns 
        // the set of states where the given agent knows the boolean expression. The knowsBooleanExpression
        // method is always the first to be executed in the chain of recursive functions, so it keeps
        //returning the set of states that we use in high up functions like this one
        // StringBuilder expression = new StringBuilder();
        // String result = "";
        // the first index of the below object will keep the state ids where the given expression is true
        // the second index will keep the state ids where the given expression is false
        Object[] anotherObjStates = new Object[]{new HashSet<>(), new HashSet<>()};

        //iterate through all the states
        Iterator itStates = qualifiedStateMap.entrySet().iterator();
        HashSet<Integer> successorStates = new HashSet<>();
        while (itStates.hasNext()) {
            Map.Entry state = (Map.Entry) itStates.next();
            Iterator itAgents = ((TreeMap) state.getValue()).entrySet().iterator();
            //all the agents at each state know the given constraint
            while (itAgents.hasNext()) {
                Iterator itSuccessors = ((ArrayList<Integer>) (((Map.Entry) itAgents.next()).getValue())).iterator();
                while (itSuccessors.hasNext()) {
                    //String[] stateLabel = stateLabelMap.get((Integer)itSuccessors.next());
                    //build a set of the successor states of this agent
                    successorStates.add((Integer) itSuccessors.next());

                }
            }
            //now in contrast with recursive "knows", instead of checking whether the truth set 
            // constains all the successor states, we check whether the successor states has
            // all the elements in truth set (i.e is the truth set a subset of the successor states)
            if (successorStates.containsAll(((HashSet) objStates[0]))) {
                ((HashSet<Integer>) anotherObjStates[0]).add((Integer) state.getKey());
            } else {
                ((HashSet<Integer>) anotherObjStates[1]).add((Integer) state.getKey());
            }
            //now clean up the successorStates set to make it ready to capture the successor states of the next state
            // this issue of not cleaning up a used object before next round has appeared other times here
            //giving rise to bugs; be more careful about it
            successorStates.clear();

        }

        //store in history, to aid in finding counter model in model checking
        truthFalseStateHistory.add(anotherObjStates);
        return anotherObjStates;
    }

    public static Object[] epistemicExpressionNOTDistKnowsEPIEXPR(Object[] epistemicExpr) {
        //IDENTIFIER:ag NOT KNOWS BRACKET phi:p BRACKET
        // called in epistemicConstraintEvaluator
        Object[] obj = Utilities.distKnowsEpistemicExpression(epistemicExpr);
        Object temp = obj[0];
        obj[0] = obj[1];
        obj[1] = temp;
        return obj;
    }

    public static Object[] epistemicExpressionNOTDistKnowsEPIEXPRMC(Object[] epistemicExpr) {
        //IDENTIFIER:ag NOT KNOWS BRACKET phi:p BRACKET
        // called in epistemicConstraintEvaluator
        Object[] obj = Utilities.distKnowsEpistemicExpression(epistemicExpr);
        Object temp = obj[0];
        obj[0] = obj[1];
        obj[1] = temp;

        //store in history, to aid in finding counter model in model checking
        truthFalseStateHistory.add(obj);
        return obj;
    }

    public static Object[] distKnowsBooleanExpression(String booleanExpression) {
        StringBuilder expression = new StringBuilder();
        StringBuilder expressionMain = new StringBuilder();
        String result = "";
        String boolExpr = "";
        //make a conjuction of all the expressions for each of the successor states
        //but before then remove the delimiting starting '[' and ending ';]' which the lexer uses to switch to the appropriate state so as to return symbols from 
        // the appropriate symbol class (there are different symbol classes for the main parser, the constraint evaluator (which handles boolean expressions and arithmetic expressions) and the 
        // epistemic constraint evaluator class which handles epistemic constraints, but also calls the constraint evaluator as subroutine.
        // since we build an conjuction of many boolean expressions, we need to remove the delimiting begining and ending symbols ('[' and ';]')
        // and do all the conjuctions, then replace them at the end of the entire conjucted expression string.
        // so we start by removing them...

        //booleanExpression = StringUtils.remove(StringUtils.remove(booleanExpression, "["), ";]");
        String holdBooleanExpression = booleanExpression;
        // the first index of the below object will keep the state ids where the given expression is true
        // the second index will keep the state ids where the given expression is false
        Object[] objStates = new Object[]{new HashSet<>(), new HashSet<>()};

        //iterate through all the states
        Iterator itStates = qualifiedStateMap.entrySet().iterator();
        while (itStates.hasNext()) {
            Map.Entry state = (Map.Entry) itStates.next();
            Iterator itAgents = ((TreeMap) state.getValue()).entrySet().iterator();
            //build a big conjuction expression for all the successor states of all agents
            while (itAgents.hasNext()) {
                Iterator itSuccessors = ((ArrayList<Integer>) (((Map.Entry) itAgents.next()).getValue())).iterator();
                while (itSuccessors.hasNext()) {
                    String[] stateLabel = stateLabelMap.get((Integer) itSuccessors.next());
                    //use the inEpistemicConstraints identifiers to fish out the relevant fields from this state
                    for (int i = 0; i < stateLabel.length; i++) {
                        //each state label is sth like q=5...
                        //we split according to the equal sign, and replace all q with 5 (for example) in the boolean expression
                        booleanExpression = booleanExpression.replaceAll(StringUtils.split(stateLabel[i], "=")[0].trim().concat("="), StringUtils.split(stateLabel[i], "=")[1].trim().concat("="));
                    }

                    expression.append("(").append(booleanExpression).append(")||");
                    booleanExpression = holdBooleanExpression;
                }
                //remove the last &&
                expression.delete(expression.length() - 2, expression.length());
                // we add this disjuction for each agent, to check for distributed knowledge
                expressionMain.append("(".concat(expression.toString()).concat(")")).append("||");
                expression.delete(0, expression.length());
                booleanExpression = holdBooleanExpression;
            }
            //remove the last || symbol
            expressionMain.delete(expressionMain.length() - 2, expressionMain.length());
            // Now add back those begining '[' and ending ';]' to now complete the boolean expression for parsing
            boolExpr = "[".concat(expressionMain.toString()).concat(";]");
            System.out.println("@@@@@@@@@@@@@@@@@@@@Teh boolean expressionis " + boolExpr);
            //now evaluate the resulting boolean expression
            ConstraintEvaluator ce = new ConstraintEvaluator(new BaseScanner(new StringReader(boolExpr)));

            try {
                result = ce.parse().value.toString();
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            //check if result is true
            if (result.equalsIgnoreCase("true")) {
                //update the list of states where K(phi) is true
                ((HashSet<Integer>) objStates[0]).add((Integer) state.getKey());
            } else {
//                ArrayList<Integer> aList = new ArrayList<Integer>();
//                Collection collect = (Collection)aList;
//                HashSet<Integer> set = new HashSet<Integer>(); 
//                     
                //update the list of states where K(phi) is false
                ((HashSet<Integer>) objStates[1]).add((Integer) state.getKey());
                //  ((HashSet)Utilities.qualifiedStateMap.keySet()).removeAll(ep[0]);
            }
            expression.delete(0, expression.length());
            expressionMain.delete(0, expressionMain.length());

        }


        //store in history, to aid in finding counter model in model checking
        truthFalseStateHistory.add(objStates);
        return objStates;
    }

    public static Object[] distKnowsBooleanExpressionMC(String booleanExpression) {
        StringBuilder expression = new StringBuilder();
        StringBuilder expressionMain = new StringBuilder();
        String result = "";
        String boolExpr = "";
        //make a conjuction of all the expressions for each of the successor states
        //but before then remove the delimiting starting '[' and ending ';]' which the lexer uses to switch to the appropriate state so as to return symbols from 
        // the appropriate symbol class (there are different symbol classes for the main parser, the constraint evaluator (which handles boolean expressions and arithmetic expressions) and the 
        // epistemic constraint evaluator class which handles epistemic constraints, but also calls the constraint evaluator as subroutine.
        // since we build an conjuction of many boolean expressions, we need to remove the delimiting begining and ending symbols ('[' and ';]')
        // and do all the conjuctions, then replace them at the end of the entire conjucted expression string.
        // so we start by removing them...

        //booleanExpression = StringUtils.remove(StringUtils.remove(booleanExpression, "["), ";]");
        String holdBooleanExpression = booleanExpression;
        // the first index of the below object will keep the state ids where the given expression is true
        // the second index will keep the state ids where the given expression is false
        Object[] objStates = new Object[]{new HashSet<>(), new HashSet<>()};

        //iterate through all the states
        Iterator itStates = qualifiedStateMap.entrySet().iterator();
        while (itStates.hasNext()) {
            Map.Entry state = (Map.Entry) itStates.next();
            Iterator itAgents = ((TreeMap) state.getValue()).entrySet().iterator();
            //build a big conjuction expression for all the successor states of all agents
            while (itAgents.hasNext()) {
                Iterator itSuccessors = ((ArrayList<Integer>) (((Map.Entry) itAgents.next()).getValue())).iterator();
                while (itSuccessors.hasNext()) {
                    String[] stateLabel = stateLabelMap.get((Integer) itSuccessors.next());
                    //use the inEpistemicConstraints identifiers to fish out the relevant fields from this state
                    for (int i = 0; i < stateLabel.length; i++) {
                        //each state label is sth like q=5...
                        //we split according to the equal sign, and replace all q with 5 (for example) in the boolean expression
                        booleanExpression = booleanExpression.replaceAll(StringUtils.split(stateLabel[i], "=")[0].trim().concat("="), StringUtils.split(stateLabel[i], "=")[1].trim().concat("="));
                    }

                    expression.append("(").append(booleanExpression).append(")||");
                    booleanExpression = holdBooleanExpression;
                }
                //remove the last &&
                expression.delete(expression.length() - 2, expression.length());
                // we add this disjuction for each agent, to check for distributed knowledge
                expressionMain.append("(".concat(expression.toString()).concat(")")).append("||");
                expression.delete(0, expression.length());
                booleanExpression = holdBooleanExpression;
            }
            //remove the last || symbol
            expressionMain.delete(expressionMain.length() - 2, expressionMain.length());
            // Now add back those begining '[' and ending ';]' to now complete the boolean expression for parsing
            boolExpr = "[".concat(expressionMain.toString()).concat(";]");
            System.out.println("@@@@@@@@@@@@@@@@@@@@Teh boolean expressionis " + boolExpr);
            //now evaluate the resulting boolean expression
            ConstraintEvaluator ce = new ConstraintEvaluator(new ModelCheckerScanner(new StringReader(boolExpr)));

            try {
                result = ce.parse().value.toString();
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            //check if result is true
            if (result.equalsIgnoreCase("true")) {
                //update the list of states where K(phi) is true
                ((HashSet<Integer>) objStates[0]).add((Integer) state.getKey());
            } else {
//                ArrayList<Integer> aList = new ArrayList<Integer>();
//                Collection collect = (Collection)aList;
//                HashSet<Integer> set = new HashSet<Integer>(); 
//                     
                //update the list of states where K(phi) is false
                ((HashSet<Integer>) objStates[1]).add((Integer) state.getKey());
                //  ((HashSet)Utilities.qualifiedStateMap.keySet()).removeAll(ep[0]);
            }
            expression.delete(0, expression.length());
            expressionMain.delete(0, expressionMain.length());

        }


        //store in history, to aid in finding counter model in model checking
        truthFalseStateHistory.add(objStates);
        return objStates;
    }

    public static Object[] epistemicExpressionNOTDistKNOWSBE(String boolExpr) {
        //IDENTIFIER:id NOT KNOWS boolean_expr:be 
        // called in epistemicConstraintEvaluator
        // boolExpr = "[".concat(boolExpr).concat(";]");
        Object[] obj = Utilities.distKnowsBooleanExpression(boolExpr);
        Object temp = obj[0];
        obj[0] = obj[1];
        obj[1] = temp;
        return obj;
    }

    public static Object[] epistemicExpressionNOTDistKNOWSBEMC(String boolExpr) {
        //IDENTIFIER:id NOT KNOWS boolean_expr:be 
        // called in epistemicConstraintEvaluator
        // boolExpr = "[".concat(boolExpr).concat(";]");
        Object[] obj = Utilities.distKnowsBooleanExpressionMC(boolExpr);
        Object temp = obj[0];
        obj[0] = obj[1];
        obj[1] = temp;

        //store in history, to aid in finding counter model in model checking
        truthFalseStateHistory.add(obj);
        return obj;
    }

    public static Object[] distKnowsPHIExpression(String phiExpression) {
        StringBuilder expression = new StringBuilder();
        StringBuilder expressionMain = new StringBuilder();
        String result = "";
        String holdPhi = phiExpression;

        // the first index of the below object will keep the state ids where the given expression is true
        // the second index will keep the state ids where the given expression is false
        Object[] objStates = new Object[]{new HashSet<>(), new HashSet<>()};

        //iterate through all the states
        Iterator itStates = qualifiedStateMap.entrySet().iterator();
        while (itStates.hasNext()) {
            Map.Entry state = (Map.Entry) itStates.next();
            //     Iterator itSuccessors = ((ArrayList) ((TreeMap) state.getValue()).get(agentId)).iterator();

            //for the phi expression, say "liv knows s", what I want to do is to rewrite it as
            // "liv knows s==val" where val is the value of s at the selected state (that is the state for which 
            // we would examine the successors.
            // so I take the values from this state (e.g s=10) split it and replace it with "s==10" so we can then evaluate
            // or check it against all successor states (itself being a successor state). Any deviation from this value
            // among the successor states means that liv does not know s at that state, so we drop it as usual.
            //HOWEVER, THERE IS A PROBLEM, THE REPLACEMENT OF STRING WILL CAUSE PROBLEMS IF WE HAVE 
            // VARIABLES LIKE "ab" and "a": in this case if we replace "a" before replacing "ab" we could get a 
            // string like "a==5b". So we must adjust the replace all regex argument to say that whatever string identifier is 
            // followed by NO alphanumeric character

            String[] stateSelfLabel = stateLabelMap.get((Integer) state.getKey());
            for (int i = 0; i < stateSelfLabel.length; i++) {
                phiExpression = phiExpression.replaceAll(stateSelfLabel[i].split("=")[0].trim(), stateSelfLabel[i].split("=")[0].trim().concat("==").concat(stateSelfLabel[i].split("=")[1].trim()));
            }
            String holdReWrittenPhi = phiExpression;

            Iterator itAgents = ((TreeMap) state.getValue()).entrySet().iterator();
            //build a big conjuction expression for all the successor states of all agents
            while (itAgents.hasNext()) {
                // within the following while loop, each state will now subtitute the "s" or lvar with their own value, and evaluate the boolean expression
                Iterator itSuccessors = ((ArrayList<Integer>) (((Map.Entry) itAgents.next()).getValue())).iterator();
                while (itSuccessors.hasNext()) {
                    String[] stateLabel = stateLabelMap.get((Integer) itSuccessors.next());
                    //use the inEpistemicConstraints identifiers to fish out the relevant fields from this state
                    for (int i = 0; i < stateLabel.length; i++) {
                        //each state label is sth like q=5...
                        //we split according to the equal sign, and replace all q with 5 (for example) in the boolean expression
                        phiExpression = phiExpression.replaceAll(StringUtils.split(stateLabel[i], "=")[0].trim(), StringUtils.split(stateLabel[i], "=")[1].trim());
                    }
                    //make a conjuction of all the expressions for each of the successor states
                    expression.append("(").append(phiExpression).append(")||");
                    phiExpression = holdReWrittenPhi;
                }
                //remove the last && symbol
                expression.delete(expression.length() - 2, expression.length());
                // we add this disjuction for each agent, to check for distributed knowledge
                expressionMain.append("(".concat(expression.toString()).concat(")")).append("||");
                expression.delete(0, expression.length());
                phiExpression = holdReWrittenPhi;
            }
            //remove the last && symbol
            expressionMain.delete(expressionMain.length() - 2, expressionMain.length());

            System.out.println("@@@@@@@@@@@@@@@@@@@@Teh boolean expressionis " + expressionMain.toString());
            //now evaluate the resulting boolean expression
            ConstraintEvaluator ce = new ConstraintEvaluator(new BaseScanner(new StringReader("[".concat(expressionMain.toString()).concat(";]"))));

            try {
                result = ce.parse().value.toString();
                System.out.println("The result is ===> " + result);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            expressionMain.delete(0, expressionMain.length());
            expression.delete(0, expression.length());
            phiExpression = holdPhi;
            //check if result is true
            if (result.equalsIgnoreCase("true")) {
                //update the list of states where K(phi) is true
                ((HashSet<Integer>) objStates[0]).add((Integer) state.getKey());
            } else {
//                ArrayList<Integer> aList = new ArrayList<Integer>();
//                Collection collect = (Collection)aList;
//                HashSet<Integer> set = new HashSet<Integer>(); 
//                     
                //update the list of states where K(phi) is false
                ((HashSet<Integer>) objStates[1]).add((Integer) state.getKey());
                //  ((HashSet)Utilities.qualifiedStateMap.keySet()).removeAll(ep[0]);
            }

        }
        return objStates;
    }

    public static Object[] distKnowsPHIExpressionMC(String phiExpression) {
        StringBuilder expression = new StringBuilder();
        StringBuilder expressionMain = new StringBuilder();
        String result = "";
        String holdPhi = phiExpression;

        // the first index of the below object will keep the state ids where the given expression is true
        // the second index will keep the state ids where the given expression is false
        Object[] objStates = new Object[]{new HashSet<>(), new HashSet<>()};

        //iterate through all the states
        Iterator itStates = qualifiedStateMap.entrySet().iterator();
        while (itStates.hasNext()) {
            Map.Entry state = (Map.Entry) itStates.next();
            //     Iterator itSuccessors = ((ArrayList) ((TreeMap) state.getValue()).get(agentId)).iterator();

            //for the phi expression, say "liv knows s", what I want to do is to rewrite it as
            // "liv knows s==val" where val is the value of s at the selected state (that is the state for which 
            // we would examine the successors.
            // so I take the values from this state (e.g s=10) split it and replace it with "s==10" so we can then evaluate
            // or check it against all successor states (itself being a successor state). Any deviation from this value
            // among the successor states means that liv does not know s at that state, so we drop it as usual.
            //HOWEVER, THERE IS A PROBLEM, THE REPLACEMENT OF STRING WILL CAUSE PROBLEMS IF WE HAVE 
            // VARIABLES LIKE "ab" and "a": in this case if we replace "a" before replacing "ab" we could get a 
            // string like "a==5b". So we must adjust the replace all regex argument to say that whatever string identifier is 
            // followed by NO alphanumeric character

            String[] stateSelfLabel = stateLabelMap.get((Integer) state.getKey());
            for (int i = 0; i < stateSelfLabel.length; i++) {
                phiExpression = phiExpression.replaceAll(stateSelfLabel[i].split("=")[0].trim(), stateSelfLabel[i].split("=")[0].trim().concat("==").concat(stateSelfLabel[i].split("=")[1].trim()));
            }
            String holdReWrittenPhi = phiExpression;

            Iterator itAgents = ((TreeMap) state.getValue()).entrySet().iterator();
            //build a big conjuction expression for all the successor states of all agents
            while (itAgents.hasNext()) {
                // within the following while loop, each state will now subtitute the "s" or lvar with their own value, and evaluate the boolean expression
                Iterator itSuccessors = ((ArrayList<Integer>) (((Map.Entry) itAgents.next()).getValue())).iterator();
                while (itSuccessors.hasNext()) {
                    String[] stateLabel = stateLabelMap.get((Integer) itSuccessors.next());
                    //use the inEpistemicConstraints identifiers to fish out the relevant fields from this state
                    for (int i = 0; i < stateLabel.length; i++) {
                        //each state label is sth like q=5...
                        //we split according to the equal sign, and replace all q with 5 (for example) in the boolean expression
                        phiExpression = phiExpression.replaceAll(StringUtils.split(stateLabel[i], "=")[0].trim(), StringUtils.split(stateLabel[i], "=")[1].trim());
                    }
                    //make a conjuction of all the expressions for each of the successor states
                    expression.append("(").append(phiExpression).append(")||");
                    phiExpression = holdReWrittenPhi;
                }
                //remove the last && symbol
                expression.delete(expression.length() - 2, expression.length());
                // we add this disjuction for each agent, to check for distributed knowledge
                expressionMain.append("(".concat(expression.toString()).concat(")")).append("||");
                expression.delete(0, expression.length());
                phiExpression = holdReWrittenPhi;
            }
            //remove the last && symbol
            expressionMain.delete(expressionMain.length() - 2, expressionMain.length());

            System.out.println("@@@@@@@@@@@@@@@@@@@@Teh boolean expressionis " + expressionMain.toString());
            //now evaluate the resulting boolean expression
            //now evaluate the resulting boolean expression
            ConstraintEvaluator ce = new ConstraintEvaluator(new ModelCheckerScanner(new StringReader("[".concat(expression.toString()).concat(";]"))));

            try {
                result = ce.parse().value.toString();
                System.out.println("The result is ===> " + result);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            expressionMain.delete(0, expressionMain.length());
            expression.delete(0, expression.length());
            phiExpression = holdPhi;
            //check if result is true
            if (result.equalsIgnoreCase("true")) {
                //update the list of states where K(phi) is true
                ((HashSet<Integer>) objStates[0]).add((Integer) state.getKey());
            } else {
//                ArrayList<Integer> aList = new ArrayList<Integer>();
//                Collection collect = (Collection)aList;
//                HashSet<Integer> set = new HashSet<Integer>(); 
//                     
                //update the list of states where K(phi) is false
                ((HashSet<Integer>) objStates[1]).add((Integer) state.getKey());
                //  ((HashSet)Utilities.qualifiedStateMap.keySet()).removeAll(ep[0]);
            }

        }
        return objStates;
    }

    public static Object[] epistemicExpressionNOTDistKNOWSPHI(String phiExpr) {
        //IDENTIFIER:ag NOT KNOWS BRACKET phi:p BRACKET
        // called in epistemicConstraintEvaluator
        Object[] obj = Utilities.distKnowsPHIExpression(phiExpr);
        Object temp = obj[0];
        obj[0] = obj[1];
        obj[1] = temp;
        return obj;
    }

    public static Object[] epistemicExpressionNOTDistKNOWSPHIMC(String phiExpr) {
        //IDENTIFIER:ag NOT KNOWS BRACKET phi:p BRACKET
        // called in epistemicConstraintEvaluator
        Object[] obj = Utilities.distKnowsPHIExpressionMC(phiExpr);
        Object temp = obj[0];
        obj[0] = obj[1];
        obj[1] = temp;

        //store in history, to aid in finding counter model in model checking
        truthFalseStateHistory.add(obj);
        return obj;
    }

    public static String checkModel(String eProperty, String state) {
        String answer = "***";  // by default
        String stateID = "";
        
        String baseDir = System.getProperty("user.dir").replace('\\', '/').concat("/dots/");

        eProperty = eProperty.replaceAll("\\[", "%");
        eProperty = eProperty.replaceAll("]", "%");
        System.out.println(eProperty);

        ModelChecker mc = new ModelChecker(new ModelCheckerScanner(new StringReader(eProperty)));
        Object[] mcResult;
        try {
            mcResult = (Object[]) mc.parse().value;
            if (state.equalsIgnoreCase("*")) {
                // we check for all the states on the graph
                // the given property should be true for all the states on the graph to be true
                if (((HashSet) mcResult[0]).containsAll(Utilities.qualifiedStateMap.keySet())) {
                    answer = "true";
                    EpistemicKeypad.buttonCounterModel.setEnabled(false);
                } else {
                    answer = "false";
                    EpistemicKeypad.buttonCounterModel.setEnabled(true);
                    
                    StringBuilder expression = new StringBuilder();

                    //called from the parse tree after evaluating each epistemic constraint
                    HashSet<Integer> predecessorStates = new HashSet<>();
                    HashSet<Integer> predecessors = new HashSet<>();

                    //since no specific state was selected, just choose any state from the uppermost(outmost/last) entry of the falseSet
                    state = ((HashSet<Integer>) Utilities.truthFalseStateHistory.get(Utilities.truthFalseStateHistory.size()-1)[1]).toArray(new Integer[0])[0].toString();
                    predecessors.add(Integer.decode(state));
                    // add the orginator state to the hash set also
                    predecessorStates.add(Integer.decode(state));

                    int nodeCount = Utilities.truthFalseStateHistory.size();
                    Integer ss;
                    HashSet<Integer> tempPredecessors = new HashSet<>();
                    for (int i = 0; i < nodeCount-1; i++) {
                        
                        for (int j = 0; j < predecessors.size(); j++) {
                            //get all the successors of the predecessors and add them to the next round of predecessors
                            // and also the to the set of states which are to be considered possible
                            Iterator itAgents = qualifiedStateMap.get(predecessors.toArray(new Integer[0])[j]).entrySet().iterator();

//                        Iterator itStates = qualifiedStateMap.entrySet().iterator();
//                        
//                        while (itStates.hasNext()) {
//                            Map.Entry stateTemp = (Map.Entry) itStates.next();
//
//                            Iterator itAgents = ((TreeMap) stateTemp.getValue()).entrySet().iterator();
                            //build a big conjuction expression for all the successor states of all agents
                            while (itAgents.hasNext()) {
                                // within the following while loop, each state will now subtitute the "s" or lvar with their own value, and evaluate the boolean expression
                                Iterator itSuccessors = ((ArrayList<Integer>) (((Map.Entry) itAgents.next()).getValue())).iterator();
                                while (itSuccessors.hasNext()) {
                                    ss = (Integer) itSuccessors.next();
                                    tempPredecessors.add(ss);
                                    predecessorStates.add(ss);

                                }

                            }
                        }
                        //replace the existing predecessors with the successors discovered in this round
                        predecessors.clear();
                        predecessors = new HashSet<>(tempPredecessors);
                        tempPredecessors.clear();


                    }
                    predecessors.clear();
                    tempPredecessors.clear();
                    
                    //construct a constraint whereby all the agents consdier all the items in predecessorState set possible
                    Iterator itPredecessorStates = predecessorStates.iterator();
                    Integer s;
                    
                    while (itPredecessorStates.hasNext()) {
                        s = (Integer) itPredecessorStates.next();

                        Iterator itAgents = qualifiedStateMap.get(s).entrySet().iterator();
                        //all the agents at the given state know the given constraint
                        while (itAgents.hasNext()) {
                            expression.append("(").append((String) ((Map.Entry) itAgents.next()).getKey()).append("!@!(");
                            String[] stateLabel = stateLabelMap.get(s);
                            //use the inEpistemicConstraints identifiers to fish out the relevant fields from this state
                            for (int i = 0; i < stateLabel.length; i++) {
                                expression.append(stateLabel[i].replaceAll("=", "==")).append("&&");
                            }
                            expression.delete(expression.length() - 2, expression.length());
                            expression.append("))||");
                        }
                    }
                    predecessorStates.clear();
                    //remove the last || symbol
                    expression.delete(expression.length() - 2, expression.length());
                    String epistemicConstraint = "{".concat(expression.toString()).concat(";}");
                    System.out.println("the epistemic constraint is ==>" + epistemicConstraint);
                    //now make a copy of the qualified state map
                    Iterator itStatesMap = qualifiedStateMap.entrySet().iterator();
                    TreeMap keepQualifiedStateMap = new TreeMap<Integer, Map<String, ArrayList<Integer>>>(); // duplicates this object to avoid tampering with the original as this method will modify the map in use

                    while (itStatesMap.hasNext()) {
                        Map.Entry stateTemp = (Map.Entry) itStatesMap.next();
                        keepQualifiedStateMap.put((Integer) stateTemp.getKey(), new TreeMap<String, ArrayList<Integer>>());
                        Iterator itAgentsTemp = ((TreeMap) stateTemp.getValue()).entrySet().iterator();
                        //build a big conjuction expression for all the successor states of all agents
                        while (itAgentsTemp.hasNext()) {
                            Map.Entry agent = (Map.Entry) itAgentsTemp.next();
                            Iterator itSuccessors = ((ArrayList<Integer>) (agent.getValue())).iterator();
                            ((TreeMap<String, ArrayList<Integer>>) keepQualifiedStateMap.get((Integer) stateTemp.getKey())).put((String) agent.getKey(), new ArrayList<Integer>());
                            while (itSuccessors.hasNext()) {
                                ((TreeMap<String, ArrayList<Integer>>) keepQualifiedStateMap.get((Integer) stateTemp.getKey())).get((String) agent.getKey()).add((Integer) itSuccessors.next());
                                // String[] stateLabel = stateLabelMap.get((Integer) itSuccessors.next());
                            }
                        }
                    }

                    //now solve the epistemic constraint on the copy qualified state map, replace this later with the copied one
                    epistemicConstraint = epistemicConstraint.replaceAll("\\[", "%");
                    epistemicConstraint = epistemicConstraint.replaceAll("]", "%");
                    System.out.println(epistemicConstraint);
                    //  aConstraint = "[".concat(aConstraint).concat(";]"); // to put it in the right format for our constant evaluator
                    EpistemicConstraintEvaluator ece = new EpistemicConstraintEvaluator(new BaseScanner(new StringReader(epistemicConstraint)));
                    //String result = "";
                    try {
                        ece.parse();  //no return value, the graph is updated automatically once an epistemic constraint is evaluated fully
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                    TreeMap tempMap = new TreeMap<Integer, Map<String, ArrayList<Integer>>>(); // duplicates this object to avoid tampering with the original as this method will modify the map in use
                    //now swap the Utilities.qualifiedStateMap (which has now been modified by the epistemicConstraintEvaluator)
                    // with the original Utilities.qualifiedStateMap which was store prior to modification in keepQualifiedStateMap

                    tempMap = keepQualifiedStateMap;
                    keepQualifiedStateMap = (TreeMap<Integer, Map<String, ArrayList<Integer>>>) Utilities.qualifiedStateMap;
                    Utilities.qualifiedStateMap = tempMap;

                    //now use that modified version of the map to plot the graph of the counter model
                    Utilities.generateDot(keepQualifiedStateMap, "counterModel.dot");
                    Runtime.getRuntime().exec("dot -Tjpg " + baseDir + "counterModel.dot -o " + baseDir + "counterModel.jpg");

                }
            } else {
                //we search in the Truth Set for the given specific state which should be numeric

                if (((HashSet) mcResult[0]).contains(Integer.decode(state))) {
                    answer = "true";
                    EpistemicKeypad.buttonCounterModel.setEnabled(false);
                    
                } else {
                    answer = "false";
                    EpistemicKeypad.buttonCounterModel.setEnabled(true);
                    StringBuilder expression = new StringBuilder();

                    //called from the parse tree after evaluating each epistemic constraint
                    HashSet<Integer> predecessorStates = new HashSet<>();
                    HashSet<Integer> predecessors = new HashSet<>();

                    //add the originator state to the arraylist
                    predecessors.add(Integer.decode(state));
                    // add the orginator state to the hash set also
                    predecessorStates.add(Integer.decode(state));

                    int nodeCount = Utilities.truthFalseStateHistory.size();
                    Integer ss;
                    HashSet<Integer> tempPredecessors = new HashSet<>();
                    for (int i = 0; i < nodeCount-1; i++) {
                        
                        for (int j = 0; j < predecessors.size(); j++) {
                            //get all the successors of the predecessors and add them to the next round of predecessors
                            // and also the to the set of states which are to be considered possible
                            Iterator itAgents = qualifiedStateMap.get(predecessors.toArray(new Integer[0])[j]).entrySet().iterator();

//                        Iterator itStates = qualifiedStateMap.entrySet().iterator();
//                        
//                        while (itStates.hasNext()) {
//                            Map.Entry stateTemp = (Map.Entry) itStates.next();
//
//                            Iterator itAgents = ((TreeMap) stateTemp.getValue()).entrySet().iterator();
                            //build a big conjuction expression for all the successor states of all agents
                            while (itAgents.hasNext()) {
                                // within the following while loop, each state will now subtitute the "s" or lvar with their own value, and evaluate the boolean expression
                                Iterator itSuccessors = ((ArrayList<Integer>) (((Map.Entry) itAgents.next()).getValue())).iterator();
                                while (itSuccessors.hasNext()) {
                                    ss = (Integer) itSuccessors.next();
                                    tempPredecessors.add(ss);
                                    predecessorStates.add(ss);

                                }

                            }
                        }
                        //replace the existing predecessors with the successors discovered in this round
                        predecessors.clear();
                        predecessors = new HashSet<>(tempPredecessors);
                        tempPredecessors.clear();


                    }
                    predecessors.clear();
                    tempPredecessors.clear();
                    
                    //construct a constraint whereby all the agents consdier all the items in predecessorState set possible
                    Iterator itPredecessorStates = predecessorStates.iterator();
                    Integer s;
                    
                    while (itPredecessorStates.hasNext()) {
                        s = (Integer) itPredecessorStates.next();

                        Iterator itAgents = qualifiedStateMap.get(s).entrySet().iterator();
                        //all the agents at the given state know the given constraint
                        while (itAgents.hasNext()) {
                            expression.append("(").append((String) ((Map.Entry) itAgents.next()).getKey()).append("!@!(");
                            String[] stateLabel = stateLabelMap.get(s);
                            //use the inEpistemicConstraints identifiers to fish out the relevant fields from this state
                            for (int i = 0; i < stateLabel.length; i++) {
                                expression.append(stateLabel[i].replaceAll("=", "==")).append("&&");
                            }
                            expression.delete(expression.length() - 2, expression.length());
                            expression.append("))||");
                        }
                    }
                    predecessorStates.clear();
                    //remove the last || symbol
                    expression.delete(expression.length() - 2, expression.length());
                    String epistemicConstraint = "{".concat(expression.toString()).concat(";}");
                    System.out.println("the epistemic constraint is ==>" + epistemicConstraint);
                    //now make a copy of the qualified state map
                    Iterator itStatesMap = qualifiedStateMap.entrySet().iterator();
                    TreeMap keepQualifiedStateMap = new TreeMap<Integer, Map<String, ArrayList<Integer>>>(); // duplicates this object to avoid tampering with the original as this method will modify the map in use

                    while (itStatesMap.hasNext()) {
                        Map.Entry stateTemp = (Map.Entry) itStatesMap.next();
                        keepQualifiedStateMap.put((Integer) stateTemp.getKey(), new TreeMap<String, ArrayList<Integer>>());
                        Iterator itAgentsTemp = ((TreeMap) stateTemp.getValue()).entrySet().iterator();
                        //build a big conjuction expression for all the successor states of all agents
                        while (itAgentsTemp.hasNext()) {
                            Map.Entry agent = (Map.Entry) itAgentsTemp.next();
                            Iterator itSuccessors = ((ArrayList<Integer>) (agent.getValue())).iterator();
                            ((TreeMap<String, ArrayList<Integer>>) keepQualifiedStateMap.get((Integer) stateTemp.getKey())).put((String) agent.getKey(), new ArrayList<Integer>());
                            while (itSuccessors.hasNext()) {
                                ((TreeMap<String, ArrayList<Integer>>) keepQualifiedStateMap.get((Integer) stateTemp.getKey())).get((String) agent.getKey()).add((Integer) itSuccessors.next());
                                // String[] stateLabel = stateLabelMap.get((Integer) itSuccessors.next());
                            }
                        }
                    }

                    //now solve the epistemic constraint on the copy qualified state map, replace this later with the copied one
                    epistemicConstraint = epistemicConstraint.replaceAll("\\[", "%");
                    epistemicConstraint = epistemicConstraint.replaceAll("]", "%");
                    System.out.println(epistemicConstraint);
                    //  aConstraint = "[".concat(aConstraint).concat(";]"); // to put it in the right format for our constant evaluator
                    EpistemicConstraintEvaluator ece = new EpistemicConstraintEvaluator(new BaseScanner(new StringReader(epistemicConstraint)));
                    //String result = "";
                    try {
                        ece.parse();  //no return value, the graph is updated automatically once an epistemic constraint is evaluated fully
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                    TreeMap tempMap = new TreeMap<Integer, Map<String, ArrayList<Integer>>>(); // duplicates this object to avoid tampering with the original as this method will modify the map in use
                    //now swap the Utilities.qualifiedStateMap (which has now been modified by the epistemicConstraintEvaluator)
                    // with the original Utilities.qualifiedStateMap which was store prior to modification in keepQualifiedStateMap

                    tempMap = keepQualifiedStateMap;
                    keepQualifiedStateMap = (TreeMap<Integer, Map<String, ArrayList<Integer>>>) Utilities.qualifiedStateMap;
                    Utilities.qualifiedStateMap = tempMap;

                    //now use that modified version of the map to plot the graph of the counter model
                    Utilities.generateDot(keepQualifiedStateMap, "counterModel.dot");
                    
                    Runtime.getRuntime().exec("dot -Tjpg " + baseDir + "counterModel.dot -o " + baseDir + "counterModel.jpg");

                    
                }

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        //clear the truthFalseStateHistory in readiness for the next model checking action
        Utilities.truthFalseStateHistory.clear();
        return answer;
    }

    public static String[] getPartition() {
        return partition;
    }

    public static String[] getStateValues() {
        return stateValues;
    }
}
