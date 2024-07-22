package gg.auroramc.quests.config.quest;

import gg.auroramc.aurora.api.config.AuroraConfig;
import gg.auroramc.aurora.api.config.premade.ItemConfig;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;

import java.io.File;
import java.util.List;
import java.util.Map;

@Getter
public class QuestConfig extends AuroraConfig {
    private String name;
    private String difficulty;
    private ItemConfig menuItem;
    private List<String> lockedLore;
    private List<String> completedLore;
    private Map<String, TaskConfig> tasks;
    private Map<String, ConfigurationSection> rewards;
    private StartRequirementConfig startRequirements;

    public QuestConfig(File file) {
        super(file);
    }
}
