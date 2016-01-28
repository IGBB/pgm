package proteogenomicmapping;

import codetable.CodeTable;
import genesplicerparser.GeneSplicerParser;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Set;

/**
 * A class to map the given peptide sequences to a given genome
 * @author Brandon
 */
public class Mapper {
    
    // <editor-fold defaultstate="collapsed" desc="Mode Enum">
    public enum MapperMode {

        Prokaryote,
        Eukaryote,
        Codon,
        GeneSplicer
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">

    MapperMode mode;

    StateMachine stateMachine;
    List<BioSequence> peptides;
    CodeTable codeTable;
    Translator translator;

    String referenceFilename;
    String outputFilename;
    String outputFastaFilename;
    String outputGff3Filename;

    PrintWriter outputFile;
    PrintWriter outputFastaFile;
    PrintWriter outputGff3File;

    /**
     * mode specific parameters
     */
    int codons;
    GeneSplicerParser geneSplicerParser;
    Set<String> beginSpliceSites;
    Set<String> endSpliceSites;
// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Constructors">
    public Mapper(List<BioSequence> peptides, StateMachine stateMachine,
            String referenceFilePath, String outputFilename, String outputFastaFilename, String outputGff3Filename,
            CodeTable codeTable, MapperMode mode,
            String beginSpliceSitesPath, String endSpliceSitesPath,
            int codons, GeneSplicerParser geneSplicerParser) throws FileNotFoundException, IOException {

        this.peptides = peptides;
        this.stateMachine = stateMachine;
        this.referenceFilename = referenceFilePath;
        this.outputFilename = outputFilename;
        this.outputFastaFilename = outputFastaFilename;
        this.outputGff3Filename = outputGff3Filename;

        this.codeTable = codeTable;
        this.translator = new Translator(codeTable.getCodonTable());

        this.mode = mode;

        this.beginSpliceSites = FileReading.getBeginSpliceSites(beginSpliceSitesPath);
        this.endSpliceSites = FileReading.getEndSpliceSites(endSpliceSitesPath);
        this.codons = codons;
        this.geneSplicerParser = geneSplicerParser;

    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="File IO">
    /**
     * open all of the output files for writing
     * @throws IOException
     */
    public void openOutputFiles() throws IOException {
        // setup the output files
        outputFile = new PrintWriter(new BufferedWriter(new FileWriter(outputFilename)));
        outputFastaFile = new PrintWriter(new BufferedWriter(new FileWriter(outputFastaFilename)));
        outputGff3File = new PrintWriter(new BufferedWriter(new FileWriter(outputGff3Filename)));
    }

    /**
     * print the header for the output file
     */
    protected void printHeaders() {
        outputFile.print("Peptide ID\tPeptide Sequence\tGenome ID\tStart\tEnd\tStrand\tReading Frame\t");
        outputFile.print("RT Peptide Sequence\tePST Start\tePST End\tePST\tePST Length\tTranslated ePST\t");
        outputFile.print("Start Codon\tPeptide Probability\tPeptide Count");
        outputFile.println();

        outputGff3File.println("##gff-version 3");
    }

    /**
     * close all of the output files
     */
    public void closeOutputFiles() {
        outputFile.close();
        outputFastaFile.close();
        outputGff3File.close();
    }
    
    public void findUniqueEpsts() {
        
    }
    
    // </editor-fold>

    public void map() throws IOException, FileNotFoundException {
        openOutputFiles();

        printHeaders();
        int i = 0;
        FASTASequenceReader referenceSequences = new FASTASequenceReader(referenceFilename);
        while(referenceSequences.hasNext()) {
            BioSequence sequence = referenceSequences.next();
            if(i++ % 100 == 0) { System.out.print("."); }
            search(sequence);
        }

        closeOutputFiles();
        
        findUniqueEpsts();
    }

    /**
     * use the state machine to search the given peptide for the peptides
     * append the results to the output file
     * @param peptide the peptide DNA strand to search
     */
    //protected void search(RichSequence reference) {
    protected void search(BioSequence reference) {

        String f1 = reference.toString().toUpperCase();

        // sequence is the translated and reading-frame adjusted peptide sequence
        String sequence = translator.translateSequence(f1);

	// F1
	search(sequence, f1, "F1", reference.getId());

	// F2
        String f2 = translator.getReadingFrame(f1, "F2");
        sequence = translator.translateSequence(f2);
        search(sequence, f2, "F2", reference.getId());

	// F3
        String f3 = translator.getReadingFrame(f1, "F3");
        sequence = translator.translateSequence(f3);
        search(sequence, f3, "F3", reference.getId());

	// R1
        String r1 = translator.getReadingFrame(f1, "R1");
        sequence = translator.translateSequence(r1);
        search(sequence, r1, "R1", reference.getId());

	// R2
        String r2 = translator.getReadingFrame(f1, "R2");
        sequence = translator.translateSequence(r2);
        search(sequence, r2, "R2", reference.getId());

	// R3
        String r3 = translator.getReadingFrame(f1, "R3");
        sequence = translator.translateSequence(r3);
        search(sequence, r3, "R3", reference.getId());
    }

    /**
     * use the state machine to determine if any of the
     * peptides appear in the given peptide sequence
     *
     * refer to the readingFrame to adjust coordinates as necessary
     * @param peptide the translated sequence
     * @param reference the untranslated reference DNA sequence
     * @param readingFrame the reading frame from which this sequence was translated
     * @param sequenceName the name of this peptide sequence
     */
    protected void search(String peptide, String reference, String readingFrame, String sequenceName) {

        // which node am i in the state machine
        int state = 0;

	int peptideLength= peptide.length();
        int referenceLength = reference.length();

	for (int i = 0; i < peptideLength - 1; i++)
	{
            // determine which state to move to next
//            char ch = peptide.charAt(i);
//            ch -= 'A';
            String ch = peptide.substring(i, i+1);
            
            // check the new state
            //state = stateMachine.nodes.get(state).edges[ch];
            state = stateMachine.nodes.get(state).getNextState(ch);

            // did we match a pattern?
            int peptideIndex = stateMachine.nodes.get(state).pattern;
            if(peptideIndex != -1) {
                // peptideIndex-1 because the peptide indices are base 1
                String peptideName = peptides.get(peptideIndex - 1).getId();
                String peptideSequence = peptides.get(peptideIndex - 1).toString();
                int length = peptides.get(peptideIndex - 1).length();

                BioSequence matchedPeptide = peptides.get(peptideIndex-1);

                // the end position of the reverse translated peptide
                // i*3 currently points to the beginning of the stop codon
                // before accounting for reading frame
                int endRTP = i * 3;
                
                // adjust endRTP so that it points to the end of the stop codon
                endRTP += 3;

                // multiply by three because the peptide is a protein
                length *= 3;
                int startRTP = endRTP - length;

                // reverseTranslatedPeptide is the reverse translated peptide match
                String reverseTranslatedPeptide = reference.substring(startRTP, endRTP);

                // find the actual epst
                int startEpst = startRTP;
                int endEpst = endRTP;

                // find the epst
                IntWrapper start = new IntWrapper(startRTP);
                IntWrapper end = new IntWrapper(endRTP);
                String epst = findEpst(reference, startRTP, endRTP, start, end);
                startEpst = start.value;
                endEpst = end.value;

                // adjust the coordinates for the reading frame
                String strand = "+";
                if(readingFrame.startsWith("R")) { // reverse reading frame
                    strand = "-";

                    // flip the coordinates if on the reverse strand
                    startRTP = referenceLength - startRTP;
                    endRTP = referenceLength - endRTP;
                    startEpst = referenceLength - startEpst;
                    endEpst = referenceLength - endEpst;

                    /**
                     * because coordinates for the "start" of the reverse strand are
                     * at the end of the sequence, to keep the coordinates relative
                     * to the positive strand, there is no need to correct the coordinates
                     */
//                    if(readingFrame.substring(1).equals("2")) {
//                        startRTP--; endRTP--;
//                        startEpst--; endEpst--;
//                    } else if(readingFrame.substring(1).equals("3")) {
//                        startRTP-=2; endRTP-=2;
//                        startEpst-=2; endEpst-=2;
//                    }
                } else { // positive strand
                    if(readingFrame.substring(1).equals("2")) {
                        startRTP++; endRTP++;
                        startEpst++; endEpst++;
                    } else if(readingFrame.substring(1).equals("3")) {
                        startRTP+=2; endRTP+=2;
                        startEpst+=2; endEpst+=2;
                    }
                }
                
                // clamp the bounds
                if (startRTP < 0) {
                    startRTP = 0;
                }

                if (endRTP >= referenceLength) {
                    endRTP = referenceLength - 1;
                }
                if (startEpst < 0) {
                    startEpst = 0;
                }

                if (endEpst >= referenceLength) {
                    endEpst = referenceLength - 1;
                }

                // add one to start and end values because sequences are base 1
                startRTP++;
                // for some reason?? endRTP is already correct??
                //endRTP++;
                startEpst++;
                endEpst++;


                // translate the DNA version of the epst to a protein version
                int epstLength = Math.abs(startEpst - endEpst);
                String translatedEpst = translator.translateSequence(epst);

                String startCodon = "-";
                if(startRTP != startEpst) {
                    startCodon = epst.substring(0, 3);
                }

                // print everything out to the files
                outputFile.print(peptideName + "\t");
                outputFile.print(peptideSequence + "\t");

                outputFile.print(sequenceName + "\t" + startRTP + "\t" + endRTP + "\t" + strand + "\t" + readingFrame + "\t" + reverseTranslatedPeptide + "\t");
                outputFile.print(startEpst + "\t" + endEpst + "\t" + epst + "\t" + epstLength + "\t" + translatedEpst + "\t");
                outputFile.print(startCodon + "\t" + matchedPeptide.probability + "\t" + matchedPeptide.count);
                outputFile.println();

                outputGff3File.print(sequenceName + "\t" + "ProteogenomicMapping,RTP" + "\t" + "region" + "\t" + startRTP + "\t" + endRTP + "\t");
                outputGff3File.print("." + "\t" + strand + "\t" + "." + "\t" + "ID=" + peptideName + "; Name=" + peptideName);
                outputGff3File.println();

                outputGff3File.print(sequenceName + "\t" + "ProteogenomicMapping,ePST" + "\t" + "region" + "\t" + startEpst + "\t" + endEpst + "\t");
                outputGff3File.print("." + "\t" + strand + "\t" + "." + "\t" + "ID=" + peptideName + "; Name=" + peptideName);
                outputGff3File.println();


                outputFastaFile.println(">" + peptideName);
                outputFastaFile.println(epst);
            }
	}
    }

//    /**
//     * Find the complete epst which starts before startRTP and ends after endRTP.
//     * Note that peptide has already been translated into the appropriate reading
//     * frame, so the  start and end positions of the rtp are relative to the translated peptide.
//     * @param reference the reference genome against which the search is made
//     * @param startRTP the beginning of the reverse translated peptide
//     * @param endRTP the end of the reverse translated peptide
//     * @param startEpst a return parameter that will have the coordinates of the start of the epst
//     * @param endEpst a return paramter that will have the coordinates of the end of the epst
//     * @return the string representation of the complete epst
//     */
//    protected String findEpst(String reference, int startRTP, int endRTP, IntWrapper startEpst, IntWrapper endEpst) {
//
//        // check for the codons hack
//        if(codons > 0) {
//            return findEpstCodon(reference, startRTP, endRTP, startEpst, endEpst);
//        } else if (isEukaryote) {
//            return findEpstEukaryote(reference, startRTP, endRTP, startEpst, endEpst);
//        } else if (geneSplicerParser != null) {
//            return findEpstGeneSplicer(reference, startRTP, endRTP, startEpst, endEpst);
//        } else {
//            return findEpstProkaryote(reference, startRTP, endRTP, startEpst, endEpst);
//        }
//    }

    /**
     * Find the complete epst which starts before startRTP and ends after endRTP.
     * Note that peptide has already been translated into the appropriate reading
     * frame, so the  start and end positions of the rtp are relative to the translated peptide.
     * @param reference the reference genome against which the search is made
     * @param startRTP the beginning of the reverse translated peptide
     * @param endRTP the end of the reverse translated peptide
     * @param startEpst a return parameter that will have the coordinates of the start of the epst
     * @param endEpst a return paramter that will have the coordinates of the end of the epst
     * @return the string representation of the complete epst
     */
    protected String findEpst(String reference, int startRTP, int endRTP, IntWrapper startEpst, IntWrapper endEpst) {

        // check the mode
        switch(mode) {
            case Eukaryote:
                return findEpstEukaryote(reference, startRTP, endRTP, startEpst, endEpst);
            case Codon:
                return findEpstCodon(reference, startRTP, endRTP, startEpst, endEpst);
            case GeneSplicer:
                return findEpstGeneSplicer(reference, startRTP, endRTP, startEpst, endEpst);
            default:
                return findEpstProkaryote(reference, startRTP, endRTP, startEpst, endEpst);
        }
    }

    /**
     * a simple version of finding epst by simply going up and downstream
     * of the start and end of the rtp by a given number of codons.
     * the paramters are the same as findEpst
     */
    protected String findEpstCodon(String reference, int startRTP, int endRTP, IntWrapper startEpst, IntWrapper endEpst) {
        // update the start and end positions of the epst
        startEpst.value -= 3 * codons;
        endEpst.value += 3 * codons;

        // clamp the bounds
	if (startEpst.value < 0) {
		startEpst.value = 0;
	}

	if (endEpst.value >= reference.length()) {
		endEpst.value = reference.length() - 1;
	}

        String epst = reference.substring(startEpst.value, endEpst.value + 1);
        return epst;
    }

    /**
     * find startEpst by stepping backward from startRTP until encountering an in-frame stop codon and
     * then stepping forward until encountering an in-frame start codon (before startRTP).
     * if an in-frame start codon is not encountered, use the in-frame stop codon as the
     * beginning of the epst
     *
     * find endEpst by stepping forward from endRTP until encountering an in-frame stop codon
     */
    protected String findEpstProkaryote(String reference, int startRTP, int endRTP, IntWrapper startEpst, IntWrapper endEpst) {
        // find startEpst

        // step backward from startRTP until encountering an in-frame stop codon
        int inframeStop = startRTP;
        for(; inframeStop > 0; inframeStop -= 3) {
            if(contains(codeTable.getEndCodons(), reference, inframeStop)) {
                break;
            }
        }

        // step forward from inframeStop until encountering an in-frame start codon
        // but only step forward until startRTP
        for(startEpst.value = inframeStop; startEpst.value < startRTP; startEpst.value += 3) {
            if(contains(codeTable.getStartCodons(), reference, startEpst.value)) {
                break;
            }
        }

        // if no inframe-start codon was encountered when stepping forward
        // use the start of the reverse translated peptide
        if(startEpst.value >= startRTP) {
            startEpst.value = startRTP;
        }

        // find the stop by going forward to the first in-frame stop
        for(endEpst.value = endRTP; endEpst.value < reference.length(); endEpst.value += 3) {
            if(contains(codeTable.getEndCodons(), reference, endEpst.value)) {
                break;
            }
        }

        // endEpst is pointing to the beginning of the stop codon
	// so add 2 to the end position
	// so that startEpst points to the beginning of the start codon
	// and endEpst points to the end of the stop codon
        endEpst.value += 2;

        // clamp bounds
        if(startEpst.value < 0) {
            startEpst.value = 0;
        }
        if(endEpst.value >= reference.length()) {
            endEpst.value = reference.length() - 1;
        }

        // and find the epst
        String epst = reference.substring(startEpst.value, endEpst.value + 1);

        return epst;
    }

    /**
     * Find startEpst by stepping backward from startRTP until encountering an
     * in-frame stop, in-frame start, or (any frame) splice site start
     *
     * Find stopEpst by stepping forward from endRTP until encountering an
     * in-frame stop or (any frame) splice site stop
     *
     * the parameters are the same as findEpst
     */
    protected String findEpstEukaryote(String reference, int startRTP, int endRTP, IntWrapper startEpst, IntWrapper endEpst) {
        // use frameCounter to detect whether or not we are inframe with the reverse translated peptide
        int frameCounter = 0;

        // start at startRTP and step backwards
        for(startEpst.value = startRTP; startEpst.value > 0; startEpst.value--, frameCounter++) {
            // only check in-frame codons for starts or stops
            if(frameCounter % 3 == 0) {
                // stop
                if(contains(codeTable.getEndCodons(), reference, startEpst.value)) {
                    break;
                }
                // start
                if(contains(codeTable.getStartCodons(), reference, startEpst.value)) {
                    break;
                }
            }

            // always check for start splice sites
            if(contains(beginSpliceSites, reference, startEpst.value)) {
                break;
            }
        }

        // reset the frame counter
        frameCounter = 0;

        // start from stopRTP and step forward
        for(endEpst.value = endRTP; endEpst.value < reference.length(); endEpst.value++, frameCounter++) {
            // only check for in-frame stop codons
            if(frameCounter % 3 == 0) {
                // stop codon
                if(contains(codeTable.getEndCodons(), reference, endEpst.value)) {
                    break;
                }
            }

            // but always check for splice site stops
            if(contains(endSpliceSites, reference, endEpst.value)) {
                break;
            }
        }

        // clamp bounds
        if(startEpst.value < 0) {
            startEpst.value = 0;
        }
        if(endEpst.value >= reference.length()) {
            endEpst.value = reference.length() - 1;
        }

        // and find the epst
        String epst = reference.substring(startEpst.value, endEpst.value + 1);
        return epst;
    }

    /**
     * Find startEpst by stepping backward from startRTP until encountering an
     * in-frame stop, in-frame start, or (any frame) splice site start
     *
     * Find stopEpst by stepping forward from endRTP until encountering an
     * in-frame stop or (any frame) splice site stop
     *
     * the parameters are the same as findEpst
     */
    protected String findEpstGeneSplicer(String reference, int startRTP, int endRTP, IntWrapper startEpst, IntWrapper endEpst) {
        // use frameCounter to detect whether or not we are inframe with the reverse translated peptide
        int frameCounter = 0;

        // start at startRTP and step backwards
        for(startEpst.value = startRTP; startEpst.value > 0; startEpst.value--, frameCounter++) {
            // only check in-frame codons for starts or stops
            if(frameCounter % 3 == 0) {
                // stop
                if(contains(codeTable.getEndCodons(), reference, startEpst.value)) {
                    break;
                }
                // start
                if(contains(codeTable.getStartCodons(), reference, startEpst.value)) {
                    break;
                }
            }

            // always check for acceptor
            if(geneSplicerParser.getAcceptors().contains(startEpst.value)) {
                break;
            }
        }

        // reset the frame counter
        frameCounter = 0;

        // start from stopRTP and step forward
        for(endEpst.value = endRTP; endEpst.value < reference.length(); endEpst.value++, frameCounter++) {
            // only check for in-frame stop codons
            if(frameCounter % 3 == 0) {
                // stop codon
                if(contains(codeTable.getEndCodons(), reference, endEpst.value)) {
                    break;
                }
            }

            // but always check for donor
            if(geneSplicerParser.getDonors().contains(endEpst.value)) {
                break;
            }
        }

        // clamp bounds
        if(startEpst.value < 0) {
            startEpst.value = 0;
        }
        if(endEpst.value >= reference.length()) {
            endEpst.value = reference.length() - 1;
        }

        // and find the epst
        String epst = reference.substring(startEpst.value, endEpst.value + 1);
        return epst;
    }


    /**
     * check if the sequence beginning at position of peptide is within this set.
     * because some of the elements of the sets (such as splice site boundaries) are
     * not of uniform length, this method is used over the <tt>contains</tt> method.
     * @param set the set in which to search
     * @param peptide the peptide sequence
     * @param position the start position within the peptide sequence
     * @return if the substring of peptide starting at position is within the set
     */
    protected boolean contains(Set<String> set, String reference, int position) {
        for(String setString : set) {
            int stopPosition = Math.min(reference.length(), position + setString.length());
            String referenceString = reference.substring(position, stopPosition);
            if(referenceString.equals(setString)) {
                return true;
            }
        }
        return false;
    }
}
