package anissia.domain.translator.model

import anissia.infrastructure.common.As

class NewApplyCommand(
    var website: String = ""
) {
    fun validate() {
        require(As.isWebSite(website, false)) { "사이트주소는 공백이거나 http:// https:// 로시작해야합니다." }
    }
}
