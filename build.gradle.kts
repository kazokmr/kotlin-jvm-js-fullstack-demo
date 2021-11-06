import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpack

plugins {
    kotlin("multiplatform") version "1.5.31"
    application
    kotlin("plugin.serialization") version "1.5.31"
}

group = "me.kazokmr"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "16"
        }
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
        withJava()
    }
    js(IR) {
        binaries.executable()
        browser {
            commonWebpackConfig {
                cssSupport.enabled = true
            }
        }
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.0")
                implementation("io.ktor:ktor-client-core:1.6.5")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation("io.ktor:ktor-server-core:1.6.5")
                implementation("io.ktor:ktor-server-netty:1.6.5")
                implementation("io.ktor:ktor-serialization:1.6.5")
                implementation("ch.qos.logback:logback-classic:1.2.6")
            }
        }
        val jvmTest by getting
        val jsMain by getting {
            dependencies {
//                implementation("io.ktor:ktor-client-js:1.6.5")
//                implementation("io.ktor:ktor-client-json:1.6.5")
//                implementation("io.ktor:ktor-client-serialization:1.6.5")

//                implementation("org.jetbrains.kotlin-wrappers:kotlin-react:17.0.2-pre.262-kotlin-1.5.31")
//                implementation("org.jetbrains.kotlin-wrappers:kotlin-react-dom:17.0.2-pre.262-kotlin-1.5.31")
            }
        }
        val jsTest by getting
    }
}

application {
    mainClass.set("ServerKt")
}

tasks.named<Jar>("jvmJar") {
    val taskName = if (project.hasProperty("isProduction")) {
        "jsBrowserProductionWebpack"
    } else {
        "jsBrowserDevelopmentWebpack"
    }
    val webpackTask = tasks.getByName<KotlinWebpack>(taskName)
    dependsOn(webpackTask)
    from(File(webpackTask.destinationDirectory, webpackTask.outputFileName))
}

tasks.named<JavaExec>("run") {
    dependsOn(tasks.named<Jar>("jvmJar"))
    classpath(tasks.named<Jar>("jvmJar"))
}
