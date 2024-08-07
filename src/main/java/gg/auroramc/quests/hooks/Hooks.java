package gg.auroramc.quests.hooks;

import gg.auroramc.quests.hooks.auraskills.AuraSkillsHook;
import gg.auroramc.quests.hooks.auroralevels.AuroraLevelsHook;
import gg.auroramc.quests.hooks.citizens.CitizensHook;
import gg.auroramc.quests.hooks.customfishing.CustomFishingHook;
import gg.auroramc.quests.hooks.luckperms.LuckPermsHook;
import gg.auroramc.quests.hooks.mmolib.MMOLibHook;
import gg.auroramc.quests.hooks.mythicmobs.MythicHook;
import gg.auroramc.quests.hooks.oraxen.OraxenHook;
import gg.auroramc.quests.hooks.shopguiplus.ShopGUIPlusHook;
import gg.auroramc.quests.hooks.shopkeepers.ShopkeepersHook;
import gg.auroramc.quests.hooks.worldguard.WorldGuardHook;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.util.Set;

@Getter
public enum Hooks {
    AURORA_LEVELS(AuroraLevelsHook.class, "AuroraLevels"),
    AURA_SKILLS(AuraSkillsHook.class, "AuraSkills"),
    CUSTOM_FISHING(CustomFishingHook.class, "CustomFishing"),
    MYTHIC_MOBS(MythicHook.class, "MythicMobs"),
    WORLD_GUARD(WorldGuardHook.class, "WorldGuard"),
    CITIZENS(CitizensHook.class, "Citizens"),
    SHOPKEEPERS(ShopkeepersHook.class, "Shopkeepers"),
    ORAXEN(OraxenHook.class, "Oraxen"),
    MMOLIB(MMOLibHook.class, "MythicLib"),
    SHOP_GUI_PLUS(ShopGUIPlusHook.class, "ShopGUIPlus"),
    ECONOMY_SHOP_GUI(CitizensHook.class, Set.of("EconomyShopGUI", "EconomyShopGUI-Premium")),
    LUCK_PERMS(LuckPermsHook.class, "LuckPerms");

    private final Class<? extends Hook> clazz;
    private final Set<String> plugins;

    Hooks(Class<? extends Hook> clazz, String plugin) {
        this.clazz = clazz;
        this.plugins = Set.of(plugin);
    }

    Hooks(Class<? extends Hook> clazz, Set<String> plugins) {
        this.clazz = clazz;
        this.plugins = plugins;
    }

    public boolean canHook() {
        for (String plugin : plugins) {
            if (Bukkit.getPluginManager().getPlugin(plugin) != null) {
                return true;
            }
        }
        return false;
    }
}
