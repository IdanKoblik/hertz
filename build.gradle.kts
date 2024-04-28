plugins {
    id("java")
    id("maven-publish")
    id("org.jetbrains.kotlin.jvm") version "1.6.10"
}

group = "com.github.idankoblik"
version = "1.2.7"

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
        if (project.findProperty("apartium.nexus.username") != null) {
            maven {
                name = "BetaReleases"
                url = uri("https://nexus.voigon.dev/repository/beta-releases/")
                credentials {
                    username = project.findProperty("apartium.nexus.username").toString()
                    password = project.findProperty("apartium.nexus.password").toString()
                }
            }
        }
    }
}

dependencies {
    implementation("net.dv8tion:JDA:${ project.findProperty("jda.version") }")
    compileOnly("org.projectlombok:${ project.findProperty("lombok.version") }")
    implementation("com.google.guava:guava:${ project.findProperty("guava.version") }")
}