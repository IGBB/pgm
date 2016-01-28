/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package proteogenomicmapping;

import genesplicerparser.GeneSplicerParser;
import proteogenomicmapping.Mapper.MapperMode;
import java.util.List;
import codetable.CodeTable;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Brandon
 */
public class MapperTest {

    Mapper instance;

    public MapperTest() throws FileNotFoundException, IOException, Exception {
        instance = getBaseMapper();
    }

    /**
     * Test of search method, of class Mapper.
     *
     * test the search method
     * this method takes as input an amino acid sequence (peptide) to search
     * this is always one of the input sequences which has been translated
     * into the given reading frame.  reference is the untranslated DNA sequence.
     * the sequence name is the fasta (or other) identifier attached to the sequence
     */
    @Test
    public void testSearchF1() throws FileNotFoundException, IOException, Exception {
        System.out.println("search, F1");
        instance.openOutputFiles();
        
        String reference = "TAGATTGAATGAAGGGTGACGATGAATTCGGCCGTGGCGAACGGCGAACGGGAATGATCTAGGTAT";
        String readingFrame = "F1";
        String sequenceName = "test sequence";
        String peptide = instance.translator.getReadingFrame(reference, readingFrame);
        peptide = instance.translator.translateSequence(peptide);
        
        instance.search(peptide, reference, readingFrame, sequenceName);

        instance.closeOutputFiles();

        // now check the output file
        List<String> outputFileLines = FileReading.getLinesList(instance.outputFilename);
        String result = outputFileLines.get(0);
        String expResult = "test	VANG	test sequence	34	45	+	F1	GTGGCGAACGGC	22	57	ATGAATTCGGCCGTGGCGAACGGCGAACGGGAATGA	35	MNSAVANGERE*";
        assertEquals(expResult, result);
    }

    @Test
    public void testSearchF2() throws FileNotFoundException, IOException, Exception {
        System.out.println("search, F2");
        instance.openOutputFiles();

        String reference = "ATAGATTGAATGAAGGGTGACGATGAATTCGGCCGTGGCGAACGGCGAACGGGAATGATCTAGGTAT";
        String readingFrame = "F2";
        String sequenceName = "test sequence";
        reference = instance.translator.getReadingFrame(reference, readingFrame);
        String peptide = instance.translator.translateSequence(reference);

        instance.search(peptide, reference, readingFrame, sequenceName);

        instance.closeOutputFiles();

        // now check the output file
        List<String> outputFileLines = FileReading.getLinesList(instance.outputFilename);
        String result = outputFileLines.get(0);
        String expResult = "test	VANG	test sequence	35	46	+	F2	GTGGCGAACGGC	23	58	ATGAATTCGGCCGTGGCGAACGGCGAACGGGAATGA	35	MNSAVANGERE*";
        assertEquals(expResult, result);
    }

    @Test
    public void testSearchF3() throws FileNotFoundException, IOException, Exception {
        System.out.println("search, F3");
        instance.openOutputFiles();

        String reference = "TATAGATTGAATGAAGGGTGACGATGAATTCGGCCGTGGCGAACGGCGAACGGGAATGATCTAGGTAT";
        String readingFrame = "F3";
        String sequenceName = "test sequence";
        reference = instance.translator.getReadingFrame(reference, readingFrame);
        String peptide = instance.translator.translateSequence(reference);

        instance.search(peptide, reference, readingFrame, sequenceName);

        instance.closeOutputFiles();

        // now check the output file
        List<String> outputFileLines = FileReading.getLinesList(instance.outputFilename);
        String result = outputFileLines.get(0);
        String expResult = "test	VANG	test sequence	36	47	+	F3	GTGGCGAACGGC	24	59	ATGAATTCGGCCGTGGCGAACGGCGAACGGGAATGA	35	MNSAVANGERE*";
        assertEquals(expResult, result);
    }

