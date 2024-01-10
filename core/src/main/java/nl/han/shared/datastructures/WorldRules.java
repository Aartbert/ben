package nl.han.shared.datastructures;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WorldRules {
    private long seed;
    private WorldSize worldSize;
    private int dungeonDepth;
    private ItemSpawnRules itemSpawnRules;
    private MonsterSpawnRules monsterSpawnRules;

    @Getter
    @Setter
    public static class WorldSize {
        private int width;
        private int height;
    }

    @Getter
    @Setter
    public static class ItemSpawnRules {
        private int min;
        private int max;
    }

    @Getter
    @Setter
    public static class MonsterSpawnRules {
        private int min;
        private int max;
    }

    @Override
    public String toString() {
        return WorldRulesBuilder.convertToString(this);
    }
}
