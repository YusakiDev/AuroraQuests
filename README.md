# AuroraQuests

AuroraQuests is a powerful and flexible Minecraft plugin designed to bring immersive questing experiences to your server. 
Featuring fully configurable GUI menus, AuroraQuests allows you to create diverse quest pools, including global storylines 
and timed random quests with cron scheduling. With support for over 30 task types, multiple rewards, and integration with 
popular plugins, AuroraQuests ensures a seamless and engaging experience. The plugin also boasts features like 
configurable difficulties, per-pool leaderboards, leveling systems, MySQL cross-server sync, PlaceholderAPI placeholders, 
and a Developer API, making it the ultimate tool for crafting compelling quests.


## Developer API

### Maven

```xml
<repository>
    <id>auroramc</id>
    <url>https://repo.auroramc.gg/releases/</url>
</repository>
```

```xml
<dependency>
    <groupId>gg.auroramc</groupId>
    <artifactId>AuroraQuests</artifactId>
    <version>{VERSION}</version>
    <scope>provided</scope>
</dependency>
```
### Gradle

**Groovy DSL:**
```gradle
repositories {
    maven {
        url "https://repo.auroramc.gg/releases/"
    }
}

dependencies {
    compileOnly 'gg.auroramc:AuroraQuests:{VERSION}'
}
```

**Kotlin DSL:**
```Gradle Kotlin DSL
repositories { 
    maven("https://repo.auroramc.gg/releases/")
}

dependencies { 
    compileOnly("gg.auroramc:AuroraQuests:{VERSION}")
}
```