package io.github.ensozos.testsupport;

import javafx.util.Pair;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.io.*;
import java.net.URL;


/**
 * Convenience methods for reading and writing test data to/from files.
 */
public class FileUtil {

    public static final String RESOURCE_DIR = "src/test/resources/";
    public static final String DATA_DIR = RESOURCE_DIR + "data/series/";
    public static final String RESULTS_DIR = RESOURCE_DIR + "results/";

    private static final String DELIMITER = ",";


    /**
     * @param filepath the file containing a comma delimited list of doubles
     * @return array of those doubles
     */
    public static INDArray readIndArray(String filepath) {
        double[] doubleArray = readDoublesFromFile(filepath);

        return Nd4j.create(doubleArray, new int[]{1, doubleArray.length});
    }

    public static Pair<INDArray, INDArray> readIndArrayPair(String filepath) {
        double[][] doubleArray = readTwoRowsOfDoublesFromFile(filepath);

        return new Pair<>(
                Nd4j.create(doubleArray[0], new int[]{1, doubleArray[0].length}),
                Nd4j.create(doubleArray[1], new int[]{1, doubleArray[1].length})
        );
    }

    /**
     * @param filepath file that contains a comma delimited list of doubles on a single line
     * @return array of doubles
     */
    private static double[] readDoublesFromFile(String filepath) {
        double[] array;
        try {
            BufferedReader fileReader = getReader(filepath);
            String line = fileReader.readLine();
            array = parseLineOfDoubles(line);
        }
        catch (IOException e) {
            throw new IllegalStateException("Could not read file " + filepath, e);
        }

        return array;
    }

    /**
     * @param filepath file that contains two comma delimited list of doubles on a single line
     * @return two element array containing to arrays of doubles.
     */
    private static double[][] readTwoRowsOfDoublesFromFile(String filepath) {
        double[][] array = new double[2][];
        try {
            BufferedReader fileReader = getReader(filepath);

            array[0] = parseLineOfDoubles(fileReader.readLine());
            array[1] = parseLineOfDoubles(fileReader.readLine());
        }
        catch (IOException e) {
            throw new IllegalStateException("Could not read file " + filepath, e);
        }

        return array;
    }

    private static BufferedReader getReader(String filepath) throws FileNotFoundException {

        File file;
        file = new File(filepath);
        //System.out.println("reading from " + file.getAbsolutePath());

        return new BufferedReader(new FileReader(file));
    }

    private static double[] parseLineOfDoubles(String line) {
        String[] values = line.split(DELIMITER);
        double[] array = new double[values.length];

        for (int i = 0; i < values.length; i++) {
            array[i] = Double.parseDouble(values[i]);
        }
        return array;
    }
}
