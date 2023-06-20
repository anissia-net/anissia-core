# Anissia Core
애니시아 코어 프로젝트
- 주소 : [anissia.net](https://anissia.net)

## 개발환경
* language : kotlin jdk 17
* **[Elastic Search](https://www.elastic.co) (필수)**
  ```
  # 도커 예시
  docker run --name elasticsearch -p 9200:9200 -p 9300:9300 --restart=always -e "xpack.security.enabled=false" -e "discovery.type=single-node"  docker.elastic.co/elasticsearch/elasticsearch:8.7.0
  ```

## 실행
각 IDE에서 실행하거나 직접 gradle wrapper를 이용하여 실행


#### 실행/빌드 명령어
```
# 로컬 실행 (기본값)
gradlew bootRun

# 개발 실행 (애니시아 개발서버 VPN 필요)
gradlew bootRun -Dspring.profiles.active=dev

# 운영 실행 (운영서버 내에서만 가능)
gradlew bootRun -Dspring.profiles.active=prod

# 빌드
gradlew build

# 실행
java -jar anissia-core-1.0.jar --spring.profiles.active=prod
```

#### 로컬 실행
- Elastic Search 를 설치한다 (위 개발환경 참고)
- 기본데이터 생성 ~~http://localhost:8001/data/test/basic~~
    - 현재 지원하지 않음 (다시 지원할 예정)
    - 기본데이터 (계정)
      
      |구분|계정|암호|
      |---|---|---|
      |관리자|admin@test.com|asdfasdf|
      |사용자|user@test.com|asdfasdf|
    
- 데이터를 초기화하려면 프로젝트 폴더 내 다음 파일을 삭제한다.
    - anissia-local.lock.db
    - anissia-local.mv.db


#### 로컬 DB 접근 정보
   - H2를 탑재하여 별도의 DB세팅 없이 사용 가능합니다.
   - 별도의 SQL 클라이언트 사용시 아래의 연결정보를 입력합니다.\
     (최초 bootRun 실행, anissia-local.mv.db 파일 생성 후 접속 가능)
      
      |jdbc url| jdbc:h2:<프로젝트 경로>/anissia-local;AUTO_SERVER=TRUE |
      |--------------------------------------------------|---|
      |user| sa                                               |
      |password| anissia                                          |
      
      jdbc url example
      
      |OS|jdbc url example|
      |---|---|
      |windows|jdbc:h2:file:C:/Users/username/anissia-core/anissia-local;AUTO_SERVER=TRUE|
      |mac|jdbc:h2:/Users/username/anissia-core/anissia-local;AUTO_SERVER=TRUE|


## 참고 
* [애니시아 문서](https://github.com/anissia-net/document)
* [애니편성표 API](https://github.com/anissia-net/document/blob/main/api_anime_schdule.md)
* [애니시아 CORE 프로젝트](https://github.com/anissia-net/anissia-core)
* [애니시아 WEB 프로젝트](https://github.com/anissia-net/anissia-web)
