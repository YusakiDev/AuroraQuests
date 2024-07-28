package gg.auroramc.quests.config.quest;

import lombok.Getter;

import java.util.Set;

@Getter
public class FilterConfig {
    private SmartList worlds;
    private SmartList regions;
    private SmartList biomes;
    private Integer minYLevel;
    private Integer maxYLevel;
    private HandConfig hand;

    @Getter
    public static class SmartList {
        private String type;
        private Set<String> value;
    }

    @Getter
    public static class HandConfig {
        private Set<String> items;
    }
}
