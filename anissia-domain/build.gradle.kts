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

// very important
// 이것이 설정되어 있지않은 경우 Entity 내 모든 조인필드를
// getter 를 통한 사용여부와 상관없이 강제적으로 모두 get 하여 셀릭트 1 + n 문제를 읽으킨다.
allOpen {
	annotation("javax.persistence.Entity")
	annotation("javax.persistence.Embeddable")
	annotation("javax.persistence.MappedSuperclass")
}

dependencies {
	// DB connector
	implementation("com.zaxxer:HikariCP")
	runtimeOnly("org.mariadb.jdbc:mariadb-java-client")
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
