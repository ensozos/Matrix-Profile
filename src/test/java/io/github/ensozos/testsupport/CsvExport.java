package io.github.ensozos.testsupport;

import javafx.util.Pair;
import org.nd4j.linalg.api.ndarray.INDArray;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;


public class CsvExport {

    /**
     * Print the series and profile to a CSV file so that it can be visualized.
     * @param target the series that was processed
     * @param expResult expected matrix profile and indices
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
    }

}
