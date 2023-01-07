import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.20"
    idea
    `maven-publish`
    `java-library`
    signing
}

java{
    registerFeature("okhttpSupport"){
        usingSourceSet(sourceSets["main"])
        withSourcesJar()
        withJavadocJar()
    }
}

dependencies {
    testImplementation(kotlin("test"))
    "okhttpSupportImplementation"("com.squareup.okhttp3:okhttp:4.10.0")
}

fun JavaToolchainSpec.configure() {
    languageVersion.set(JavaLanguageVersion.of(17))
    vendor.set(JvmVendorSpec.GRAAL_VM)
}

allprojects {
    group = "space.iseki.envproxyselector"

    repositories {
        mavenCentral()
    }
    tasks.withType<JavaCompile>().configureEach {
        javaCompiler.set(javaToolchains.compilerFor { configure() })
    }
    tasks.withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
        kotlinJavaToolchain.toolchain.use(javaToolchains.launcherFor { configure() })
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
            if (version.toString().endsWith("SNAPSHOT")) {
                // uri("https://s01.oss.sonatype.org/content/repositories/snapshots")
                url = uri("https://oss.sonatype.org/content/repositories/snapshots")
            } else {
                // uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2")
                url = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2")
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
                        url.set("http://opensource.org/licenses/MIT")
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

