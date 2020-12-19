# 애니메이션 편성표 API
|버전|작성자|날짜|비고|
|-|-|-|-|
|2.0|박용서|2020-12-19|API 주소/필드 변경|
|1.2|박용서|2019-12-25|Cross Origin 접근 허용|
|1.1|박용서|2019-12-14|주소변경 / 애니메이션 목록에 bcType 값 추가|
|1.0|박용서|2019-08-16|초안|
- 1차(2009-2013), 2차(2013-2020) API는 폐기되며 앞으로는 버전으로 관리.

<br/><br/>

# 애니메이션 목록
### 요청
```
https://anissia.net/api/anime/schedule/<week>
```

* **week**: text(1)
    * 요일(기타, 신작 포함)
      
      |0|1|2|3|4|5|6|7|8|
      |-|-|-|-|-|-|-|-|-|
      |일|월|화|수|목|금|토|기타|신작|

### 응답 (JSON ARRAY)
```
[{item 0}, {item 2},... {item n}]
```
#### item 구성

|변수명|타입|공백허용|설명|예제값|비고|
|-|-|-|-|-|
|animeNo|int(20)|X|애니등록번호|123|애니자막 호출시 사용|
|week|text(1)|X|요일|1|요청값 **week** 을 반환|
|status|int(1)|X|ON: 반영중, OFF: 결방|ON|OFF일경우 제목앞에 [결방] 표시 권장|
|time|text(10)|O|week 0-6: 시간 <br/> week 7-8: 날짜|00:00 <br/> 2019-08-01|날짜 `yyyy-99-99` -> `yyyy` 치환권장 <br/> 날짜 `2099-99-99` -> `미정` 치환권장|
|subject|text(100)|X|제목|애니메이션 제목|-|
|genres|text(64)|X|장르 (다중값)|모험,판타지| **공백없는쉼표**로 구분되며 치환사용을 권장|
|startDate|text(10)|O|시작일|2019-01-02|공백이 아니며 요일이면서 미래인경우: 제목앞에 [01/02] 처럼 표기 권장|
|endDate|text(10)|O|종료일|2019-08-16|공백이 아니며 요일이면서 과거인경우: 제목앞에 [完] 표기 권장|
|website|text(128)|O|공식사이트|https://anissia.net|빈값가능|

- 기본적으로 모든 날짜/시간은 타임존 Asia/Seoul(+09:00)이 적용됨

<br/><br/>

# 애니메이션 자막 정보
### 요청
```
https://anissia.net/api/anime/caption/<animeNo>
```

* **animeNo**: int(20) = 애니등록번호 (위 애니메이션 목록 참고)

### 응답 (JSON ARRAY)
```
[{item 0}, {item 2},... {item n}]
```
#### item 구성

|변수명|타입|공백허용|설명|예제값|비고|
|-|-|-|-|-|
|episode|text(10)|O|회차<br/>소수점1자리 가능|0<br/>30 <br/> 12.3|-|
|updDt|text(19)|X|자막등록시간|2012-02-01 00:00:00|타임존 Asia/Seoul(+09:00)이 적용됨|
|website|text(512)|O|자막링크주소|https://anissia.net|빈값가능|
|name|text(32)|X|자막제작자이름|박용서|-|

<br/><br/>

# 프록시 없이 자바스크립트로 직접 접근 (권장)
- 2009년 ~ 2020년 까지 지원되던 JSONP API 지원중단
    - Cross Origin 접근 허용으로 대신함
    - IE 10 이상 사용가능 : **하단 Access-Control-Allow-Origin 설명 참조**
        - 2020년 01월 기준 IE 9 이하 사용자가 거의 없다고 판단되어 JSONP API 지원중단
- 구현예제
   ``` javascript
   // 이 예제는 간결한 소스코드상 fetch를 사용하였지만, fetch는 IE에서 지원되지 않음으로 다른 라이브러리 사용권장
   // 브라우저의 개발자도구를 열고 간단하게 아래 예제를 실행할 수 있음.
   // - GitHub는 기본정책이 Cross Origin 차단임으로 새탭을 열어서 개발자도구 사용.
   fetch('https://anissia.net/api/anime/timetable/0') // 0: 일요일
       .then(e => e.json())
       .then(list => list.forEach(node => console.log(`${node.bcTimeOrDate} ${node.subject}`)));
   ```

<br/><br/>

# 참고
**애니편성표 소스**
* [애니메이션 2015 버전](https://github.com/anissia-net/anissia-web/blob/master/src/views/timetable/2015.vue)
* [애니메이션 2009 버전](https://github.com/anissia-net/anissia-web/blob/master/src/views/timetable/2009.vue)
* [API 호출 (timetable, caption)](https://github.com/anissia-net/anissia-web/blob/master/src/services/AnimeService.ts)
* [애니편성표 목록 객체](https://github.com/anissia-net/anissia-web/blob/master/src/models/AnimeTimetable.ts)
* [애니편성표 자막 객체](https://github.com/anissia-net/anissia-web/blob/master/src/models/AnimeCaption.ts)

**기타**
* [Access-Control-Allow-Origin 설명](https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Access-Control-Allow-Origin)
