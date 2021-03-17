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

## 실행
각 IDE에서 실행하거나 직접 gradle wrapper를 이용하여 실행


#### 로컬 (기본값)
   - H2를 탑재하여 별도의 DB세팅 없이 사용 가능합니다.
   - 별도의 SQL 클라이언트 사용시 아래의 연결정보를 입력합니다.\
     (최초 bootRun 실행, anissia-local.mv.db 파일 생성 후 접속 가능)
      
      |jdbc url|jdbc:h2:프로젝트폴더경로/anissia-local;AUTO_SERVER=TRUE|
      |-|-|
      |user|sa|
      |password|anissia|
      
      jdbc url example
      
      |OS|jdbc url example|
      |-|-|
      |windows|jdbc:h2:file:C:/Users/username/anissia-core/anissia-local;AUTO_SERVER=TRUE|
      |mac|jdbc:h2:/Users/username/anissia-core/anissia-local;AUTO_SERVER=TRUE|
      
   - 최초 사이트 접속 계정
   
      |구분|계정|암호|
      |-|-|-|
      |관리자|admin@admin.com|admin|
      |사용자|user@user.com|user|
      
```
gradlew bootRun
```

#### 개발 / 운영
   - 별도 VPN 설치 및 접근권한 부여 서버에서만 접속가능
   - 대부분 위 로컬로 테스트 가능합니다.
```
# IntelliJ 기준으로 gradle 실행 Arguments 에 -Penv=<VALUE> 입력

# 개발
gradlew bootRun -Penv dev

# 운영
gradlew bootRun -Penv prod
```

## 참고 
[Anissia WEB 프로젝트](https://github.com/anissia-net/anissia-web)
