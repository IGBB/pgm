package codetable;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Split the code file table into separate tokens for parsing
 * @author Brandon
 */
public class Tokenizer {

    /**
     * the various states of the fsm for tokenizing the file
     */
    enum State {
        Start,
        Integer,
        Token,
        String
    }

    /**
     * keep track of the different types of characters used in parsing the file
     */
    static final String[] controlCharacters = { ":", "=", "{", "}", ",", "-" };
    static final String[] whiteSpaceCharacters = { " ", "\t", "\n" };
    static final String[] digitCharacters = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9" };

    /**
     * check if the given array contains the given string
     * @param needle the string to search for
     * @param haystack the array in which to search
     * @return true if the needle is in the haystack
     */
    protected static boolean contains(String needle, String[] haystack) {
        for(String s : haystack) {
            if(s.equals(needle)) {
                return true;
            }
        }
        return false;
    }

    /**
     * check if s does not belong to any of the given types of characters
     * @param s the string to consider
     * @return true if s is not a control, white space or digit character
     */
    protected static boolean isTokenCharacter(String s) {
        return !(contains(s, controlCharacters) || contains(s, whiteSpaceCharacters) || contains(s, digitCharacters));
    }

    /**
     * the text in the code table file
     */
    String source;

    /**
     * the currently built token
     */
    String currentToken;

    /**
     * the previously built tokens
     */
    List<Token> tokens;

    /**
     * the current state in tokenizing the file
     */
    State currentState;

    /**
     * the current position within the source text
     */
    int currentIndex;

    public Tokenizer(String filename) throws FileNotFoundException, IOException {
        // initialize variables
        currentState = State.Start;
        currentIndex = 0;
        tokens = new ArrayList<Token>();
        source = "";

        // read the source
        BufferedReader sourceFile = new BufferedReader(new FileReader(filename));
        for(String line = sourceFile.readLine(); line != null; line = sourceFile.readLine()) {
            if(line.length() < 2 || !line.substring(0, 2).equals("--")) {
                source += line;
            }
        }
    }

    /**
     * tokenize the code file and return the tokens
     * @return the tokens from the file
     */
    public List<Token> getTokens() {
        while(currentIndex < source.length()) {
            analyze();
        }
        return tokens;
    }

    protected void analyze() {
        switch(currentState) {
            case Start:
                start();
                break;
            case Integer:
                integer();
                break;
            case String:
                string();
                break;
            case Token:
                token();
                break;
        }
    }

    protected void start() {
        String currentChar = getCurrentChar();
        if(contains(currentChar,controlCharacters)) {
            // then create a token for the control character
            tokens.add(new Token(currentChar, currentChar));

            // stay in the start state

            // move to the next character in the source
            currentIndex++;
        } else if(contains(currentChar, whiteSpaceCharacters)) {
            // ignore this character
            // stay in the start state
            // move to the next character
            currentIndex++;
        } else if (currentChar.equals("\"")) {
            // begin a new string constant
            currentToken = "";

            // move to the string state
            currentState = State.String;

            // move to the next character
            currentIndex++;
        } else if (contains(currentChar, digitCharacters)) {
            // begin a new integer constant, save this character
            currentToken = currentChar;

            // move to the integer state
            currentState = State.Integer;

            // move to the next character
            currentIndex++;
        } else {
            // assume it is a character beginning a token, save this character
            currentToken = currentChar;

            // move to the token state
            currentState = State.Token;

            // move to the next character
            currentIndex++;
        }
    }

    protected void integer() {
        String currentChar = getCurrentChar();
        if(contains(currentChar, digitCharacters)) {
            // continue the integer constant, save this character
            currentToken += currentChar;

            // stay in the integer state
            // move to the next character
            currentIndex++;
        } else {
            // assume beginning a new token

            // create the integer token
            tokens.add(new Token("integer", currentToken));

            // move to the start state
            currentState = State.Start;

            // do not move to the next character
        }
    }

    protected void token() {
        String currentChar = getCurrentChar();
        if(contains(currentChar, digitCharacters) || isTokenCharacter(currentChar)) {
            // continue the token, save this character
            currentToken += currentChar;

            // stay in the token state
            // move to the next character
            currentIndex++;
        } else {
            // assume beginning a new token

            // create the integer token
            tokens.add(new Token("token", currentToken));

            // move to the start state
            currentState = State.Start;

            // do not move to the next character
        }
    }

    protected void string() {
        String currentChar = getCurrentChar();
        if(!currentChar.equals("\"")) {
            // continue the string constant, save this character
            currentToken += currentChar;

            // stay in the string state
            // move to the next character
            currentIndex++;
        } else {
            // it is the end of the string constant

            // create the integer token
            tokens.add(new Token("string", currentToken));

            // move to the start state
            currentState = State.Start;

            // move to the next character
            currentIndex++;
        }
    }

    /**
     * helper method to find the next character to process
     * from the source string
     * @return the next unprocessed character from the source string
     */
    protected String getCurrentChar() {
        return source.substring(currentIndex, currentIndex + 1);
    }
}