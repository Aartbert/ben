package nl.han;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.java.Log;
import nl.han.gamestate.saver.GlobalConfigRepository;
import nl.han.shared.datastructures.GlobalConfig;

import java.util.UUID;

/**
 * The ConfigManager class is responsible for managing the configuration settings of the application.
 * It provides methods to save the configuration to a database using SQLUtils and GlobalConfig.
 *
 * @author Sem Gerrits
 */
@Log
@Singleton
public class ConfigManager {
    @Inject
    private GlobalConfigRepository globalConfigRepository;

    private GlobalConfig globalConfig;

    /**
     * Loads the configuration from the {@link GlobalConfigRepository GlobalConfigRepository}.
     */
    public void initialLoad() {
        GlobalConfig config = globalConfigRepository.getFirst();
        if (config == null) {
            config = new GlobalConfig(UUID.randomUUID().toString().substring(0, 6), "", "");
            globalConfigRepository.save(config);
        }
        globalConfig = config;
    }

    /**
     * Saves the configuration by calling the save method of the globalConfig object.
     * The configuration is saved using the connection obtained from sqlUtils.
     * If an SQLException occurs during the saving process, it is logged as a severe error.
     *
     * @param config The configuration to be saved
     * @author Sem Gerrits
     */
    public void saveAgentConfig(String config) {
        globalConfig.setAgentConfigRules(config);
        globalConfigRepository.save(globalConfig);
    }

    /**
     * Loads the configuration by calling the load method of the globalConfig object.
     *
     * @author Sem gerrits, Lars Meijerink
     */
    public String getAgentConfig() {
        return globalConfig.getAgentConfigRules();
    }

    /**
     * Saves the monster configuration by calling the save method of the globalConfig object.
     * The configuration is saved using the connection obtained from sqlUtils.
     * If an SQLException occurs during the saving process, it is logged as a severe error.
     *
     * @param config The configuration to be saved
     * @author Laurens van Brecht
     */
    public void saveMonsterConfig(String config) {
        globalConfig.setMonsterConfigRules(config);
        globalConfigRepository.save(globalConfig);
    }

    /**
     * Loads the monster configuration by calling the load method of the globalConfig object.
     *
     * @author Laurens van Brecht
     */
    public String getMonsterConfig() {
        return globalConfig.getMonsterConfigRules();
    }

    /**
     * Gets the name of the user from the globalConfig object.
     *
     * @return The name of the user as a String
     */
    public String getUserName() {
        return globalConfig.getUserName();
    }

    /**
     * Sets the name of the user in the globalConfig object.
     *
     * @param userName The name of the user as a String
     */
    public void setUserName(String userName) {
        globalConfig.setUserName(userName);
        globalConfigRepository.save(globalConfig);
    }
}
