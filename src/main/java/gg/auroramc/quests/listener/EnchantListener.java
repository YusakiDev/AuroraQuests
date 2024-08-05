package gg.auroramc.quests.listener;

import gg.auroramc.aurora.api.item.TypeId;
import gg.auroramc.quests.AuroraQuests;
import gg.auroramc.quests.api.quest.TaskType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;

import java.util.Map;

public class EnchantListener implements Listener {
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEnchant(EnchantItemEvent e) {
        var player = e.getEnchanter();
        for (var ench : e.getEnchantsToAdd().entrySet()) {
            AuroraQuests.getInstance().getQuestManager()
                    .progress(
                            player,
                            TaskType.ENCHANT,
                            1,
                            Map.of(
                                    "type", TypeId.fromDefault(ench.getKey().getKey().getKey()),
                                    "level", ench.getValue().doubleValue()
                            ));
        }
    }
}
