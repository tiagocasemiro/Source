import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.31"
    id("org.jetbrains.compose") version "1.0.0-beta1"
    id("org.openjfx.javafxplugin") version "0.0.10"
}

group = "br.com.source"
version = "v0.0.9-alpha"

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation("org.eclipse.jgit:org.eclipse.jgit:5.13.0.202109080827-r")
    implementation("org.eclipse.jgit:org.eclipse.jgit.archive:5.13.0.202109080827-r")
    implementation("org.eclipse.jgit:org.eclipse.jgit.ssh.jsch:5.13.0.202109080827-r")

    implementation("org.jetbrains.compose.components:components-splitpane-desktop:1.0.0-beta6-dev446")
    implementation("org.dizitart:nitrite:3.4.3")

    // Koin core features
    implementation("io.insert-koin:koin-core:3.1.4")

    // FX Swing
    implementation("org.openjfx:javafx-swing:11-ea+24")

    // Log4J
    implementation("org.slf4j:slf4j-simple:1.7.32")
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
            packageVersion = "0.0.9"
            description = "The Linux gui git client"
            linux {
                iconFile.set(project.file("src/main/resources/source-launch-icon.png"))
            }
        }
    }
}