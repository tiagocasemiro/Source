import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.21"
    id("org.jetbrains.compose") version "1.0.0-alpha3"
}

group = "br.com.source"
version = "0.0.1-DEV"

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation("org.eclipse.jgit:org.eclipse.jgit:5.13.0.202109080827-r")
    // implementation("org.jetbrains.compose.components:components-splitpane-desktop:1.0.0-alpha4-build398")
    implementation("org.dizitart:nitrite:3.4.3")
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "11"
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Rpm, TargetFormat.Deb, TargetFormat.AppImage)
            packageName = "Source"
            packageVersion = "1.0.0"
        }
    }
}