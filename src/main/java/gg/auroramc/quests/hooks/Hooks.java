package gg.auroramc.quests.hooks;

import gg.auroramc.quests.hooks.luckperms.LuckPermsHook;
import lombok.Getter;

@Getter
public enum Hooks {
//    AURORA_LEVELS(AuroraLevelsHook.class, "AuroraLevels"),
//    AURA_SKILLS(AuraSkillsHook.class, "AuraSkills"),
//    CUSTOM_FISHING(CustomFishingHook.class, "CustomFishing"),
//    MMOITEMS(MMOItemsHook.class, "MMOItems"),
//    MYTHIC_MOBS(MythicHook.class, "MythicMobs"),
//    ORAXEN(OraxenHook.class, "Oraxen"),
//    WORLD_GUARD(WorldGuardHook.class, "WorldGuard"),
//    MMOLIB(MMOLibHook.class, "MythicLib"),
    LUCK_PERMS(LuckPermsHook.class, "LuckPerms");

    private final Class<? extends Hook> clazz;
    private final String plugin;

    Hooks(Class<? extends Hook> clazz, String plugin) {
        this.clazz = clazz;
        this.plugin = plugin;
    }
}
