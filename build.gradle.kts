import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version Versions.Spring.BOOT
    id("com.diffplug.spotless") version Versions.Plugin.SPOTLESS
    id("io.spring.dependency-management") version Versions.Spring.DEPENDENCY_MANAGEMENT
    kotlin("jvm") version Versions.KOTLIN
    kotlin("kapt") version Versions.KOTLIN
    kotlin("plugin.spring") version Versions.KOTLIN
    kotlin("plugin.jpa") version Versions.KOTLIN
    kotlin("plugin.allopen") version Versions.KOTLIN
}

apply {
    plugin("com.diffplug.spotless")
}

repositories {
    mavenCentral()
}

group = "com.briolink"
version = "1.0-SNAPSHOT"

tasks.withType<JavaCompile> {
    sourceCompatibility = Versions.JAVA
    targetCompatibility = Versions.JAVA
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = Versions.JAVA
    }
}

spotless {
    kotlin {
        target("**/*.kt")

        // https://github.com/diffplug/spotless/issues/142
        ktlint().userData(
            mapOf(
                "indent_style" to "space",
                "max_line_length" to "140",
                "indent_size" to "4",
                "ij_kotlin_code_style_defaults" to "KOTLIN_OFFICIAL",
                "ij_kotlin_line_comment_at_first_column" to "false",
                "ij_kotlin_line_comment_add_space" to "true",
                "ij_kotlin_name_count_to_use_star_import" to "2147483647",
                "ij_kotlin_name_count_to_use_star_import_for_members" to "2147483647",
                "ij_kotlin_keep_blank_lines_in_declarations" to "1",
                "ij_kotlin_keep_blank_lines_in_code" to "1",
                "ij_kotlin_keep_blank_lines_before_right_brace" to "0",
                "ij_kotlin_align_multiline_parameters" to "false",
                "ij_continuation_indent_size" to "4",
                "insert_final_newline" to "true",
            )
        )

        trimTrailingWhitespace()
        indentWithSpaces()
        endWithNewline()
    }

    kotlinGradle {
        target("**/*.gradle.kts", "*.gradle.kts")

        ktlint().userData(mapOf("indent_size" to "4", "continuation_indent_size" to "4"))

        trimTrailingWhitespace()
        indentWithSpaces()
        endWithNewline()
    }
}

tasks.withType<KotlinCompile> {
    dependsOn("spotlessApply")
    dependsOn("spotlessCheck")
}

repositories {
    mavenCentral()
    mavenLocal()
    setOf(
        36319712, // BL Common
        // 36548529, // BL Dictionary
    ).forEach {
        maven {
            url = uri("https://gitlab.com/api/v4/projects/$it/packages/maven")
            authentication {
                create<HttpHeaderAuthentication>("header")
            }
            credentials(HttpHeaderCredentials::class) {
                name = "Deploy-Token"
                value = System.getenv("CI_DEPLOY_PASSWORD")
            }
        }
    }
}

dependencies {
    // Spring Boot
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    annotationProcessor("org.springframework.boot:spring-boot-autoconfigure-processor")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    kapt("org.springframework.boot:spring-boot-autoconfigure-processor")
    kapt("org.springframework.boot:spring-boot-configuration-processor")
    implementation("me.paulschwarz:spring-dotenv:${Versions.Spring.DOTENV}")
    implementation("org.springframework.security:spring-security-core")
    implementation("org.springframework.security:spring-security-oauth2-jose")
    implementation("org.springframework.security:spring-security-oauth2-resource-server")

    // FasterXML
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    // SWAGGER
    implementation("io.springfox:springfox-boot-starter:${Versions.SPRINGFOX}")

    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    // BRIOLINK LOCATION
    implementation("com.briolink.lib:dictionary:${Versions.Briolink.DICTIONARY}")
    implementation("com.briolink.lib:common:${Versions.Briolink.COMMON}")

    // Liquibase
    implementation("org.liquibase:liquibase-core:${Versions.LIQUIBASE_CORE}")

    // Kotlin Logging
    implementation("io.github.microutils:kotlin-logging-jvm:${Versions.KOTLIN_LOGGING_JVM}")

    // postgtrsql JDBC Driver
    runtimeOnly("org.postgresql:postgresql")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.compileJava {
    dependsOn(tasks.processResources)
}
