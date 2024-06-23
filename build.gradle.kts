plugins {
    id("java")
    id("idea")
    id("xland.gradle.forge-init-injector") version "1.1.1"
}

group = "xland.mcmod"
version = ext["project_version"].toString()

repositories {
    maven("https://maven.aliyun.com/repository/public")
    maven("https://lss233.littleservice.cn/repositories/minecraft")
    maven("https://covid-trump.github.io/mvn")
}

dependencies {
    compileOnly("xland.mcmod:enchlevel-langpatch:2.0.0")
    compileOnly("org.jetbrains:annotations:19.0.0")
    implementation("com.google.guava:guava:21.0")
    implementation("org.apache.logging.log4j:log4j-api:2.8.1")
}

java.sourceCompatibility = JavaVersion.VERSION_1_8
java.targetCompatibility = JavaVersion.VERSION_1_8

forgeInitInjector {
    modId = "langpatchconf"
    stubPackage = "Eri4cCOS35DHuw2QLQbLZ"
    neoFlag("post_20_5")    // will not support 1.20.2-4 NeoForge
}

tasks.processResources {
    inputs.property("version", project.version)
    filesMatching(setOf("fabric.mod.json", "META-INF/mods.toml", "META-INF/neoforge.mods.toml")) {
        expand("version" to project.version)
    }
}
