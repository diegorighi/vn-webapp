plugins {
    java
    id("org.springframework.boot") version "3.3.2"
    id("io.spring.dependency-management") version "1.1.5"
    jacoco
    checkstyle
    id("com.github.spotbugs") version "6.0.4"
}

group = "com.vanessaviagem"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_21

repositories {
    mavenCentral()
}

dependencies {
    // DevTools - Hot reload para desenvolvimento (nao vai para producao)
    developmentOnly("org.springframework.boot:spring-boot-devtools")

    implementation("org.springframework.boot:spring-boot-starter-graphql")
    implementation("com.graphql-java:graphql-java-extended-scalars:22.0")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-web")

    // Security - OAuth2 Resource Server with JWT
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")

    // Cache for DEK (Data Encryption Keys)
    implementation("com.github.ben-manes.caffeine:caffeine:3.1.8")

    // JSON processing
    implementation("com.fasterxml.jackson.core:jackson-databind")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")

    // Flyway for database migrations
    implementation("org.flywaydb:flyway-core")
    implementation("org.flywaydb:flyway-database-postgresql")

    // Playwright for browser automation (Azul, US Airlines)
    implementation("com.microsoft.playwright:playwright:1.41.0")

    runtimeOnly("org.postgresql:postgresql")
    runtimeOnly("com.h2database:h2")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.junit.jupiter:junit-jupiter-params")
    testImplementation("org.testcontainers:postgresql:1.19.3")
    testImplementation("org.testcontainers:junit-jupiter:1.19.3")

    // SpotBugs annotations for suppressions
    compileOnly("com.github.spotbugs:spotbugs-annotations:4.8.3")
}

tasks.test {
    useJUnitPlatform()
    // Pass system properties to test JVM
    systemProperties(System.getProperties().mapKeys { it.key.toString() })
}

// =============================================================================
// JaCoCo Configuration
// =============================================================================
jacoco {
    toolVersion = "0.8.11"
}

tasks.withType<Test> {
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}

tasks.jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit {
                minimum = "0.80".toBigDecimal()
            }
        }
        rule {
            element = "CLASS"
            includes = listOf("com.vanessaviagem.*.domain.*")
            limit {
                minimum = "0.85".toBigDecimal()
            }
        }
    }
}

tasks.named("check") {
    dependsOn(tasks.jacocoTestCoverageVerification)
}

// =============================================================================
// Checkstyle Configuration
// =============================================================================
checkstyle {
    toolVersion = "10.12.5"
    configFile = file("${rootDir}/config/checkstyle/checkstyle.xml")
    configDirectory.set(file("${rootDir}/config/checkstyle"))
    isIgnoreFailures = false
    maxWarnings = 0
}

tasks.withType<Checkstyle> {
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}

// =============================================================================
// SpotBugs Configuration
// =============================================================================
spotbugs {
    ignoreFailures.set(false)
    showStackTraces.set(true)
    showProgress.set(true)
    effort.set(com.github.spotbugs.snom.Effort.MAX)
    reportLevel.set(com.github.spotbugs.snom.Confidence.MEDIUM)
    excludeFilter.set(file("${rootDir}/config/spotbugs/exclude-filter.xml"))
}

tasks.withType<com.github.spotbugs.snom.SpotBugsTask> {
    reports.create("xml") {
        required.set(true)
    }
    reports.create("html") {
        required.set(true)
        setStylesheet("fancy-hist.xsl")
    }
}

// AIVDEV-NOTE: Checkstyle 10.12.5, SpotBugs 6.0.4, JaCoCo 0.8.11
// What: Build fails on any style violation, bug pattern, or coverage below 80%
// Why: Enforce code quality gates as per project standards
// Impact: CI pipeline will reject non-compliant code
