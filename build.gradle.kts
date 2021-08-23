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
import care.data4life.sdk.result.dependency.d4l
import care.data4life.sdk.result.dependency.gitHub

plugins {
    id("care.data4life.sdk.result.dependency")

    id("care.data4life.sdk.result.dependency-updates")
    id("care.data4life.sdk.result.download-scripts")
    id("care.data4life.sdk.result.publishing")
    id("care.data4life.sdk.result.quality-spotless")
    id("care.data4life.sdk.result.versioning")
}

allprojects {
    repositories {
        mavenCentral()
        google()

        gitHub(project)

        d4l()
    }
}

tasks.named<Wrapper>("wrapper") {
    gradleVersion = "6.9.1"
    distributionType = Wrapper.DistributionType.ALL
}
