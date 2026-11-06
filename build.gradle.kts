plugins {
    id("net.neoforged.moddev.legacyforge") version "2.0.148"
    id("com.diffplug.spotless") version "6.21.0"
    id("maven-publish")
    java
    idea
}

val modVersion: String by project
val modloader: String by project
val minecraftVersion: String by project
val parchmentMinecraftVersion: String by project
val parchmentVersion: String by project
val fabricLoaderVersion: String by project
val fabricApiVersion: String by project
val trinketsVersion: String by project
val ccaVersion: String by project
val clothVersion: String by project
val modMenuVersion: String by project
val ae2Version: String by project
val guidemeVersion: String by project
val architecturyVersion: String by project
val runtimeItemlistMod: String by project
val jeiMinecraftVersion: String by project
val jeiVersion: String by project
val reiVersion: String by project
val emiVersion: String by project
val forgeVersion: String by project
val curiosVersion: String by project

version = "$modVersion-SNAPSHOT"

val pr = System.getenv("PR_NUMBER") ?: ""
if (pr != "") {
    version = "$modVersion+pr$pr"
}

val tag = System.getenv("TAG") ?: ""
if (tag != "") {
    if (!tag.contains(modloader)) {
        throw GradleException("Tags for the $modloader version should contain ${modloader}: $tag")
    }
    version = tag
}

dependencies {
    modImplementation("org.appliedenergistics:guideme:${guidemeVersion}")
    modImplementation("top.theillusivec4.curios:curios-forge:${curiosVersion}")
    modImplementation("me.shedaniel.cloth:cloth-config-${modloader}:${clothVersion}")
    modImplementation("dev.architectury:architectury-${modloader}:${architecturyVersion}")
    modImplementation("appeng:appliedenergistics2-${modloader}:${ae2Version}")

    modCompileOnly("me.shedaniel:RoughlyEnoughItems-${modloader}:${reiVersion}")
    modCompileOnly("mezz.jei:jei-${jeiMinecraftVersion}-${modloader}:${jeiVersion}")
    modCompileOnly("dev.emi:emi-${modloader}:${emiVersion}+${minecraftVersion}:api")

    when (runtimeItemlistMod) {
        "rei" -> modRuntimeOnly("me.shedaniel:RoughlyEnoughItems-${modloader}:${reiVersion}")

        "jei" -> modRuntimeOnly("mezz.jei:jei-${jeiMinecraftVersion}-${modloader}:${jeiVersion}")

        "emi" -> {
            modRuntimeOnly("dev.emi:emi-${modloader}:${emiVersion}+${minecraftVersion}")
            //modRuntimeOnly("mezz.jei:jei-${jeiMinecraftVersion}-${modloader}:${jeiVersion}")
        }
    }

    annotationProcessor("org.spongepowered:mixin:0.8.5:processor")
    implementation("com.google.code.findbugs:jsr305:3.0.2")

    //testing
    //modRuntimeOnly("maven.modrinth:aeinfinitybooster:1.20.1-1.0.0+20")
}

repositories {
    mavenLocal()
    mavenCentral()
    maven {
        url = uri("https://modmaven.dev/")
        content {
            includeGroup("net.fabricmc.fabric-api")
            includeGroup("mezz.jei")
        }
    }
    maven {
        url = uri("https://repo.expandium.net/releases")
        content {
            includeGroup("appeng")
        }
    }
    maven {
        url = uri("https://repo.expandium.net/snapshots")
        content {
            includeGroup("appeng")
        }
    }
    maven {
        url = uri("https://maven.bai.lol")
        content {
            includeGroup("mcp.mobius.waila")
            includeGroup("lol.bai")
        }
    }
    maven {
        url = uri("https://maven.shedaniel.me/")
        content {
            includeGroup("me.shedaniel")
            includeGroup("me.shedaniel.cloth")
            includeGroup("dev.architectury")
        }
    }
    maven {
        url = uri("https://maven.terraformersmc.com/")
        content {
            includeGroup("com.terraformersmc")
            includeGroup("dev.emi")
        }
    }
    maven {
        url = uri("https://maven.ladysnake.org/releases")
        content {
            includeGroup("dev.onyxstudios.cardinal-components-api")
        }
    }
    maven {
        url = uri("https://maven.parchmentmc.net/")
        content {
            includeGroup("org.parchmentmc.data")
        }
    }
    maven {
        url = uri("https://maven.theillusivec4.top/")
        content {
            includeGroup("top.theillusivec4.curios")
        }
    }
    maven {
        url = uri("https://repo.spongepowered.org/maven")
        content {
            includeGroup("org.spongepowered")
        }
    }
    maven {
        url = uri("https://api.modrinth.com/maven")
        content {
            includeGroup("maven.modrinth")
        }
    }
}

val neoForgeDep = "${minecraftVersion}-${forgeVersion}"

legacyForge {
    enable {
        neoForgeVersion = neoForgeDep
    }

    runs {
        configureEach {
            systemProperty("fml.earlyprogresswindow", "false")
            systemProperty("forge.logging.console.level", "debug")
            gameDirectory.set(project.file("run"))
        }
        create("client") {
            client()
        }
        create("server") {
            server()
        }
    }

    mods {
        create("ae2wtlib") {
            sourceSet(sourceSets.main.get())
        }
    }
}

mixin {
    add(sourceSets.main.get(), "ae2wtlib.mixins.refmap.json")
    config("ae2wtlib.mixins.json")
}

java {
    withSourcesJar()
}

tasks {
    jar {
        manifest {
            attributes(mapOf(
                    "MixinConfigs" to "ae2wtlib.mixins.json"
            ))
        }
    }

    processResources {
        val resourceTargets = "META-INF/mods.toml"

        val replaceProperties = mapOf(
                "version" to version as String,
                "ae2_version" to ae2Version
        )

        inputs.properties(replaceProperties)
        filesMatching(resourceTargets) {
            expand(replaceProperties)
        }
    }
    withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.release.set(17)
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            groupId = project.property("mavenGroup") as String
            artifactId = "ae2wtlib"
            version = project.version as String
            from(components["java"])
        }
    }
    repositories {
        maven {
            name = "expandium"
            url = uri("https://repo.expandium.net/releases")
            credentials {
                username = project.findProperty("MAVEN_USERNAME") as String
                password = project.findProperty("MAVEN_TOKEN") as String
            }
        }
    }
}

configure<com.diffplug.gradle.spotless.SpotlessExtension> {
    java {
        target("/src/*/java/**/*.java")

        endWithNewline()
        indentWithSpaces()
        removeUnusedImports()
        toggleOffOn()
        eclipse().configFile("codeformat/codeformat.xml")
        importOrderFile("codeformat/ae2wtlib.importorder")
    }
}
