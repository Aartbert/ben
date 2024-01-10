package nl.han.shared.datastructures;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.experimental.UtilityClass;

@UtilityClass
public class WorldRulesBuilder {
    private static final Gson gson = new Gson();

    public static WorldRules convertToWorldRules(Config config) {
        return gson.fromJson(config.getRules(), WorldRules.class);
    }

    public static String convertToString(WorldRules rules) {
        return gson.toJson(rules);
    }
}
