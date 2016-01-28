package proteogenomicmapping;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;

/**
 * Representation of the fsm built by Aho-Corasick algorithm
 * @author bm542
 */
public class StateMachine {

    // the maximum number of edges for a node
    public static final int maxEdges = 32;

    /**
     * the states in the state machine
     */
    List<Node> nodes;

    /**
     * the list of peptides from which the machine will be built
     */
    List<BioSequence> peptides;

    /**
     * a mapping from the peptide sequences to their
     * index within the peptide file (e.g. the first peptide in the file is...)
     * the mapping is base 1
     */
    Map<BioSequence, Integer> indices;

    // <editor-fold defaultstate="collapsed" desc="Constructors">
    public StateMachine(List<BioSequence> peptides) throws FileNotFoundException, IOException {

        nodes = new ArrayList<Node>();

        // read in the peptide sequences
        this.peptides = peptides;

        // keep track of the original indices, base 1
        indices = new HashMap<BioSequence, Integer>();
        for(int i = 0; i < peptides.size(); i++) {
            indices.put(peptides.get(i), i + 1);
        }

        // sort the peptide sequences
        Collections.sort(peptides);

        // then construct the tree for the algorithm
        constructTree(0, peptides.size(), true);

        // and the failure links
        computeFailureLinks();

        // and finally finish out the state machine
        computeStateTransitions();
    }// </editor-fold>

