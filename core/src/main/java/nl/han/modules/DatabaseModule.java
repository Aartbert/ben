package nl.han.modules;

import com.google.inject.AbstractModule;
import nl.han.HSQLDBUtils;
import nl.han.ISQLUtils;

/**
 * This class is responsible for configuring bindings with the SQL utils interface and his implementations. <br/>
 *
 * @author Djurre Tieman
 */
public class DatabaseModule extends AbstractModule {
    /**
     * This method is responsible for configuring bindings with the SQL utils interface and his implementations. <br/>
     *
     * @author Djurre Tieman
     */
    @Override
    protected void configure() {
        bind(ISQLUtils.class).to(HSQLDBUtils.class);
    }
}
