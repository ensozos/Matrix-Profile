package io.github.ensozos.core.order;


import java.util.Random;
import java.util.stream.IntStream;

public class RandomOrder implements Order {
    private int numIndices;
    private int index;
    private int[] indices;

    /**
     * Random indices of length w. Uses all of them.
     *
     * @param w size w
     */
    public RandomOrder(int w) {
        this(w, 1.0, new Random());
    }

    /**
     * Some fraction of Random indices of length w.
     * No matter how small fraction is, we will always use at least 1 random index.
     *
     * @param w size w
     * @param fraction only use this fraction of the random indices. Must be in (0, 1].
     */
    public RandomOrder(int w, double fraction) {
        this(w, fraction, new Random());
    }

    /**
     * Random indices of length numIndices
     *
     * @param w size w
     * @param fraction only use this fraction of the random indices. Must be in (0, 1].
     * @param rnd the random number generate to use. Call this from unit tests for deterministic results
     */
    public RandomOrder(int w, double fraction, Random rnd) {
        assert fraction > 0 && fraction <= 1.0;
        this.numIndices = (int) Math.max(1, Math.round(fraction * w));
        this.index = -1;
        this.indices = new int[w];

        IntStream.range(0, w).forEach(n -> this.indices[n] = n);
        shuffle(indices, rnd, numIndices);
    }

    /**
     * Get the next random index
     *
     * @return random int index
     */
    @Override
    public int getNext() {
        this.index++;
        if (this.index < numIndices)
            return indices[index];

        return -1;
    }

    /**
     * Shuffle array with Fisher-Yates algorithm
     *
     * @param array array to shuffle
     * @param rnd random number generator to use
     * @param numIndices only need to shuff the first numIndices because that is all that will be used.
     */
    private void shuffle(int[] array, Random rnd, int numIndices) {
        int n = array.length;

        for (int i = 0; i < numIndices; i++) {
            int randomValue = i + rnd.nextInt(n - i);

            int randomElement = array[randomValue];
            array[randomValue] = array[i];
            array[i] = randomElement;
        }
    }

}
