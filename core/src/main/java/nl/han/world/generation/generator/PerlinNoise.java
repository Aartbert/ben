package nl.han.world.generation.generator;

/**
 * This class generates the perlin noise.
 */
public class PerlinNoise {
    private static final double ROOT2OVER2 = 0.7071067811865476;
    private static final double SKEW_2D = 0.366025403784439;
    private static final double UNSKEW_2D = -0.21132486540518713;
    private static final long PRIME_X = 0x5205402B9270C86FL;
    private static final long PRIME_Y = 0x598CD327003817B5L;
    private static final float RSQUARED_2D = 2.0f / 3.0f;
    private static final long HASH_MULTIPLIER = 0x53A3F72DEEC546F5L;
    private static final int N_GRADS_2D_EXPONENT = 7;
    private static final int N_GRADS_2D = 1 << N_GRADS_2D_EXPONENT;
    private float[] gradients2D;
    private static final double NORMALIZER_2D = 0.05481866495625118;

    /**
     * Generates a 2D noise value between 0 and 255 for the given seed and x and y coordinates.
     *
     * @param seed The seed to use for the noise.
     * @param x    The x coordinate to use for the noise.
     * @param y    The y coordinate to use for the noise.
     * @return The 2D noise value between -1 and 1 for the given seed and x and y coordinates.
     */
    public float noise2_ImproveX(long seed, double x, double y) {
        // Skew transform and rotation baked into one.
        double skewX = x * ROOT2OVER2;
        double skewY = y * (ROOT2OVER2 * (1 + 2 * SKEW_2D));

        return noise2UnskewedBase(seed, skewY + skewX, skewY - skewX);
    }

    /**
     * Generates 2D noise value between 0 and 255 for the given seed and x and y coordinates, this used to calculate the grayscale of the map.
     *
     * @param seed The seed to use for the noise.
     * @param xs   The x coordinate to use for the noise.
     * @param ys   The y coordinate to use for the noise.
     * @return The 2D noise value between -1 and 1 for the given seed and x and y coordinates.
     */
    private float noise2UnskewedBase(long seed, double xs, double ys) {

        // Get base points and offsets.
        int roosterX = fastFloor(xs);
        int roosterY = fastFloor(ys);
        float fractalX = (float) (xs - roosterX);
        float fractalY = (float) (ys - roosterY);

        // Prime pre-multiplication for hash.
        long gridX = roosterX * PRIME_X;
        long gridY = roosterY * PRIME_Y;

        // Unskew.
        float t = (fractalX + fractalY) * (float) UNSKEW_2D;
        float deltaX1 = fractalX + t;
        float deltaY1 = fractalY + t;

        // First vertex.
        float temp0 = RSQUARED_2D - deltaX1 * deltaX1 - deltaY1 * deltaY1;
        float value = (temp0 * temp0) * (temp0 * temp0) * grad(seed, gridX, gridY, deltaX1, deltaY1);

        // Second vertex.
        float temp1 = (float) (2 * (1 + 2 * UNSKEW_2D) * (1 / UNSKEW_2D + 2)) * t + ((float) (-2 * (1 + 2 * UNSKEW_2D) * (1 + 2 * UNSKEW_2D)) + temp0);
        float deltaX2 = deltaX1 - (float) (1 + 2 * UNSKEW_2D);
        float deltaY2 = deltaY1 - (float) (1 + 2 * UNSKEW_2D);
        value += (temp1 * temp1) * (temp1 * temp1) * grad(seed, gridX + PRIME_X, gridY + PRIME_Y, deltaX2, deltaY2);

        // Third and fourth vertices.
        // Nested conditionals were faster than compact bit logic/arithmetic.
        float xyDifference = fractalX - fractalY;
        if (t < UNSKEW_2D) {
            if (fractalX + xyDifference > 1) {
                float deltaX3 = deltaX1 - (float) (3 * UNSKEW_2D + 2);
                float deltaY3 = deltaY1 - (float) (3 * UNSKEW_2D + 1);
                float temp2 = RSQUARED_2D - deltaX3 * deltaX3 - deltaY3 * deltaY3;
                if (temp2 > 0) {
                    value += (temp2 * temp2) * (temp2 * temp2) * grad(seed, gridX + (PRIME_X << 1), gridY + PRIME_Y, deltaX3, deltaY3);
                }
            } else {
                float deltaX3 = deltaX1 - (float) UNSKEW_2D;
                float deltaY3 = deltaY1 - (float) (UNSKEW_2D + 1);
                float temp2 = RSQUARED_2D - deltaX3 * deltaX3 - deltaY3 * deltaY3;
                if (temp2 > 0) {
                    value += (temp2 * temp2) * (temp2 * temp2) * grad(seed, gridX, gridY + PRIME_Y, deltaX3, deltaY3);
                }
            }

            if (fractalY - xyDifference > 1) {
                float deltaX4 = deltaX1 - (float) (3 * UNSKEW_2D + 1);
                float deltaY4 = deltaY1 - (float) (3 * UNSKEW_2D + 2);
                float temp3 = RSQUARED_2D - deltaX4 * deltaX4 - deltaY4 * deltaY4;
                if (temp3 > 0) {
                    value += (temp3 * temp3) * (temp3 * temp3) * grad(seed, gridX + PRIME_X, gridY + (PRIME_Y << 1), deltaX4, deltaY4);
                }
            } else {
                float deltaX4 = deltaX1 - (float) (UNSKEW_2D + 1);
                float deltaY4 = deltaY1 - (float) UNSKEW_2D;
                float temp3 = RSQUARED_2D - deltaX4 * deltaX4 - deltaY4 * deltaY4;
                if (temp3 > 0) {
                    value += (temp3 * temp3) * (temp3 * temp3) * grad(seed, gridX + PRIME_X, gridY, deltaX4, deltaY4);
                }
            }
        } else {
            if (fractalX + xyDifference < 0) {
                float deltaX3 = deltaX1 + (float) (1 + UNSKEW_2D);
                float deltaY3 = deltaY1 + (float) UNSKEW_2D;
                float temp2 = RSQUARED_2D - deltaX3 * deltaX3 - deltaY3 * deltaY3;
                if (temp2 > 0) {
                    value += (temp2 * temp2) * (temp2 * temp2) * grad(seed, gridX - PRIME_X, gridY, deltaX3, deltaY3);
                }
            } else {
                float deltaX3 = deltaX1 - (float) (UNSKEW_2D + 1);
                float deltaY3 = deltaY1 - (float) UNSKEW_2D;
                float temp2 = RSQUARED_2D - deltaX3 * deltaX3 - deltaY3 * deltaY3;
                if (temp2 > 0) {
                    value += (temp2 * temp2) * (temp2 * temp2) * grad(seed, gridX + PRIME_X, gridY, deltaX3, deltaY3);
                }
            }

            if (fractalY < xyDifference) {
                float deltaX3 = deltaX1 + (float) UNSKEW_2D;
                float deltaY3 = deltaY1 + (float) (UNSKEW_2D + 1);
                float temp2 = RSQUARED_2D - deltaX3 * deltaX3 - deltaY3 * deltaY3;
                if (temp2 > 0) {
                    value += (temp2 * temp2) * (temp2 * temp2) * grad(seed, gridX, gridY - PRIME_Y, deltaX3, deltaY3);
                }
            } else {
                float deltaX3 = deltaX1 - (float) UNSKEW_2D;
                float deltaY3 = deltaY1 - (float) (UNSKEW_2D + 1);
                float temp2 = RSQUARED_2D - deltaX3 * deltaX3 - deltaY3 * deltaY3;
                if (temp2 > 0) {
                    value += (temp2 * temp2) * (temp2 * temp2) * grad(seed, gridX, gridY + PRIME_Y, deltaX3, deltaY3);
                }
            }
        }

        return value;
    }

