buildscript {
    repositories {
        maven("https://jitpack.io")
    }
    dependencies {
        classpath("com.github.siordache-forks:javafx-gradle-plugin:0.0.9-rc1")
    }
}

plugins {
    kotlin("jvm") version "1.6.10"
    id("org.openjfx.javafxplugin") version "0.0.8"
    id("org.beryx.jlink") version "2.25.0"
    application
}

apply<org.openjfx.gradle.JavaFXPlugin>()

group = "net.hirlab"
version = "1.2.0"

val tornadofxVersion = "1.7.20"
val coroutinesVersion = "1.6.1"

repositories {
    mavenCentral()
}

application {
    mainClass.set("net.hirlab.ktsignage.MainKt")
    mainModule.set("net.hirlab.ktsignage")
}

configure<org.openjfx.gradle.JavaFXOptions> {
    version = "11.0.2"
    modules("javafx.base", "javafx.graphics", "javafx.controls")
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
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
    jar {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        manifest {
            attributes(
                mapOf(
                    "Main-Class" to "net.hirlab.ktsignage.MainKt",
                    "Implementation-Title" to project.name,
                    "Implementation-Version" to project.version
                )
            )
        }
        from(configurations.compileClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    }
}

val compileKotlin: org.jetbrains.kotlin.gradle.tasks.KotlinCompile by tasks
val compileJava: JavaCompile by tasks
compileJava.destinationDirectory.set(compileKotlin.destinationDirectory)

jlink {
    launcher {
        name = "KtSignage"
    }
    jpackage {
        icon = "${projectDir}/src/main/resources/sys/data/KtSignage.icns"
    }
    addExtraDependencies("javafx")
}