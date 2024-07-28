package gg.auroramc.quests.config;

import gg.auroramc.aurora.api.config.AuroraConfig;
import gg.auroramc.aurora.api.config.premade.ItemConfig;
import gg.auroramc.quests.AuroraQuests;
import lombok.Getter;

import java.io.File;
import java.util.Map;

@Getter
public class MainMenuConfig extends AuroraConfig {
    private String title;
    private FillerConfig filler;
    private Map<String, Config.DisplayComponent> displayComponents;
    private Map<String, ItemConfig> items;
    private Map<String, ItemConfig> customItems;
    private TaskStatuses taskStatuses;
    private ProgressBar progressBar;

    @Getter
    public static final class ProgressBar {
        private Integer length = 20;
        private String filledCharacter;
        private String unfilledCharacter;
    }

    public MainMenuConfig(AuroraQuests plugin) {
        super(getFile(plugin));
    }

    @Getter
    public static class FillerConfig {
        private Boolean enabled = false;
        private ItemConfig item;
    }

    @Getter
    public static class TaskStatuses {
        private String completed = "";
        private String notCompleted = "";
    }

    public static File getFile(AuroraQuests plugin) {
        return new File(plugin.getDataFolder(), "main_menu.yml");
    }

    public static void saveDefault(AuroraQuests plugin) {
        if (!getFile(plugin).exists()) {
            plugin.saveResource("main_menu.yml", false);
        }
    }
}
