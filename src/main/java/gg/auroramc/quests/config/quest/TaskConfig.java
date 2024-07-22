package gg.auroramc.quests.config.quest;

import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;

@Getter
public class TaskConfig {
    private String task;
    private String display;
    private ConfigurationSection args;
    private FilterConfig filters;
}
