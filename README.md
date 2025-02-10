# Anissia Core
애니시아 코어 프로젝트
- 주소 : [anissia.net](https://anissia.net)

## 개발환경
* Kotlin (JDK 23)
  * **[Elastic Search](https://www.elastic.co)** (설치필요)
    <br/>도커 설치시 예시
    ```
    # 엘라스틱서치
    docker run -d --name elasticsearch -p 9200:9200 --restart=always -e "xpack.security.enabled=false" -e "discovery.type=single-node" docker.elastic.co/elasticsearch/elasticsearch:8.17.1
      # 애플 실리콘에서는 arm64 버전과함게 CLI_JAVA_OPTS=-XX:UseSVE=0, ES_JAVA_OPTS=-XX:UseSVE=0 옵션을 줘야한다.
        - https://discuss.elastic.co/t/issue-with-elasticsearch-docker-deployment-on-apple-silicon-m4-processor-macos-15-2/373214
        - https://github.com/elastic/elasticsearch/issues/118583#issuecomment-2546897726
      # 볼륨 /Users/anissia/db/elasticsearch1 에 연동해서 애플 실리콘에서 설치할 경우
      docker run -d --name elasticsearch -p 9200:9200 --restart=always -e 'CLI_JAVA_OPTS=-XX:UseSVE=0' -e 'ES_JAVA_OPTS=-XX:UseSVE=0' -e 'xpack.security.enabled=false' -e 'discovery.type=single-node' -v /Users/anissia/db/elasticsearch1:/usr/share/elasticsearch/data docker.elastic.co/elasticsearch/elasticsearch:8.17.1-arm64
    ```
* **[Maria DB](https://mariadb.org)** (설치필요)
  <br/>도커 설치시 예시
  ```
  docker run -d --name mariadb -p 3306:3306 --restart=always -e MYSQL_ROOT_PASSWORD=root mariadb
  ```
  공통 (anissia db 생성 후 anissia / anissia 계정 생성 필요)
  ```
  CREATE DATABASE anissia;
  CREATE USER 'anissia'@'%' IDENTIFIED BY 'anissia';
  GRANT ALL PRIVILEGES ON * . * TO 'anissia'@'%';
  FLUSH PRIVILEGES;
  ```

## 실행
각 IDE에서 실행하거나 직접 gradle wrapper를 이용하여 실행

### 실행/빌드 명령어
```
# 로컬 실행
gradlew bootRun -Dspring.profiles.active=local

# 개발 실행
gradlew bootRun -Dspring.profiles.active=dev

# 운영 실행
gradlew bootRun -Dspring.profiles.active=prod

# 빌드
gradlew build

# 실행
java -jar anissia-core-1.0.jar --spring.profiles.active=prod
```

### 기타
- gradlew update
```
./gradlew wrapper --gradle-version latest --distribution-type all
```

### 로컬 기본 데이터 생성
- http://localhost:8080/install (다시 작성할 예정)

## 임시
- 린트 : Possibly blocking call in non-blocking context 제외.


## 참고 
* [애니시아 문서](https://github.com/anissia-net/document)
* [애니편성표 API](https://github.com/anissia-net/document/blob/main/api_anime_schdule.md)
* [애니시아 CORE 프로젝트](https://github.com/anissia-net/anissia-core)
* [애니시아 WEB 프로젝트](https://github.com/anissia-net/anissia-web)
