package nl.han.shared.utils.random;

import java.util.Random;

/**
 * This class is a wrapper class that is used to reach java.util.Random through the ICOCRandom interface.
 *
 * @author Lucas van Steveninck
 */
public class COCRandom implements ICOCRandom {
    Random random;

    /**
     * Seedless constructor for the COCRandom class. An instance of COCRandom with a random seed will be created.
     */
    public COCRandom() {
        random = new Random();
    }

    /**
     * Set seed constructor for the COCRandom class. An instance of COCRandom with a set seed will be created.
     *
     * @param seed The seed that should be used to determine random values.
     */
    public COCRandom(long seed) {
        random = new Random(seed);
    }

    /**
     * Sets the seed that should be used to determine random values;
     *
     * @param seed The seed that should be used to determine random values;
     */
    public void setSeed(long seed) {
        random.setSeed(seed);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int nextInt() {
        return random.nextInt();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float nextFloat() {
        return random.nextFloat();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int nextInt(int bound) {
        return random.nextInt(bound);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float nextFloat(float bound) {
        return random.nextFloat(bound);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int nextInt(int origin, int bound) {
        return random.nextInt(origin, bound);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float nextFloat(float origin, float bound) {
        return random.nextFloat(origin, bound);
    }
}
