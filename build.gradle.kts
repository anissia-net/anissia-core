plugins {
	val kotlinVersion = "2.0.0"
	id("org.jetbrains.kotlin.jvm") version kotlinVersion
	id("org.jetbrains.kotlin.plugin.spring") version kotlinVersion
	id("org.jetbrains.kotlin.plugin.allopen") version kotlinVersion
	id("org.jetbrains.kotlin.plugin.jpa") version kotlinVersion
	id("org.springframework.boot") version "3.3.2"
	id("io.spring.dependency-management") version "1.1.6"
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

	// elasticsearch
	// - https://www.elastic.co/guide/en/elasticsearch/client/java-api-client/current/getting-started-java.html
	//implementation("co.elastic.clients:elasticsearch-java:8.14.1") // 일반
	implementation("org.elasticsearch.client:elasticsearch-rest-client:8.14.1") // 로우레벨 (결정)

	// lib
	implementation("me.saro:kit:0.2.0")
	implementation("org.mindrot:jbcrypt:0.4")
	implementation("me.saro:jwt:3.0.0")

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

configure<JavaPluginExtension> {
	sourceCompatibility = JavaVersion.VERSION_21
	targetCompatibility = JavaVersion.VERSION_21
}
