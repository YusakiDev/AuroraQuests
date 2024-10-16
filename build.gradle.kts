import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import java.net.URI
import java.util.*

fun loadProperties(filename: String): Properties {
    val properties = Properties()
    if (!file(filename).exists()) {
        return properties
    }
    file(filename).inputStream().use { properties.load(it) }
    return properties
}

plugins {
    id("java")
    id("io.github.goooler.shadow") version "8.1.7"
    id("maven-publish")
}

group = "gg.auroramc"
version = "1.3.3"

repositories {
    mavenCentral()
    mavenLocal()
    maven("https://papermc.io/repo/repository/maven-public/")
    maven("https://repo.auroramc.gg/repository/maven-public/")
    maven("https://repo.aikar.co/content/groups/aikar/")
    maven("https://mvn.lumine.io/repository/maven-public/")
    maven("https://maven.enginehub.org/repo/")
    maven("https://maven.citizensnpcs.co/repo")
    maven("https://jitpack.io/")
    maven("https://repo.projectshard.dev/repository/releases/")
    maven("https://repo.oraxen.com/releases")
    maven("https://nexus.phoenixdevt.fr/repository/maven-public/")
    maven("https://repo.fancyplugins.de/releases")
    maven("https://repo.tabooproject.org/repository/releases/")
    maven("https://repo.bg-software.com/repository/api/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21-R0.1-SNAPSHOT")
    compileOnly("gg.auroramc:Aurora:1.6.0")
    compileOnly("gg.auroramc:AuroraLevels:1.5.1")
    compileOnly("net.luckperms:api:5.4")
    compileOnly("dev.aurelium:auraskills-api-bukkit:2.2.0")
    compileOnly("io.lumine:Mythic-Dist:5.6.1")
    compileOnly("net.luckperms:api:5.4")
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.0.7")
    compileOnly("net.citizensnpcs:citizens-main:2.0.33-SNAPSHOT") {
        exclude(group = "*", module = "*")
    }
    compileOnly("com.github.Xiao-MoMi:Custom-Fishing:2.2.26")
    compileOnly("com.nisovin.shopkeepers:ShopkeepersAPI:2.22.3")
    compileOnly("com.github.Gypopo:EconomyShopGUI-API:1.7.1")
    compileOnly("io.th0rgal:oraxen:1.179.0")
    compileOnly("com.github.brcdev-minecraft:shopgui-api:3.0.0")
    compileOnly("io.lumine:MythicLib-dist:1.6.2-SNAPSHOT")
    compileOnly("de.oliver:FancyNpcs:2.2.2")
    compileOnly("ink.ptms.adyeshach:all:2.0.0-snapshot-1")
    compileOnly("com.bgsoftware:SuperiorSkyblockAPI:2024.3")

    implementation("co.aikar:acf-paper:0.5.1-SNAPSHOT")

    compileOnly("org.quartz-scheduler:quartz:2.3.2")
    compileOnly("com.cronutils:cron-utils:9.2.0")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    compileOnly("org.projectlombok:lombok:1.18.32")
    annotationProcessor("org.projectlombok:lombok:1.18.32")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

tasks.withType<ShadowJar> {
    archiveFileName.set("AuroraQuests-${project.version}.jar")

    relocate("co.aikar.commands", "gg.auroramc.quests.libs.acf")
    relocate("co.aikar.locales", "gg.auroramc.quests.libs.locales")

    exclude("acf-*.properties")
}

tasks.processResources {
    filteringCharset = "UTF-8"
    filesMatching("plugin.yml") {
        expand("version" to project.version)
    }
}

tasks {
    build {
        dependsOn(shadowJar)
    }
}

val publishing = loadProperties("publish.properties")

publishing {
    repositories {
        maven {
            name = "AuroraMC"
            url = if (version.toString().endsWith("SNAPSHOT")) {
                URI.create("https://repo.auroramc.gg/repository/maven-snapshots/")
            } else {
                URI.create("https://repo.auroramc.gg/repository/maven-releases/")
            }
            credentials {
                username = publishing.getProperty("username")
                password = publishing.getProperty("password")
            }
        }
    }

    publications.create<MavenPublication>("mavenJava") {
        groupId = "gg.auroramc"
        artifactId = "AuroraQuests"
        version = project.version.toString()

        from(components["java"])
    }
}