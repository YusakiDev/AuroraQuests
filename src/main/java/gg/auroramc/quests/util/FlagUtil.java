package gg.auroramc.quests.util;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class FlagUtil {
    public static boolean isBlocked(Player player, StateFlag flag) {
        return queryFlagState(player.getLocation(), player, flag);
    }

    public static boolean isBlocked(Player player, Location location, StateFlag flag) {
        return queryFlagState(location, player, flag);
    }

    private static boolean queryFlagState(Location location, Player player, StateFlag flag) {
        World world = location.getWorld();
        if (world == null) return false;

        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();

        RegionManager regions = container.get(BukkitAdapter.adapt(location.getWorld()));
        if (regions == null) {
            return false;
        }

        ApplicableRegionSet set = regions.getApplicableRegions(BukkitAdapter.adapt(location).toVector().toBlockPoint());
        LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
        StateFlag.State state = set.queryState(localPlayer, flag);
        return state == StateFlag.State.DENY;
    }
}