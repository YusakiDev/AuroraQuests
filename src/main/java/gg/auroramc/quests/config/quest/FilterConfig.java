package gg.auroramc.quests.config.quest;

import lombok.Getter;

import java.util.List;

@Getter
public class FilterConfig {
    private SmartList worlds;
    private SmartList regions;
    private SmartList biomes;
    private Integer minYLevel;
    private Integer maxYLevel;
    private List<String> locations;
    private HandConfig hand;

    @Getter
    public static class SmartList {
        private String type;
        private List<String> value;
    }

    @Getter
    public static class HandConfig {
        private List<String> items;
        private List<String> enchantsEvery;
        private List<String> enchantsAny;
    }
}
