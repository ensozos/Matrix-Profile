package io.github.ensozos.core.order;

import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertEquals;


public class RandomOrderTest {

    @Test
    public void testRandomOrderIndex() {

        RandomOrder order = new RandomOrder(3, new Random(2));

        assertEquals(1, order.getNext());
        assertEquals(0, order.getNext());
        assertEquals(2, order.getNext());
        assertEquals(-1, order.getNext());
        assertEquals(-1, order.getNext());
    }
}
