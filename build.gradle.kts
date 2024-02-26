import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.2.2"
    id("io.spring.dependency-management") version "1.1.4"
    kotlin("jvm") version "1.9.22"
    kotlin("plugin.spring") version "1.9.22"
}

group = "com.example"
version = "1.0-SNAPSHOT"

var mockkVersion = "1.13.7"
var coroutines_version = "1.7.3"

java {
    sourceCompatibility = JavaVersion.VERSION_21
}

repositories {
    mavenCentral()
}

dependencies {

    implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    //implementation("org.springframework.boot:spring-boot-starter-websocket")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")

    // per https://docs.spring.io/spring-framework/reference/languages/kotlin/coroutines.html
    //add coroitines-core

    //per https://docs.spring.io/spring-data/mongodb/reference/kotlin/coroutines.html#kotlin.coroutines.dependencies
    // add kotlinx-coroutines-reactive
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactive")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test")

    testImplementation ("io.mockk:mockk:$mockkVersion")
    testImplementation ("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutines_version")

    //testImplementation("org.junit.jupiter:junit-jupiter-params:5.9.2")

}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict"
        jvmTarget = "21"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
