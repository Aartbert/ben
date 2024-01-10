package nl.han.compiler.utils;

/**
 * Class to represent a three-parameter data transfer object.
 *
 * @see java.util.Map.Entry Entry
 *
 * @param a The first parameter
 * @param b The second parameter
 * @param c The third parameter
 * @author Tom Gerritsen
 */
public record Point<A, B, C>(A a, B b, C c) {

    /**
     * Static method to easily create a new {@link Point point} from its
     * base parameters. Useful in combination with {@link Volume#of(Point[]) Volume.of()}.
     *
     * @see Volume#of(Point[]) Volume.of()
     * @see java.util.Map.Entry#entry(Object, Object) Entry.entry()
     *
     * @param a The first parameter of the {@link Point}
     * @param b The second parameter of the {@link Point}
     * @param c The third parameter of the {@link Point}
     * @return The newly created {@link Point}
     */
    public static <A, B, C> Point<A, B, C> point(A a, B b, C c) {
        return new Point<>(a, b, c);
    }
}
