package gg.auroramc.quests;

import gg.auroramc.aurora.api.AuroraAPI;
import gg.auroramc.aurora.api.AuroraLogger;
import gg.auroramc.quests.api.data.QuestData;
import gg.auroramc.quests.hooks.HookManager;
import gg.auroramc.quests.placeholder.QuestPlaceholderHandler;
import org.bukkit.plugin.java.JavaPlugin;

public class AuroraQuests extends JavaPlugin {
    private static AuroraLogger l;

    public static AuroraLogger logger() {
        return l;
    }

    @Override
    public void onLoad() {
        l = AuroraAPI.createLogger("AuroraQuests", () -> false);
        HookManager.loadHooks(this);
    }

    @Override
    public void onEnable() {
        AuroraAPI.getUserManager().registerUserDataHolder(QuestData.class);
        AuroraAPI.registerPlaceholderHandler(new QuestPlaceholderHandler());

        HookManager.enableHooks(this);
    }

    public void reload() {

    }

    @Override
    public void onDisable() {

    }
}