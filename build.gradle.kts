import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "com.xhstormr.ssqr"
version = "1.0-SNAPSHOT"

plugins {
    idea
    application
    kotlin("jvm") version "1.3.60"
    id("org.jlleitschuh.gradle.ktlint") version "9.1.1"
}

repositories {
    maven("https://maven.aliyun.com/repository/central")
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("com.google.zxing:core:+")
    implementation("com.google.code.gson:gson:+")
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
                .bufferedWriter().use { configurations.runtimeClasspath.get().forEach { s -> it.write("$s\n") } }
    }

    withType<Jar> {
        dependsOn(beforeJar)
        manifest.attributes["Main-Class"] = "com.xhstormr.ssqr.App"
        from(buildDir.resolve("tmp/1.txt").bufferedReader().readLines().map { zipTree(it) })
    }

    withType<Wrapper> {
        gradleVersion = "6.0"
        distributionType = Wrapper.DistributionType.ALL
    }

    withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "12"
            freeCompilerArgs = listOf("-Xjsr305=strict", "-Xjvm-default=enable")
        }
    }

    withType<JavaCompile> {
        options.encoding = Charsets.UTF_8.name()
        options.isFork = true
        options.isIncremental = true
        sourceCompatibility = JavaVersion.VERSION_12.toString()
        targetCompatibility = JavaVersion.VERSION_12.toString()
    }
}
