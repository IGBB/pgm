package codetable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Given a list of tokens from the code table file,
 * parse it into a list of code tables
 * @author Brandon
 */
public class Parser {

    /**
     * the tokenized version of the code table file
     */
    List<Token> tokens;

    /**
     * all of the parsed code tables
     */
    Map<String, CodeTable> codeTables;

    /**
     * the name of the code file
     */
    String name;

    /**
     * the current token to be parsed
     */
    int currentToken;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        codeTables = new HashMap<String, CodeTable>();
    }

    public Map<String, CodeTable> getCodeTables() throws Exception {

        // check the beginning of the file

        // first token should be "token" and the name
        if(tokens.get(0).syntacticCode.equals("token")) {
            name = tokens.get(0).semanticValue;
        } else {
            throw new Exception("Invalid token");
        }

        // then [:][:][=][{]
        if(!tokens.get(1).syntacticCode.equals(":")) {
            throw new Exception("Invalid token");
        }
        if(!tokens.get(2).syntacticCode.equals(":")) {
            throw new Exception("Invalid token");
        }
        if(!tokens.get(3).syntacticCode.equals("=")) {
            throw new Exception("Invalid token");
        }
        if(!tokens.get(4).syntacticCode.equals("{")) {
            throw new Exception("Invalid token");
        }

        currentToken = 5;

        // now get the tables
        while(getNextTable());

        return codeTables;
    }

    /**
     * read the next table from the
     * @return
     */
    protected boolean getNextTable() throws Exception {
        CodeTable t = new CodeTable(tokens, currentToken);
        codeTables.put(t.getPrimaryName(), t);

        currentToken = t.currentIndex;

        // current token points to just after the '}'
        // at the end of the table
        // if a comma follows, then there is another table
        return (tokens.get(currentToken++).syntacticCode.equals(","));
    }
}
