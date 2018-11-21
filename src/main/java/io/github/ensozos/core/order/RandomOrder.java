package io.github.ensozos.core.order;


import java.util.Random;
import java.util.stream.IntStream;

public class RandomOrder implements Order {
    private int w;
    private int index;
    private int[] indices;

    /**
     * Random indices of length w
     *
     * @param w size w
     */
    public RandomOrder(int w) {
        this(w, new Random());
    }

    /**
     * Random indices of length w
     *
     * @param w size w
     * @param rnd the random number generate to use. Call this from unit tests for deterministic results
     */
    public RandomOrder(int w, Random rnd) {
        this.w = w;
        this.index = -1;
        this.indices = new int[w];

        IntStream.range(0, w).forEach(n -> this.indices[n] = n);
        shuffle(indices, rnd);
    }

    /**
     * Get the next random index
     *
     * @return random int index
     */
    @Override
    public int getNext() {
        this.index++;
        if (this.index < w)
            return indices[index];

        return -1;
    }

    /**
     * Shuffle array with Fisher-Yates algorithm
     *
     * @param array array to shuffle
     * @param rnd random number generator to use
     */
    private void shuffle(int[] array, Random rnd) {
        int n = array.length;

        for (int i = 0; i < array.length; i++) {
            int randomValue = i + rnd.nextInt(n - i);

            int randomElement = array[randomValue];
            array[randomValue] = array[i];
            array[i] = randomElement;
        }
    }

}
