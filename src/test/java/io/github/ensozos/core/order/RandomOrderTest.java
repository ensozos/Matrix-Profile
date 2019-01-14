package io.github.ensozos.core.order;

import org.junit.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertEquals;


public class RandomOrderTest {

    @Test
    public void testRandomOrderIndex() {

        RandomOrder order = new RandomOrder(3, 1.0, new Random(2));

        List<Integer> indices = new LinkedList<>();
        for (int i = 0; i < 5; i++) {
            indices.add(order.getNext());
        }

        assertEquals("[1, 0, 2, -1, -1]", indices.toString());
    }

    @Test
    public void testRandomOrderIndexWhenOnly10percentUsed() {

        RandomOrder order = new RandomOrder(30, 0.1, new Random(2));

        List<Integer> indices = new LinkedList<>();
        for (int i = 0; i < 5; i++) {
            indices.add(order.getNext());
        }

        assertEquals("[28, 27, 6, -1, -1]", indices.toString());
    }

    @Test
    public void testRandomOrderIndexWhenOnly20percentUsed() {

        RandomOrder order = new RandomOrder(30, 0.2, new Random(2));

        List<Integer> indices = new LinkedList<>();
        for (int i = 0; i < 7; i++) {
            indices.add(order.getNext());
        }

        assertEquals("[28, 27, 6, 4, 21, 5, -1]", indices.toString());
    }
}
