package proteogenomicmapping;

/**
 *
 * @author Brandon
 */
public class BioSequence implements Comparable<BioSequence> {

    // <editor-fold defaultstate="collapsed" desc="Fields">
    protected String id;
    protected StringBuilder sequence;
    protected double probability;
    protected int count;

    private String toStringCache;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Getters and Setters">
    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the sequence
     */
    public StringBuilder getSequence() {
        return sequence;
    }

    /**
     * @param sequence the sequence to set
     */
    public void setSequence(StringBuilder sequence) {
        this.sequence = sequence;
    }

    /**
     * @return the probability
     */
    public double getProbability() {
        return probability;
    }

    /**
     * @param probability the probability to set
     */
    public void setProbability(double probability) {
        this.probability = probability;
    }

    /**
     * @return the count
     */
    public int getCount() {
        return count;
    }

    /**
     * @param count the count to set
     */
    public void setCount(int count) {
        this.count = count;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Constructors">
    public BioSequence() {
        this("");
    }

    public BioSequence(String id) {
        this.id = id;
        sequence = new StringBuilder();
        probability = 1;
        count = 1;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="StringBuilder Passthroughs">
    public void append(String s) {
        sequence.append(s);
        toStringCache = null;
    }

    public int length() {
        return sequence.length();
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Object Overrides">
    @Override
    public String toString() {
        if(toStringCache == null) {
            toStringCache = sequence.toString();
        }
        return toStringCache;
    }

    public int compareTo(BioSequence o) {
        return toString().compareTo(o.toString());
    }
    // </editor-fold>
}