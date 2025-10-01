import org.gradle.kotlin.dsl.compileOnly
import org.gradle.kotlin.dsl.testImplementation

plugins {
    id("java-library")
    id("maven-publish")
    alias(libs.plugins.jetbrains.kotlin.jvm)
    alias(libs.plugins.kotlinx.serialization)
}
java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}
kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11
    }
}
dependencies {
    compileOnly(libs.lint.api)
    testImplementation(libs.lint.tests)
    implementation(libs.kotlinx.serialization.json)
}
tasks.jar {
    manifest {
        attributes(mapOf("Lint-Registry-v2" to "com.helpfullintrules.lint_rules.AndroidCustomLintIssueRegistry"))
    }
}
publishing {
    publications {
        create<MavenPublication>("android-lint-rules") {
            groupId = "com.github.AlmightyMegadeth00"
            artifactId = "AndroidCustomLintRules"
            version = "1.0"

            afterEvaluate {
                artifact(tasks.jar)
            }
        }
    }
}