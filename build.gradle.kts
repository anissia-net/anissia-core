import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	val kotlinVersion = "1.5.0"
	kotlin("jvm") version kotlinVersion apply false
	kotlin("kapt") version kotlinVersion apply false
	kotlin("plugin.spring") version kotlinVersion apply false
	kotlin("plugin.jpa") version kotlinVersion apply false
	kotlin("plugin.allopen") version kotlinVersion apply false
	id("org.springframework.boot") version "2.4.5" apply false
	id("io.spring.dependency-management") version "1.0.11.RELEASE" apply false
	id("org.ec4j.editorconfig") version "0.0.3"
}

allprojects {
	group = "anissia"
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
