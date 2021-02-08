import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	val kotlinVersion = "1.4.20"
	kotlin("jvm") version kotlinVersion apply false
	kotlin("kapt") version kotlinVersion apply false
	kotlin("plugin.spring") version kotlinVersion apply false
	kotlin("plugin.jpa") version kotlinVersion apply false
	kotlin("plugin.allopen") version kotlinVersion apply false
	id("org.springframework.boot") version "2.4.0" apply false
	id("io.spring.dependency-management") version "1.0.10.RELEASE" apply false
}

allprojects {
	group = "net.dhant"
	version = "1.0"

	repositories {
		mavenCentral()
	}
}

subprojects {
	apply(plugin = "kotlin")

	dependencies {
		val implementation by configurations
		implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
		implementation("org.jetbrains.kotlin:kotlin-reflect")
	}

	configure<JavaPluginExtension> {
		sourceCompatibility = JavaVersion.VERSION_13
		targetCompatibility = JavaVersion.VERSION_13
	}

	tasks.withType<KotlinCompile> {
		kotlinOptions {
			freeCompilerArgs = listOf("-Xjsr305=strict")
			jvmTarget = "13"
		}
	}

	tasks.withType<Test> {
		useJUnitPlatform()
	}
}
