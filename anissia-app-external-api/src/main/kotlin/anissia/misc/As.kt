package anissia.misc

import anissia.rdb.dto.AnimeDto
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
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
        val OBJECT_MAPPER = jacksonObjectMapper()
        const val IS_NAME = "[0-9A-Za-z가-힣㐀-䶵一-龻ぁ-ゖゝ-ヿ々_]{2,16}"
        val DTF_ISO_YMD = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val DTF_ISO_YMDHMS = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
        val DTF_ISO_CAPTION = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")
        val DTF_RANK_HOUR = DateTimeFormatter.ofPattern("yyyyMMddHH")
        val DTF_USER_YMDHMS = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분 ss일")

        fun getResource(path: String): URL = As::class.java.getResource(path)!!

        fun toJsonString(value: Any): String = OBJECT_MAPPER.writeValueAsString(value)!!

        //fun <T> toClass(json: String): T = OBJECT_MAPPER.readValue(json, object: TypeReference<T>(){})

        //fun <T> toClassList(json: String): List<T> = OBJECT_MAPPER.readValue(json, object: TypeReference<List<T>>(){})

        fun String.escapeHtml() = HtmlUtils.htmlEscape(this)

        fun <T> String.toClassByJson(valueTypeRef: TypeReference<T>) = OBJECT_MAPPER.readValue(this, valueTypeRef)!!

        fun <T> String.toMapByJson() = this.toClassByJson(object: TypeReference<Map<String, Any>>(){})

        fun <T, U> replacePage(page: Page<U>, list: List<T>): Page<T> = PageImpl(list, page.pageable, page.totalElements)

        fun <T> filterPage(page: Page<T>, filter: (T) -> Boolean): Page<T> = PageImpl(page.content.filter { filter(it) }, page.pageable, page.totalElements)

        fun throwHttp400(msg: String) {
            val errors = BeanPropertyBindingResult(null, "").apply { reject("400", msg) }
            throw MethodArgumentNotValidException(MethodParameter(As.javaClass.constructors[0], 0, 0), errors)
        }

        fun throwHttp400If(msg: String, isError: Boolean) {
            if (isError) { throwHttp400(msg) }
        }
        fun throwHttp400Exception(msg: String, exec: () -> Unit) = try { exec() } catch (e: Exception) { throwHttp400(msg) }

        fun isWebSite(website: String, allowEmpty: Boolean = false) =
            (allowEmpty && website == "") || website.startsWith("https://") || website.startsWith("http://")
    }
}
