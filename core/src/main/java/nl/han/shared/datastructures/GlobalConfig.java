package nl.han.shared.datastructures;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GlobalConfig {
    private String userName;
    private String agentConfigRules;
    private String monsterConfigRules;
}