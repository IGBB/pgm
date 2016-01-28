package proteogenomicmapping;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * this class handles reading in and populating default values
 * for sets read in from files.  For example, reading in peptide
 * sequences, codons and splice sites
 * @author bm542
 */
public class FileReading {

    /**
     * Read each line of the given file into an entry in a set
     * @param path the path of the file to read
     * @return a set in which each entry corresponds to a line in the file
     */
    public static Set<String> getLinesSet(String path) throws FileNotFoundException, IOException {
        Set<String> set = new HashSet<String>();
        BufferedReader file = new BufferedReader(new FileReader(path));
        for(String line = file.readLine(); line != null; line = file.readLine()) {
            set.add(line);
        }
        return set;
    }

    /**
     * Read each line of the given file into an entry in a list
     * @param path the path of the file to read
     * @return a set in which each entry corresponds to a line in the file
     */
    public static List<String> getLinesList(String path) throws FileNotFoundException, IOException {
        List<String> list = new ArrayList<String>();
        BufferedReader file = new BufferedReader(new FileReader(path));
        for(String line = file.readLine(); line != null; line = file.readLine()) {
            list.add(line);
        }
        return list;
    }

    /**
     * check if the given file exists
     * @param path the path to the potential file
     * @return true if a file exists at the given path
     */
    public static boolean exists(String path) {
        File f = new File(path);
        return f.exists();
    }

    /**
     * return the set of start codons
     * if a file exists at path, return that
     * otherwise, return a default set of start codons
     * @param path the file which, if exists, contains the start codons
     * @return the set of start codons
     */
    public static Set<String> getStartCodons(String path) throws FileNotFoundException, IOException {
        if(exists(path)) {
            return getLinesSet(path);
        }
        return getDefaultStartCodons();
    }

    /**
     * return the default set of start codons, which is only "ATG"
     * @return the set of start codons
     */
    public static Set<String> getDefaultStartCodons() {
        Set<String> defaultStartCodons = new HashSet<String>();
        defaultStartCodons.add("ATG");
        return defaultStartCodons;
    }

    /**
     * return the set of end codons
     * if a file exists at path, return that
     * otherwise, return a default set of end codons
     * @param path the file which, if exists, contains the end codons
     * @return the set of end codons
     */
    public static Set<String> getEndCodons(String path) throws FileNotFoundException, IOException {
        if(exists(path)) {
            return getLinesSet(path);
        }
        return getDefaultEndCodons();
    }

    /**
     * return the default set of end codons,
     * which is "TAA", "TAG", "TGA"
     * @return the set of end codons
     */
    public static Set<String> getDefaultEndCodons() {
        Set<String> defaultEndCodons = new HashSet<String>();
        defaultEndCodons.add("TAA");
        defaultEndCodons.add("TAG");
        defaultEndCodons.add("TGA");
        return defaultEndCodons;
    }


    /**
     * return the set of beginning splice sites
     * if a file exists at path, return that
     * otherwise, return a default set of beginning splice sites
     * @param path the file which, if exists, contains the beginning splice sites
     * @return the set of beginning splice sites
     */
    public static Set<String> getBeginSpliceSites(String path) throws FileNotFoundException, IOException {
        if(exists(path)) {
            return getLinesSet(path);
        }
        return getDefaultBeginSpliceSites();
    }

    /**
     * return the default set of beginning splice sites, which is only "CAGG"
     * @return the set of start codons
     */
    public static Set<String> getDefaultBeginSpliceSites() {
        Set<String> defaultBeginSpliceSites = new HashSet<String>();
        defaultBeginSpliceSites.add("CAGG");
        return defaultBeginSpliceSites;
    }

    /**
     * return the set of ending splice sites
     * if a file exists at path, return that
     * otherwise, return a default set of ending splice sites
     * @param path the file which, if exists, contains the ending splice sites
     * @return the set of ending splice sites
     */
    public static Set<String> getEndSpliceSites(String path) throws FileNotFoundException, IOException {
        if(exists(path)) {
            return getLinesSet(path);
        }
        return getDefaultEndSpliceSites();
    }

    /**
     * return the default set of ending splice sites,
     * which is "AAGGTAAGT", "AAGGTGAGT", "CAGGTAAGT", "CAGGTGAGT"
     * @return the set of ending splice sites
     */
    public static Set<String> getDefaultEndSpliceSites() {
        Set<String> defaultEndSpliceSites = new HashSet<String>();
        defaultEndSpliceSites.add("AAGGTAAGT");
        defaultEndSpliceSites.add("AAGGTGAGT");
        defaultEndSpliceSites.add("CAGGTAAGT");
        defaultEndSpliceSites.add("CAGGTGAGT");
        return defaultEndSpliceSites;
    }
}
