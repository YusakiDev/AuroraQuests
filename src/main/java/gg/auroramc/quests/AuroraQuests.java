package gg.auroramc.quests;

import gg.auroramc.aurora.api.AuroraAPI;
import gg.auroramc.aurora.api.AuroraLogger;
import gg.auroramc.quests.api.data.QuestData;
import gg.auroramc.quests.api.quest.QuestManager;
import gg.auroramc.quests.command.CommandManager;
import gg.auroramc.quests.config.ConfigManager;
import gg.auroramc.quests.hooks.HookManager;
import gg.auroramc.quests.listener.*;
import gg.auroramc.quests.placeholder.QuestPlaceholderHandler;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;

@Getter
public class AuroraQuests extends JavaPlugin {
    @Getter
    private static AuroraQuests instance;
    private static AuroraLogger l;

    public static AuroraLogger logger() {
        return l;
    }

    private ConfigManager configManager;
    private CommandManager commandManager;
    private QuestManager questManager;

    @Override
    public void onLoad() {
        instance = this;
        configManager = new ConfigManager(this);
        l = AuroraAPI.createLogger("AuroraQuests", () -> configManager.getConfig().getDebug());

        configManager.reload();

        for (var pool : configManager.getQuestPools().values()) {
            AuroraAPI.getLeaderboards().registerBoard(
                    "quests_" + pool.getId(),
                    (user) -> (double) user.getData(QuestData.class).getCompletedCount(pool.getId()),
                    (lb) -> AuroraAPI.formatNumber(((Double) lb.getValue()).longValue()),
                    configManager.getConfig().getLeaderboards().getCacheSize(),
                    configManager.getConfig().getLeaderboards().getMinCompleted()
            );
        }

        HookManager.loadHooks(this);
    }

    @Override
    public void onEnable() {
        AuroraAPI.getUserManager().registerUserDataHolder(QuestData.class);
        AuroraAPI.registerPlaceholderHandler(new QuestPlaceholderHandler());

        commandManager = new CommandManager(this);
        commandManager.reload();

        registerListeners();

        questManager = new QuestManager(this);

        HookManager.enableHooks(this);

        questManager.reload();

        Bukkit.getPluginManager().registerEvents(new PlayerListener(this), this);
    }

    public void reload() {
        configManager.reload();
        commandManager.reload();
        questManager.reload();
        Bukkit.getOnlinePlayers().forEach(player -> {
            questManager.rollQuestsIfNecessary(player);
            questManager.tryStartGlobalQuests(player);
        });
    }

    @Override
    public void onDisable() {
        commandManager.unregisterCommands();
        try {
            l.info("Shutting down scheduler...");
            StdSchedulerFactory.getDefaultScheduler().shutdown(true);
        } catch (SchedulerException e) {
            l.severe("Failed to shutdown scheduler: " + e.getMessage());
        }
    }

    private void registerListeners() {
        var pm = Bukkit.getPluginManager();
        pm.registerEvents(new BlockShearingListener(), this);
        pm.registerEvents(new BreedingEggListener(), this);
        pm.registerEvents(new BrewingListener(), this);
        pm.registerEvents(new BuildingListener(), this);
        pm.registerEvents(new CommandListener(), this);
        pm.registerEvents(new ConsumeListener(), this);
        pm.registerEvents(new CraftListener(), this);
        pm.registerEvents(new EnchantListener(), this);
        pm.registerEvents(new ExpEarnListener(), this);
        pm.registerEvents(new FarmingListener(), this);
        pm.registerEvents(new FishingListener(), this);
        pm.registerEvents(new MilkingListener(), this);
        pm.registerEvents(new MiningListener(), this);
        pm.registerEvents(new MobKillingListener(), this);
        pm.registerEvents(new PlayerKillingListener(), this);
        pm.registerEvents(new ShearingListener(), this);
        pm.registerEvents(new SmeltingListener(), this);
        pm.registerEvents(new TamingListener(), this);
    }
}