package nl.han.modules;

import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class is responsible for creating modules so Guice can use them.<br/>
 * {@link Binding} is used to bind interface to his correct implementation.
 *
 * @author Jochem Kalsbeek
 * @see Binding
 */
@NoArgsConstructor
public class ModuleFactory {

    protected List<Binding<?, ?>> bindings = new ArrayList<>();

    public ModuleFactory(Binding<?, ?> binding) {
        bindings.add(binding);
    }

    /**
     * Adds a binding to the factory. The binding will be used to create a module. <br/>
     * {@link Binding} is used to bind interface to his correct implementation.
     *
     * @param binding The binding to add to the factory.
     * @return The ModuleFactory with the added binding. This can be used to chain calls.
     */
    public ModuleFactory add(Binding<?, ?> binding) {
        bindings.add(binding);
        return this;
    }

    /**
     * This method is responsible for creating modules so Guice can use them.<br/>
     * {@link Binding} is used to bind interface to his correct implementation.
     *
     * @author Jochem Kalsbeek
     * @see Binding
     */
    public List<DefaultModule<?, ?>> createModules() { //NOSONAR - These types are not known, so we can't specify them.
        return bindings.stream().map(Binding::create).collect(Collectors.toList());
    }
}
