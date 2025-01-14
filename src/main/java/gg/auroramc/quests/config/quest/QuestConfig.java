package gg.auroramc.quests.config.quest;

import gg.auroramc.aurora.api.config.AuroraConfig;
import gg.auroramc.aurora.api.config.decorators.IgnoreField;
import gg.auroramc.aurora.api.config.premade.ItemConfig;
import lombok.Getter;
import lombok.Setter;
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
    private List<String> uncompletedLore;
    private Map<String, TaskConfig> tasks;
    private ConfigurationSection rewards;
    private StartRequirementConfig startRequirements;

    @Setter
    @IgnoreField
    private String id;

    @Setter
    @IgnoreField
    private PoolConfig poolConfig;

    public QuestConfig(File file) {
        super(file);
    }
}
