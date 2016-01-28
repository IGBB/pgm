package codetable;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Represent one mapping from letters to amino acids
 * @author Brandon
 */
public class CodeTable {

    /**
     * parse all of the code tables from the given file,
     * then return the one with the given name
     * @param filename the name of the code table file
     * @param tableName the name of the desired table
     * @return the code table in the given file with the given name
     * @throws FileNotFoundException
     * @throws IOException
     * @throws Exception
     */
    public static CodeTable getCodeTable(String filename, String tableName) throws FileNotFoundException, IOException, Exception {
        TreeMap<String, CodeTable> tables = getCodeTables(filename);
        return tables.get(tableName);
    }

    /**
     * tokenize and parse all of the code tables in the given file
     * @param filename the code table file to parse
     * @return a mapping from code table name to code table
     *          because a tree map data structure is used,
     *          the keys in the mapping are sorted
     */
    public static TreeMap<String, CodeTable> getCodeTables(String filename) throws FileNotFoundException, IOException, Exception {
        // first, tokenize the file
        Tokenizer t = new Tokenizer(filename);
        List<Token> tokens = t.getTokens();

        // then parse the tokens to get the code tables
        Parser p = new Parser(tokens);
        Map<String, CodeTable> tables = p.getCodeTables();

        // finally, put the code tables into a map
        TreeMap<String, CodeTable> map = new TreeMap<String, CodeTable>();
        for(CodeTable table : tables.values()) {
            map.put(table.getPrimaryName(), table);
        }

        // and return the map
        return map;
    }

    List<String> names;
    String id;
    String ncbi;
    String sncbi;
    String base1;
    String base2;
    String base3;

    /**
     * the current index within the list of tokens
     * in general, the table will not start with the first token
     * and will not end with the last token
     */
    int currentIndex;

    public CodeTable(List<Token> tokens, int currentIndex) throws Exception {
        names = new ArrayList<String>();

        // currentIndex should point to the beginning '{'
        // after finishing, it should point to the '}'
        if(!tokens.get(currentIndex++).syntacticCode.equals("{")) {
            throw new Exception("Invalid token");
        }

        // read all the names
        while(tokens.get(currentIndex).semanticValue.equals("name")) {
            if(!tokens.get(currentIndex++).semanticValue.equals("name")) {
                throw new Exception("Invalid token");
            }

            if(!tokens.get(currentIndex++).syntacticCode.equals("string")) {
                throw new Exception("Invalid token");
            } else {
                names.add(tokens.get(currentIndex - 1).semanticValue);
            }

            if(!tokens.get(currentIndex++).syntacticCode.equals(",")) {
                throw new Exception("Invalid token");
            }
        }

        // id
        if(!tokens.get(currentIndex++).semanticValue.equals("id")) {
            throw new Exception("Invalid token");
        }

        if(!tokens.get(currentIndex++).syntacticCode.equals("integer")) {
            throw new Exception("Invalid token");
        } else {
            id = tokens.get(currentIndex - 1).semanticValue;
        }

        if(!tokens.get(currentIndex++).syntacticCode.equals(",")) {
            throw new Exception("Invalid token");
        }

        // ncbieaa
        if(!tokens.get(currentIndex++).semanticValue.equals("ncbieaa")) {
            throw new Exception("Invalid token");
        }

        if(!tokens.get(currentIndex++).syntacticCode.equals("string")) {
            throw new Exception("Invalid token");
        } else {
            ncbi = tokens.get(currentIndex - 1).semanticValue;
        }

        if(!tokens.get(currentIndex++).syntacticCode.equals(",")) {
            throw new Exception("Invalid token");
        }

        // sncbieaa
        if(!tokens.get(currentIndex++).semanticValue.equals("sncbieaa")) {
            throw new Exception("Invalid token");
        }

        if(!tokens.get(currentIndex++).syntacticCode.equals("string")) {
            throw new Exception("Invalid token");
        } else {
            sncbi = tokens.get(currentIndex - 1).semanticValue;
        }

        // base 1
        if(!tokens.get(currentIndex++).syntacticCode.equals("-")) {
            throw new Exception("Invalid token");
        }

        if(!tokens.get(currentIndex++).syntacticCode.equals("-")) {
            throw new Exception("Invalid token");
        }

        if(!tokens.get(currentIndex++).semanticValue.equals("Base1")) {
            throw new Exception("Invalid token");
        }

        if(!tokens.get(currentIndex++).syntacticCode.equals("token")) {
            throw new Exception("Invalid token");
        } else {
            base1 = tokens.get(currentIndex - 1).semanticValue;
        }

        // base 2
        if(!tokens.get(currentIndex++).syntacticCode.equals("-")) {
            throw new Exception("Invalid token");
        }

        if(!tokens.get(currentIndex++).syntacticCode.equals("-")) {
            throw new Exception("Invalid token");
        }

        if(!tokens.get(currentIndex++).semanticValue.equals("Base2")) {
            throw new Exception("Invalid token");
        }

        if(!tokens.get(currentIndex++).syntacticCode.equals("token")) {
            throw new Exception("Invalid token");
        } else {
            base2 = tokens.get(currentIndex - 1).semanticValue;
        }

        // base 3
        if(!tokens.get(currentIndex++).syntacticCode.equals("-")) {
            throw new Exception("Invalid token");
        }

        if(!tokens.get(currentIndex++).syntacticCode.equals("-")) {
            throw new Exception("Invalid token");
        }

        if(!tokens.get(currentIndex++).semanticValue.equals("Base3")) {
            throw new Exception("Invalid token");
        }

        if(!tokens.get(currentIndex++).syntacticCode.equals("token")) {
            throw new Exception("Invalid token");
        } else {
            base3 = tokens.get(currentIndex - 1).semanticValue;
        }

        // move past the last '}'
        if(!tokens.get(currentIndex++).syntacticCode.equals("}")) {
            throw new Exception("Invalid token");
        }

        // store the current index for use for the next code table
        this.currentIndex = currentIndex;
    }

