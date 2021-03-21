# Anissia Core
애니시아 코어 프로젝트

## 개발환경
* language : kotlin jdk 13
* conventions :
   * Tab : 4 spaces
   * Charset : UTF-8
* IDE : IntelliJ IDEA (Ultimate, Community), Eclipse
   * IDE가 제공하는 방법으로 gradle import 를 합니다.
   * Eclipse 의 경우 4 space tab / encoding UTF-8 세팅 필수.
* charset
   * 글자가 깨질경우 -Dfile.encoding=UTF-8 옵션을 추가합니다.
   * IntelliJ 기준 Help -> Edit Custom VM Options
* **Elastic Search (필수)**
    - 아래의 주소에가서 다운 / 설치
    - windows (자동설치 버전을 다운받아도된다.)
        - https://www.elastic.co/downloads/elasticsearch
        - 압축해제 후 elasticsearch-x.x.x/bin 로이동
        - elasticsearch-service install elasticsearch
        - 실행 -> service.msc -> elasticsearch 실행
    - Mac
        - brew install elasticsearch
        - brew services start elasticsearch
        - curl -XGET localhost:9200
    - Linux
        - apt-get install elasticsearch

## 실행
각 IDE에서 실행하거나 직접 gradle wrapper를 이용하여 실행


#### 실행/빌드 명령어
```
# IntelliJ 기준으로 gradle 실행 Arguments 에 -Penv=<VALUE> 입력

# 로컬 실행 (기본값)
gradlew :anissia-app-external-api:bootRun

# 개발 실행 (애니시아 개발서버 VPN 필요)
gradlew :anissia-app-external-api:bootRun -Penv dev

# 운영 실행 (운영서버 내에서만 가능)
gradlew :anissia-app-external-api:bootRun -Penv prod

# 마찬가지로 빌드도 아래와 같이한다 (운영빌드 예제)
gradlew :anissia-app-external-api:build -Penv prod
```

#### 로컬 실행
- Elastic Search 를 설치한다 (위 개발환경 참고)
- 기본데이터 생성 http://localhost:8001/data/test/basic
    - 기본데이터 (계정)
      
      |구분|계정|암호|
      |---|---|---|
      |관리자|admin@test.com|asdfasdf|
      |사용자|user@test.com|asdfasdf|
    
- 데이터를 초기화하려면 anissia-app-external-api 폴더 내 다음 파일을 삭제한다.
    - anissia-local.lock.db
    - anissia-local.mv.db


#### 로컬 DB 접근 정보
   - H2를 탑재하여 별도의 DB세팅 없이 사용 가능합니다.
   - 별도의 SQL 클라이언트 사용시 아래의 연결정보를 입력합니다.\
     (최초 bootRun 실행, anissia-local.mv.db 파일 생성 후 접속 가능)
      
      |jdbc url|jdbc:h2:<anissia-app-external-api 경로>/anissia-local;AUTO_SERVER=TRUE|
      |---|---|
      |user|sa|
      |password|anissia|
      
      jdbc url example
      
      |OS|jdbc url example|
      |---|---|
      |windows|jdbc:h2:file:C:/Users/username/anissia-core/anissia-app-external-api/anissia-local;AUTO_SERVER=TRUE|
      |mac|jdbc:h2:/Users/username/anissia-core/anissia-app-external-api/anissia-local;AUTO_SERVER=TRUE|


## 참고 
[Anissia WEB 프로젝트](https://github.com/anissia-net/anissia-web)
