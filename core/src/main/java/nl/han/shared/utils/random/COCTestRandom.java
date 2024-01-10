package nl.han.shared.utils.random;

import lombok.Setter;

/**
 * This class is used as a way to mock random results through the ICOCRandom interface.
 *
 * @author Lucas van Steveninck
 */
public class COCTestRandom implements ICOCRandom {
    @Setter
    private int[] integers;
    private int integerIndex = 0;
    @Setter
    private float[] floats;
    private int floatIndex = 0;

    /**
     * @inheritDoc
     */
    @Override
    public int nextInt() {
        checkIntegers();
        return integers[++integerIndex - 1];
    }

    /**
     * @inheritDoc
     */
    @Override
    public float nextFloat() {
        checkFloats();
        return floats[++floatIndex - 1];
    }

    /**
     * @inheritDoc
     */
    @Override
    public int nextInt(int bound) {
        checkIntegers();
        while (integers[integerIndex] >= bound) {
            integerIndex++;
            checkIntegers();
        }

        return integers[++integerIndex - 1];
    }

    /**
     * @inheritDoc
     */
    @Override
    public float nextFloat(float bound) {
        checkFloats();
        while (floats[floatIndex] >= bound) {
            floatIndex++;
            checkFloats();
        }
        return floats[++floatIndex - 1];
    }

    /**
     * @inheritDoc
     */
    @Override
    public int nextInt(int origin, int bound) {
        checkIntegers();
        while (!(integers[integerIndex] >= origin && integers[integerIndex] < bound)) {
            integerIndex++;
            checkIntegers();
        }
        return integers[++integerIndex - 1];
    }

    /**
     * @inheritDoc
     */
    @Override
    public float nextFloat(float origin, float bound) {
        checkFloats();
        while (!(floats[floatIndex] >= origin && floats[floatIndex] < bound)) {
            floatIndex++;
            checkFloats();
        }
        return floats[++floatIndex - 1];
    }

    /**
     * This method will check whether there are more integers available to be returned.
     *
     * @throws IndexOutOfBoundsException If there are no more available integers that can be returned.
     */
    private void checkIntegers() {
        if (integers == null || integerIndex >= integers.length) throw new IndexOutOfBoundsException("There are no more available integers that can be returned.");
    }

    /**
     * This method will check whether there are more floats available to be returned.
     *
     * @throws IndexOutOfBoundsException If there are no more available floats that can be returned.
     */
    private void checkFloats() {
        if (floats == null || floatIndex >= floats.length) throw new IndexOutOfBoundsException("There are no more available floats that can be returned.");
    }
}
