/*
 * Copyright (c) 2021 D4L data4life gGmbH / All rights reserved.
 *
 * D4L owns all legal rights, title and interest in and to the Software Development Kit ("SDK"),
 * including any intellectual property rights that subsist in the SDK.
 *
 * The SDK and its documentation may be accessed and used for viewing/review purposes only.
 * Any usage of the SDK for other purposes, including usage for the development of
 * applications/third-party applications shall require the conclusion of a license agreement
 * between you and D4L.
 *
 * If you are interested in licensing the SDK for your own applications/third-party
 * applications and/or if you’d like to contribute to the development of the SDK, please
 * contact D4L by email to help@data4life.care.
 */
import care.data4life.gradle.result.dependency.Dependency
import care.data4life.gradle.result.config.LibraryConfig

plugins {
    id("org.jetbrains.kotlin.multiplatform")

    // Android
    id("com.android.library")

    // Publish
    id("care.data4life.gradle.result.script.publishing-config")
}

group = LibraryConfig.group

kotlin {
    android {
        publishLibraryVariants("release", "debug")
    }

    jvm()

    ios {}

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(care.data4life.gradle.result.dependency.Dependency.multiplatform.kotlin.stdlibCommon)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(care.data4life.gradle.result.dependency.Dependency.multiplatform.kotlin.testCommon)
                implementation(care.data4life.gradle.result.dependency.Dependency.multiplatform.kotlin.testCommonAnnotations)
            }
        }

        val androidMain by getting {
            dependencies {
                implementation(care.data4life.gradle.result.dependency.Dependency.multiplatform.kotlin.stdlibAndroid)
            }
        }
        val androidTest by getting {
            dependencies {
                implementation(care.data4life.gradle.result.dependency.Dependency.multiplatform.kotlin.testJvm)
                implementation(care.data4life.gradle.result.dependency.Dependency.multiplatform.kotlin.testJvmJunit)
            }
        }

        val jvmMain by getting {
            dependencies {
                implementation(care.data4life.gradle.result.dependency.Dependency.multiplatform.kotlin.stdlibJdk8)
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(care.data4life.gradle.result.dependency.Dependency.multiplatform.kotlin.testJvm)
                implementation(care.data4life.gradle.result.dependency.Dependency.multiplatform.kotlin.testJvmJunit)
            }
        }

        val iosMain by getting {
            dependencies {
                implementation(care.data4life.gradle.result.dependency.Dependency.multiplatform.kotlin.stdlibNative)
            }
        }

        val iosTest by getting {
            dependencies {
                dependencies {
                    implementation(care.data4life.gradle.result.dependency.Dependency.multiplatform.kotlin.testCommon)
                    implementation(care.data4life.gradle.result.dependency.Dependency.multiplatform.kotlin.testCommonAnnotations)
                }
            }
        }
    }
}

android {
    compileSdkVersion(LibraryConfig.android.compileSdkVersion)

    defaultConfig {
        minSdkVersion(LibraryConfig.android.minSdkVersion)
        targetSdkVersion(LibraryConfig.android.targetSdkVersion)

        versionCode = 1
        versionName = "${project.version}"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testInstrumentationRunnerArguments(
            mapOf(
                "clearPackageData" to "true"
            )
        )
    }

    resourcePrefix(LibraryConfig.android.resourcePrefix)

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    sourceSets {
        getByName("main") {
            manifest.srcFile("src/androidMain/AndroidManifest.xml")
            java.setSrcDirs(setOf("src/androidMain/kotlin"))
            res.setSrcDirs(setOf("src/androidMain/res"))
        }

        getByName("test") {
            java.setSrcDirs(setOf("src/androidTest/kotlin"))
            res.setSrcDirs(setOf("src/androidTest/res"))
        }
    }
}
