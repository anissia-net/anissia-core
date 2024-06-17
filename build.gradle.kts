import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	val kotlinVersion = "2.0.0"
	id("org.jetbrains.kotlin.jvm") version kotlinVersion
	id("org.jetbrains.kotlin.kapt") version kotlinVersion
	id("org.jetbrains.kotlin.plugin.spring") version kotlinVersion
	id("org.jetbrains.kotlin.plugin.allopen") version kotlinVersion
	id("org.jetbrains.kotlin.plugin.jpa") version kotlinVersion
	id("org.springframework.boot") version "3.3.0"
	id("io.spring.dependency-management") version "1.1.5"
	id("org.ec4j.editorconfig") version "0.1.0"
	id("idea")
}

group = "anissia"
version = "1.0"

repositories {
	mavenCentral()
}

idea {
	module {
		excludeDirs = listOf("build", "logs", "out", "tmp").map { file(it) }.toSet()
	}
}

dependencies {
	// JPA query dsl
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-data-elasticsearch")
	implementation("org.springframework.boot:spring-boot-starter-mail")

	// DB connector
	implementation("com.zaxxer:HikariCP")
	runtimeOnly("org.mariadb.jdbc:mariadb-java-client")

	// lib
	implementation("me.saro:kit-ee:0.1.8")
	implementation("org.mindrot:jbcrypt:0.4")
	implementation("me.saro:jwt:2.1.0")

	// logger
	implementation("org.slf4j:slf4j-api")
	implementation("ch.qos.logback:logback-classic")

	// spring
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("org.springframework.boot:spring-boot-starter-validation")

	// test
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.graphql:spring-graphql-test")
	testImplementation("org.springframework.boot:spring-boot-starter-webflux")
}

kotlin {
	compilerOptions {
		//compilerOptions.jvmTarget.set(JvmTarget.JVM_21)
		//apiVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_1)
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

configure<JavaPluginExtension> {
	sourceCompatibility = JavaVersion.VERSION_21
	targetCompatibility = JavaVersion.VERSION_21
}
