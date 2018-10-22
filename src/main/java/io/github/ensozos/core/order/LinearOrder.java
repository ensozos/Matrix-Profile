package io.github.ensozos.core.order;


public class LinearOrder implements Order {
    private int w;
    private int index;

    /**
     * Indices of lenght w in linear order
     *
     * @param w
     */
    public LinearOrder(int w) {
        this.w = w;
        this.index = -1;

    }

    /**
     * Get the next index
     *
     * @return the next int index
     */
    @Override
    public int getNext() {
        this.index++;
        if (this.index < this.w)
            return this.index;

        return -1;
    }
}
