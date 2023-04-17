package anissia.domain.session.core.model

class DoTokenLoginCommand(
    val absoluteToken: String = ""
) {
    /** token number */
    val tn get() = absoluteToken.substringBefore('-', "0").toLong()

    /** token */
    val token get() = absoluteToken.substringAfter('-', "")
}
