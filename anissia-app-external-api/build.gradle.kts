import org.springframework.boot.gradle.tasks.run.BootRun
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
	kotlin("jvm")
	kotlin("kapt")
	kotlin("plugin.spring")
	kotlin("plugin.jpa")
	kotlin("plugin.allopen")
	id("org.springframework.boot")
	id("io.spring.dependency-management")
}

dependencies {
	// project
	implementation(project(":anissia-data"))
	implementation(project(":anissia-data-elasticsearch"))

	// JPA
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-data-elasticsearch")

	// lib
	implementation("me.saro:kit-ee:+")
	implementation("org.jsoup:jsoup:+")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("joda-time:joda-time:+")
	implementation("org.apache.lucene:lucene-analyzers-nori:+")

	// logger
	implementation("org.slf4j:slf4j-api")
	implementation("ch.qos.logback:logback-classic")
	implementation("com.github.gavlyukovskiy:p6spy-spring-boot-starter:+")

	// spring
	implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-mail")
	kapt("org.springframework.boot:spring-boot-configuration-processor")

	// spring security
	implementation("org.springframework.boot:spring-boot-starter-security")

	// test
	testImplementation("org.springframework.boot:spring-boot-starter-test") {
		exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
	}
}

sourceSets {
	main {
		// project environment :  ex) prod : -Penv=prod
		val env = project.findProperty("env") ?: "local"

		resources {
			srcDir("src/main/resources-${env}")
		}
	}
}

tasks.getByName<BootRun>("bootRun") {
	sourceResources(sourceSets["main"])
}
