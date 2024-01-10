package nl.han.compiler.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Stream;

/**
 * A three-parameter mapping, the extension of a {@link java.util.List List}
 * and {@link java.util.Map Map}. This class holds a collection of {@link Point points}, which
 * are comprised of the three parameters, {@link A}, {@link B}, {@link C}.
 *
 * @see Point
 *
 * @param <A> The first parameter in this class
 * @param <B> The second parameter in this class
 * @param <C> The third parameter in this class
 * @author Tom Gerritsen
 */
public class Volume<A, B, C> extends ArrayList<Point<A, B, C>> {

    /**
     * Static method to easily create a {@link Volume volume}
     * from a array of {@link Point points}. Useful to use in combination with
     * {@link Point#point(Object, Object, Object) Point.point()}
     *
     * @see Point#point(Object, Object, Object) Point.point()
     * @see java.util.List#of() List.of()
     * @see java.util.Map#of() Map.of()
     *
     * @param entries An array of {@link Point points}
     * @return A new {@link Volume} with the same content
     */
    @SafeVarargs
    public static <A, B, C> Volume<A, B, C> of(Point<A, B, C>... entries) {
        Volume<A, B, C> volume = new Volume<>();

        volume.addAll(Arrays.asList(entries));

        return volume;
    }

    /**
     * Transforms the current {@link Volume} into a {@link Stream} of {@link Object object}
     * to use in a {@link org.junit.jupiter.params.ParameterizedTest ParameterizedTest} with
     * {@link org.junit.jupiter.params.provider.MethodSource MethodSource}.
     *
     * @return A {@link Stream} of {@link Object object}
     */
    public Stream<Object[]> provide() {
        return stream()
                .map(entry -> new Object[]{entry.a(), entry.b(), entry.c()});
    }
}
