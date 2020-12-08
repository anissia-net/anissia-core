package anissia.misc

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import me.saro.kit.dates.Dates
import org.springframework.web.util.HtmlUtils
import java.util.*

/**
 * As: anissia
 * anissia locally utils
 */
class As {
    companion object {
        private val OBJECT_MAPPER = ObjectMapper()

        const val IS_NAME = "[0-9A-Za-z가-힣㐀-䶵一-龻ぁ-ゖゝ-ヿ々_]{2,16}"

        /**
         * anissia broadcast type<br>
         * 1:mon - 7:sun to 0:sun - 6:sat
         */
        fun nowBcType(): String = (Dates.toZonedDateTime(Calendar.getInstance()).dayOfWeek.value % 7).toString()

        fun getResource(path: String) = As::class.java.getResource(path)!!

        fun toJson(value: Any) = OBJECT_MAPPER.writeValueAsString(value)!!

        fun toJsonObject(value: Any, vararg pairs: Pair<String, Any>)
                = OBJECT_MAPPER.convertValue(value, object: TypeReference<MutableMap<String, Any>>(){})
                .run { this.putAll(pairs); toJson(this) }

        fun String.escapeHtml() = HtmlUtils.htmlEscape(this)

        fun <T> String.toClassByJson(valueTypeRef: TypeReference<T>) = OBJECT_MAPPER.readValue(this, valueTypeRef)!!

        fun <T> String.toMapByJson() = this.toClassByJson(object: TypeReference<Map<String, Object>>(){})
    }
}