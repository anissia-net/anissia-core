package anissia.dto.request

import anissia.misc.As

data class TranslatorApplyRequest (
        var website: String = ""
) {
        fun validate() {
                As.throwHttp400If("사이트주소는 공백이거나 http:// https:// 로시작해야합니다.", !As.isWebSite(website, false))
        }
}
