/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.ksp)
    alias(libs.plugins.dagger.hilt.android)
    alias(libs.plugins.google.protobuf)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.google.jetpackcamera.data.settings"
    compileSdk = libs.versions.compileSdk.get().toInt()
    compileSdkPreview = libs.versions.compileSdkPreview.get()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
        testOptions.targetSdk = libs.versions.targetSdk.get().toInt()
        lint.targetSdk = libs.versions.targetSdk.get().toInt()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    packagingOptions {
        resources.pickFirsts.add("**.proto")
    }

    sourceSets {
        getByName("debug") {
            resources.srcDirs("build/generated/ksp/debug/resources")
        }
        getByName("release") {
            resources.srcDirs("build/generated/ksp/release/resources")
        }
    }

    flavorDimensions += "flavor"
    productFlavors {
        create("stable") {
            dimension = "flavor"
            isDefault = true
        }

        create("preview") {
            dimension = "flavor"
            targetSdkPreview = libs.versions.targetSdkPreview.get()
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        jvmToolchain(17)
    }

    @Suppress("UnstableApiUsage")
    testOptions {
        managedDevices {
            localDevices {
                create("pixel2Api28") {
                    device = "Pixel 2"
                    apiLevel = 28
                }
                create("pixel8Api34") {
                    device = "Pixel 8"
                    apiLevel = 34
                    systemImageSource = "aosp_atd"
                }
            }
        }
    }
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)

    // Hilt
    implementation(libs.dagger.hilt.android)
    ksp(libs.dagger.hilt.compiler)

    // proto datastore
    implementation(libs.androidx.datastore)
    implementation(libs.protobuf.kotlin.lite)
    implementation(libs.protobuf.generate)
    ksp(libs.protobuf.generate)

    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.truth)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.truth)
    androidTestImplementation(libs.kotlinx.coroutines.test)
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:4.28.2"
    }

    generateProtoTasks {
        all().forEach {task ->
            task.builtins {
                create("java") {
                    option("lite")
                }
            }

            task.builtins {
                create("kotlin") {
                    option("lite")
                }
            }
        }
    }
}

