package nl.han.modules;

import com.google.inject.AbstractModule;
import lombok.AllArgsConstructor;

/**
 * This class is responsible for creating modules so Guice can use them.<br/>
 * {@link Binding} is used to bind interface to his correct implementation.
 *
 * @author Jochem Kalsbeek
 * @see Binding
 */
@AllArgsConstructor
public class DefaultModule<B, T extends B> extends AbstractModule {

    protected Class<B> bindingType;
    protected Class<T> toType;

    /**
     * This method is responsible for creating modules so Guice can use them.<br/>
     * {@link Binding} is used to bind interface to his correct implementation.
     *
     * @author Jochem Kalsbeek
     * @see Binding
     */
    @Override
    protected void configure() {
        bind(bindingType).to(toType);
    }

}
