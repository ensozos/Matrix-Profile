package io.github.ensozos.core.order;


import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class LinearOrderTest {


    @Test
    public void testLinearOrderIndex() {

        LinearOrder order = new LinearOrder(3);

        assertEquals(0, order.getNext());
        assertEquals(1, order.getNext());
        assertEquals(2, order.getNext());
        assertEquals(-1, order.getNext());
        assertEquals(-1, order.getNext());
    }
}
