plugins {
    id("java")
    id("java-library")
    id("maven-publish")
    id("com.gradleup.shadow").version("9.4.3")
}

group = "lofe.fastbytes"
version = "1.3"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:6.1.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.junit.platform:junit-platform-launcher")
    testImplementation("org.java-websocket:Java-WebSocket:1.5.3") // for tests with already working library
}

var targetJavaVersion = 23
java {
    var javaVersion = JavaVersion.toVersion(targetJavaVersion)
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
    if (JavaVersion.current() < javaVersion) {
        toolchain.languageVersion = JavaLanguageVersion.of(targetJavaVersion)
    }
}

tasks.test {
    useJUnitPlatform()
}

val sourcesJar by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

extensions.configure<PublishingExtension> {
    repositories {
        maven {
            name = "modoruReleases"
            url = uri("https://repository.modoru.fun/releases")

            credentials {
                username = System.getenv("MODORU_USERNAME") ?: ""
                password = System.getenv("MODORU_TOKEN") ?: ""
            }
        }
    }

    publications {
        create<MavenPublication>("maven") {
            artifactId = "fastbytes"
            group = "lofe.fastbytes"
            version = rootProject.version.toString()

            from(components["java"])
            artifact(tasks.named("sourcesJar"))
        }
    }
}