    /**
     * Returns the floor of the given double.
     *
     * @param x The double to get the floor of.
     * @return The floor of the given double.
     */
    private int fastFloor(double x) {
        int xi = (int) x;
        return x < xi ? xi - 1 : xi;
    }

    /**
     * Returns the gradient for the given seed, primeX, primeY, dx and dy.
     *
     * @param seed   The seed to use for the gradient.
     * @param primeX The primeX to use for the gradient.
     * @param primeY The primeY to use for the gradient.
     * @param dx     The dx to use for the gradient.
     * @param dy     The dy to use for the gradient.
     * @return The gradient for the given seed, primeX, primeY, dx and dy.
     */
    private float grad(long seed, long primeX, long primeY, float dx, float dy) {
        long hash = seed ^ primeX ^ primeY;
        hash *= HASH_MULTIPLIER;
        hash ^= hash >> (64 - N_GRADS_2D_EXPONENT + 1);
        int gradiantI = (int) hash & ((N_GRADS_2D - 1) << 1);
        return gradients2D[gradiantI] * dx + gradients2D[gradiantI | 1] * dy;
    }

    {

        gradients2D = new float[N_GRADS_2D * 2];
        float[] grad2 = {
                0.38268343236509f, 0.923879532511287f,
                0.923879532511287f, 0.38268343236509f,
                0.923879532511287f, -0.38268343236509f,
                0.38268343236509f, -0.923879532511287f,
                -0.38268343236509f, -0.923879532511287f,
                -0.923879532511287f, -0.38268343236509f,
                -0.923879532511287f, 0.38268343236509f,
                -0.38268343236509f, 0.923879532511287f,
                //-------------------------------------//
                0.130526192220052f, 0.99144486137381f,
                0.608761429008721f, 0.793353340291235f,
                0.793353340291235f, 0.608761429008721f,
                0.99144486137381f, 0.130526192220051f,
                0.99144486137381f, -0.130526192220051f,
                0.793353340291235f, -0.60876142900872f,
                0.608761429008721f, -0.793353340291235f,
                0.130526192220052f, -0.99144486137381f,
                -0.130526192220052f, -0.99144486137381f,
                -0.608761429008721f, -0.793353340291235f,
                -0.793353340291235f, -0.608761429008721f,
                -0.99144486137381f, -0.130526192220052f,
                -0.99144486137381f, 0.130526192220051f,
                -0.793353340291235f, 0.608761429008721f,
                -0.608761429008721f, 0.793353340291235f,
                -0.130526192220052f, 0.99144486137381f,
        };
        for (int i = 0; i < grad2.length; i++) {
            grad2[i] = (float) (grad2[i] / NORMALIZER_2D);
        }
        for (int i = 0, j = 0; i < gradients2D.length; i++, j++) {
            if (j == grad2.length) j = 0;
            gradients2D[i] = grad2[j];
        }
    }
}