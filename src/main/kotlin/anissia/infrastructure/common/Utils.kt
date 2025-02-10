package anissia.infrastructure.common

import anissia.domain.session.model.SessionItem
import anissia.shared.ApiErrorException
import anissia.shared.ApiResponse
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.mindrot.jbcrypt.BCrypt
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.core.MethodParameter
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.validation.BeanPropertyBindingResult
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.util.HtmlUtils
import reactor.core.Disposable
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.net.URL
import java.net.URLEncoder
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class Utils

private val OBJECT_MAPPER = ObjectMapper()
val REGEX_NAME: Regex = Regex("[0-9A-Za-z가-힣㐀-䶵一-龻ぁ-ゖゝ-ヿ々_]{2,16}")
val REGEX_MAIL: Regex = Regex("[_a-z0-9\\-]+(\\.[_a-z0-9\\-]+)*@([_a-z0-9\\-]+\\.)+[a-z]{2,}")
val REGEX_ANIME_DATE: Regex = Regex("\\d{4}-\\d{2}-\\d{2}")
val DTF_ISO_YMD: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
val DTF_ISO_YMDHMS: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
val DTF_ISO_CAPTION: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")
val DTF_RANK_HOUR: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHH")
val DTF_USER_YMDHMS: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분 ss일")
val EN_BASE64_URL: Base64.Encoder = Base64.getUrlEncoder()
val DE_BASE64_URL: Base64.Decoder = Base64.getUrlDecoder()
val BCRYPT_SALT: String = BCrypt.gensalt(10)

private val typeRefSessionItem = object: TypeReference<SessionItem>() {}
private val _log = logger<Utils>()

inline fun <reified T> logger(): Logger = LoggerFactory.getLogger(T::class.java)

fun <T, F> Mono<T>.doOnNextMono(call: (T) -> Mono<F>): Mono<T> = this.flatMap { call(it).thenReturn(it) }

fun <T> Mono<T>.subscribeBoundedElastic(): Disposable = this.subscribeOn(Schedulers.boundedElastic()).subscribe()
fun <T> Flux<T>.subscribeBoundedElastic(): Disposable = this.subscribeOn(Schedulers.boundedElastic()).subscribe()

//val Mono<Any>.toApiResponse: Mono<ApiResponse<Any>> get() = this.switchIfEmpty(Mono.just("")).map { ApiResponse.ok(it) }
val <T> Mono<T>.toApiResponse: Mono<ApiResponse<T>> get() =
    this.switchIfEmpty(Mono.error(ApiErrorException("")))
        .map { ApiResponse.ok(it) }

fun <T> Page<T>.filterPage(filter: (T) -> Boolean): Page<T> = PageImpl(this.content.filter { filter(it) }, this.pageable, this.totalElements)
//fun <T, U> Page<U>.replacePage(list: List<T>): Page<T> = PageImpl(list, this.pageable, this.totalElements)

fun <T> Mono<Page<T>>.filterPage(filter: (T) -> Boolean): Mono<Page<T>> = this.map { page -> PageImpl(page.content.filter { filter(it) }, page.pageable, page.totalElements) }
//fun <T, U> Mono<Page<U>>.replacePage(list: List<T>): Mono<Page<T>> = this.map { page -> PageImpl(list, page.pageable, page.totalElements) }
fun <T, R> Mono<Page<T>>.mapPageItem(mapper: (T) -> R): Mono<Page<R>> = this.map { page -> page.map { mapper(it) } }

val ServerWebExchange.sessionItem: SessionItem get() =
    OBJECT_MAPPER.readValue(this.request.headers.getFirst("jud")?.decodeBase64Url, typeRefSessionItem)

val String.enBCrypt: String get() = BCrypt.hashpw(this, BCRYPT_SALT)
fun String.eqBCrypt(plaintext: String): Boolean = BCrypt.checkpw(plaintext, this)

val Any.toJson: String get() = OBJECT_MAPPER.writeValueAsString(this)
val Any.toJsonBytes: ByteArray get() = OBJECT_MAPPER.writeValueAsBytes(this)
fun <T> String.toClassByJson(valueTypeRef: TypeReference<T>): T = OBJECT_MAPPER.readValue(this, valueTypeRef)!!

val String.toResource: URL get() = Utils::class.java.getResource(this)!!

val String.escapeHtml: String get() = HtmlUtils.htmlEscape(this)
val String.encodeUrl: String get() = URLEncoder.encode(this, Charsets.UTF_8)
val String.encodeBase64Url: String get() = EN_BASE64_URL.encodeToString(this.toByteArray(Charsets.UTF_8))
val String.decodeBase64Url: String get() = DE_BASE64_URL.decode(this).toString(Charsets.UTF_8)

// anissia anime date yyyy-MM-dd, yyyy-MM-99, yyyy-99-99 or empty
val String.isAsAnimeDate: Boolean get() = when {
    this.isEmpty() -> true
    else -> this.matches(REGEX_ANIME_DATE) && try {
        LocalDate.parse(this.replace("-99", "-01"), DTF_ISO_YMD); true
    } catch (e: Exception) { false }
}


//        fun toJsonString(vararg value: Any): String {
//            val map = mutableMapOf<Any, Any>()
//            value.forEach { map.putAll(OBJECT_MAPPER.convertValue(it, object: TypeReference<Map<String, Any>>() {})) }
//            return OBJECT_MAPPER.writeValueAsString(map)!!
//        }
//        fun <T> filterPage(page: Page<T>, filter: (T) -> Boolean): Page<T> = PageImpl(page.content.filter { filter(it) }, page.pageable, page.totalElements)
//        fun <T, U> replacePage(page: Page<U>, list: List<T>): Page<T> = PageImpl(list, page.pageable, page.totalElements)

fun getHttp400(msg: String): MethodArgumentNotValidException =
    BeanPropertyBindingResult(null, "")
        .apply { _log.error(msg); reject("400", msg) }
        .run { MethodArgumentNotValidException(MethodParameter(Utils::class.java.constructors[0], -1, 0), this) }

fun throwHttp400(msg: String) {
    throw getHttp400(msg)
}

fun throwHttp400If(msg: String, isError: Boolean) {
    if (isError) { throwHttp400(msg) }
}

fun throwHttp400Exception(msg: String, exec: () -> Unit): Unit = try { exec() } catch (e: Exception) { throwHttp400(msg) }

fun isWebSite(website: String, allowEmpty: Boolean = false): Boolean =
    (allowEmpty && website == "") || website.startsWith("https://") || website.startsWith("http://")
