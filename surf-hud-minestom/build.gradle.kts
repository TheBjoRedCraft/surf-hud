import dev.slne.surf.api.gradle.util.slneReleases

plugins {
    id("dev.slne.surf.api.gradle.minestom")
}

repositories {
    mavenLocal()
}

minestomPluginFile {
    id = "surf-hud-minestom"
    main = "dev.slne.surf.hud.minestom.MinestomMain"
    authors = listOf("red")
}

dependencies {
    api(projects.surfHudCore)
}

publishing {
    repositories {
        slneReleases()
    }
}
