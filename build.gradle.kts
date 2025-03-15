import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
	val kotlinVersion = "2.1.20-RC3"
	id("org.jetbrains.kotlin.jvm") version kotlinVersion
	id("org.jetbrains.kotlin.plugin.spring") version kotlinVersion
	id("org.jetbrains.kotlin.plugin.allopen") version kotlinVersion
	id("org.jetbrains.kotlin.plugin.jpa") version kotlinVersion
	id("org.springframework.boot") version "3.4.3"
	id("io.spring.dependency-management") version "1.1.7"
	id("org.ec4j.editorconfig") version "0.1.0"
	id("idea")
	java
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
	implementation("org.springframework.boot:spring-boot-starter-mail")

	// DB connector
	implementation("com.zaxxer:HikariCP")
	runtimeOnly("org.mariadb.jdbc:mariadb-java-client")

	// elasticsearch
	implementation("org.elasticsearch.client:elasticsearch-rest-client:8.17.3")

	// lib
	implementation("me.saro:kit:0.2.3")
	implementation("org.mindrot:jbcrypt:0.4")
	implementation("me.saro:jwt:6.0.0")

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

java {
	toolchain {
		languageVersion.set(JavaLanguageVersion.of(23))
	}
}

kotlin {
	compilerOptions {
		jvmTarget.set(JvmTarget.JVM_23)
	}
}

configure<JavaPluginExtension> {
	sourceCompatibility = JavaVersion.VERSION_23
	targetCompatibility = JavaVersion.VERSION_23
}
