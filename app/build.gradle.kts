plugins {

    // Apply the application plugin to add support for building a CLI application in Java.
    application

    // Lombok
    id("io.freefair.lombok") version "9.5.0"
}

repositories {

    // Use Maven Central for resolving dependencies.
    mavenCentral()
}

dependencies {

    // This dependency is used by the application.
    implementation(libs.guava)

    // ▛▘▛▌▌▌▛▘▛▘█▌
    // ▄▌▙▌▙▌▌ ▙▖▙▖

    // Code validation
    implementation("javax.validation:validation-api:2.0.1.Final")

    // Lombok
    compileOnly("org.projectlombok:lombok:1.18.46")
    annotationProcessor("org.projectlombok:lombok:1.18.46")

    // ▗     ▗ ▘
    // ▜▘█▌▛▘▜▘▌▛▌▛▌
    // ▐▖▙▖▄▌▐▖▌▌▌▙▌
    //            ▄▌

    // jUnit (Jupiter)
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // Lombok Testing
    testCompileOnly("org.projectlombok:lombok:1.18.46")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.46")
}

// Apply a specific Java toolchain to ease working on different environments.
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

application {

    // Define the main class for the application.
    mainClass = "snitcher.ka.Main"
}

tasks.named<Test>("test") {

    // Use JUnit Platform for unit tests.
    useJUnitPlatform()
}