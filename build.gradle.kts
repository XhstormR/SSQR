import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    repositories {
        maven("http://maven.aliyun.com/nexus/content/groups/public/")
    }

    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${extra["kotlin_version"]}")
    }
}

repositories {
    maven("http://maven.aliyun.com/nexus/content/groups/public/")
}

dependencies {
    compile("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    compile("com.google.zxing:core:+")
}

version = "1.0-SNAPSHOT"

plugins {
    idea
    application
    kotlin("jvm") version "1.2.21"
}

tasks {
    val exe by creating(Exec::class) {
        dependsOn("jar")
        buildDir.resolve("bin").mkdirs()
        val launch4jExe = "${ext["launch4j_home"]}/launch4jc.exe"
        val launch4jCfg = "$rootDir/assets/config.xml"
        commandLine(launch4jExe, launch4jCfg)
    }

    val beforeJar by creating {
        buildDir
                .resolve("tmp").apply { mkdirs() }
                .resolve("1.txt").apply { createNewFile() }
                .bufferedWriter().use { configurations.compile.forEach { s -> it.write("$s\n") } }
    }

    withType<Jar> {
        dependsOn(beforeJar)
        version = ""
        manifest.attributes["Main-Class"] = "com.xhstormr.ssqr.SSQR"
        from(buildDir.resolve("tmp/1.txt").bufferedReader().readLines().map { zipTree(it) })
    }

    withType<Wrapper> {
        gradleVersion = "4.5"
        distributionType = Wrapper.DistributionType.ALL
    }

    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}
