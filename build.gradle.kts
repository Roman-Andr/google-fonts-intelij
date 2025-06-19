plugins {
    id("java")
    id("org.jetbrains.intellij") version "1.17.4"
    kotlin("jvm")
}

group = "me.RomanAndr"
version = "1.4.0"

repositories {
    mavenCentral()
}

intellij {
    version.set("2023.1.5")
    type.set("IC")
    updateSinceUntilBuild.set(false)

    plugins.set(listOf(/* Plugin Dependencies */))
}

tasks {
    withType<JavaCompile> {
    }

    patchPluginXml {
        sinceBuild.set("231")
    }

    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }
}
dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("com.github.ben-manes.caffeine:caffeine:3.2.1")
}
kotlin {
    jvmToolchain(17)
}