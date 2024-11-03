package anissia.domain.account.command

class EditUserPasswordCommand (
    var oldPassword: String = "",
    var newPassword: String = "",
) {
    fun validate() {
        if (oldPassword.isBlank()) throw IllegalArgumentException("암호를 입력해 주세요.")
        if (newPassword.isBlank()) throw IllegalArgumentException("암호를 입력해 주세요.")
        if (newPassword.length < 8 || newPassword.length > 128) throw IllegalArgumentException("암호는 8자 이상 128자 이하로 입력해 주세요.")
        if (oldPassword == newPassword) throw IllegalArgumentException("새 암호가 기존 암호와 같습니다.")
    }
}
