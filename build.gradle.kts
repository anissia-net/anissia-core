import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

// project environment :  ex) prod : -Penv=prod
val env = project.findProperty("env") ?: "local"

plugins {
	val kotlinVersion = "1.8.10"
	id("org.jetbrains.kotlin.jvm") version kotlinVersion
	id("org.jetbrains.kotlin.kapt") version kotlinVersion
	id("org.jetbrains.kotlin.plugin.spring") version kotlinVersion
	id("org.jetbrains.kotlin.plugin.allopen") version kotlinVersion
	id("org.jetbrains.kotlin.plugin.jpa") version kotlinVersion
	id("org.springframework.boot") version "3.0.5"
	id("io.spring.dependency-management") version "1.1.0"
	id("org.ec4j.editorconfig") version "0.0.3"
	id("idea")
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

	// JPA query dsl
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-data-elasticsearch:2.3.12.RELEASE")
	implementation("org.springframework.boot:spring-boot-starter-mail")

	// DB connector
	implementation("com.zaxxer:HikariCP")
	if (env != "local") {
		runtimeOnly("org.mariadb.jdbc:mariadb-java-client")
	} else {
		runtimeOnly("com.h2database:h2")
	}

	// lib
	implementation("me.saro:kit-ee:0.1.8")
	implementation("org.jsoup:jsoup:1.15.4")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("joda-time:joda-time:2.12.2")
	implementation("org.mindrot:jbcrypt:0.4")
	implementation("me.saro:jwt:2.0.1")

	// logger
	implementation("org.slf4j:slf4j-api")
	implementation("ch.qos.logback:logback-classic")
	if (env != "prod") {
		implementation("com.github.gavlyukovskiy:p6spy-spring-boot-starter:1.9.0")
	}

	// spring
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("org.springframework.boot:spring-boot-starter-validation")

	//implementation("org.springframework.cloud:spring-cloud-starter-consul-all:4.0.2")

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
