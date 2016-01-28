package proteogenomicmapping;

import java.util.HashMap;
import java.util.Map;

/**
 * Complement and translate between DNA and amino acids
 * using a particular codon table
 * @author Brandon
 */
public class Translator {

    /**
     * a mapping between a codon and an amino acid
     */
    Map<String, String> codonTable;
    
    /**
     * a mapping between an amino acid and its complement
     */
    Map<String, String> complementTable;

    /**
     * create a translator using default mappings
     */
    public Translator() {
        populateTables();
    }

    /**
     * create a translator using the given mapping
     * between codons and amino acids
     * @param codonTable the mapping between codons and amino acids
     */
    public Translator(Map<String, String> codonTable) {
        this.codonTable = codonTable;
        populateComplementTable();
    }

    /**
     * translate the given DNA sequence into a protein sequence
     * NOTE sequence needs to be all upper case
     * @param sequence the DNA sequence
     * @return the translated protein sequence
     */
    public String translateSequence(String sequence) {
        int l = sequence.length();
	int l_3 = l / 3;
        StringBuffer sb = new StringBuffer();
        sb.setLength(l_3);

	for(int i = 0; i < l - 2; i+= 3) {
            sb.setCharAt(i / 3, translate(sequence.substring(i, i + 3)).charAt(0));
	}

        return sb.toString();
    }

    /**
     * use the codon table to translate the given codon
     * to an amino acid.
     * Return "X" if the codon does not correspond to a known amino acid
     * @param codon the three character sequence to translate
     * @return the amino acid corresponding to the codon
     *          or "X" if the codon is not in the table
     */
    public String translate(String codon) {
        String aminoAcid = codonTable.get(codon);
        if(aminoAcid == null) {
            return "X";
        }
        return aminoAcid;
    }

    /**
     * return the (upper-case) complement
     * of the given nucleotide
     * @param nucleotide the nucleotide to complement
     * @return the complement of nucleotide
     */
    public String complement(String nucleotide) {
        return complementTable.get(nucleotide);
    }

    /**
     * populate the complement table and the codon
     * table with default values.
     * (the complement table only has default values)
     */
    protected void populateTables() {
        populateComplementTable();
        populateCodonTable();
    }

    /**
     * create the mapping between a nucleotide
     * and its complement
     */
    protected void populateComplementTable() {
        complementTable = new HashMap<String, String>();
        complementTable.put("A", "T");
        complementTable.put("a", "T");
        complementTable.put("C", "G");
        complementTable.put("c", "G");
        complementTable.put("G", "C");
        complementTable.put("g", "C");
        complementTable.put("T", "A");
        complementTable.put("t", "A");
        complementTable.put("N", "N");
        complementTable.put("n", "N");
    }


