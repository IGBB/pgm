/*
 * What happens when I change this and push the change
 */

package proteogenomicmapping;

import argumentparser.ArgumentParser;
import bioinformaticsanalysis.SequestDifference;
import codetable.CodeTable;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import genesplicerparser.GeneSplicerParser;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import proteogenomicmapping.Mapper.MapperMode;

/**
 *
 * @author Brandon
 */
public class Main {

    public enum Mode {
        PGM,
        SequestDifference
    }
    
    // <editor-fold defaultstate="collapsed" desc="Argument Parsing">
    protected static JSAPResult parseArguments(String[] args) throws JSAPException {

        ArgumentParser ap = new ArgumentParser();
        ap.registerStringParameter("peptideFilePath", "oldserum.mspep.fa", 'p');
        ap.registerStringParameter("referenceFilePath", "bursa.old.fa", 'r');
        ap.registerStringParameter("codeFile", "genetic_code_table.txt", 'c');
        ap.registerStringParameter("codeName", "Standard", 'n');

        ap.registerStringParameter("outputFilename", "bursa.out", 'o');
        ap.registerStringParameter("outputFastaFilename", "bursa.out.fa", 'f');
        ap.registerStringParameter("outputGff3Filename", "bursa.gff3", '3');

        ap.registerBooleanParameter("isEukaryote", "false", 'i');
        ap.registerIntegerParameter("codons", "0", 'd');
        ap.registerStringParameter("geneSplicerOutputPath", "", 'g');
        ap.registerStringParameter("beginSpliceSitesPath", "", 'b');
        ap.registerStringParameter("endSpliceSitesPath", "", 'e');

        ap.registerSwitch("tabbedFile", 't');

        ap.registerSwitch("help", 'h');
        
        ap.registerStringParameter("mode", "PGM", 'm');

        JSAPResult config = ap.parse(args);
        return config;
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Help Text">
    protected static void printHelp() {
        System.out.print("This program performs proteogenomic mapping as described in <citation>.\n");
        System.out.print("The program can either be run with a graphical user interface (GUI) or from the command line.\n");
        System.out.print("\n");
        System.out.print("In order to run the GUI mode, simply run the program with no arguments.\n");
        System.out.print("\tjava -jar ProteogenomicMapping.jar\n");
        System.out.print("\n");
        System.out.print("In order to run from the command line, run the program using the appropriate arguments.\n");
        System.out.print("\n");
        System.out.print("--help, -h\t\t\tdisplay this message\n");
        System.out.print("--peptideFilePath, -p\t\tthe path to the fasta file containing the peptides for which to search (needles)\n");
        System.out.print("--referenceFilePath, -r\tthe path to the fasta file containing the reference sequences against which to search (haystacks)\n");
        System.out.print("--codeFile, -c\t\tthe path to the file containing the mapping from codons to amino acids, as well as start and end sequences, see <ncbi_link> for more details\n");
        System.out.print("--codeName, -n\t\t[optional] the particular code table to use for the mapping.  By default, the 'Standard' table will be used.\n");

        System.out.print("--outputFilename, -o\t\tthe path where the full output will be written\n");
        System.out.print("--outputFastaFilename, -f\t\tthe path where the fasta file containing the ePSTs generated according to the 'isEukaryote' and 'codon' flags will be written\n");
        System.out.print("--outputGff3Filename, -3\t\t[optional] the path where the gff file containing the ePSTs and rtps will be written.\n");

        System.out.print("--isEukaryote, -i\t\t[optional] a boolean string ('true' or 'false') indicating whether the organism is eukaryotic.  If this is 'true', then splicing will be performed; otherwise, splice sites will be ignored.\n");
        System.out.print("--beginSpliceSitesPath, -b\t[optional] the path to the file defining the DNA sequences used to mark the beginning of splice sites.  By default, the cannonical begin splice sites are used.\n");
        System.out.print("--endSpliceSitesPath, -e\t[optional] the path to the file defining the DNA sequences used to mark the ending of splice sites.  By default, the cannonical ending splice sites are used.\n");
        System.out.print("--codons, -d\t\t\t[optional] an integer indicating that, rather than searching for start and stop codons or beginning and ending splice sites, simply look upstream and downstream a fixed amount from the reverse translated peptide.  This flag overrides the 'isEukaryote' flag if both are present.\n");
        System.out.print("--geneSplicerOutputPath, -g\t\t[optional] the path with the output of gene splicer.  these mark the exact coordinates of splice site boundaries and are used when determining the ePSTs.\n");

        System.out.print("--tabbedFile, -t\t\t[optional] instead of a fasta file, the lines of the input file are of the form <sequence>\\t<probability>.\n");
        System.out.print("--mode, -m\t\t[optional] the mode of the program to use. \"PGM\" (default) or \"SequestDifference\".\n");
        System.out.print("\n");

        System.out.print("So, for example, to use the standard codon table and generate the ePSTs according to the algorithm described in the paper for a prokaryote\n");
        System.out.print("\tjava -jar ProteogenomicMapping.jar --peptideFilePath myPeptides.fa --referenceFilePath myGenome.fa --codeFile genetic_code_table.txt --outputFilePath myMapping.txt --fastaFilePath myMapping.fa\n");
        System.out.print("\t-- or --\n");
        System.out.print("\tjava -jar ProteogenomicMapping.jar -p myPeptides.fa -r myGenome.fa -c genetic_code_table.txt -o myMapping.txt -f myMapping.fa -3 myMapping.gff3\n");
        System.out.print("\n");

        System.out.print("The output file contains 13 columns.\n");
        System.out.print("\n");
        System.out.print("Peptide ID\t\t\tThe identifier of the mapped peptide from the given peptide fasta file.\n");
        System.out.print("Peptide Sequence\t\tThe sequence of the mapped peptide from the given peptide fasta file.\n");
        System.out.print("Genome ID\t\t\tThe identifier of the sequence into which the peptide mapped from the given reference fasta file.\n");
        System.out.print("Start\t\t\t\tThe beginning (base 1) position of the reference sequence to which the peptide mapped.\n");
        System.out.print("End\t\t\t\tThe ending (base 1) position of the reference sequence to which the peptide mapped.\n");
        System.out.print("Strand\t\t\tThe strand onto which the peptide mapped.\n");
        System.out.print("Reading Frame\t\t\tThe reading frame into which the peptide mapped.\n");
        System.out.print("RT Peptide Sequence\t\tThe DNA sequence onto which the peptide mapped.\n");
        System.out.print("ePST Start\t\t\tThe starting position of the generated ePST, based on the 'isEukaryote' and 'codon' flags.\n");
        System.out.print("ePST End\t\t\tThe ending position of the generated ePST, based on the 'isEukaryote' and 'codon' flags.\n");
        System.out.print("ePST\t\t\t\tThe DNA sequence of the reference sequence from the start to the end of the ePST.\n");
        System.out.print("ePST Length\t\t\tThe length of the generated ePST, in terms of DNA nucleotides.\n");
        System.out.print("Translated ePST\t\tThe protein sequence of the ePST.\n");
        System.out.print("\n\n");


        System.out.println("The output gff3 file contains the standard 9 columns (http://gmod.org/wiki/GFF3).");
        System.out.println("It contains two entries for each ePST.  The first entry contains only the positions of the RTP.");
        System.out.println("The second entry contains the positions of the entire ePST.");
        System.out.println("");
        System.out.println("seqid\t\tThe Genome ID is used for the sequence ID");
        System.out.println("source\t\t\"ProteogenomicMapping,RTP\" or \"ProteogenomicMapping,ePST\" depending on whether the entry is an RTP or ePST");
        System.out.println("type\t\t\"region\" all entries are treated as generic features");
        System.out.println("start\t\tThe start of the the RTP or ePST depending upon the type of entry");
        System.out.println("end\t\t\tThe end of the RTP or ePST depending upon the type of the entry");
        System.out.println("score\t\t\".\" no entries are assigned a score");
        System.out.println("strand\t\tThe strand to which the RTP mapped");
        System.out.println("phase\t\t\".\" no entries are assigned a phase");
        System.out.println("attributes\t\"ID=<Peptide ID>; Name=<Peptide ID>\" all entries are assigned both an ID and a name based on the peptide id");
        System.out.println("\n");

        System.out.print("This project uses the JSAP Project: http://martiansoftware.com/jsap/\n");
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Runners">
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, IOException, Exception {
        if (args.length == 0) {
            startGui();
        } else {
            JSAPResult config = parseArguments(args);

            if (config.getString("mode").length() == 0 && config.getBoolean("help")) {
                printHelp();
                System.exit(0);
            }
            
            // which mode?
            Mode m = Mode.valueOf(config.getString("mode"));
            if(m == Mode.SequestDifference) {
                SequestDifference.main(args);
                return;
            }

            String peptideFilePath = config.getString("peptideFilePath");
            String referenceFilePath = config.getString("referenceFilePath");
            String codeFile = config.getString("codeFile");
            String codeName = config.getString("codeName");

            String outputFilename = config.getString("outputFilename");
            String outputFastaFilename = config.getString("outputFastaFilename");
            String outputGff3Filename = config.getString("outputGff3Filename");

            String beginSpliceSitesPath = config.getString("beginSpliceSitesPath");
            String endSpliceSitesPath = config.getString("endSpliceSitesPath");
            boolean isEukaryote = config.getBoolean("isEukaryote");
            int codons = config.getInt("codons");
            String geneSplicerOutputPath = config.getString("geneSplicerOutputPath");
            boolean tabbedFile = config.getBoolean("tabbedFile");

            GeneSplicerParser geneSplicerParser = null;
            File f = new File(geneSplicerOutputPath);
            if (f.exists()) {
                geneSplicerParser = new GeneSplicerParser(geneSplicerOutputPath);
            }

            List<BioSequence> peptides;
            if (tabbedFile) {
                peptides = TabbedSequenceReader.readSequences(peptideFilePath);
            } else {
                peptides = FASTASequenceReader.readSequences(peptideFilePath);
            }

            MapperMode mode = Mapper.MapperMode.Prokaryote;
            if (isEukaryote) {
                mode = MapperMode.Eukaryote;
            } else if (codons > 0) {
                mode = MapperMode.Codon;
            } else if (geneSplicerParser != null) {
                mode = MapperMode.GeneSplicer;
            }


            StateMachine sm = new StateMachine(peptides);

            CodeTable table = CodeTable.getCodeTable(codeFile, codeName);

            Mapper mapper = new Mapper(peptides, sm,
                    referenceFilePath, outputFilename, outputFastaFilename, outputGff3Filename,
                    table, mode,
                    beginSpliceSitesPath, endSpliceSitesPath,
                    codons, geneSplicerParser);

            // perform the mapping
            mapper.map();
        }
    }

    public static void startGui() {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new MainFrame().setVisible(true);
            }
        });
    }// </editor-fold>

}
