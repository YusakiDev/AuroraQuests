package gg.auroramc.quests.config;

import lombok.Getter;

import java.util.Map;

@Getter
public class QuestPoolConfig {
    private String type;
    private Map<String, Integer> difficulties;
    private String resetFrequency;
}
