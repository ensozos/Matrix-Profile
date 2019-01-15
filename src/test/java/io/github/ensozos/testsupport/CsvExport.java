package io.github.ensozos.testsupport;

import javafx.util.Pair;
import org.nd4j.linalg.api.ndarray.INDArray;
import java.io.*;
import java.net.URL;


public class CsvExport {

    /**
     * Print the series and profile to a CSV file so that it can be visualized.
     * @param target the series that was processed
     * @param expResult expected matrix profile and indices
     * @param filename name of the file
     */
    public static void printToCsvFile(INDArray target, Pair<INDArray, INDArray> expResult, String filename) {
        try{
            printToCsvStream(target, expResult, new PrintStream(new File(filename)));
        } catch (FileNotFoundException e) {
            throw new IllegalStateException("File, " + filename + " not found.", e);
        }
    }

    /**
     * Print the series and profile as CSV text.
     * @param target the series that was processed
     * @param expResult expected matrix profile and indices
     */
    public static void printAsCsv(INDArray target, Pair<INDArray, INDArray> expResult) {
        printToCsvStream(target, expResult, new PrintStream(System.out));
    }

    /**
     * Print the series and profile to a CSV file so that it can be visualized.
     * @param result the pair to write to a file
     * @param filename name of the file
     */
    public static void printPairToFile(Pair<INDArray, INDArray> result, String filename) {

        PrintStream stream;
        try {
            File file = new File(filename);
            //System.out.println("writing to " + file.getAbsolutePath());
            stream = new PrintStream(file);
        }
        catch (IOException e) {
            throw new IllegalStateException("Could not create " + filename);
        }

        printSeries(stream, result.getKey());
        printSeries(stream, result.getValue());
        stream.close();
    }

    private static void printSeries(PrintStream strm, INDArray series) {
        long keyLen = series.length();
        for (long i = 0; i < keyLen; i++) {
            strm.print(series.getDouble(i));
            if (i < keyLen - 1) {
                strm.print(",");
            }
        }
        strm.println();
    }

    /**
     * Print the series and profile to a CSV file so that it can be visualized.
     * @param target the series that was processed
     * @param expResult expected matrix profile and indices
     */
    private static void printToCsvStream(INDArray target, Pair<INDArray, INDArray> expResult, PrintStream stream) {
        stream.println("row,series,MP,MPindx");  // csv header row
        for (int i = 0; i < target.columns(); i++) {
            stream.print(i + "," + target.getDouble(0, i) + ",");
            if (i < expResult.getKey().columns()) {
                stream.println(expResult.getKey().getDouble(0, i) + "," + expResult.getValue().getDouble(0,i));
            } else {
                stream.println(",");
            }
        }
        stream.close();
    }

}
