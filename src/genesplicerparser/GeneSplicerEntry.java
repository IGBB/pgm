package genesplicerparser;

/**
 * One entry from a GeneSplicer output file
 * @author bm542
 */
public class GeneSplicerEntry implements Comparable<GeneSplicerEntry> {

    // <editor-fold defaultstate="collapsed" desc="Fields">
    public enum Type {
        Acceptor, Donor
    }

    protected int start;
    protected int end;
    protected Type type;
    /**
     * @return the start
     */
    public int getStart() {
        return start;
    }

    /**
     * @param start the start to set
     */
    public void setStart(int start) {
        this.start = start;
    }

    /**
     * @return the end
     */
    public int getEnd() {
        return end;
    }

    /**
     * @param end the end to set
     */
    public void setEnd(int end) {
        this.end = end;
    }

    /**
     * @return the type
     */
    public Type getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(Type type) {
        this.type = type;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Constructors">
    /**
     * create a default GeneSplicer entry
     */
    public GeneSplicerEntry() {
        start = -1;
        end = -1;
        type = null;
    }

    /**
     * parse the line from the GeneSplicer result file
     * currently ignores the strength/confidence values
     * @param line
     */
    public GeneSplicerEntry(String line) {
        String[] split = line.split(" ");
        start = Integer.parseInt(split[0]);
        end = Integer.parseInt(split[1]);
        if(line.contains("donor")) {
            type = Type.Donor;
        } else {
            type = Type.Acceptor;
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Object Overrides">
    public int compareTo(GeneSplicerEntry o) {
        return start - o.start;
    }
    // </editor-fold>
}
