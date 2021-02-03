package anissia.misc

import anissia.rdb.dto.AnimeDto
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import me.saro.kit.dates.Dates
import org.springframework.core.MethodParameter
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.util.HtmlUtils
import java.lang.reflect.Parameter
import java.net.URL
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.function.Predicate
import org.springframework.validation.BeanPropertyBindingResult
import kotlin.reflect.full.primaryConstructor


/**
 * As: anissia
 * anissia locally utils
 */
class As {
    companion object {
        private val OBJECT_MAPPER = ObjectMapper()
        const val IS_NAME = "[0-9A-Za-z가-힣㐀-䶵一-龻ぁ-ゖゝ-ヿ々_]{2,16}"
        val DTF_YMDHMS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        const val DTF_CAPTION_S = "yyyy-MM-dd'T'HH:mm"
        val DTF_CAPTION = DateTimeFormatter.ofPattern(DTF_CAPTION_S)
        val DTF_RANK_HOUR = DateTimeFormatter.ofPattern("yyyyMMddHH")

        fun getResource(path: String): URL = As::class.java.getResource(path)!!

        fun toJsonString(value: Any): String = OBJECT_MAPPER.writeValueAsString(value)!!

//        fun toJsonObject(value: Any, vararg pairs: Pair<String, Any>): String =
//            OBJECT_MAPPER.convertValue(value, object: TypeReference<MutableMap<String, Any>>(){}).run { this.putAll(pairs); toJson(this) }

        fun String.escapeHtml() = HtmlUtils.htmlEscape(this)

        fun <T> String.toClassByJson(valueTypeRef: TypeReference<T>) = OBJECT_MAPPER.readValue(this, valueTypeRef)!!

        fun <T> String.toMapByJson() = this.toClassByJson(object: TypeReference<Map<String, Any>>(){})

        fun <T, U> replacePage(page: Page<U>, list: List<T>): Page<T> = PageImpl(list, page.pageable, page.totalElements)

        fun <T> filterPage(page: Page<T>, filter: (T) -> Boolean): Page<T> = PageImpl(page.content.filter { filter(it) }, page.pageable, page.totalElements)

        fun throwHttp400(obj: Any, field: String, msg: String) {
            val errors = BeanPropertyBindingResult(obj, obj::class.simpleName?:"")
            errors.rejectValue(field, msg)
            throw MethodArgumentNotValidException(MethodParameter(obj.javaClass.constructors[0], 0, 0), errors)
        }
    }
}