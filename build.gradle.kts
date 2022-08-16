plugins {
    kotlin("jvm") version "1.6.10"
    id("org.openjfx.javafxplugin") version "0.0.8"
    application
}
group = "net.hirlab"
version = "1.1.2"

val tornadofxVersion = "1.7.20"
val coroutinesVersion = "1.6.1"

repositories {
    mavenCentral()
}

javafx {
    version = "11.0.2"
    modules = listOf("javafx.controls", "javafx.graphics")
}

application {
    mainClassName = "net.hirlab.ktsignage.MainKt"
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("no.tornado:tornadofx:$tornadofxVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-javafx:$coroutinesVersion")
    implementation("com.google.guava:guava:31.1-jre")
    implementation("com.google.inject:guice:5.1.0")
    implementation("com.sun.activation:javax.activation:1.2.0")
    implementation("com.squareup.okhttp3:okhttp:4.9.3")
    implementation("org.json:json:20180813")
    testImplementation(kotlin("test-junit"))
    testImplementation("com.google.truth:truth:1.1.3")
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}