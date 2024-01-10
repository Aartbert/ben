package nl.han.shared.utils.random;

/**
 * This class is responsible for logic regarding random number generation.
 *
 * @author Lucas van Steveninck
 */
public interface ICOCRandom {

    /**
     * Returns the next integer from a sequence of integers.
     *
     * @return A random integer.
     */
    int nextInt();

    /**
     * Returns the next float from a sequence of floats.
     *
     * @return A random float.
     */
    float nextFloat();

    /**
     * Returns the next integer from a sequence of integers that is below a certain bound.
     *
     * @param bound The bound that the integer that is returned should comply to.
     * @return A random integer.
     */
    int nextInt(int bound);

    /**
     * Returns the next float from a sequence of floats that is below a certain bound.
     *
     * @param bound The bound that the float that is returned should comply to.
     * @return A random float.
     */
    float nextFloat(float bound);

    /**
     * Returns the next integer from a sequence of integers that is above a certain origin and below a certain bound.
     *
     * @param origin The origin that the integer that is returned should comply to.
     * @param bound The bound that the integer that is returned should comply to.
     * @return A random integer.
     */
    int nextInt(int origin, int bound);

    /**
     * Returns the next float from a sequence of floats that is above a certain origin and below a certain bound.
     *
     * @param origin The origin that the float that is returned should comply to.
     * @param bound The bound that the float that is returned should comply to.
     * @return A random float.
     */
    float nextFloat(float origin, float bound);
}
