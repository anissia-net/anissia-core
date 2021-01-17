package anissia.misc

class KoUtil {
    companion object {
        // 일반 분해
        private val KO_INIT_S = "ㄱㄲㄴㄷㄸㄹㅁㅂㅃㅅㅆㅇㅈㅉㅊㅋㅌㅍㅎ".toCharArray() // 19
        private val KO_INIT_M = "ㅏㅐㅑㅒㅓㅔㅕㅖㅗㅘㅙㅚㅛㅜㅝㅞㅟㅠㅡㅢㅣ".toCharArray() // 21
        private val KO_INIT_E = charArrayOf(0.toChar()) + "ㄱㄲㄳㄴㄵㄶㄷㄹㄺㄻㄼㄽㄾㄿㅀㅁㅂㅄㅅㅆㅇㅈㅊㅋㅌㅍㅎ".toCharArray() // 28

        // 완전 분해
        private val KO_ATOM_S = "ㄱ ㄱㄱ ㄴ ㄷ ㄷㄷ ㄹ ㅁ ㅂ ㅂㅂ ㅅ ㅅㅅ ㅇ ㅈ ㅈㅈ ㅊ ㅋ ㅌ ㅍ ㅎ".split(" ").map { it.toCharArray() }.toTypedArray()
        private val KO_ATOM_M = "ㅏ ㅐ ㅑ ㅒ ㅓ ㅔ ㅕ ㅖ ㅗ ㅗㅏ ㅗㅐ ㅗㅣ ㅛ ㅜ ㅜㅓ ㅜㅔ ㅜㅣ ㅠ ㅡ ㅡㅣ ㅣ".split(" ").map { it.toCharArray() }.toTypedArray()
        private val KO_ATOM_E = arrayOf(charArrayOf()) + ("ㄱ ㄱㄱ ㄱㅅ ㄴ ㄴㅈ ㄴㅎ ㄷ ㄹ ㄹㄱ ㄹㅁ ㄹㅂ ㄹㅅ ㄹㅌ ㄹㅍ ㄹㅎ ㅁ ㅂ ㅂㅅ ㅅ ㅅㅅ ㅇ ㅈ ㅊ ㅋ ㅌ ㅍ ㅎ".split(" ").map { it.toCharArray() }.toTypedArray())

        // 쌍자음이나 이중모음을 분해
        private val KO_ATOM_P = "ㄱ ㄱㄱ ㄱㅅ ㄴ ㄴㅈ ㄴㅎ ㄷ ㄸ ㄹ ㄹㄱ ㄹㅁ ㄹㅂ ㄹㅅ ㄹㄷ ㄹㅍ ㄹㅎ ㅁ ㅂ ㅂㅂ ㅂㅅ ㅅ ㅅㅅ ㅇ ㅈ ㅈㅈ ㅊ ㅋ ㅌ ㅍ ㅎ ㅏ ㅐ ㅑ ㅒ ㅓ ㅔ ㅕ ㅖ ㅗ ㅗㅏ ㅗㅐ ㅗㅣ ㅛ ㅜ ㅜㅓ ㅜㅔ ㅜㅣ ㅠ ㅡ ㅡㅣ ㅣ".split(" ").map { it.toCharArray() }.toTypedArray()


        /** 한글부분을 초성으로 교체합니다.  */
        fun toKoChosung(text: String?): String? {
            if (text == null) {
                return null
            }

            // 한글자가 한글자와 그대로 대응됨.
            // 때문에 기존 텍스트를 토대로 작성된다.
            val rv = text.toCharArray()
            var ch: Char
            for (i in rv.indices) {
                ch = rv[i]
                if (ch in '가'..'힣') {
                    rv[i] = KO_INIT_S[(ch - '가') / 588] // 21 * 28
                }
            }
            return String(rv)
        }

        /** 한글부분을 자소로 분리합니다. <br></br>많다 = [ㅁㅏㄶㄷㅏ]  */
        fun toKoJaso(text: String?): String? {
            if (text == null) {
                return null
            }
            // StringBuilder의 capacity가 0으로 등록되는 것 방지.
            if (text.isEmpty()) {
                return ""
            }

            // 한글자당 최대 3글자가 될 수 있다.
            // 추가 할당 없이 사용하기위해 capacity 를 최대 글자 수 만큼 지정하였다.
            val rv = StringBuilder(text.length * 3)
            for (ch in text.toCharArray()) {
                if (ch in '가'..'힣') {
                    // 한글의 시작부분을 구함
                    var ce = ch - '가'
                    // 초성을 구함
                    rv.append(KO_INIT_S[ce / 588]) // 21 * 28
                    // 중성을 구함
                    rv.append(KO_INIT_M[ce % 588.also { ce = it } / 28]) // 21 * 28
                    // 종성을 구함
                    if (ce % 28.also { ce = it } != 0) {
                        rv.append(KO_INIT_E[ce])
                    }
                } else {
                    rv.append(ch)
                }
            }
            return rv.toString()
        }

        /** 한글부분을 자소로 완전 분리합니다. <br></br>많다 = [ㅁㅏㄴㅎㄷㅏ] */
        fun toKoJasoAtom(text: String?): String {
            if (text == null) {
                return ""
            }
            // StringBuilder의 capacity가 0으로 등록되는 것 방지.
            if (text.isEmpty()) {
                return ""
            }

            // 한글자당 최대 6글자가 될 수 있다.
            // 추가 할당 없이 사용하기위해 capacity 를 최대 글자 수 만큼 지정하였다.
            val rv = StringBuilder(text.length * 6)
            for (ch in text.toCharArray()) {
                if (ch in '가'..'힣') {
                    // 한글의 시작부분을 구함
                    var ce = ch - '가'
                    // 초성을 구함
                    rv.append(KO_ATOM_S[ce / 588]) // 21 * 28
                    // 중성을 구함
                    rv.append(KO_ATOM_M[ce % 588.also { ce = it } / 28]) // 21 * 28
                    // 종성을 구함
                    if (ce % 28.also { ce = it } != 0) {
                        rv.append(KO_ATOM_E[ce])
                    }
                } else if (ch in 'ㄱ'..'ㅣ') {
                    rv.append(KO_ATOM_P[ch - 'ㄱ'])
                } else {
                    rv.append(ch)
                }
            }
            return rv.toString()
        }
    }

}