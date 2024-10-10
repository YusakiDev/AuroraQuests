package gg.auroramc.quests.listener;

import gg.auroramc.aurora.api.AuroraAPI;
import gg.auroramc.quests.AuroraQuests;
import gg.auroramc.quests.api.quest.TaskType;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.persistence.PersistentDataType;

import java.util.Map;

public class MobKillingListener implements Listener {
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        Player player = entity.getKiller();
        if (player == null) return;
        if (entity instanceof Player) return;
        if (player.hasMetadata("NPC")) return;

        var id = AuroraAPI.getEntityManager().resolveId(entity);
        if (id.namespace().equals("mythicmobs")) return;
        var manager = AuroraQuests.getInstance().getQuestManager();

        manager.progress(player, TaskType.KILL_MOB, 1, Map.of("type", id));

        for (var drop : event.getDrops()) {
            var typeId = AuroraAPI.getItemManager().resolveId(drop);
            manager.progress(player, TaskType.ENTITY_LOOT, drop.getAmount(), Map.of("type", typeId));
        }

        var level = getMobLevel(entity);
        if (level != null) {
            manager.progress(player, TaskType.KILL_LEVELLED_MOB, 1, Map.of("type", id, "level", level));
        }
    }

    private Double getMobLevel(LivingEntity livingEntity) {
        var levelledMobsPlugin = Bukkit.getPluginManager().getPlugin("LevelledMobs");
        if (levelledMobsPlugin == null) return null;
        var levelKey = new NamespacedKey(levelledMobsPlugin, "level");
        var level = livingEntity.getPersistentDataContainer().get(levelKey, PersistentDataType.INTEGER);
        return level != null ? level.doubleValue() : null;
    }
}
