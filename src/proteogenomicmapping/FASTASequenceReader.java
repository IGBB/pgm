package proteogenomicmapping;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Brandon
 */
public class FASTASequenceReader implements Iterator<BioSequence> {

    BufferedReader sequenceFile;
    String currentLine;

    public FASTASequenceReader(String filename) throws FileNotFoundException, IOException {
        sequenceFile = new BufferedReader(new FileReader(filename));
        currentLine = sequenceFile.readLine();
    }

    public boolean hasNext() {
        return currentLine != null;
    }

    public BioSequence next() {
        try {
            // assume line is not null and pointing to the identifier line (">Sequence ID");
            String id = currentLine.substring(1);

            BioSequence next = new BioSequence(id);
            for (currentLine = sequenceFile.readLine();
                currentLine != null && !currentLine.startsWith(">");
                currentLine = sequenceFile.readLine()) {

                next.append(currentLine);
            }

            // close the file if we reached the end of it
            if(currentLine == null) {
                sequenceFile.close();
            }
            return next;
        } catch (IOException ex) {
            ex.printStackTrace();
            System.exit(-1);
        }
        // should never reach here
        assert(false);
        return null;
    }

    public void remove() {
        throw new UnsupportedOperationException("Remove is not supported for this iterator.");
    }

    public static List<BioSequence> readSequences(String filename) throws FileNotFoundException, IOException {
        List<BioSequence> list = new ArrayList<BioSequence>();
        Iterator<BioSequence> sequences = new FASTASequenceReader(filename);

        while(sequences.hasNext()) {
            list.add(sequences.next());
        }

        return list;
    }
}
