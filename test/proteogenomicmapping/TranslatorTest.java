package proteogenomicmapping;

import codetable.CodeTable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test to ensure translation and complementation
 * works correctly for DNA sequences
 * @author Brandon
 */
public class TranslatorTest {

    Translator instance;

    public TranslatorTest() throws FileNotFoundException, IOException, Exception {
        CodeTable table = CodeTable.getCodeTable("genetic_code_table.txt", "Standard");
        Map<String, String> codonTable = table.getCodonTable();
        instance = new Translator(codonTable);
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of translateSequence method, of class Translator.
     * This method translates a DNA sequence into a protein sequence.
     * The translation is performed using the Standard code table for this test.
     * translateSequence does _not_ perform any sort of reading frame adjustments.
     * If the sequence is in another reading frame, that already needs to
     * be calculated.
     */
    @Test
    public void testTranslateSequence() {
        System.out.println("translateSequence");
        String sequence = "CGTTGCCAACCCGGGCCACACCAAACGGTGTGGAA";
        String expResult = "RCQPGPHQTVW";
        String result = instance.translateSequence(sequence);
        assertEquals(expResult, result);
    }

    /**
     * Test of translate method, of class Translator.
     * This method converts a single codon (a string of size 3)
     * representing DNA bases into a size 1 string
     * representing the amino acid which those 3 codons represent.
     * If there is no specified amino acid for those 3 bases, "X"
     * is returned.  This allows easily handling "N"s.
     * Only "A" "C" "G" and "T" are valid base characters.
     */
    @Test
    public void testTranslate() {
        System.out.println("translate, present");
        String codon = "TTT";
        String expResult = "F";
        String result = instance.translate(codon);
        assertEquals(expResult, result);

        System.out.println("translate, absent");
        codon = "TTN";
        expResult = "X";
        result = instance.translate(codon);
        assertEquals(expResult, result);
    }

    /**
     * Test of complement method, of class Translator.
     * complement() just looks up the complement of a single DNA base.
     * It does _not_ complement an entire sequence.
     */
    @Test
    public void testComplement() {
        System.out.println("complement");
        String nucleotide = "A";
        String expResult = "T";
        String result = instance.complement(nucleotide);
        assertEquals(expResult, result);
    }

    /**
     * Test of getReadingFrame of class Translator
     * this class transforms the given sequence into the given reading frame.
     * the sequence is assumed to be "untransformed".  That is, the sequence
     * is assumed to be in F1 reading frame.  Thus, if the reading frame is
     * in the reverse strand, it is shifted.  The frame shift is then taken
     * into consideration.
     *
     * An additional call is needed to translate the transformed DNA sequence
     * into an amino acid sequence
     *
     * This order of operations is important when getting the reading frame
     * for a DNA sequence that has already been transformed!
     */
    @Test
    public void testGetReadingFrame() {
        String sequence  = "CGTTGCCAACCCGGGCCACACCAAACGGTGTGGAA";

        System.out.println("getReadingFrame, F1");
        String expResult = "RCQPGPHQTVW";
        String result = instance.getReadingFrame(sequence, "F1");
        result = instance.translateSequence(result);
        assertEquals(expResult, result);

        System.out.println("getReadingFrame, F2");
        expResult = "VANPGHTKRCG";
        result = instance.getReadingFrame(sequence, "F2");
        result = instance.translateSequence(result);
        assertEquals(expResult, result);

        System.out.println("getReadingFrame, F3");
        expResult = "LPTRATPNGVE";
        result = instance.getReadingFrame(sequence, "F3");
        result = instance.translateSequence(result);
        assertEquals(expResult, result);

        System.out.println("getReadingFrame, R1");
        expResult = "FHTVWCGPGWQ";
        result = instance.getReadingFrame(sequence, "R1");
        result = instance.translateSequence(result);
        assertEquals(expResult, result);

        System.out.println("getReadingFrame, R2");
        expResult = "STPFGVARVGN";
        result = instance.getReadingFrame(sequence, "R2");
        result = instance.translateSequence(result);
        assertEquals(expResult, result);

        System.out.println("getReadingFrame, R3");
        expResult = "PHRLVWPGLAT";
        result = instance.getReadingFrame(sequence, "R3");
        result = instance.translateSequence(result);
        assertEquals(expResult, result);
    }

    /**
     * Test of getReverseComplement of class Translator.
     * getReverseComplement() first reverses the sequence string.
     * it then takes the complement of each base.
     */
    @Test
    public void testGetReverseComplement() {
        System.out.println("reverseComplement");
        String sequence  = "CGTTGCCAACCCGGGCCACACCAAACGGTGTGGAA";
        String expResult = "TTCCACACCGTTTGGTGTGGCCCGGGTTGGCAACG";
        String result = instance.getReverseComplement(sequence);
        assertEquals(expResult, result);
    }

}