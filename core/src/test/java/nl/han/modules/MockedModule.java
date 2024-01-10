package nl.han.modules;

import static org.mockito.Mockito.mock;

/**
 * This class is responsible for creating mock modules so Guice can use them.<br/>
 *
 * {@link Binding} is used to bind interface to his correct implementation.
 *
 * @author Jochem Kalsbeek
 * @see Binding
 */
public class MockedModule<T, B extends T> extends DefaultModule<T, B> {
    public MockedModule(Class<T> bindingType, Class<B> toType) {
        super(bindingType, toType);
    }

    /**
     * This method is responsible for creating mock modules so Guice can use them.<br/>
     *
     * {@link Binding} is used to bind interface to his correct implementation.
     *
     * @author Jochem Kalsbeek
     * @see Binding
     */
    @Override
    protected void configure() {
        bind(bindingType).toInstance(mock(toType));
    }
}