    /**
     * given the three bases specified by base1, base2 and base3
     * calculate a mapping between the bases and the amino acid
     * specified by the ncbi string
     * @return the mapping from codons to amino acid
     */
    public Map<String, String> getCodonTable() {
        Map<String, String> codonTable = new HashMap<String, String>();
        for(int i = 0; i < ncbi.length(); i++) {
            String codon = base1.substring(i, i + 1) + base2.substring(i, i + 1) + base3.substring(i, i + 1);
            String aminoAcid = ncbi.substring(i, i + 1);
            codonTable.put(codon, aminoAcid);
        }

        return codonTable;
    }

    /**
     * find all of the codons for which the sncbi
     * value is 'M', which is considered to be the start codon
     * @return the set of codons for which the sncbi value is 'M'
     */
    public Set<String> getStartCodons() {
        Set<String> startCodons = new HashSet<String>();

        for(int i = 0; i < sncbi.length(); i++) {
            if(sncbi.substring(i, i + 1).equals("M")) {
                String startCodon = base1.substring(i, i + 1) + base2.substring(i, i + 1) + base3.substring(i, i + 1);
                startCodons.add(startCodon);
            }
        }

        return startCodons;
    }

    /**
     * find all of the codons for which the ncbi
     * value is '*', which is considered to be the end codon
     * @return the set of codons for which the ncbi value is '*'
     */
    public Set<String> getEndCodons() {
        Set<String> startCodons = new HashSet<String>();

        for(int i = 0; i < sncbi.length(); i++) {
            if(ncbi.substring(i, i + 1).equals("*")) {
                String startCodon = base1.substring(i, i + 1) + base2.substring(i, i + 1) + base3.substring(i, i + 1);
                startCodons.add(startCodon);
            }
        }

        return startCodons;
    }

    /**
     * return the main name for this table
     * @return the first name value encountered when parsing this table
     */
    public String getPrimaryName() {
        if(names.size() > 0) {
            return names.get(0);
        }

        return "";
    }
}