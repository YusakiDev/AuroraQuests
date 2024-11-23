package gg.auroramc.quests.hooks.mythicdungeons;

import gg.auroramc.aurora.api.item.TypeId;
import gg.auroramc.quests.AuroraQuests;
import gg.auroramc.quests.api.quest.TaskType;
import gg.auroramc.quests.hooks.Hook;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import net.playavalon.mythicdungeons.api.events.dungeon.PlayerFinishDungeonEvent;

import java.util.Map;

public class DungeonsHook implements Hook, Listener {
    private AuroraQuests plugin;

    @Override
    public void hook(AuroraQuests plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onDungeonComplete(PlayerFinishDungeonEvent event) {
        var player = event.getPlayer();
        var dungeon = event.getDungeon();
        var instance = event.getInstance().asPlayInstance();

        Map<String, Object> params = instance != null && instance.getDifficulty() != null ? Map.of(
                "type", new TypeId("mythicdungeons", dungeon.getFolder().getName()),
                "difficulty", instance.getDifficulty().getNamespace()
        ) : Map.of(
                "type", new TypeId("mythicdungeons", dungeon.getFolder().getName())
        );

        plugin.getQuestManager().progress(player, TaskType.COMPLETE_DUNGEON, 1, params);
    }
}
