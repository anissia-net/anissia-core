job "anissia-core" {
  datacenters = ["dc1"]

  group "anissia-core-group" {
    task "spring-app" {
      driver = "exec"
      config {
        command = "/usr/bin/java"
        args = ["-jar", "build/libs/anissia-core.jar"]
        environment = {
          SERVER_PORT = "8080"
          SPRING_APPLICATION_NAME = "anissia-core"
          SPRING_PROFILES_ACTIVE = "prod"
        }
      }
      resources {
        cpu    = 500
        memory = 1024
      }
    }
  }
}
