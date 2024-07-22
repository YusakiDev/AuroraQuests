package gg.auroramc.quests.config.quest;

import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
public class StartRequirementConfig {
    private boolean alwaysShowInMenu = false;
    private List<String> quests;
    private List<String> permissions;
    private Map<String, PlaceholderConfig> placeholders;
    private FilterConfig filters;

    public static class PlaceholderConfig {
        private String placeholder;
        private String equals;
        private String notEquals;
        private Integer higherThan;
        private Integer lowerThan;
    }
}
