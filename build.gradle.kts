import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

// project environment :  ex) prod : -Penv=prod
val env = project.findProperty("env") ?: "local"

plugins {
	val kotlinVersion = "1.7.10"
	kotlin("jvm") version kotlinVersion
	kotlin("kapt") version kotlinVersion
	kotlin("plugin.spring") version kotlinVersion
	kotlin("plugin.allopen") version kotlinVersion
	kotlin("plugin.jpa") version kotlinVersion
	id("org.springframework.boot") version "2.7.1"
	id("io.spring.dependency-management") version "1.0.12.RELEASE"
	id("org.ec4j.editorconfig") version "0.0.3"
}

group = "anissia"
version = "1.0"

java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
	mavenCentral()
}

// very important
// 이것이 설정되어 있지않은 경우 Entity 내 모든 조인필드를
// getter 를 통한 사용여부와 상관없이 강제적으로 모두 get 하여 셀릭트 1 + n 문제를 읽으킨다.
allOpen {
	annotation("javax.persistence.Entity")
	annotation("javax.persistence.Embeddable")
	annotation("javax.persistence.MappedSuperclass")
}

dependencies {
	// kotlin
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation("org.jetbrains.kotlin:kotlin-reflect")

	// db
	implementation("com.zaxxer:HikariCP")
	runtimeOnly("org.mariadb.jdbc:mariadb-java-client")
	//runtimeOnly(if (env != "local") "org.mariadb.jdbc:mariadb-java-client" else "com.h2database:h2")

	// JPA query dsl
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-data-elasticsearch:2.6.11")

	implementation("com.querydsl:querydsl-jpa")
	implementation("com.querydsl:querydsl-apt")
	implementation("com.querydsl:querydsl-sql:+")
	kapt("com.querydsl:querydsl-apt:+:jpa")

	// DB connector
	implementation("com.zaxxer:HikariCP")
	runtimeOnly("org.mariadb.jdbc:mariadb-java-client")
	runtimeOnly("com.h2database:h2")

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
	testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "17"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

configure<JavaPluginExtension> {
	sourceCompatibility = JavaVersion.VERSION_17
	targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "17"
	}
}

sourceSets {
	main {
		resources {
			srcDir("src/main/resources-${env}")
		}
	}
}

tasks.getByName<org.springframework.boot.gradle.tasks.run.BootRun>("bootRun") {
	sourceResources(sourceSets["main"])
}
