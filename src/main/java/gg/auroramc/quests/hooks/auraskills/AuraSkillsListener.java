package gg.auroramc.quests.hooks.auraskills;

import dev.aurelium.auraskills.api.event.loot.LootDropEvent;
import dev.aurelium.auraskills.api.event.skill.XpGainEvent;
import gg.auroramc.aurora.api.AuroraAPI;
import gg.auroramc.quests.AuroraQuests;
import gg.auroramc.quests.api.quest.TaskType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.Map;

public class AuraSkillsListener implements Listener {
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onSkillXpGain(XpGainEvent e) {
        var player = e.getPlayer();
        var xp = e.getAmount();
        var skill = e.getSkill().getId().toString();

        AuroraQuests.getInstance().getQuestManager().progress(player, TaskType.GAIN_AURASKILLS_XP, xp, Map.of("type", skill));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onExtraDrop(LootDropEvent e) {
        var item = e.getItem();
        var manager = AuroraQuests.getInstance().getQuestManager();
        var typeId = AuroraAPI.getItemManager().resolveId(item);
        
        Map<String, Object> params = Map.of("type", typeId);

        switch (e.getCause()) {
            case FARMING_LUCK, FARMING_OTHER_LOOT ->
                    manager.progress(e.getPlayer(), TaskType.FARM, item.getAmount(), params);
            case FISHING_LUCK, TREASURE_HUNTER, EPIC_CATCH, FISHING_OTHER_LOOT ->
                    manager.progress(e.getPlayer(), TaskType.FISH, item.getAmount(), params);
            case FORAGING_LUCK, FORAGING_OTHER_LOOT, MINING_LUCK, EXCAVATION_OTHER_LOOT, LUCKY_SPADES,
                 MINING_OTHER_LOOT, EXCAVATION_LUCK, METAL_DETECTOR ->
                    manager.progress(e.getPlayer(), TaskType.BLOCK_LOOT, item.getAmount(), params);
            case MOB_LOOT_TABLE -> manager.progress(e.getPlayer(), TaskType.ENTITY_LOOT, item.getAmount(), params);
            case LUCK_DOUBLE_DROP -> {
                manager.progress(e.getPlayer(), TaskType.FARM, item.getAmount(), params);
                manager.progress(e.getPlayer(), TaskType.FISH, item.getAmount(), params);
                manager.progress(e.getPlayer(), TaskType.BLOCK_LOOT, item.getAmount(), params);
                manager.progress(e.getPlayer(), TaskType.ENTITY_LOOT, item.getAmount(), params);
            }

        }
    }
}
