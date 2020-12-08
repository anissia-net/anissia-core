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
	// DB connector
	implementation("com.zaxxer:HikariCP")
	runtimeOnly("mysql:mysql-connector-java")
	runtimeOnly("com.h2database:h2")

	// JPA
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")

	// JPA query dsl
	implementation("com.querydsl:querydsl-jpa")
	implementation("com.querydsl:querydsl-apt")
	implementation("com.querydsl:querydsl-sql:+")
	kapt("com.querydsl:querydsl-apt:+:jpa")

	// test
	testImplementation("org.springframework.boot:spring-boot-starter-test") {
		exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
	}
}

tasks.getByName<BootJar>("bootJar") {
	enabled = false
}

tasks.getByName<Jar>("jar") {
	enabled = true
}
