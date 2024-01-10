package nl.han.shared.datastructures;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BoundedValueTest {

    private static final int LOWER_BOUND = -10;
    private static final int UPPER_BOUND = 10;
    private static final int VALUE = 0;
    private BoundedValue boundedValue;

    @BeforeEach
    void setup() {
        boundedValue = new BoundedValue(UUID.randomUUID(), VALUE, UPPER_BOUND, LOWER_BOUND);
    }

    @Test
    void testSetup() {
        assertEquals(VALUE, boundedValue.getValue());
        assertEquals(UPPER_BOUND, boundedValue.getUpperBound());
        assertEquals(LOWER_BOUND, boundedValue.getLowerBound());
    }

    @Test
    void testSetValueWithinBounds() {
        boundedValue.setValue(8);
        assertEquals(8, boundedValue.getValue());
    }

    @Test
    void testSetValueAboveUpperBound() {
        boundedValue.setValue(UPPER_BOUND + 10);
        assertEquals(UPPER_BOUND, boundedValue.getValue());
    }

    @Test
    void testSetValueBelowLowerBound() {
        boundedValue.setValue(LOWER_BOUND - 10);
        assertEquals(LOWER_BOUND, boundedValue.getValue());
    }

    @Test
    void testSetUpperBound() {
        boundedValue.setUpperBound(UPPER_BOUND + 10);
        assertEquals(UPPER_BOUND + 10, boundedValue.getUpperBound());
        assertEquals(VALUE, boundedValue.getValue());
    }

    @Test
    void testSetUpperBoundBelowLowerBound() {
        boundedValue.setUpperBound(LOWER_BOUND - 10);
        assertEquals(LOWER_BOUND, boundedValue.getUpperBound());
        assertEquals(LOWER_BOUND, boundedValue.getValue());
    }

    @Test
    void testSetLowerBound() {
        boundedValue.setLowerBound(LOWER_BOUND - 10);
        assertEquals(LOWER_BOUND - 10, boundedValue.getLowerBound());
        assertEquals(VALUE, boundedValue.getValue());
    }

    @Test
    void testSetLowerBoundAboveUpperBound() {
        boundedValue.setLowerBound(UPPER_BOUND + 10);
        assertEquals(UPPER_BOUND, boundedValue.getLowerBound());
        assertEquals(UPPER_BOUND, boundedValue.getValue());
    }

    @Test
    void testGetPercentage() {
        double expectedPercentage = 0.5;
        double actualPercentage = boundedValue.convertToPercentage();
        assertEquals(expectedPercentage, actualPercentage);
    }

    @Test
    void testGetPercentageWithNegativeBounds() {
        boundedValue.setLowerBound(-20);
        boundedValue.setUpperBound(-10);
        boundedValue.setValue(-15);

        double expectedPercentage = 0.5;
        double actualPercentage = boundedValue.convertToPercentage();
        assertEquals(expectedPercentage, actualPercentage);
    }
}