package gg.auroramc.quests.api.quest;

import gg.auroramc.aurora.api.AuroraAPI;
import gg.auroramc.quests.config.quest.TaskConfig;
import gg.auroramc.quests.hooks.HookManager;
import gg.auroramc.quests.hooks.worldguard.WorldGuardHook;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Map;

public class FilteredTaskEvaluator implements TaskEvaluator {
    @Override
    public boolean evaluate(Player player, TaskConfig config, Map<String, Object> params) {
        if (config.getFilters() == null) return true;
        var filters = config.getFilters();
        boolean valid = true;


        Location location;
        if (params.containsKey("location") && params.get("location") instanceof Location l) {
            location = l;
        } else {
            location = player.getLocation();
        }

        if (filters.getBiomes() != null) {
            var biomeValid = false;
            if (filters.getBiomes().getType().equals("whitelist")) {
                biomeValid = filters.getBiomes().getValue().contains(location.getBlock().getBiome().getKey().toString());
            } else if (filters.getBiomes().getType().equals("blacklist")) {
                biomeValid = !filters.getBiomes().getValue().contains(location.getBlock().getBiome().getKey().toString());
            }
            valid = biomeValid;
        }

        if (filters.getMaxYLevel() != null) {
            valid = valid && location.getY() <= filters.getMaxYLevel();
        }

        if (filters.getMinYLevel() != null) {
            valid = valid && location.getY() >= filters.getMinYLevel();
        }

        if (filters.getWorlds() != null) {
            var worldValid = false;
            if (filters.getWorlds().getType().equals("whitelist")) {
                worldValid = filters.getWorlds().getValue().contains(location.getWorld().getName());
            } else if (filters.getWorlds().getType().equals("blacklist")) {
                worldValid = !filters.getWorlds().getValue().contains(location.getWorld().getName());
            }
            valid = valid && worldValid;
        }

        if (filters.getHand() != null) {
            var itemId = AuroraAPI.getItemManager().resolveId(player.getInventory().getItemInMainHand());
            valid = valid && filters.getHand().getItems().contains(itemId.toString());
        }

        if (filters.getRegions() != null && HookManager.isEnabled(WorldGuardHook.class)) {
            var regionValid = false;
            if (filters.getRegions().getType().equals("whitelist")) {
                regionValid = HookManager.getHook(WorldGuardHook.class).isInAnyRegion(player, location, filters.getRegions().getValue());
            } else if (filters.getRegions().getType().equals("blacklist")) {
                regionValid = !HookManager.getHook(WorldGuardHook.class).isInAnyRegion(player, location, filters.getRegions().getValue());
            }
            valid = valid && regionValid;
        }

        return valid;
    }
}
