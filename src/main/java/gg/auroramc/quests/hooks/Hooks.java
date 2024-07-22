package gg.auroramc.quests.hooks;

import lombok.Getter;

@Getter
public enum Hooks {
    DUMMY(null, "Dummy");
//    AURORA_LEVELS(AuroraLevelsHook.class, "AuroraLevels"),
//    AURA_SKILLS(AuraSkillsHook.class, "AuraSkills"),
//    LUCK_PERMS(LuckPermsHook.class, "LuckPerms"),
//    CUSTOM_FISHING(CustomFishingHook.class, "CustomFishing"),
//    MMOITEMS(MMOItemsHook.class, "MMOItems"),
//    MYTHIC_MOBS(MythicHook.class, "MythicMobs"),
//    ORAXEN(OraxenHook.class, "Oraxen"),
//    WORLD_GUARD(WorldGuardHook.class, "WorldGuard"),
//    MMOLIB(MMOLibHook.class, "MythicLib");

    private final Class<? extends Hook> clazz;
    private final String plugin;

    Hooks(Class<? extends Hook> clazz, String plugin) {
        this.clazz = clazz;
        this.plugin = plugin;
    }
}
