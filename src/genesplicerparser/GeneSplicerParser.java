package genesplicerparser;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A parser to read in the donor and acceptor entries
 * from a GeneSplicer output file
 * @author bm542
 */
public class GeneSplicerParser {

    // <editor-fold defaultstate="collapsed" desc="Fields">
    /**
     * a list of the acceptor GeneSplicer entries
     */
    List<Integer> acceptors;

    /**
     * a list of the donor GeneSplicer entries
     */
    List<Integer> donors;

    /**
     * a sorted list of the acceptor GeneSplicer entries
     * @return the acceptors
     */
    public List<Integer> getAcceptors() {
        return acceptors;
    }

    /**
     * a sorted list of the acceptor GeneSplicer entries
     * @param acceptors the acceptors to set
     */
    public void setAcceptors(List<Integer> acceptors) {
        this.acceptors = acceptors;
    }

    /**
     * a sorted list of the donor GeneSplicer entries
     * @return the donors
     */
    public List<Integer> getDonors() {
        return donors;
    }

    /**
     * a sorted list of the donor GeneSplicer entries
     * @param donors the donors to set
     */
    public void setDonors(List<Integer> donors) {
        this.donors = donors;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Constructors">
    /**
     * default constructor to initialize empty
     * acceptor and donor sites
     */
    public GeneSplicerParser() {
        acceptors = new ArrayList<Integer>();
        donors = new ArrayList<Integer>();
    }

    /**
     * construct the acceptor and donor lists based on the
     * GeneSplicer output file
     * @param filepath the path to the GeneSplicer output file
     */
    public GeneSplicerParser(String filepath) throws FileNotFoundException, IOException {
        // initialize the lists
        this();

        // read in the file
        BufferedReader br = new BufferedReader(new FileReader(filepath));
        for(String line = br.readLine(); line != null; line = br.readLine()) {
            // parse the line
            GeneSplicerEntry entry = new GeneSplicerEntry(line);

            // add the entry to the appropriate list
            if(entry.type == GeneSplicerEntry.Type.Acceptor) {
                acceptors.add(entry.start);
            } else {
                donors.add(entry.start);
            }
        }
    }
    // </editor-fold>

}
