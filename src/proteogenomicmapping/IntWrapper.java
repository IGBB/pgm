/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package proteogenomicmapping;

/**
 *
 * @author Brandon
 */
public class IntWrapper {

    // <editor-fold defaultstate="collapsed" desc="Fields">
    public int value;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Constructors">
    public IntWrapper() {
        value = 0;
    }

    public IntWrapper(int value) {
        this.value = value;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Object Overrides">
    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @Override
    public IntWrapper clone() {
        return new IntWrapper(value);
    }
    // </editor-fold>
}
