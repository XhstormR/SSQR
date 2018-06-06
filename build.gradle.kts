import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    repositories {
        maven("http://mirrors.163.com/maven/repository/maven-public/")
    }

    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${extra["kotlin_version"]}")
    }
}

repositories {
    maven("http://mirrors.163.com/maven/repository/maven-public/")
}

dependencies {
    compile("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    compile("com.google.zxing:core:+")
    compile("com.google.code.gson:gson:+")
}

version = "1.0-SNAPSHOT"

plugins {
    idea
    application
    kotlin("jvm") version "1.2.41"
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
        gradleVersion = "4.6"
        distributionType = Wrapper.DistributionType.ALL
    }

    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
    }

    withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.isIncremental = true
        sourceCompatibility = JavaVersion.VERSION_1_8.toString()
        targetCompatibility = JavaVersion.VERSION_1_8.toString()
    }
}