    /**
     * write this state machine to a file
     * @param file the path of the file to write
     */
    public void write(String file) throws IOException {
        String s = toString();
        PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));
        pw.println(s);
        pw.close();
    }

    /**
     * construct the basic structure of the fsm
     * which is essentially a tree where the edges represent
     * characters in a breadth first type search of the list of peptides
     * @param start begin constructing the tree from the given peptide index
     * @param limit stop constructing the tree at this peptide
     * @param usePeptideNumericID if true, use the absolute peptide index
     *                              otherwise, use the relative index (index - start)
     */
    protected void constructTree(int start, int limit, boolean usePeptideNumericID) {
        // add the root node to the list
        nodes.add(new Node());

        // loop over the peptide sequences from start to limit
        for(int i = start; i < limit; i++) {
            // look at this node in the list
            Integer nodeIndex = 0;

            String peptideSequence = peptides.get(i).toString();

            // for each character in the sequence
            for(int j = 0; j < peptideSequence.length(); j++) {
                // convert the base at this position to an index
                //byte edge = (byte)(peptideSequence.charAt(j) - 'A');
                String edge = peptideSequence.substring(j, j+1);

                // does the edge already exist
                //if(nodes.get(nodeIndex).edges[edge] != 0) {
                if(nodes.get(nodeIndex).edges.containsKey(edge)) {
                    // then go to the next node
                    //nodeIndex = nodes.get(nodeIndex).edges[edge];
                    nodeIndex = nodes.get(nodeIndex).edges.get(edge);
                } else {
                    // then the edge does not exist, so create a new node and edge
                    // add the new edge from the current node to the new node
                    // and the new node
                    //nodes.get(nodeIndex).edges[edge] = nodes.size();
                    nodes.get(nodeIndex).edges.put(edge, nodes.size());
                    Node newNode = new Node();
                    newNode.level = nodes.get(nodeIndex).level + 1;
                    newNode.parent = nodeIndex;
                    newNode.bChar = edge;

                    // and add it to the list
                    nodes.add(newNode);

                    // and follow it
                    nodeIndex = nodes.size() - 1;
                }
            }

            if(usePeptideNumericID) {
                nodes.get(nodeIndex).pattern = indices.get(peptides.get(i));
            } else {
                nodes.get(nodeIndex).pattern = (int)(i - start);
            }
        }
    }

    /**
     * update the fsm structure to account for mismatches
     * if the mismatch is a prefix represented by a previous node
     * the failure link points to the node instead of the root
     */
    protected void computeFailureLinks() {
        // perform a breadth-first search to determine when failure links
        // should point to something other than the root
        Queue<Node> queue = new LinkedList<Node>();
        
        // start with the root
        queue.add(nodes.get(0));
        
        while(!queue.isEmpty()) {
            // get the next node
            Node node = queue.remove();
            
//            // push all of its children
//            for(int n = 0; n < maxEdges; n++) {
//                // if there is a node for this link
//                if(node.edges[n] != 0) { // -1) {
//                    int childIndex = node.edges[n];
//                    Node child = nodes.get(childIndex);
//                    queue.add(child);
//                }
//            }

            for(Entry<String, Integer> link : node.edges.entrySet()) {
                Node child = nodes.get(link.getValue());
                queue.add(child);
            }

            // if the node is not a root,
            if(node.level > 1) {
                // look at the fail link for the parent
                Node parent = nodes.get(node.parent);
                int parentFailLink = parent.failLink;

                // keep backtracking until the fail link for
                // the character from node is found
                while(parentFailLink != 0) {
                    parent = nodes.get(parentFailLink);
                    //if(parent.edges[node.bChar] != 0) {// -1) {
                    if(parent.edges.containsKey(node.bChar)) {
                        break;
                    }
                    parentFailLink = parent.failLink;
                }

                parent = nodes.get(parentFailLink);
                Integer failLink = parent.edges.get(node.bChar);
                //if(parent.edges[node.bChar] != 0) {// -1) {
                if(failLink != null) {
                    // then we have found the fail link
                    // for this node
                    //int failLink = parent.edges[node.bChar];
                    Node failNode = nodes.get(failLink);

                    if(node.pattern == -1 && failNode.pattern != -1) {
                        node.pattern = failNode.pattern;
                    }

                    node.failLink = failLink;
                }
            }
        }
    }

    /**
     * based on the computed tree structure and fail links,
     * generate the actual transition function for the state machine
     */
    protected void computeStateTransitions() {
        // use a breadth-first search
        Queue<Node> queue = new LinkedList<Node>();
        queue.add(nodes.get(0));

        while(!queue.isEmpty()) {
            Node node = queue.remove();

//            // push all of its children
//            for(int n = 0; n < maxEdges; n++) {
//                // if there is a node for this link
//                if(node.edges[n] != 0) {// -1) {
//                    int childIndex = node.edges[n];
//                    Node child = nodes.get(childIndex);
//                    queue.add(child);
//                }
//            }
            
            for(Entry<String, Integer> link : node.edges.entrySet()) {
                Node child = nodes.get(link.getValue());
                queue.add(child);
            }

            if(node.level != 0) {
                // check the fail link
                Node failNode = nodes.get(node.failLink);

                // update the pattern if necessary
                if(node.pattern == -1 && failNode.pattern != -1) {
                    node.pattern = failNode.pattern;
                }

//                // copy over all of the fail links
//                for(int n = 0; n < maxEdges; n++) {
//                    //if(node.edges[n] == -1 && failNode.edges[n] != -1) {
//                    if(node.edges[n] == 0 && failNode.edges[n] != 0) {
//                        node.edges[n] = failNode.edges[n];
//                    }
//                }

                for(Entry<String, Integer> link : failNode.edges.entrySet()) {
                    if(!node.edges.containsKey(link.getKey())) {
                        node.edges.put(link.getKey(), link.getValue());
                    }
                }
            }
        }
    }

    /**
     * represent a node in the tree
     */
    protected class Node {
        //int[] edges;
        Map<String, Integer> edges;
        int level;
        int failLink;
        int parent;
        int pattern;
        //byte bChar;
        String bChar;

        public Node() {
            edges = new HashMap<String, Integer>();
//            edges = new int[maxEdges];
//            for(int i = 0; i < maxEdges; i++) {
//                edges[i] = 0;//-1;
//            }

            level = 0;
            failLink = 0;
            parent = 0;
            pattern = -1;
            //bChar = 0;
            bChar = "";
        }

        public int getNextState(String edge) {
            Integer nextState = edges.get(edge);
            if(nextState == null) {
                nextState = 0;
            }
            return nextState;
        }
    }

}
