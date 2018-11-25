import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

version = "1.0-SNAPSHOT"

plugins {
    idea
    application
    kotlin("jvm") version "1.3.10"
}

repositories {
    jcenter()
}

dependencies {
    compile(kotlin("stdlib-jdk8"))
    compile("com.google.zxing:core:+")
    compile("com.google.code.gson:gson:+")
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
        manifest.attributes["Main-Class"] = "com.xhstormr.ssqr.App"
        from(buildDir.resolve("tmp/1.txt").bufferedReader().readLines().map { zipTree(it) })
    }

    withType<Wrapper> {
        gradleVersion = "4.10.1"
        distributionType = Wrapper.DistributionType.ALL
    }

    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
    }

    withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.isFork = true
        options.isIncremental = true
        sourceCompatibility = JavaVersion.VERSION_11.toString()
        targetCompatibility = JavaVersion.VERSION_11.toString()
    }
}
