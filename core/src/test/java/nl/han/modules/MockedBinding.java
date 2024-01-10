package nl.han.modules;


/**
 * This class is responsible for creating mock modules so Guice can use them.<br/>
 *
 * {@link Binding} is used to bind interface to his correct implementation.
 * {@link MockedModule} is used to bind interface to his correct implementation.
 *
 * @author Jochem Kalsbeek
 * @see Binding
 * @see MockedModule
 */
public class MockedBinding<T, B extends T> extends Binding<T, B> {
    public MockedBinding(Class<T> bindingType, Class<B> toType) {
        super(bindingType, toType);
    }
    /**
     * This method is responsible for creating mock modules so Guice can use them.<br/>
     *
     * {@link MockedModule} is used to bind interface to his correct implementation.
     *
     * @author Jochem Kalsbeek
     * @see MockedModule
     */
    public MockedModule<T, B> create() {
        return new MockedModule<>(bindingType, toType);
    }
}
