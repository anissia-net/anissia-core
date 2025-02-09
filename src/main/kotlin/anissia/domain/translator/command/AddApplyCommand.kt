package anissia.domain.translator.command

import anissia.infrastructure.common.isWebSite

class AddApplyCommand(
    var website: String = ""
) {
    fun validate() {
        require(isWebSite(website, false)) { "사이트주소는 공백이거나 http:// https:// 로시작해야합니다." }
    }
}