    @Test
    public void testSearchR1() throws FileNotFoundException, IOException, Exception {
        System.out.println("search, R1");
        instance.openOutputFiles();

        //String reference = "TAGATTGAATGAAGGGTGACGATGAATTCGGCCGTGGCGAACGGCGAACGGGAATGATCTAGGTAT";
        String reference = "ATACCTAGATCATTCCCGTTCGCCGTTCGCCACGGCCGAATTCATCGTCACCCTTCATTCAATCTA";
        String readingFrame = "R1";
        String sequenceName = "test sequence";
        reference = instance.translator.getReadingFrame(reference, readingFrame);
        String peptide = instance.translator.translateSequence(reference);

        instance.search(peptide, reference, readingFrame, sequenceName);

        instance.closeOutputFiles();

        // now check the output file
        List<String> outputFileLines = FileReading.getLinesList(instance.outputFilename);
        String result = outputFileLines.get(0);
        String expResult = "test	VANG	test sequence	34	21	-	R1	GTGGCGAACGGC	46	11	ATGAATTCGGCCGTGGCGAACGGCGAACGGGAATGA	35	MNSAVANGERE*";
        assertEquals(expResult, result);
    }

    @Test
    public void testSearchR2() throws FileNotFoundException, IOException, Exception {
        System.out.println("search, R2");
        instance.openOutputFiles();

        //String reference = "TAGATTGAATGAAGGGTGACGATGAATTCGGCCGTGGCGAACGGCGAACGGGAATGATCTAGGTAT";
        String reference = "ATACCTAGATCATTCCCGTTCGCCGTTCGCCACGGCCGAATTCATCGTCACCCTTCATTCAATCTAG";
        String readingFrame = "R2";
        String sequenceName = "test sequence";
        reference = instance.translator.getReadingFrame(reference, readingFrame);
        String peptide = instance.translator.translateSequence(reference);

        instance.search(peptide, reference, readingFrame, sequenceName);

        instance.closeOutputFiles();

        // now check the output file
        List<String> outputFileLines = FileReading.getLinesList(instance.outputFilename);
        String result = outputFileLines.get(0);
        String expResult = "test	VANG	test sequence	34	21	-	R2	GTGGCGAACGGC	46	11	ATGAATTCGGCCGTGGCGAACGGCGAACGGGAATGA	35	MNSAVANGERE*";
        assertEquals(expResult, result);
    }

    @Test
    public void testSearchR3() throws FileNotFoundException, IOException, Exception {
        System.out.println("search, R3");
        instance.openOutputFiles();

        //String reference = "TAGATTGAATGAAGGGTGACGATGAATTCGGCCGTGGCGAACGGCGAACGGGAATGATCTAGGTAT";
        String reference = "ATACCTAGATCATTCCCGTTCGCCGTTCGCCACGGCCGAATTCATCGTCACCCTTCATTCAATCTAGG";
        String readingFrame = "R3";
        String sequenceName = "test sequence";
        reference = instance.translator.getReadingFrame(reference, readingFrame);
        String peptide = instance.translator.translateSequence(reference);

        instance.search(peptide, reference, readingFrame, sequenceName);

        instance.closeOutputFiles();

        // now check the output file
        List<String> outputFileLines = FileReading.getLinesList(instance.outputFilename);
        String result = outputFileLines.get(0);
        String expResult = "test	VANG	test sequence	34	21	-	R3	GTGGCGAACGGC	46	11	ATGAATTCGGCCGTGGCGAACGGCGAACGGGAATGA	35	MNSAVANGERE*";
        assertEquals(expResult, result);
    }

    protected final Mapper getBaseMapper() throws FileNotFoundException, IOException, Exception {

        String peptideFilePath = "testPeptides.fasta";
        String referenceFilePath = "testReference.fasta";
        String codeFile = "genetic_code_table.txt";
        String codeName = "Standard";

        String outputFilename = "testOutput.txt";
        String outputFastaFilename = "testOutput.fasta";
        String outputGff3Filename = "testOutput.gff3";

        String beginSpliceSitesPath = "";
        String endSpliceSitesPath = "";

        int codons = -1;
        GeneSplicerParser gsp = null;

        List<BioSequence> peptides = FASTASequenceReader.readSequences(peptideFilePath);


        StateMachine sm = new StateMachine(peptides);
        CodeTable table = CodeTable.getCodeTable(codeFile, codeName);
        MapperMode mode = Mapper.MapperMode.Prokaryote;

        Mapper m = new Mapper(peptides, sm,
                referenceFilePath, outputFilename, outputFastaFilename, outputGff3Filename,
                table, mode,
                beginSpliceSitesPath, endSpliceSitesPath,
                codons, gsp);

        return m;
    }

}