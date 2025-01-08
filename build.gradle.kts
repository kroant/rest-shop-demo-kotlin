plugins {
    id("org.springframework.boot") version "3.4.1"
    id("io.spring.dependency-management") version "1.1.7"
    kotlin("jvm") version "2.1.0"
    kotlin("plugin.spring") version "2.1.0"
    kotlin("plugin.jpa") version "2.1.0"
    kotlin("kapt") version "2.1.0"
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
    implementation("org.springframework.retry:spring-retry:2.0.11")

    implementation("net.javacrumbs.shedlock:shedlock-spring:6.0.2")
    implementation("net.javacrumbs.shedlock:shedlock-provider-jdbc-template:6.0.2")

    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.7.0")

    // Newer version of HikariCP required because of SQLTimeoutException closes connection.
    // https://github.com/brettwooldridge/HikariCP/issues/1388
    implementation("com.zaxxer:HikariCP:6.2.1")
    runtimeOnly("com.h2database:h2:2.3.232")

    compileOnly("org.mapstruct:mapstruct:1.6.3")
    kapt("org.mapstruct:mapstruct-processor:1.6.3")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("io.rest-assured:kotlin-extensions:5.5.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.4.0")
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