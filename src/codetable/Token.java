package codetable;

/**
 * A token from the source for the code table
 * @author Brandon
 */
public class Token {

    /**
     * the semantics attached to the type of token
     */
    String syntacticCode;

    /**
     * the actual value of the token from the source
     */
    String semanticValue;

    /**
     * create a new token
     * @param syntacticCode the syntactic code for the token
     * @param semanticValue the semantic value for the token
     */
    public Token(String syntacticCode, String semanticValue) {
        this.syntacticCode = syntacticCode;
        this.semanticValue = semanticValue;
    }
}
