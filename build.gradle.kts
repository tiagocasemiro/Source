import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.31"
    id("org.jetbrains.compose") version "1.0.0"
    id("org.openjfx.javafxplugin") version "0.0.10"
}

group = "br.com.source"
version = "v0.0.10-alpha"

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

dependencies {
    // Compose desktop Linux
    implementation(compose.desktop.currentOs)
    implementation(compose.desktop.linux_x64)
    implementation(compose.desktop.linux_arm64)
    implementation(compose.desktop.common)

    // JGit manager
    implementation("org.eclipse.jgit:org.eclipse.jgit:6.2.0.202206071550-r")
    implementation("org.eclipse.jgit:org.eclipse.jgit.archive:6.2.0.202206071550-r")
    implementation("org.eclipse.jgit:org.eclipse.jgit.ssh.jsch:6.2.0.202206071550-r")

    // Split Panel component
    implementation("org.jetbrains.compose.components:components-splitpane-desktop:1.0.1")

    // Database manager
    implementation("org.dizitart:nitrite:3.4.3")

    // Koin core features
    implementation("io.insert-koin:koin-core:3.1.4")

    // FX Swing
    implementation("org.openjfx:javafx-swing:11-ea+24")

    // Log4J
    implementation("org.slf4j:slf4j-simple:1.7.32")
    implementation("mx4j:mx4j-jmx:3.0.1")

}

javafx {
    modules("javafx.swing")
    configuration = "compileOnly"
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "15"
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Rpm, TargetFormat.Deb, TargetFormat.AppImage)
            packageName = "Source-App"
            packageVersion = "0.0.10"
            description = "The Linux gui git client"
            linux {
                iconFile.set(project.file("src/main/resources/source-launch-icon.png"))
            }
        }
    }
}