    /**
     * create a mapping between a codon and the
     * corresponding amino acid using a default mapping
     */
    protected void populateCodonTable() {
        codonTable.put("UUU", "F");
        codonTable.put("TTT", "F");

        codonTable.put("UUC", "F");
        codonTable.put("TTC", "F");

        codonTable.put("UUA", "L");
        codonTable.put("TTA", "L");

        codonTable.put("UUG", "L");
        codonTable.put("TTG", "L");


        codonTable.put("CUU", "L");
        codonTable.put("CTT", "L");

        codonTable.put("CUC", "L");
        codonTable.put("CTC", "L");

        codonTable.put("CUA", "L");
        codonTable.put("CTA", "L");

        codonTable.put("CUG", "L");
        codonTable.put("CTG", "L");


        codonTable.put("AUU", "I");
        codonTable.put("ATT", "I");

        codonTable.put("AUC", "I");
        codonTable.put("ATC", "I");

        codonTable.put("AUA", "I");
        codonTable.put("ATA", "I");

        codonTable.put("AUG", "M");
        codonTable.put("ATG", "M");


        codonTable.put("GUU", "V");
        codonTable.put("GTT", "V");

        codonTable.put("GUC", "V");
        codonTable.put("GTC", "V");

        codonTable.put("GUA", "V");
        codonTable.put("GTA", "V");

        codonTable.put("GUG", "V");
        codonTable.put("GTG", "V");

        ////////
        codonTable.put("UCU", "S");
        codonTable.put("TCT", "S");

        codonTable.put("UCC", "S");
        codonTable.put("TCC", "S");

        codonTable.put("UCA", "S");
        codonTable.put("TCA", "S");

        codonTable.put("UCG", "S");
        codonTable.put("TCG", "S");


        codonTable.put("CCU", "P");
        codonTable.put("CCT", "P");

        codonTable.put("CCC", "P");
        codonTable.put("CCC", "P");

        codonTable.put("CCA", "P");
        codonTable.put("CCA", "P");

        codonTable.put("CCG", "P");
        codonTable.put("CCG", "P");


        codonTable.put("ACU", "T");
        codonTable.put("ACT", "T");

        codonTable.put("ACC", "T");
        codonTable.put("ACC", "T");

        codonTable.put("ACA", "T");
        codonTable.put("ACA", "T");

        codonTable.put("ACG", "T");
        codonTable.put("ACG", "T");


        codonTable.put("GCU", "A");
        codonTable.put("GCT", "A");

        codonTable.put("GCC", "A");
        codonTable.put("GCC", "A");

        codonTable.put("GCA", "A");
        codonTable.put("GCA", "A");

        codonTable.put("GCG", "A");
        codonTable.put("GCG", "A");

        ////////
        codonTable.put("UAU", "Y");
        codonTable.put("TAT", "Y");

        codonTable.put("UAC", "Y");
        codonTable.put("TAC", "Y");

        codonTable.put("UAA", "W");
        codonTable.put("TAA", "W");

        codonTable.put("UAG", "W");
        codonTable.put("TAG", "W");

        codonTable.put("CAU", "H");
        codonTable.put("CAT", "H");

        codonTable.put("CAC", "H");
        codonTable.put("CAC", "H");

        codonTable.put("CAA", "Q");
        codonTable.put("CAA", "Q");

        codonTable.put("CAG", "Q");
        codonTable.put("CAG", "Q");


        codonTable.put("AAU", "N");
        codonTable.put("AAT", "N");

        codonTable.put("AAC", "N");
        codonTable.put("AAC", "N");

        codonTable.put("AAA", "K");
        codonTable.put("AAA", "K");

        codonTable.put("AAG", "K");
        codonTable.put("AAG", "K");


        codonTable.put("GAU", "D");
        codonTable.put("GAT", "D");

        codonTable.put("GAC", "D");
        codonTable.put("GAC", "D");

        codonTable.put("GAA", "E");
        codonTable.put("GAA", "E");

        codonTable.put("GAG", "E");
        codonTable.put("GAG", "E");

        ////////
        codonTable.put("UGU", "C");
        codonTable.put("TGT", "C");

        codonTable.put("UGC", "C");
        codonTable.put("TGC", "C");

        codonTable.put("UGA", "W");
        codonTable.put("TGA", "W");

        codonTable.put("UGG", "W");
        codonTable.put("TGG", "W");


        codonTable.put("CGU", "R");
        codonTable.put("CGT", "R");

        codonTable.put("CGC", "R");
        codonTable.put("CGC", "R");

        codonTable.put("CGA", "R");
        codonTable.put("CGA", "R");

        codonTable.put("CGG", "R");
        codonTable.put("CGG", "R");


        codonTable.put("AGU", "S");
        codonTable.put("AGT", "S");

        codonTable.put("AGC", "S");
        codonTable.put("AGC", "S");

        codonTable.put("AGA", "R");
        codonTable.put("AGA", "R");

        codonTable.put("AGG", "R");
        codonTable.put("AGG", "R");


        codonTable.put("GGU", "G");
        codonTable.put("GGT", "G");

        codonTable.put("GGC", "G");
        codonTable.put("GGC", "G");

        codonTable.put("GGA", "G");
        codonTable.put("GGA", "G");

        codonTable.put("GGG", "G");
        codonTable.put("GGG", "G");
    }

    

    /**
     * return the given sequence transformed into the given reading frame
     * for forward reading frames, it simply entails dropping characters
     * from the beginning of the string
     * for reverse reading frames, the translator must be used to get the
     * complement string of the sequence, then some characters are dropped
     * from the beginning of those strings
     * @param sequence the sequence to transform into the given reading frame
     * @param readingFrame the reading frame for the returned string,
     *          valid values are "F1", "F2", "F3", "R1", "R2", "R3"
     * @return
     */
    public String getReadingFrame(String sequence, String readingFrame) {
        // make sure not to generate an index out of bounds error
        // by specifying a beginning index greater than the lenght
        // of the sequence
        int length = sequence.length();

        if(readingFrame.equals("F1")) {
            return sequence;
        } else if (readingFrame.equals("F2")) {
            return sequence.substring(Math.min(1, length));
        } else if (readingFrame.equals("F3")) {
            return sequence.substring(Math.min(2, length));
        } else if (readingFrame.equals("R1")) {
            return getReverseComplement(sequence);
        } else if (readingFrame.equals("R2")) {
            return getReverseComplement(sequence).substring(Math.min(1, length));
        } else {
            return getReverseComplement(sequence).substring(Math.min(2, length));
        }
    }

    /**
     * use the translator to find the reverse complement of the given sequence
     * @param sequence the sequence of which to find the reverse complement
     * @return the reverse complement string of sequence
     */
    public String getReverseComplement(String sequence) {
        StringBuilder reverseComplement = new StringBuilder();
        
        // first, just reverse the sequence
        StringBuilder reverseSequence = new StringBuilder(sequence);
        reverseSequence.reverse();
        
        // then populate the reverse complement
        for(int i = 0; i < reverseSequence.length(); i++) {
            String reverseCharacter = complement(reverseSequence.substring(i, i + 1));
            reverseComplement.append(reverseCharacter);
        }
        
        return reverseComplement.toString();
    }

}
