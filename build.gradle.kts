import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import com.android.build.api.dsl.CommonExtension
import java.util.Properties

plugins {
    alias(libs.plugins.kotlin) apply false
    alias(libs.plugins.agp.app) apply false
    alias(libs.plugins.agp.lib) apply false
    alias(libs.plugins.nav.safeargs.kotlin) apply false
}

fun String.execute(currentWorkingDir: File = file("./")): String {
    return providers.exec {
        workingDir = currentWorkingDir
        commandLine = split("\\s".toRegex())
    }.standardOutput.asText.get().trim()
}

val gitCommitCount by extra("git rev-list HEAD --count".execute().toInt())
val gitCommitHash by extra("git rev-parse --verify --short HEAD".execute())

val minSdkVer by extra(28)
val targetSdkVer by extra(36)
val buildToolsVer by extra("36.0.0")

val appVerName by extra("4.2.2")
val configVerCode by extra(90)
val serviceVerCode by extra(97)
val minBackupVerCode by extra(65)

val androidSourceCompatibility by extra(JavaVersion.VERSION_21)
val androidTargetCompatibility by extra(JavaVersion.VERSION_21)

val localProperties = Properties()
localProperties.load(file("local.properties").inputStream())
val officialBuild by extra(localProperties.getProperty("officialBuild", "false") == "true")
val localPropsExtra by extra(localProperties)

tasks.register("clean", Delete::class) {
    delete(rootProject.layout.buildDirectory)
}

subprojects {
    val rootExtra = rootProject.extra
    val lp = rootExtra["localPropsExtra"] as Properties

    plugins.withId("com.android.application") {
        extensions.configure<ApplicationExtension> {
            compileSdk = rootExtra["targetSdkVer"] as Int
            buildToolsVersion = rootExtra["buildToolsVer"] as String

            defaultConfig {
                minSdk = rootExtra["minSdkVer"] as Int
                targetSdk = rootExtra["targetSdkVer"] as Int
                versionCode = rootExtra["gitCommitCount"] as Int
                versionName = rootExtra["appVerName"] as String

                if (lp.getProperty("buildWithGitSuffix").toBoolean()) {
                    versionNameSuffix = ".${rootExtra["gitCommitCount"]}"
                }
            }

            val config = lp.getProperty("fileDir")?.let {
                signingConfigs.create("config") {
                    storeFile = project.file(it)
                    storePassword = lp.getProperty("storePassword")
                    keyAlias = lp.getProperty("keyAlias")
                    keyPassword = lp.getProperty("keyPassword")
                }
            }

            buildTypes {
                all {
                    signingConfig = config ?: signingConfigs.getByName("debug")
                }
                getByName("release") {
                    isMinifyEnabled = true
                    isShrinkResources = true
                    proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
                }
            }

            compileOptions {
                sourceCompatibility = rootExtra["androidSourceCompatibility"] as JavaVersion
                targetCompatibility = rootExtra["androidTargetCompatibility"] as JavaVersion
            }
        }
    }

    plugins.withId("com.android.library") {
        extensions.configure<LibraryExtension> {
            compileSdk = rootExtra["targetSdkVer"] as Int
            buildToolsVersion = rootExtra["buildToolsVer"] as String

            defaultConfig {
                minSdk = rootExtra["minSdkVer"] as Int
                consumerProguardFiles("proguard-rules.pro")
            }

            val config = lp.getProperty("fileDir")?.let {
                signingConfigs.create("config") {
                    storeFile = project.file(it)
                    storePassword = lp.getProperty("storePassword")
                    keyAlias = lp.getProperty("keyAlias")
                    keyPassword = lp.getProperty("keyPassword")
                }
            }

            buildTypes {
                all {
                    signingConfig = config ?: signingConfigs.getByName("debug")
                }
                getByName("release") {
                    isMinifyEnabled = false
                    proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
                }
            }

            compileOptions {
                sourceCompatibility = rootExtra["androidSourceCompatibility"] as JavaVersion
                targetCompatibility = rootExtra["androidTargetCompatibility"] as JavaVersion
            }
        }
    }
}
