package gg.auroramc.quests;

import gg.auroramc.aurora.api.AuroraAPI;
import gg.auroramc.aurora.api.AuroraLogger;
import gg.auroramc.aurora.api.command.CommandDispatcher;
import gg.auroramc.quests.api.data.QuestData;
import gg.auroramc.quests.api.quest.QuestManager;
import gg.auroramc.quests.command.CommandManager;
import gg.auroramc.quests.config.ConfigManager;
import gg.auroramc.quests.hooks.HookManager;
import gg.auroramc.quests.listener.*;
import gg.auroramc.quests.menu.PoolMenu;
import gg.auroramc.quests.placeholder.QuestPlaceholderHandler;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import lombok.Getter;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

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
    private ScheduledTask unlockTask;

    @Override
    public void onLoad() {
        instance = this;
        configManager = new ConfigManager(this);
        l = AuroraAPI.createLogger("AuroraQuests", () -> configManager.getConfig().getDebug());

        configManager.reload();

        for (var pool : configManager.getQuestPools().values()) {
            if (pool.getType().equals("global") && !configManager.getConfig().getLeaderboards().getIncludeGlobal()) {
                continue;
            }
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

        Bukkit.getPluginManager().registerEvents(new PlayerListener(this), this);

        Bukkit.getGlobalRegionScheduler().run(this, (task) -> {
            questManager.reload();
            reloadUnlockTask();
        });

        CommandDispatcher.registerActionHandler("quest-pool", (player, input) -> {
            var split = input.split("---");
            var poolId = split[0].trim();
            var pool = questManager.getQuestPool(poolId);
            if (pool == null) return;
            if (split.length > 1) {
                new PoolMenu(player, pool, () -> CommandDispatcher.dispatch(player, split[1].trim())).open();
            } else {
                new PoolMenu(player, pool).open();
            }
        });

        new Metrics(this, 23779);
    }

    public void reload() {
        configManager.reload();
        commandManager.reload();
        questManager.reload();

        reloadUnlockTask();
        CompletableFuture.runAsync(() ->
                Bukkit.getOnlinePlayers().forEach(player -> {
                    questManager.tryUnlockQuestPools(player);
                    questManager.tryStartGlobalQuests(player);
                    questManager.rollQuestsIfNecessary(player);
                    questManager.getRewardAutoCorrector().correctRewards(player);
                }));
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

        if (unlockTask != null && !unlockTask.isCancelled()) {
            unlockTask.cancel();
        }
    }

    private void registerListeners() {
        var pm = Bukkit.getPluginManager();
        pm.registerEvents(new BlockShearingListener(), this);
        pm.registerEvents(new BreedingEggListener(), this);
        pm.registerEvents(new BreedingListener(), this);
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

    private void reloadUnlockTask() {
        var cf = configManager.getConfig().getUnlockTask();

        if (!cf.getEnabled()) {
            if (unlockTask != null && !unlockTask.isCancelled()) {
                unlockTask.cancel();
                unlockTask = null;
            }
            return;
        }

        unlockTask = Bukkit.getAsyncScheduler().runAtFixedRate(this, (task) -> {
            Bukkit.getOnlinePlayers().forEach(player -> {
                questManager.tryUnlockQuestPools(player);
                questManager.tryStartGlobalQuests(player);
            });
        }, cf.getInterval(), cf.getInterval(), TimeUnit.SECONDS);
    }
}