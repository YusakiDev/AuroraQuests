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
    private Integer menuRows = 6;
    private FillerConfig filler;
    private Map<String, ItemConfig> items;
    private Map<String, ItemConfig> customItems;

    public MainMenuConfig(AuroraQuests plugin) {
        super(getFile(plugin));
    }

    @Getter
    public static class FillerConfig {
        private Boolean enabled = false;
        private ItemConfig item;
    }

    public static File getFile(AuroraQuests plugin) {
        return new File(plugin.getDataFolder(), "menu_main.yml");
    }

    public static void saveDefault(AuroraQuests plugin) {
        if (!getFile(plugin).exists()) {
            plugin.saveResource("menu_main.yml", false);
        }
    }
}
