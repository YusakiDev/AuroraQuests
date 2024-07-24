package gg.auroramc.quests;

import gg.auroramc.aurora.api.AuroraAPI;
import gg.auroramc.aurora.api.AuroraLogger;
import gg.auroramc.quests.api.data.QuestData;
import gg.auroramc.quests.api.quest.QuestManager;
import gg.auroramc.quests.command.CommandManager;
import gg.auroramc.quests.config.ConfigManager;
import gg.auroramc.quests.hooks.HookManager;
import gg.auroramc.quests.listener.PlayerListener;
import gg.auroramc.quests.placeholder.QuestPlaceholderHandler;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class AuroraQuests extends JavaPlugin {
    private static AuroraLogger l;

    public static AuroraLogger logger() {
        return l;
    }

    private ConfigManager configManager;
    private CommandManager commandManager;
    private QuestManager questManager;

    @Override
    public void onLoad() {
        configManager = new ConfigManager(this);
        l = AuroraAPI.createLogger("AuroraQuests", () -> configManager.getConfig().getDebug());

        configManager.reload();

        HookManager.loadHooks(this);
    }

    @Override
    public void onEnable() {
        AuroraAPI.getUserManager().registerUserDataHolder(QuestData.class);
        AuroraAPI.registerPlaceholderHandler(new QuestPlaceholderHandler());

        commandManager = new CommandManager(this);
        commandManager.reload();

        questManager = new QuestManager(this);

        HookManager.enableHooks(this);

        questManager.reload();

        Bukkit.getPluginManager().registerEvents(new PlayerListener(this), this);
    }

    public void reload() {
        configManager.reload();
        commandManager.reload();
        questManager.reload();
    }

    @Override
    public void onDisable() {
        commandManager.unregisterCommands();
    }
}