package anissia.misc


/**
 * @author PARK Yong Seo
 */
class KoUtil {
    companion object {
        // korean -> ja/mo initial
        private val KO_INIT_S = "ㄱㄲㄴㄷㄸㄹㅁㅂㅃㅅㅆㅇㅈㅉㅊㅋㅌㅍㅎ".toCharArray() // 19
        private val KO_INIT_M = "ㅏㅐㅑㅒㅓㅔㅕㅖㅗㅘㅙㅚㅛㅜㅝㅞㅟㅠㅡㅢㅣ".toCharArray() // 21
        private val KO_INIT_E = charArrayOf(0.toChar()) + "ㄱㄲㄳㄴㄵㄶㄷㄹㄺㄻㄼㄽㄾㄿㅀㅁㅂㅄㅅㅆㅇㅈㅊㅋㅌㅍㅎ".toCharArray() // 28

        // korean -> ja/mo atom
        private val KO_ATOM_S = "ㄱ ㄱㄱ ㄴ ㄷ ㄷㄷ ㄹ ㅁ ㅂ ㅂㅂ ㅅ ㅅㅅ ㅇ ㅈ ㅈㅈ ㅊ ㅋ ㅌ ㅍ ㅎ".split(" ").map { it.toCharArray() }.toTypedArray()
        private val KO_ATOM_M = "ㅏ ㅐ ㅑ ㅒ ㅓ ㅔ ㅕ ㅖ ㅗ ㅗㅏ ㅗㅐ ㅗㅣ ㅛ ㅜ ㅜㅓ ㅜㅔ ㅜㅣ ㅠ ㅡ ㅡㅣ ㅣ".split(" ").map { it.toCharArray() }.toTypedArray()
        private val KO_ATOM_E = arrayOf(charArrayOf()) + ("ㄱ ㄱㄱ ㄱㅅ ㄴ ㄴㅈ ㄴㅎ ㄷ ㄹ ㄹㄱ ㄹㅁ ㄹㅂ ㄹㅅ ㄹㅌ ㄹㅍ ㄹㅎ ㅁ ㅂ ㅂㅅ ㅅ ㅅㅅ ㅇ ㅈ ㅊ ㅋ ㅌ ㅍ ㅎ".split(" ").map { it.toCharArray() }.toTypedArray())

        // ja/mo initial -> ja/mo atom
        private val KO_ATOM_P = "ㄱ ㄱㄱ ㄱㅅ ㄴ ㄴㅈ ㄴㅎ ㄷ ㄸ ㄹ ㄹㄱ ㄹㅁ ㄹㅂ ㄹㅅ ㄹㄷ ㄹㅍ ㄹㅎ ㅁ ㅂ ㅂㅂ ㅂㅅ ㅅ ㅅㅅ ㅇ ㅈ ㅈㅈ ㅊ ㅋ ㅌ ㅍ ㅎ ㅏ ㅐ ㅑ ㅒ ㅓ ㅔ ㅕ ㅖ ㅗ ㅗㅏ ㅗㅐ ㅗㅣ ㅛ ㅜ ㅜㅓ ㅜㅔ ㅜㅣ ㅠ ㅡ ㅡㅣ ㅣ".split(" ").map { it.toCharArray() }.toTypedArray()

        /**
         * korean to chosung<br/>
         * ex) 박용서 = ㅂㅇㅅ
         */
        fun toChosung(text: String?): String = text?.run {
            var i = 0
            val arr = CharArray(length)
            for (ch in this) {
                arr[i++] = if (ch in '가'..'힣') KO_INIT_S[(ch - '가') / 588] else ch
            }
            String(arr)
        } ?: ""

        /**
         * korean to ja/so<br/>
         * 많다 = ㅁㅏㄶㄷㅏ
         */
        fun toJaso(text: String?): String = text?.run {
            var i = 0
            val arr = CharArray(length * 3) // maximum ja/so length is korean length * 3
            for (ch in this) {
                if (ch in '가'..'힣') {
                    var ce = ch - '가' // get index start of korean
                    arr[i++] = KO_INIT_S[ce / 588]; ce %= 588 // 21 * 28 - S
                    arr[i++] = KO_INIT_M[ce / 28]; ce %= 28 // 21 * 28 - M
                    if (ce != 0) { arr[i++] = KO_INIT_E[ce] } // E
                } else {
                    arr[i++] = ch
                }
            }
            String(arr)
        } ?: ""

        /**
         * korean to ja/so atom<br/>
         * 많다 = ㅁㅏㄴㅎㄷㅏ
         */
        fun toJasoAtom(text: String?): String = text?.takeIf { it.isNotEmpty() }
            ?.let {
                StringBuilder(text.length * 6).apply { // maximum ja/so atom length is korean length * 6
                    it.forEach { ch ->
                        if (ch in '가'..'힣') {
                            var ce = ch - '가' // get index start of korean
                            this.append(KO_ATOM_S[ce / 588]); ce %= 588 // 21 * 28 - S
                            this.append(KO_ATOM_M[ce / 28]); ce %= 28 // 21 * 28 - M
                            if (ce != 0) { this.append(KO_ATOM_E[ce]) } // E
                        } else if (ch in 'ㄱ'..'ㅣ') {
                            this.append(KO_ATOM_P[ch - 'ㄱ'])
                        } else {
                            this.append(ch)
                        }
                    }
                }.toString()
            } ?: ""
    }
}
