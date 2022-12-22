import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.20"
    idea
    `maven-publish`
    `java-library`
    signing
}

group = "space.iseki.envproxyselector"
version = "0.1.0-SNAPSHOT"

dependencies {
    testImplementation(kotlin("test"))
}

val javaLanguageVersion = JavaLanguageVersion.of(8)
val toolchain18Launcher = javaToolchains.launcherFor { languageVersion.set(javaLanguageVersion) }

allprojects {
    repositories {
        mavenCentral()
    }
    tasks.withType<JavaCompile>().configureEach {
        javaCompiler.set(javaToolchains.compilerFor { languageVersion.set(javaLanguageVersion) })
    }
    tasks.withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
        kotlinJavaToolchain.toolchain.use(toolchain18Launcher)
    }
    tasks.withType<Test> {
        useJUnitPlatform()
        testLogging {
            events("passed", "skipped", "failed")
        }
    }
    tasks.withType<AbstractArchiveTask>().configureEach {
        isPreserveFileTimestamps = false
        isReproducibleFileOrder = true
    }
}

java {
    withJavadocJar()
    withSourcesJar()
}

publishing {
    repositories {
        maven {
            url = if (version.toString().endsWith("SNAPSHOT")) {
                // uri("https://s01.oss.sonatype.org/content/repositories/snapshots")
                uri("https://oss.sonatype.org/content/repositories/snapshots")
            } else {
                // uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2")
                uri("https://oss.sonatype.org/service/local/staging/deploy/maven2")
            }
            credentials {
                properties["ossrhUsername"]?.also { username = it.toString() }
                properties["ossrhPassword"]?.also { password = it.toString() }
            }
        }

    }
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            groupId = project.group.toString()
            artifactId = project.name
            version = project.version.toString()
            pom {
                name.set("Proxy env selector")
                description.set("A proxy selector that read proxy info from environment")
                url.set("https://github.com/iseki0/envproxyselector")
                licenses {
                    license {
                        name.set("MIT")
                    }
                }
                developers {
                    developer {
                        id.set("iseki_zero")
                        name.set("iseki zero")
                        email.set("iseki@iseki.space")
                    }
                }
                scm {
                    connection.set("scm:git:https://github.com/iseki0/envproxyselector.git")
                    developerConnection.set("scm:git:ssh://github.com/iseki0/envproxyselector.git")
                    url.set("https://github.com/iseki0/envproxyselector")
                }
            }
        }
    }
}

signing {
    // To use local gpg command, configure gpg options in ~/.gradle/gradle.properties
    // reference: https://docs.gradle.org/current/userguide/signing_plugin.html#example_configure_the_gnupgsignatory
    useGpgCmd()
    sign(publishing.publications["mavenJava"])
}

