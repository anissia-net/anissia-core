package anissia.rdb.domain

enum class AnimeStatus {
    ON, // on air : 방영중
    OFF, // adjournment : 휴방
    END, // ended : 완결
    DEL, // delete : 삭제대기
    REQ // request: 등록요청
}
