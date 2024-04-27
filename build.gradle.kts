plugins {
    id("java")
    id("maven-publish")
    id("org.jetbrains.kotlin.jvm") version "1.6.10"
}

group = "com.github.idankoblik"
version = "1.1"


repositories {
    mavenCentral()
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            groupId = project.group.toString()
            artifactId = project.name
            version = project.version.toString()

            from(components["java"])
        }
    }
    repositories {
        if (project.findProperty("beta.nexus.username") != null) {
            maven {
                name = "BetaReleases"
                url = uri("https://nexus.voigon.dev/repository/beta-releases/")
                credentials {
                    username = project.findProperty("beta.nexus.username").toString()
                    password = project.findProperty("beta.nexus.password").toString()
                }
            }
        }
    }
}

dependencies {
    implementation("net.dv8tion:JDA:${project.findProperty("jda.version").toString()}")
    compileOnly("org.projectlombok:lombok:${project.findProperty("lombok.version").toString()}")
    implementation("com.google.guava:guava:${project.findProperty("guava.version").toString()}")
}