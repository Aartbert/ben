package nl.han.modules;

import lombok.AllArgsConstructor;

/**
 * This class is responsible for binding interfaces to there correct implementations.<br/>
 * {@link DefaultModule} is used to bind interface to his correct implementation.
 *
 * @author Jochem Kalsbeek
 * @see DefaultModule
 */
@AllArgsConstructor
public class Binding<T, B extends T> {
    protected Class<T> bindingType;
    protected Class<B> toType;

    /**
     * This method is responsible for creating modules so Guice can use them.<br/>
     * {@link DefaultModule} is used to bind interface to his correct implementation.
     *
     * @author Jochem Kalsbeek
     * @see DefaultModule
     */
    public DefaultModule<T, B> create() {
        return new DefaultModule<>(bindingType, toType);
    }
}
