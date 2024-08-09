package gg.auroramc.quests.config;

import gg.auroramc.aurora.api.config.AuroraConfig;
import gg.auroramc.aurora.api.config.decorators.IgnoreField;
import gg.auroramc.quests.AuroraQuests;
import lombok.Getter;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class Config extends AuroraConfig {
    private Boolean debug = false;
    private Boolean purgeInvalidDataOnLogin = false;
    private String language = "en";
    private Map<String, String> difficulties;
    private Boolean preventCreativeMode = false;
    private LeaderboardConfig leaderboards;
    private Map<String, DisplayComponent> displayComponents;
    private LevelUpSound levelUpSound;
    private LevelUpMessage levelUpMessage;
    private LevelUpSound questCompleteSound;
    private LevelUpMessage questCompleteMessage;
    private CommandAliasConfig commandAliases;
    private List<String> sortOrder;
    private UnlockTaskConfig unlockTask = new UnlockTaskConfig();

    @IgnoreField
    private Map<String, Integer> sortOderMap;

    @Override
    public void load() {
        super.load();
        Map<String, Integer> difficultyOrder = new HashMap<>();
        for (int i = 0; i < sortOrder.size(); i++) {
            difficultyOrder.put(sortOrder.get(i), i);
        }
        sortOderMap = difficultyOrder;
    }

    public Config(AuroraQuests plugin) {
        super(getFile(plugin));
    }

    @Getter
    public static final class LeaderboardConfig {
        private Integer cacheSize = 10;
        private Integer minCompleted = 3;
        private Boolean includeGlobal = false;
    }

    @Getter
    public static final class DisplayComponent {
        private String title;
        private String line;
    }

    @Getter
    public static final class UnlockTaskConfig {
        private Boolean enabled = false;
        private Integer interval = 5;
    }

    @Getter
    public static final class LevelUpMessage {
        private Boolean enabled;
        private List<String> message;
    }

    @Getter
    public static final class LevelUpSound {
        private Boolean enabled;
        private String sound;
        private Float volume;
        private Float pitch;
    }

    @Getter
    public static final class CommandAliasConfig {
        private List<String> quests = List.of("quests");
    }

    public static File getFile(AuroraQuests plugin) {
        return new File(plugin.getDataFolder(), "config.yml");
    }

    public static void saveDefault(AuroraQuests plugin) {
        if (!getFile(plugin).exists()) {
            plugin.saveResource("config.yml", false);
        }
    }
}
