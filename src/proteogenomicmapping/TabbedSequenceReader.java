package proteogenomicmapping;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author bm542
 */
public class TabbedSequenceReader implements Iterator<BioSequence> {

    // <editor-fold defaultstate="collapsed" desc="Fields">
    Scanner sequenceFile;
    String currentLine;// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Constructors">
    public TabbedSequenceReader(String sequenceFilename) throws FileNotFoundException {
        sequenceFile = new Scanner(new File(sequenceFilename));
        currentLine = sequenceFile.nextLine();
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Iterator Methods">
    public boolean hasNext() {
        return currentLine != null;
    }

    public BioSequence next() {
        BioSequence next = new BioSequence();
        String[] split = currentLine.split("\t");
        String sequence = split[0];
        double probability = Double.parseDouble(split[1]);
        int count = Integer.parseInt(split[2]);

        next.append(sequence);
        next.probability = probability;
        next.count = count;

        if(sequenceFile.hasNextLine()) {
            currentLine = sequenceFile.nextLine();
        } else {
            currentLine = null;
            sequenceFile.close();
        }
        return next;
    }

    public void remove() {
        throw new UnsupportedOperationException("Not supported.");
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="File IO">
    /**
     * read the tabbed sequences from a file.
     * each line of the file is assumed to have a separate sequence, probability and count.
     * the form of the lines should be <sequence>\t<probability>\t<count>
     * @param filename
     * @return
     * @throws FileNotFoundException
     */
    public static List<BioSequence> readSequences(String filename) throws FileNotFoundException {
        List<BioSequence> sequences = new ArrayList<BioSequence>();
        Iterator<BioSequence> sequenceFile = new TabbedSequenceReader(filename);

        while (sequenceFile.hasNext()) {
            sequences.add(sequenceFile.next());
        }
        return sequences;
    }// </editor-fold>
}