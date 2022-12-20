import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.20"
    idea
    `maven-publish`
}

group = "space.iseki.envproxyselector"
version = "1.0-SNAPSHOT"

dependencies {
    testImplementation(kotlin("test"))
}

allprojects {
    repositories {
        mavenCentral()
    }
    tasks.withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
    }
    tasks.withType<Test> {
        useJUnitPlatform()
        testLogging {
            events("passed", "skipped", "failed")
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
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
