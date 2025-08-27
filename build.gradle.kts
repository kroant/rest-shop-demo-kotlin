plugins {
    id("org.springframework.boot") version "3.5.5"
    id("io.spring.dependency-management") version "1.1.7"
    kotlin("jvm") version "2.2.10"
    kotlin("plugin.spring") version "2.2.10"
    kotlin("plugin.jpa") version "2.2.10"
    kotlin("kapt") version "2.2.10"
    jacoco
}

group = "cz.kromer.restshopdemo"

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain(21)
    compilerOptions {
        freeCompilerArgs.addAll(
            "-Xjsr305=strict",
            "-Xemit-jvm-type-annotations" // Required for annotations on type variables (e.g. validation of collection items)
        )
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.retry:spring-retry")

    implementation("net.javacrumbs.shedlock:shedlock-spring:6.10.0")
    implementation("net.javacrumbs.shedlock:shedlock-provider-jdbc-template:6.10.0")

    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.11")

    runtimeOnly("com.h2database:h2")

    compileOnly("org.mapstruct:mapstruct:1.6.3")
    kapt("org.mapstruct:mapstruct-processor:1.6.3")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("io.rest-assured:kotlin-extensions")
    testImplementation("org.mockito.kotlin:mockito-kotlin:6.0.0")
}

kapt {
    arguments {
        arg("mapstruct.defaultComponentModel", "spring")
        arg("mapstruct.defaultInjectionStrategy", "constructor")
    }
}

tasks {
    test {
        useJUnitPlatform()
        finalizedBy("jacocoTestReport", "jacocoTestCoverageVerification")
    }

    withType<JacocoCoverageVerification> {
        violationRules {
            rule {
                element = "CLASS"
                limit {
                    minimum = "0.8".toBigDecimal()
                }
                excludes = listOf(
                    "cz.kromer.restshopdemo.ApplicationKt",
                    "cz.kromer.restshopdemo.config.AppConfig",
                    "cz.kromer.restshopdemo.config.SchedulingConfig"
                )
            }
        }
    }
}