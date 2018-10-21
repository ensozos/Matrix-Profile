package com.auth.mp.core.order;


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
        this.w = w;
        this.index = -1;
        this.indices = new int[w];

        IntStream.range(0, w).forEach(n -> this.indices[n] = n);
        shuffle(indices);

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
     */
    private void shuffle(int[] array) {
        int n = array.length;
        Random random = new Random();

        for (int i = 0; i < array.length; i++) {
            int randomValue = i + random.nextInt(n - i);

            int randomElement = array[randomValue];
            array[randomValue] = array[i];
            array[i] = randomElement;
        }
    }


}
